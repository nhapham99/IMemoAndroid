package com.lnb.imemo.Presentation.PersonalSetting;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import com.lnb.imemo.Model.PersonProfile;
import com.lnb.imemo.Model.ResponseRepo;
import com.lnb.imemo.R;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonalSettingActivity extends AppCompatActivity implements View.OnClickListener {
    // ui
    private static final String TAG = "PersonalSettingActivity";
    private CircleImageView user_avatar;
    private TextView change_avatar_button;
    private TextInputLayout user_email;
    private TextInputLayout user_account_name;
    private TextInputLayout user_username;
    private RadioGroup genderRadioGroup;
    private TextView user_dateOfBirth;
    private Button user_saveProfileButton;
    private RadioButton maleRadioButton;
    private RadioButton femaleRadioButton;
    private RadioButton unknownRadioButton;
    private ImageButton backButton;
    private SwipeRefreshLayout swipeRefreshLayout;

    // var
    private PersonalSettingViewModel viewModel;
    private Calendar dateOfBirth = Calendar.getInstance();
    private int GET_FILE_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_setting);
        init();
    }

    private void init() {
        user_avatar = findViewById(R.id.user_avatar);
        change_avatar_button = findViewById(R.id.change_avatar_button);
        user_email = findViewById(R.id.email_textfield);
        user_account_name = findViewById(R.id.account_name_textfield);
        user_username = findViewById(R.id.username_textfield);
        genderRadioGroup = findViewById(R.id.gender_radioGroup);
        user_dateOfBirth = findViewById(R.id.date_of_birth_textInputLayout);
        user_saveProfileButton = findViewById(R.id.save_profile_button);
        maleRadioButton = findViewById(R.id.gender_male_radioButton);
        femaleRadioButton = findViewById(R.id.gender_female_radioButton);
        unknownRadioButton = findViewById(R.id.gender_unknown_Radiobutton);
        backButton = findViewById(R.id.back_button);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.getPersonProfile();
            }
        });

        change_avatar_button.setOnClickListener(this);
        user_dateOfBirth.setOnClickListener(this);
        user_saveProfileButton.setOnClickListener(this);
        backButton.setOnClickListener(this);

        viewModel = new PersonalSettingViewModel(this);
        personalSettingObservable();
        subscribeUploadObservable();
        updateUI();
    }

    private void personalSettingObservable() {
        viewModel.getPersonalSettingObservable().observe(this, new Observer<ResponseRepo>() {
            @Override
            public void onChanged(ResponseRepo responseRepo) {
                String key = responseRepo.getKey();
                if (key == Constant.GET_PERSON_PROFILE) {
                    Utils.State state = (Utils.State) responseRepo.getData();
                    switch (state) {
                        case SUCCESS:
                            swipeRefreshLayout.setRefreshing(false);
                            updateUI();
                            break;
                        case FAILURE:
                            Toast.makeText(PersonalSettingActivity.this, "Lấy dữ liệu lỗi", Toast.LENGTH_SHORT).show();
                            break;
                        case NO_INTERNET:
                            Toast.makeText(PersonalSettingActivity.this, "Lỗi kết nối internet. Xin vùi lòng kiểm tra lại.", Toast.LENGTH_SHORT).show();
                    }
                } else if (key == Constant.UPDATE_PERSON_PROFILE) {
                    Utils.State state = (Utils.State) responseRepo.getData();
                    switch (state) {
                        case SUCCESS:
                            Toast.makeText(PersonalSettingActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                            break;
                        case FAILURE:
                            Toast.makeText(PersonalSettingActivity.this, "Cập nhật dữ liệu lỗi", Toast.LENGTH_SHORT).show();
                            break;
                        case NO_INTERNET:
                            Toast.makeText(PersonalSettingActivity.this, "Lỗi kết nối internet. Xin vùi lòng kiểm tra lại.", Toast.LENGTH_SHORT).show();
                    }
                } else if (key == Constant.UPDATE_IMAGE_PERSON_PROFILE) {
                    Utils.State state = (Utils.State) responseRepo.getData();
                    switch (state) {
                        case SUCCESS:
                            Toast.makeText(PersonalSettingActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                            Glide.with(PersonalSettingActivity.this).load(viewModel.personProfile.getPicture()).into(user_avatar);
                            break;
                        case FAILURE:
                            Toast.makeText(PersonalSettingActivity.this, "Cập nhật dữ liệu lỗi", Toast.LENGTH_SHORT).show();
                            break;
                        case NO_INTERNET:
                            Toast.makeText(PersonalSettingActivity.this, "Lỗi kết nối internet. Xin vùi lòng kiểm tra lại.", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

    private void subscribeUploadObservable() {
        viewModel.getUploadLiveData().observe(this, new Observer<ResponseRepo<Pair<Utils.State, String>>>() {
            @Override
            public void onChanged(ResponseRepo<Pair<Utils.State, String>> responseRepo) {
                if (responseRepo.getKey() == Constant.UPLOAD_FILE_KEY) {
                    Pair<Utils.State, String> response = responseRepo.getData();
                    switch (response.first) {
                        case SUCCESS:
                            Log.d(TAG, "onChanged: " + response.second);
                            Glide.with(PersonalSettingActivity.this).load(response.second).into(user_avatar);
                            break;
                        case FAILURE:
                            Toast.makeText(PersonalSettingActivity.this, "Lỗi xảy ra. Vui lòng thử lại", Toast.LENGTH_SHORT).show();
                            break;
                        case NO_INTERNET:
                            Toast.makeText(PersonalSettingActivity.this, "Vui lòng kiểm tra lại kết nối internet", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        });
    }
    
    private void updateUI() {
        PersonProfile personProfile = viewModel.personProfile;
        Log.d(TAG, "updateUI: " + personProfile.toString());
        if (personProfile.getPicture() != null) {
            Glide.with(this).load(viewModel.personProfile.getPicture()).into(user_avatar);
        }

        user_email.getEditText().setText(personProfile.getEmail());

        user_account_name.getEditText().setText(personProfile.getUsername());

        user_username.getEditText().setText(personProfile.getName());

        if (personProfile.getGender() != null) {
            if (personProfile.getGender().equals("MALE")) {
                maleRadioButton.setChecked(true);
            } else if (personProfile.getGender().equals("FEMALE")) {
                femaleRadioButton.setChecked(true);
            } else if (personProfile.getGender().equals("UNKNOWN")) {
                unknownRadioButton.setChecked(true);
            }
        }

        genderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.gender_male_radioButton:
                        viewModel.newPersonProfile.setGender("MALE");
                        break;
                    case R.id.gender_female_radioButton:
                        viewModel.newPersonProfile.setGender("FEMALE");
                        break;
                    case R.id.gender_unknown_Radiobutton:
                        viewModel.newPersonProfile.setGender("UNKNOWN");
                        break;
                }
            }
        });
        Date dateTime = null;
        if (personProfile.getBirthday() != null) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                dateTime = df.parse(personProfile.getBirthday().split("\\.")[0]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            user_dateOfBirth.setText(simpleDateFormat.format(dateTime.getTime()));
        }
    }

    private void pickDate() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dateOfBirth.set(Calendar.YEAR, year);
                dateOfBirth.set(Calendar.MONTH, month);
                dateOfBirth.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                dateOfBirth.set(Calendar.HOUR_OF_DAY, 00);
                dateOfBirth.set(Calendar.MINUTE, 00);
                dateOfBirth.set(Calendar.SECOND, 00);
                SimpleDateFormat newSimpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                user_dateOfBirth.setText(newSimpleDateFormat.format(dateOfBirth.getTime()));
            }
        };
        new DatePickerDialog(PersonalSettingActivity.this, dateSetListener, dateOfBirth.get(Calendar.YEAR), dateOfBirth.get(Calendar.MONTH), dateOfBirth.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void changeUserAvatar() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GET_FILE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_FILE_CODE) {
            if (resultCode == RESULT_OK && data.getData() != null) {
                viewModel.uploadFile(data.getData());
            } else {
                Log.d(TAG, "onActivityResult: failure");
            }
        }
    }

    private void updatePersonProfile() {
        viewModel.newPersonProfile.setEmail(user_email.getEditText().getText().toString());
        viewModel.newPersonProfile.setName(user_username.getEditText().getText().toString());
        viewModel.newPersonProfile.setUsername(user_account_name.getEditText().getText().toString());
        switch (genderRadioGroup.getCheckedRadioButtonId()) {
            case R.id.gender_male_radioButton:
                viewModel.newPersonProfile.setGender("MALE");
                break;
            case R.id.gender_female_radioButton:
                viewModel.newPersonProfile.setGender("FEMALE");
                break;
            case R.id.gender_unknown_Radiobutton:
                viewModel.newPersonProfile.setGender("UNKNOWN");
                break;
        }
        TimeZone tz = TimeZone.getTimeZone("UTC");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.'sss'Z'");
        simpleDateFormat.setTimeZone(tz);
        String dateOfBirthStr = simpleDateFormat.format(dateOfBirth.getTime());
        Log.d(TAG, "onDateSet: " + dateOfBirthStr);
        viewModel.newPersonProfile.setBirthday(dateOfBirthStr);
        viewModel.updatePersonProfile();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_button:
                finish();
                break;
            case R.id.change_avatar_button:
                changeUserAvatar();
                break;
            case R.id.date_of_birth_textInputLayout:
                pickDate();
                break;
            case R.id.save_profile_button:
                updatePersonProfile();
                break;
        }
    }
}