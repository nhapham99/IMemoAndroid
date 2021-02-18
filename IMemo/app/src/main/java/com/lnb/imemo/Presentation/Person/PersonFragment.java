package com.lnb.imemo.Presentation.Person;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.lnb.imemo.Presentation.Login.LoginActivity;
import com.lnb.imemo.Presentation.PersonalSetting.PersonalSettingActivity;
import com.lnb.imemo.Presentation.TagsSetting.TagsSettingActivity;
import com.lnb.imemo.R;
import com.lnb.imemo.Utils.Utils;

public class PersonFragment extends Fragment implements View.OnClickListener {

    private static PersonFragment mPersonFragment;
    private GoogleSignInClient mGoogleSignInClient;
    private PersonFragment() {}

    private LinearLayout personSetting;
    private LinearLayout tagsSetting;
    private LinearLayout signOut;


    public static PersonFragment getPersonFragment() {
        if (mPersonFragment == null) {
            mPersonFragment = new PersonFragment();
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
    }

    private void signOut() {
        mGoogleSignInClient.signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    private void goToPersonSetting() {
        Intent intent = new Intent(getActivity(), PersonalSettingActivity.class);
        startActivity(intent);
    }

    private void goToTagsSetting() {
        Intent intent = new Intent(getActivity(), TagsSettingActivity.class);
        startActivity(intent);
    }

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