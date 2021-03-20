package com.lnb.imemo.Presentation.Person;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.lnb.imemo.Model.User;
import com.lnb.imemo.Presentation.Login.LoginActivity;
import com.lnb.imemo.Presentation.PersonalSetting.PersonalSettingActivity;
import com.lnb.imemo.Presentation.TagsSetting.TagsSettingActivity;
import com.lnb.imemo.R;
import com.lnb.imemo.Utils.AESCrypt;
import com.lnb.imemo.Utils.Utils;

import io.reactivex.subjects.PublishSubject;

import static android.content.Context.MODE_PRIVATE;

public class PersonFragment extends Fragment implements View.OnClickListener {

    private static PersonFragment mPersonFragment;
    private GoogleSignInClient mGoogleSignInClient;
    private static PublishSubject<Pair<String, Object>> centerObserver;
    private LinearLayout personSetting;
    private LinearLayout tagsSetting;
    private LinearLayout signOut;
    private User mUser;

    public PersonFragment() {
    }

    public static PersonFragment getPersonFragment(PublishSubject<Pair<String, Object>> centerObservable) {
        if (mPersonFragment == null) {
            mPersonFragment = new PersonFragment();
            centerObserver = centerObservable;
        }
        return mPersonFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        personSetting = view.findViewById(R.id.personal_setting);
        tagsSetting = view.findViewById(R.id.tags_setting);
        signOut = view.findViewById(R.id.sign_out);
        personSetting.setOnClickListener(this);
        tagsSetting.setOnClickListener(this);
        signOut.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Utils.GOOGLE_CLIENT_ID)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        mUser = User.getUser();
    }

    public static PublishSubject<Pair<String, Object>> getCenterObserver() {
        return centerObserver;
    }

    private void signOut() {
        mGoogleSignInClient.signOut();
        mUser.clear();
        savePrefData();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }



    private void savePrefData() {
        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        try {
            editor.putString("token", AESCrypt.encrypt(""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        editor.apply();
    }

    private void goToPersonSetting() {
        Intent intent = new Intent(getActivity(), PersonalSettingActivity.class);
        startActivity(intent);
    }

    private void goToTagsSetting() {
        Intent intent = new Intent(getActivity(), TagsSettingActivity.class);
        startActivity(intent);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.personal_setting:
                goToPersonSetting();
                break;
            case R.id.tags_setting:
                goToTagsSetting();
                break;
            case R.id.sign_out:
                signOut();
                break;
        }
    }

}