package com.lnb.imemo.Presentation.Login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.lnb.imemo.R;
import com.lnb.imemo.Utils.Utils;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    // ui
    private SignInButton signInWithGGButton;
    private GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 0;

    // var
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init() {
        // register ui
        signInWithGGButton = findViewById(R.id.sign_in_with_google_button);
        signInWithGGButton.setOnClickListener(this);

        // register viewmodel
        viewModel = new LoginViewModel();

        // register observable
        subscribeLoginObservable();
        subscribeRegisterObservable();
        subscribeForgotPasswordObservable();

        // init for sign in with google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Utils.GOOGLE_CLIENT_ID)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void subscribeLoginObservable() {
        viewModel.observableGoogleLogin().observe(this, new Observer<Utils.State>() {
            @Override
            public void onChanged(Utils.State state) {
                switch (state) {
                    case SUCCESS:
                        Log.d(TAG, "onChanged login: success");
                        break;
                    case FAILURE:
                        Log.d(TAG, "onChanged login: failure");
                        break;
                    case NO_INTERNET:
                        Log.d(TAG, "onChanged login: no_internet");
                        break;
                }
            }
        });
    }

    private void subscribeRegisterObservable() {
        viewModel.observableRegister().observe(this, new Observer<Utils.RegisterState>() {
            @Override
            public void onChanged(Utils.RegisterState registerState) {
                switch (registerState) {
                    case SUCCESS:
                        Log.d(TAG, "onChanged register: success");
                        break;
                    case NO_INTERNET:
                        Log.d(TAG, "onChanged register: no internet");
                        break;
                    case FAILURE:
                        Log.d(TAG, "onChanged register: failure");
                        break;
                    case ALREADY_HAVE:
                        Log.d(TAG, "onChanged register: already have");
                        break;
                }
            }
        });
    }

    private void subscribeForgotPasswordObservable() {
        viewModel.observableForgotPasswordLiveData().observe(this, new Observer<Utils.State>() {
            @Override
            public void onChanged(Utils.State state) {
                switch (state) {
                    case SUCCESS:
                        Log.d(TAG, "onChanged: resend pass success" );
                        break;
                    case FAILURE:
                        Log.d(TAG, "onChanged: resend failure");
                        break;
                    case NO_INTERNET:
                        Log.d(TAG, "onChanged: no internet");
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_with_google_button:
                signInWithGoogle();
                break;
        }
    }

    private void signInWithGoogle() {
        // sign in with google
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            // if login success
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            viewModel.loginWithGoogle(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}