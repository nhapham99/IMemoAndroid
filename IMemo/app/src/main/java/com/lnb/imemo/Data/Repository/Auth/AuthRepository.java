package com.lnb.imemo.Data.Repository.Auth;

import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;

import com.google.gson.JsonObject;
import com.lnb.imemo.Data.APIClient;
import com.lnb.imemo.Data.Entity.ResponseRepo;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.Utils;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class AuthRepository {
    private static final String TAG = "AuthRepository";
    private Retrofit retrofit;
    private AuthApi authAPI;
    private MediatorLiveData<ResponseRepo> authRepoLiveData;
    public AuthRepository() {
        retrofit = APIClient.getInstance();
        authAPI = retrofit.create(AuthApi.class);
        authRepoLiveData = new MediatorLiveData<>();
    }

    public void getTokenFromGoogleToken(String ggToken) {
        JsonObject body = new JsonObject();
        body.addProperty(Constant.ID_TOKEN, ggToken);
        LiveData<JsonObject> source = LiveDataReactiveStreams.fromPublisher(
                authAPI.getTokenFromGoogleToken(body)
                        .onErrorReturn(new Function<Throwable, JsonObject>() {
                            @Override
                            public JsonObject apply(@NonNull Throwable throwable) throws Exception {
                                Log.d(TAG, "apply: " + throwable.getMessage());
                                String message = throwable.getMessage();
                                JsonObject jsonObject = new JsonObject();
                                if (message.contains(Utils.HTTP_ERROR.HTTP_409.getValue())) {
                                    jsonObject.addProperty(Constant.STATUS_CODE, 409);
                                } else if (message.contains(Utils.HTTP_ERROR.HTTP_NO_INTERNET.getValue())) {
                                    jsonObject.addProperty(Constant.STATUS_CODE, -1);
                                } else {
                                    jsonObject.addProperty(Constant.STATUS_CODE, -2);
                                }
                                return jsonObject;
                            }
                        })
                        .subscribeOn(Schedulers.io())
        );

        authRepoLiveData.addSource(source, jsonObject -> {
            int statusCode = jsonObject.get(Constant.STATUS_CODE).getAsInt();
            ResponseRepo<Pair<Utils.State, String>> response = new ResponseRepo<>();
            if (statusCode == 0) {
                String token = jsonObject.getAsJsonObject(Constant.RESULT)
                        .get(Constant.TOKEN)
                        .toString();
                response.setData(new Pair<>(Utils.State.SUCCESS, token));
            } else if (statusCode == -1) {
                response.setData(new Pair<>(Utils.State.NO_INTERNET, ""));
            } else {
                response.setData(new Pair<>(Utils.State.FAILURE, ""));
            }
            response.setKey(Constant.LOGIN_GOOGLE_KEY);
            authRepoLiveData.setValue(response);
            authRepoLiveData.removeSource(source);
        });

    }
    public void register(String username, String email, String password) {
        JsonObject body = new JsonObject();
        body.addProperty(Constant.USERNAME, username);
        body.addProperty(Constant.EMAIL, email);
        body.addProperty(Constant.PASSWORD, password);

        LiveData<JsonObject> source = LiveDataReactiveStreams.fromPublisher(
                authAPI.register(body)
                        .onErrorReturn(new Function<Throwable, JsonObject>() {
                            @Override
                            public JsonObject apply(@NonNull Throwable throwable) throws Exception {
                                Log.d(TAG, "apply: " + throwable.getMessage());
                                String message = throwable.getMessage();
                                JsonObject jsonObject = new JsonObject();
                                if (message.contains(Utils.HTTP_ERROR.HTTP_409.getValue())) {
                                    jsonObject.addProperty(Constant.STATUS_CODE, 409);
                                } else if (message.contains(Utils.HTTP_ERROR.HTTP_NO_INTERNET.getValue())) {
                                    jsonObject.addProperty(Constant.STATUS_CODE, -1);
                                } else {
                                    jsonObject.addProperty(Constant.STATUS_CODE, -2);
                                }
                                return jsonObject;
                            }
                        })
                        .subscribeOn(Schedulers.io())
        );


        authRepoLiveData.addSource(source, jsonObject -> {
            int statusCode = jsonObject.get(Constant.STATUS_CODE).getAsInt();
            ResponseRepo<Utils.RegisterState> response = new ResponseRepo<>();
            if (statusCode == 0) {
                response.setData(Utils.RegisterState.SUCCESS);
            } else if (statusCode == 409) {
                response.setData(Utils.RegisterState.ALREADY_HAVE);
            } else if (statusCode == -1) {
                response.setData(Utils.RegisterState.NO_INTERNET);
            } else {
                response.setData(Utils.RegisterState.FAILURE);
            }
            response.setKey(Constant.REGISTER_KEY);
            authRepoLiveData.setValue(response);
            authRepoLiveData.removeSource(source);
        });
    }
    public void login(String usernameOrEmail, String password) {
        JsonObject body = new JsonObject();
        body.addProperty(Constant.USERNAME_OR_EMAIL, usernameOrEmail);
        body.addProperty(Constant.PASSWORD, password);

        LiveData<JsonObject> source = LiveDataReactiveStreams.fromPublisher(
                authAPI.login(body)
                        .onErrorReturn(new Function<Throwable, JsonObject>() {
                            @Override
                            public JsonObject apply(@NonNull Throwable throwable) throws Exception {
                                Log.d(TAG, "apply: " + throwable.getMessage());
                                String message = throwable.getMessage();
                                JsonObject jsonObject = new JsonObject();
                                if (message.contains(Utils.HTTP_ERROR.HTTP_409.getValue())) {
                                    jsonObject.addProperty(Constant.STATUS_CODE, 409);
                                } else if (message.contains(Utils.HTTP_ERROR.HTTP_NO_INTERNET.getValue())) {
                                    jsonObject.addProperty(Constant.STATUS_CODE, -1);
                                } else {
                                    jsonObject.addProperty(Constant.STATUS_CODE, -2);
                                }
                                return jsonObject;
                            }
                        })
                        .subscribeOn(Schedulers.io())
        );

        authRepoLiveData.addSource(source, jsonObject -> {
            int statusCode = jsonObject.get(Constant.STATUS_CODE).getAsInt();
            ResponseRepo<Pair<Utils.State, String>> response = new ResponseRepo<>();
            if (statusCode == 0) {
                String token = jsonObject.getAsJsonObject(Constant.RESULT)
                        .get(Constant.TOKEN)
                        .toString();
                response.setData(new Pair<>(Utils.State.SUCCESS, token));
            } else if (statusCode == -1) {
                response.setData(new Pair<>(Utils.State.NO_INTERNET, ""));
            } else {
                response.setData(new Pair<>(Utils.State.FAILURE, ""));
            }
            response.setKey(Constant.LOGIN_KEY);
            authRepoLiveData.setValue(response);
            authRepoLiveData.removeSource(source);
        });

    }
    public void forgotPassword(String email) {
        JsonObject body = new JsonObject();
        body.addProperty(Constant.EMAIL, email);

        LiveData<JsonObject> source = LiveDataReactiveStreams.fromPublisher(
                authAPI.forgotPassword(body)
                        .onErrorReturn(new Function<Throwable, JsonObject>() {
                            @Override
                            public JsonObject apply(@NonNull Throwable throwable) throws Exception {
                                Log.d(TAG, "apply: " + throwable.getMessage());
                                String message = throwable.getMessage();
                                JsonObject jsonObject = new JsonObject();
                                if (message.contains(Utils.HTTP_ERROR.HTTP_409.getValue())) {
                                    jsonObject.addProperty(Constant.STATUS_CODE, 409);
                                } else if (message.contains(Utils.HTTP_ERROR.HTTP_NO_INTERNET.getValue())) {
                                    jsonObject.addProperty(Constant.STATUS_CODE, -1);
                                } else {
                                    jsonObject.addProperty(Constant.STATUS_CODE, -2);
                                }
                                return jsonObject;
                            }
                        })
                        .subscribeOn(Schedulers.io())
        );

        authRepoLiveData.addSource(source, jsonObject -> {
            int statusCode = jsonObject.get(Constant.STATUS_CODE).getAsInt();
            ResponseRepo<Utils.State> response = new ResponseRepo<>();
            if (statusCode == 0) {
                response.setData(Utils.State.SUCCESS);
            } else if (statusCode == -1) {
                response.setData(Utils.State.NO_INTERNET);
            } else {
                response.setData(Utils.State.FAILURE);
            }
            response.setKey(Constant.FORGOT_PASSWORD_KEY);
            authRepoLiveData.setValue(response);
            authRepoLiveData.removeSource(source);
        });
    }
    public void resetPassword(String newPassword, String mToken) {
        JsonObject body = new JsonObject();
        body.addProperty(Constant.TOKEN, mToken);
        body.addProperty(Constant.NEW_PASSWORD, newPassword);
        LiveData<JsonObject> source = LiveDataReactiveStreams.fromPublisher(
                authAPI.getTokenFromGoogleToken(body)
                        .onErrorReturn(new Function<Throwable, JsonObject>() {
                            @Override
                            public JsonObject apply(@NonNull Throwable throwable) throws Exception {
                                Log.d(TAG, "apply: " + throwable.getMessage());
                                String message = throwable.getMessage();
                                JsonObject jsonObject = new JsonObject();
                                if (message.contains(Utils.HTTP_ERROR.HTTP_409.getValue())) {
                                    jsonObject.addProperty(Constant.STATUS_CODE, 409);
                                } else if (message.contains(Utils.HTTP_ERROR.HTTP_NO_INTERNET.getValue())) {
                                    jsonObject.addProperty(Constant.STATUS_CODE, -1);
                                } else {
                                    jsonObject.addProperty(Constant.STATUS_CODE, -2);
                                }
                                return jsonObject;
                            }
                        })
                        .subscribeOn(Schedulers.io())
        );

        authRepoLiveData.addSource(source, jsonObject -> {
            int statusCode = jsonObject.get(Constant.STATUS_CODE).getAsInt();
            ResponseRepo<Utils.State> response = new ResponseRepo<>();
            if (statusCode == 0) {
                response.setData(Utils.State.SUCCESS);
            } else if (statusCode == -1) {
                response.setData(Utils.State.NO_INTERNET);
            } else {
                response.setData(Utils.State.FAILURE);
            }
            response.setKey(Constant.RESET_PASSWORD);
            authRepoLiveData.setValue(response);
            authRepoLiveData.removeSource(source);
        });
    }

    public MediatorLiveData<ResponseRepo> observableAuthRepo() {
        return authRepoLiveData;
    }
}
