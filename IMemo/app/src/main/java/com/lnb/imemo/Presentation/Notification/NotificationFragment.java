package com.lnb.imemo.Presentation.Notification;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lnb.imemo.R;

public class NotificationFragment extends Fragment {
    private static NotificationFragment mNotificationFragment;

    private NotificationFragment() { }

    public static NotificationFragment getNotificationFragment() {
        if (mNotificationFragment == null) {
            mNotificationFragment = new NotificationFragment();
        }
        return mNotificationFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }
}