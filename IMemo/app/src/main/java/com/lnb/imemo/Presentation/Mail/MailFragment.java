package com.lnb.imemo.Presentation.Mail;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lnb.imemo.R;

public class MailFragment extends Fragment {

    private static MailFragment mMailFragment;

    private MailFragment() {}

    public static MailFragment getMailFragment() {
        if (mMailFragment == null) {
            mMailFragment = new MailFragment();
        }
        return mMailFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mail, container, false);
    }
}