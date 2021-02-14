package com.lnb.imemo.Presentation.Register;

import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.lnb.imemo.Model.ResponseRepo;
import com.lnb.imemo.Data.Repository.Auth.AuthRepository;
import com.lnb.imemo.Model.User;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.Utils;

public class RegisterViewModel {
    private static final String TAG = "RegisterViewModel";
    private User mUser;
    private AuthRepository authRepository;
    private Observer<ResponseRepo> authObserver;
    private MediatorLiveData<Utils.State> loginLiveData = new MediatorLiveData<>();
    private MediatorLiveData<Utils.RegisterState> registerLiveData = new MediatorLiveData<>();

    public RegisterViewModel() {
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

    protected void register(String username, String email, String password) {
        authRepository.register(username, email, password);
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
                } else if (key.equals(Constant.REGISTER_KEY)) {
                    registerLiveData.setValue((Utils.RegisterState) response.getData());
                }
            }
        };
        authRepository.observableAuthRepo().observeForever(authObserver);
    }

    public MediatorLiveData<Utils.State> observableLogin() {
        return loginLiveData;
    }

    public MediatorLiveData<Utils.RegisterState> observableRegister() {
        return registerLiveData;
    }
}
