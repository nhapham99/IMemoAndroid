package com.lnb.imemo.Presentation.Login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.lnb.imemo.Data.Repository.Auth.AuthRepository;
import com.lnb.imemo.Model.PersonProfile;
import com.lnb.imemo.Model.ResponseRepo;
import com.lnb.imemo.Model.User;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.Utils;

import static android.content.Context.MODE_PRIVATE;

class LoginViewModel extends ViewModel {
    private static final String TAG = "LoginViewModel";
    private final User mUser;
    private final AuthRepository authRepository;
    public PersonProfile personProfile;
    private Observer<ResponseRepo> authObserver;
    private final MediatorLiveData<Utils.State> loginLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<Utils.RegisterState> registerLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<Utils.State> forgotPasswordLiveData = new MediatorLiveData<>();
    @SuppressLint("StaticFieldLeak")
    private final Context mContext;

    public LoginViewModel(Context mContext) {
        mUser = User.getUser();
        authRepository = new AuthRepository();
        personProfile = PersonProfile.getInstance();
        this.mContext = mContext;
        subscribeAuthRepo();
    }

    protected void signInWithGoogle(GoogleSignInAccount account) {
        mUser.setEmail(account.getEmail());
        mUser.setAvatarUrl(account.getPhotoUrl().toString());
        mUser.setName(account.getDisplayName());
        getTokenFromGoogleToken(account.getIdToken());
    }

    protected void getTokenFromGoogleToken(String ggToken) {
        authRepository.getTokenFromGoogleToken(ggToken);
    }

    protected void signIn(String usernameOrEmail, String password) {
        authRepository.login(usernameOrEmail, password);
    }

    protected void subscribeAuthRepo() {
        authObserver = response -> {
            String key = response.getKey();
            if (key.equals(Constant.LOGIN_GOOGLE_KEY)) {
                Pair<Utils.State, String> pair = (Pair<Utils.State, String>) response.getData();
                mUser.setToken(pair.second);
                savePrefData(pair.second);
                getPersonProfile();
            } else if (key.equals(Constant.LOGIN_KEY)) {
                Pair<Utils.State, String> pair = (Pair<Utils.State, String>) response.getData();
                mUser.setToken(pair.second);
                savePrefData(pair.second);
                getPersonProfile();
            } else if (key.equals(Constant.FORGOT_PASSWORD_KEY)) {
                forgotPasswordLiveData.setValue((Utils.State) response.getData());
            } else if (key.equals(Constant.GET_PERSON_PROFILE)) {
                Pair<Utils.State, PersonProfile> responsePersonProfile = (Pair<Utils.State, PersonProfile>) response.getData();
                switch (responsePersonProfile.first) {
                    case SUCCESS:
                        personProfile.updateInstance(responsePersonProfile.second);
                        loginLiveData.setValue(Utils.State.SUCCESS);
                        break;
                    case FAILURE:
                        loginLiveData.setValue(Utils.State.FAILURE);
                        break;
                    case NO_INTERNET:
                        loginLiveData.setValue(Utils.State.NO_INTERNET);
                        break;
                }
            }
        };
        authRepository.observableAuthRepo().observeForever(authObserver);
    }


    private void savePrefData(String token) {
        SharedPreferences pref = mContext.getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        try {
            editor.putString("token", token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        editor.apply();
    }

    public void getPersonProfile() {
        authRepository.getUserProfile(mUser.getToken());
    }

    public MediatorLiveData<Utils.State> observableForgotPasswordLiveData() {
        return forgotPasswordLiveData;
    }

    public LiveData<Utils.State> observableLogin() {
        return loginLiveData;
    }

    public LiveData<Utils.RegisterState> observableRegister() {
        return registerLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        authRepository.observableAuthRepo().removeObserver(authObserver);
    }
}
