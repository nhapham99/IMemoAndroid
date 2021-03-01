package com.lnb.imemo.Presentation.Register;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputLayout;
import com.lnb.imemo.Presentation.NavigationActivity.NavigationActivity;
import com.lnb.imemo.R;
import com.lnb.imemo.Utils.Utils;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "RegisterActivity";
    //ui
    private GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 0;
    private TextInputLayout email_text;
    private TextInputLayout username_text;
    private TextInputLayout password_text;
    private MaterialCheckBox checkBox;
    private Button register_button;
    private Button register_with_google_button;
    private TextView login_text;
    private ImageView errorIcon;
    private BlurView blurView;

    //var
    private RegisterViewModel viewModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_register);
        init();
    }

    private void init() {
        // register ui
        email_text = findViewById(R.id.register_email_textfield);
        email_text.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                email_text.getEditText().addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        email_text.setErrorEnabled(false);
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        username_text = findViewById(R.id.register_username_textfield);
        username_text.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                username_text.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password_text = findViewById(R.id.register_password_textfield);
        password_text.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                password_text.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 8) {
                    password_text.setError(getResources().getText(R.string.register_password_error));
                    password_text.setErrorEnabled(true);
                } else {
                    password_text.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        checkBox = findViewById(R.id.checkbox);
        checkBox.setOnClickListener(this);
        register_button = findViewById(R.id.register_button);
        register_button.setOnClickListener(this);
        register_with_google_button = findViewById(R.id.register_with_google_button);
        register_with_google_button.setOnClickListener(this);
        login_text = findViewById(R.id.login_textview);
        login_text.setOnClickListener(this);
        errorIcon = findViewById(R.id.error_icon);
        errorIcon.setVisibility(View.INVISIBLE);
        blurView = findViewById(R.id.blurView);
        blurView.setVisibility(View.INVISIBLE);


        // register viewmodel
        viewModel = new RegisterViewModel();

        // init for sign in with google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Utils.GOOGLE_CLIENT_ID)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // subscribe observable
        subscribeLoginObservable();
        subscribeRegisterObservable();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_button:
                register();
                break;
            case R.id.login_textview:
                finish();
                break;
            case R.id.register_with_google_button:
                registerWithGoogle();
                break;
            case R.id.checkbox:
                if (checkBox.isChecked()) {
                    errorIcon.setVisibility(View.INVISIBLE);
                }
                break;
        }
    }


    private void register() {
        String email = email_text.getEditText().getText().toString();
        String username = username_text.getEditText().getText().toString();
        String password = password_text.getEditText().getText().toString();
        Boolean allowRegister = true;

        if (email.equals("")) {
            email_text.setError(getResources().getString(R.string.input_email_empty_error));
            email_text.setErrorEnabled(true);
            allowRegister = false;
        }

        if (username.equals("")) {
            username_text.setError(getResources().getString(R.string.input_username_empty_error));
            username_text.setErrorEnabled(true);
            allowRegister = false;
        }

        if (password.equals("")) {
            password_text.setError(getResources().getString(R.string.input_password_empty_error));
            username_text.setErrorEnabled(true);
            allowRegister = false;
        }

        if (!checkBox.isChecked()) {
            errorIcon.setVisibility(View.VISIBLE);
            allowRegister = false;
        }

        if (allowRegister) {
            blurScreen();
            viewModel.register(username, email, password);
        }
    }

    private void subscribeLoginObservable() {
        viewModel.observableLogin().observe(this, new Observer<Utils.State>() {
            @Override
            public void onChanged(Utils.State state) {
                switch (state) {
                    case SUCCESS:
                        Log.d(TAG, "onChanged login: success");
                        clearScreen();
                        Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        break;
                    case FAILURE:
                        Log.d(TAG, "onChanged login: failure");
                        Toast.makeText(RegisterActivity.this,
                                getResources().getText(R.string.login_failure_with_google),
                                Toast.LENGTH_SHORT).show();
                        break;
                    case NO_INTERNET:
                        Log.d(TAG, "onChanged login: no_internet");
                        Toast.makeText(RegisterActivity.this,
                                getResources().getText(R.string.no_internet_failure),
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private void subscribeRegisterObservable() {
        viewModel.observableRegister().observe(this, new Observer<Utils.RegisterState>() {
            @Override
            public void onChanged(Utils.RegisterState state) {
                switch (state) {
                    case SUCCESS:
                        clearScreen();
                        Log.d(TAG, "register: success");
                        Toast.makeText(RegisterActivity.this,
                                getResources().getText(R.string.register_success),
                                Toast.LENGTH_SHORT).show();
                        break;
                    case FAILURE:
                        clearScreen();
                        Log.d(TAG, "register failure");
                        Toast.makeText(RegisterActivity.this,
                                getResources().getText(R.string.register_failure),
                                Toast.LENGTH_SHORT).show();
                        break;
                    case ALREADY_HAVE:
                        clearScreen();
                        Log.d(TAG, "register already have");
                        Toast.makeText(RegisterActivity.this,
                                getResources().getText(R.string.register_already_have),
                                Toast.LENGTH_SHORT).show();
                        break;
                    case NO_INTERNET:
                        clearScreen();
                        Log.d(TAG, "register no internet");
                        Toast.makeText(RegisterActivity.this,
                                getResources().getText(R.string.no_internet_failure),
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }


    private void registerWithGoogle() {
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
            if (e.getStatusCode() == 7) {
                Toast.makeText(RegisterActivity.this,
                        getResources().getText(R.string.no_internet_failure),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}