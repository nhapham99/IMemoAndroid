package com.lnb.imemo.Presentation.Login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.lnb.imemo.Presentation.Register.RegisterActivity;
import com.lnb.imemo.R;
import com.lnb.imemo.Utils.Utils;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    // ui
    private GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 0;
    private TextInputLayout email_TextIL;
    private TextInputLayout password_TextIL;
    private Button signIn_button;
    private Button signInWithGoogle_button;
    private TextView forgot_password_button;
    private TextView register_button;



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
        email_TextIL = findViewById(R.id.textfield_email);
        email_TextIL.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                email_TextIL.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password_TextIL = findViewById(R.id.textfield_password);
        password_TextIL.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                password_TextIL.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        signIn_button = findViewById(R.id.sign_in_button);
        signInWithGoogle_button = findViewById(R.id.sign_in_with_google_button);
        signIn_button.setOnClickListener(this);
        signInWithGoogle_button.setOnClickListener(this);
        forgot_password_button = findViewById(R.id.forgot_password_button);
        forgot_password_button.setOnClickListener(this);
        register_button = findViewById(R.id.register_textview);
        register_button.setOnClickListener(this);

        // register viewmodel
        viewModel = new LoginViewModel();

        // register observable
        subscribeLoginObservable();
        subscribeForgotPasswordObservable();

        // init for sign in with google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Utils.GOOGLE_CLIENT_ID)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void subscribeLoginObservable() {
        viewModel.observableLogin().observe(this, new Observer<Utils.State>() {
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
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.register_textview:
                Intent intent = new Intent(this, RegisterActivity.class);
                this.startActivity(intent);
                break;
            case R.id.forgot_password_button:
                break;

        }
    }

    private void signIn() {
        String email = email_TextIL.getEditText().getText().toString();
        String password = password_TextIL.getEditText().getText().toString();
        Boolean allowSignIn = true;
        if (email.equals("")) {
            email_TextIL.setError(getResources().getText(R.string.input_email_empty_error));
            email_TextIL.setErrorEnabled(true);
            allowSignIn = false;
        }
        if (password.equals("")) {
            password_TextIL.setError(getResources().getText(R.string.input_password_empty_error));
            password_TextIL.setErrorEnabled(true);
            allowSignIn = false;
        }
        if (allowSignIn) {
            viewModel.signIn(email, password);
        }

        Log.d(TAG, "signIn: " + email + "password" + password);
    }

    private void signInWithGoogle() {
        // sign in with google
        mGoogleSignInClient.signOut();
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
            viewModel.signInWithGoogle(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    // for watch text field
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}