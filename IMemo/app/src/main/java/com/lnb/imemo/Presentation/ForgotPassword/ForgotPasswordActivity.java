package com.lnb.imemo.Presentation.ForgotPassword;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.lnb.imemo.Model.ResponseRepo;
import com.lnb.imemo.R;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.Utils;

import eightbitlab.com.blurview.BlurView;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ForgotPasswordActivity";
    private TextInputLayout emailTextField;
    private Button getPasswordButton;
    private TextView backLoginButton;
    private BlurView blurView;

    private ForgotPasswordViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_forgot_password);
        init();
    }

    private void init() {
        emailTextField = findViewById(R.id.textfield_email);
        getPasswordButton = findViewById(R.id.get_password_button);
        backLoginButton = findViewById(R.id.back_login_screen);
        blurView = findViewById(R.id.blurView);
        blurView.setVisibility(View.INVISIBLE);

        getPasswordButton.setOnClickListener(this);
        backLoginButton.setOnClickListener(this);

        emailTextField.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (emailTextField.isErrorEnabled()) {
                        emailTextField.setErrorEnabled(false);
                    }
                }
            }
        });

        viewModel = new ForgotPasswordViewModel();
        subscribeViewModelObserver();
    }

    private void subscribeViewModelObserver() {
        viewModel.getViewModelObserver().observe(this, new Observer<ResponseRepo>() {
            @Override
            public void onChanged(ResponseRepo responseRepo) {
                String key = responseRepo.getKey();
                if (key.equals(Constant.FORGOT_PASSWORD_KEY)) {
                    Utils.State state = (Utils.State) responseRepo.getData();
                    switch (state) {
                        case SUCCESS:
                            blurView.setVisibility(View.INVISIBLE);
                            Toast.makeText(ForgotPasswordActivity.this, "Thành công. Vui lòng kiểm tra email", Toast.LENGTH_SHORT).show();
                            break;
                        case FAILURE:
                            blurView.setVisibility(View.INVISIBLE);
                            Toast.makeText(ForgotPasswordActivity.this, "Không thành công. Vui lòng thử lại", Toast.LENGTH_SHORT).show();
                            break;
                        case NO_INTERNET:
                            blurView.setVisibility(View.INVISIBLE);
                            Toast.makeText(ForgotPasswordActivity.this, "Vui lòng kiểm tra kết nối internet", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        });
    }

    private void forgotPassword() {
        if (!emailTextField.getEditText().getText().toString().equals("")) {
            viewModel.forgotPassword(emailTextField.getEditText().getText().toString());
            blurView.setVisibility(View.VISIBLE);
        } else {
            emailTextField.setError("Vui lòng điền email");
            emailTextField.setEnabled(true);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get_password_button:
                forgotPassword();
                break;
            case R.id.back_login_screen:
                finish();
                break;
        }
    }
}