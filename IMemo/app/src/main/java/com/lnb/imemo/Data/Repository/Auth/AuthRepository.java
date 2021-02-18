package com.lnb.imemo.Data.Repository.Auth;

import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lnb.imemo.Data.APIClient;
import com.lnb.imemo.Model.Diary;
import com.lnb.imemo.Model.PersonProfile;
import com.lnb.imemo.Model.ResponseRepo;
import com.lnb.imemo.Model.ResultDiaries;
import com.lnb.imemo.Model.Root;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.Utils;

import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class AuthRepository {
    private static final String TAG = "AuthRepository";
    private Retrofit retrofit;
    private AuthApi authAPI;
    private MediatorLiveData<ResponseRepo> authRepoLiveData;
    private Gson gsonBuilder;


    public AuthRepository() {
        retrofit = APIClient.getInstance();
        authAPI = retrofit.create(AuthApi.class);
        authRepoLiveData = new MediatorLiveData<>();
        gsonBuilder = new GsonBuilder().create();
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
                Log.d(TAG, "getTokenFromGoogleToken: " + token);
                String tokenSub = "Bearer " + token.substring(1, token.length() - 1);
                Log.d(TAG, "getTokenFromGoogleToken: " + tokenSub);
                response.setData(new Pair<>(Utils.State.SUCCESS, tokenSub));
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
                Log.d(TAG, "login: " + token);
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

    public void getUserProfile(String token) {
        LiveData<Root<PersonProfile>> source = LiveDataReactiveStreams.fromPublisher(
                authAPI.getUserProfile(token)
                        .onErrorReturn(new Function<Throwable, Root<PersonProfile>>() {
                            @Override
                            public Root<PersonProfile> apply(@NonNull Throwable throwable) throws Exception {
                                Log.d(TAG, "apply: " + throwable.getMessage());
                                String message = throwable.getMessage();
                                Root<PersonProfile> personProfile = new Root<>();
                                if (message.contains(Utils.HTTP_ERROR.HTTP_409.getValue())) {
                                    personProfile.setStatusCode(409);
                                } else if (message.contains(Utils.HTTP_ERROR.HTTP_NO_INTERNET.getValue())) {
                                    personProfile.setStatusCode(-1);
                                } else {
                                    personProfile.setStatusCode(-2);
                                }
                                return personProfile;
                            }
                        })
                        .subscribeOn(Schedulers.io())
        );

        authRepoLiveData.addSource(source, new Observer<Root<PersonProfile>>() {
            @Override
            public void onChanged(Root<PersonProfile> personProfileRoot) {
                ResponseRepo<Pair<Utils.State, PersonProfile>> response = new ResponseRepo<>();
                if (personProfileRoot.getStatusCode() == 0) {
                    PersonProfile personProfile = personProfileRoot.getResult();
                    response.setData(new Pair<>(Utils.State.SUCCESS, personProfile));
                } else if (personProfileRoot.getStatusCode() == -1) {
                    response.setData(new Pair<>(Utils.State.NO_INTERNET, null));
                } else {
                    response.setData(new Pair<>(Utils.State.FAILURE, null));
                }
                response.setKey(Constant.GET_PERSON_PROFILE);
                authRepoLiveData.setValue(response);
                authRepoLiveData.removeSource(source);
            }
        });
    }

    public void updatePersonProfile(String token, PersonProfile personProfile) {
        JsonObject body = new JsonObject();
        if (!personProfile.getName().equals("")) {
            body.addProperty("name", personProfile.getName());
        }
        if (personProfile.getUsername() != null) {
            if (!personProfile.getUsername().equals("")) {
                body.addProperty("username", personProfile.getUsername());
            }
        }

        if (personProfile.getGender() != null) {
            if (!personProfile.getGender().equals("")) {
                body.addProperty("gender", personProfile.getGender());
            }
        }
        if (!personProfile.getPicture().equals("")) {
            body.addProperty("picture", personProfile.getPicture());
        }
        if (!personProfile.getBirthday().equals("")) {
            body.addProperty("birthday", personProfile.getBirthday());
        }

        Log.d(TAG, "updatePersonProfile: " + body.toString());
        LiveData<Root<JsonObject>> source = LiveDataReactiveStreams
                .fromPublisher(authAPI.updateUserProfile(token, body)
                        .onErrorReturn(new Function<Throwable, Root<JsonObject>>() {
                            @Override
                            public Root<JsonObject> apply(@NonNull Throwable throwable) throws Exception {
                                throwable.printStackTrace();
                                String message = throwable.getMessage();
                                Root<JsonObject> updatePersonProfileRoot = new Root<>();
                                if (message.contains(Utils.HTTP_ERROR.HTTP_409.getValue())) {
                                    updatePersonProfileRoot.setStatusCode(409);
                                } else if (message.contains(Utils.HTTP_ERROR.HTTP_NO_INTERNET.getValue())) {
                                    updatePersonProfileRoot.setStatusCode(-1);
                                } else {
                                    updatePersonProfileRoot.setStatusCode(-2);
                                }
                                return updatePersonProfileRoot;
                            }
                        })
                        .subscribeOn(Schedulers.io()));
        authRepoLiveData.addSource(source, new Observer<Root<JsonObject>>() {
            @Override
            public void onChanged(Root<JsonObject> root) {
                ResponseRepo<Pair<Utils.State, JsonObject>> response = new ResponseRepo<>();
                if (root.getStatusCode() == 0) {
                    response.setData(new Pair<>(Utils.State.SUCCESS, root.getResult()));
                } else if (root.getStatusCode() == -1) {
                    response.setData(new Pair<>(Utils.State.NO_INTERNET, null));
                } else {
                    response.setData(new Pair<>(Utils.State.FAILURE, null));
                }
                response.setKey(Constant.UPDATE_PERSON_PROFILE);
                authRepoLiveData.setValue(response);
            }
        });
    }

    public MediatorLiveData<ResponseRepo> observableAuthRepo() {
        return authRepoLiveData;
    }
}
