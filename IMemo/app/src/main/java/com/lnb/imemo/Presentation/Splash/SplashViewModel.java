package com.lnb.imemo.Presentation.Splash;

import android.util.Log;
import android.util.Pair;

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

public class SplashViewModel extends ViewModel {
    private static final String TAG = "SplashViewModel";
    private final User mUser;
    private final AuthRepository authRepository;
    private final PersonProfile personProfile;
    private Observer<ResponseRepo> authObserver;
    private final MediatorLiveData<Utils.State> viewModelLiveData = new MediatorLiveData<>();

    public SplashViewModel() {
        mUser = User.getUser();
        authRepository = new AuthRepository();
        personProfile = PersonProfile.getInstance();
        subscribeAuthRepo();
    }

    public void getPersonProfile(String token) {
        mUser.setToken(token);
        authRepository.getUserProfile(mUser.getToken());
    }

    private void subscribeAuthRepo() {
        authObserver = response -> {
            String key = response.getKey();
            if (key.equals(Constant.GET_PERSON_PROFILE)) {
                Pair<Utils.State, PersonProfile> responsePersonProfile = (Pair<Utils.State, PersonProfile>) response.getData();
                switch (responsePersonProfile.first) {
                    case SUCCESS:
                        personProfile.updateInstance(responsePersonProfile.second);
                        viewModelLiveData.setValue(Utils.State.SUCCESS);
                        break;
                    case FAILURE:
                        viewModelLiveData.setValue(Utils.State.FAILURE);
                        break;
                    case NO_INTERNET:
                        viewModelLiveData.setValue(Utils.State.NO_INTERNET);
                        break;
                }
            }
        };
        authRepository.observableAuthRepo().observeForever(authObserver);
    }

    public MediatorLiveData<Utils.State> getViewModelObservable() {
        return viewModelLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        authRepository.observableAuthRepo().removeObserver(authObserver);
    }
}
