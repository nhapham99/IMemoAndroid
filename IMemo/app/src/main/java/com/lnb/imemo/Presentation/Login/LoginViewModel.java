package com.lnb.imemo.Presentation.Login;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.lnb.imemo.Data.APIClient;
import com.lnb.imemo.Data.Repository.Auth.AuthApi;
import com.lnb.imemo.Data.Repository.Auth.AuthRepository;
import com.lnb.imemo.Data.Entity.ResponseRepo;
import com.lnb.imemo.Model.User;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.Utils;

import retrofit2.Retrofit;

class LoginViewModel extends ViewModel {
    private static final String TAG = "LoginViewModel";
    private User mUser;
    private AuthRepository authRepository;
    private Observer<ResponseRepo> authObserver;
    private MediatorLiveData<Utils.State> loginLiveData = new MediatorLiveData<>();
    private MediatorLiveData<Utils.RegisterState> registerLiveData = new MediatorLiveData<>();
    private MediatorLiveData<Utils.State> forgotPasswordLiveData = new MediatorLiveData<>();

    public LoginViewModel() {
        mUser = User.getUser();
        authRepository = new AuthRepository();
        subscribeAuthRepo();
    }

    protected void signInWithGoogle(GoogleSignInAccount account) {
        mUser.setEmail(account.getEmail());
        mUser.setAvatarUrl(account.getPhotoUrl().toString());
        mUser.setName(account.getDisplayName());
        Log.d(TAG, "loginWithGoogle: " + account.getIdToken());
        getTokenFromGoogleToken(account.getIdToken());
    }

    protected void getTokenFromGoogleToken(String ggToken) {
        authRepository.getTokenFromGoogleToken(ggToken);
    }

    protected void signIn(String usernameOrEmail, String password) {
        authRepository.login(usernameOrEmail, password);
    }

    protected void forgotPassword(String email) {
        authRepository.forgotPassword(email);
    }

    protected void subscribeAuthRepo() {
        authObserver = new Observer<ResponseRepo>() {
            @Override
            public void onChanged(ResponseRepo response) {
                String key = response.getKey();
                if (key.equals(Constant.LOGIN_GOOGLE_KEY)) {
                    Pair<Utils.State, String> pair = (Pair<Utils.State, String>) response.getData();
                    mUser.setToken(pair.second);
                    loginLiveData.setValue(pair.first);
                } else if (key.equals(Constant.LOGIN_KEY)) {
                    Pair<Utils.State, String> pair = (Pair<Utils.State, String>) response.getData();
                    mUser.setToken(pair.second);
                    loginLiveData.setValue(pair.first);
                } else if (key.equals(Constant.FORGOT_PASSWORD_KEY)) {
                    forgotPasswordLiveData.setValue((Utils.State) response.getData());
                }
            }
        };
        authRepository.observableAuthRepo().observeForever(authObserver);
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
