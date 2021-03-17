package com.lnb.imemo.Presentation.Home.TabFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lnb.imemo.Model.Resource;
import com.lnb.imemo.Presentation.Home.TabFragment.AudioRecyclerView.AudioRecyclerViewAdapter;
import com.lnb.imemo.R;

import java.util.ArrayList;

public class AudioFragment extends Fragment {
    private final ArrayList<Resource> listAudio;
    private AudioRecyclerViewAdapter adapter;

    public void clearMedia() {
        if (adapter != null) {
            adapter.clearMedia();
        }
    }

    public void destroyMedia() {
        if (adapter != null) {
            adapter.destroyMedia();
        }
    }

    public AudioFragment(ArrayList<Resource> listAudio) {
        this.listAudio = listAudio;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_audio, container, false);
        RecyclerView memoAudioRecyclerView = view.findViewById(R.id.audio_recyclerView);
        adapter = new AudioRecyclerViewAdapter(listAudio);
        memoAudioRecyclerView.setAdapter(adapter);
        memoAudioRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        return view;
    }
}