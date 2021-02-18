package com.lnb.imemo.Presentation.ForgotPassword;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.lnb.imemo.Data.Repository.Auth.AuthRepository;
import com.lnb.imemo.Model.ResponseRepo;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.Utils;

public class ForgotPasswordViewModel extends ViewModel {
    private AuthRepository authRepository;
    private Observer<ResponseRepo> authObserver;
    private MediatorLiveData<ResponseRepo> viewModelObserver;

    public ForgotPasswordViewModel() {
        authRepository = new AuthRepository();
        viewModelObserver = new MediatorLiveData<>();
        subscribeAuthObservable();
    }

    protected void forgotPassword(String email) {
        authRepository.forgotPassword(email);
    }


    public void subscribeAuthObservable() {
        authObserver = new Observer<ResponseRepo>() {
            @Override
            public void onChanged(ResponseRepo responseRepo) {
                String key = responseRepo.getKey();
                if (key == Constant.FORGOT_PASSWORD_KEY) {
                    Utils.State state = (Utils.State) responseRepo.getData();
                    ResponseRepo<Utils.State> response = new ResponseRepo();
                    response.setKey(Constant.FORGOT_PASSWORD_KEY);
                    switch (state) {
                        case SUCCESS:
                            response.setData(Utils.State.SUCCESS);
                            break;
                        case FAILURE:
                            response.setData(Utils.State.FAILURE);
                            break;
                        case NO_INTERNET:
                            response.setData(Utils.State.NO_INTERNET);
                            break;
                    }
                    viewModelObserver.setValue(response);
                }
            }
        };
        authRepository.observableAuthRepo().observeForever(authObserver);
    }

    public MediatorLiveData<ResponseRepo> getViewModelObserver() {
        return viewModelObserver;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        authRepository.observableAuthRepo().removeObserver(authObserver);
    }
}
