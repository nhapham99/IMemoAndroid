package com.lnb.imemo.Presentation.Login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.lnb.imemo.Presentation.ForgotPassword.ForgotPasswordActivity;
import com.lnb.imemo.Presentation.NavigationActivity.NavigationActivity;
import com.lnb.imemo.Presentation.Register.RegisterActivity;
import com.lnb.imemo.R;
import com.lnb.imemo.Utils.Utils;

import java.net.URISyntaxException;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

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
    private BlurView blurView;

    // var
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
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
        blurView = findViewById(R.id.blurView);
        blurView.setVisibility(View.INVISIBLE);

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
        //blurScreen();
    }

    private void blurScreen() {
        blurView.setVisibility(View.VISIBLE);
        float radius = 0.5f;

        View decorView = getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();

        blurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(this))
                .setBlurRadius(radius)
                .setBlurAutoUpdate(true);
    }

    private void clearScreen() {
        blurView.setVisibility(View.INVISIBLE);
    }

    private void subscribeLoginObservable() {
        viewModel.observableLogin().observe(this, new Observer<Utils.State>() {
            @Override
            public void onChanged(Utils.State state) {
                switch (state) {
                    case SUCCESS:
                        Log.d(TAG, "onChanged login: success");
                        Intent intent  = new Intent(LoginActivity.this, NavigationActivity.class);
                        startActivity(intent);
                        finish();
                        clearScreen();
                        break;
                    case FAILURE:
                        Log.d(TAG, "onChanged login: failure");
                        Toast.makeText(LoginActivity.this, "Tài khoản hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                        clearScreen();
                        break;
                    case NO_INTERNET:
                        Log.d(TAG, "onChanged login: no_internet");
                        Toast.makeText(LoginActivity.this, "Không có kết nối mạng. Xin vui lòng thử lại", Toast.LENGTH_SHORT).show();
                        clearScreen();
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

    private void goToForgotPasswordScreen() {
        startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
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
                goToForgotPasswordScreen();
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
            blurScreen();
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
            blurScreen();
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