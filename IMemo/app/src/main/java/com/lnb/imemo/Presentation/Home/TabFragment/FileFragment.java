package com.lnb.imemo.Presentation.Home.TabFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lnb.imemo.Model.Resource;
import com.lnb.imemo.Presentation.Home.TabFragment.FileRecyclerViewAdapter.FileRecyclerViewAdapter;
import com.lnb.imemo.R;

import java.util.ArrayList;

public class FileFragment extends Fragment {
    private final ArrayList<Resource> listFile;
    public FileFragment(ArrayList<Resource> listFile) {
        this.listFile = listFile;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_file, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        RecyclerView fileRecyclerView = view.findViewById(R.id.file_recyclerView);
        FileRecyclerViewAdapter fileAdapter = new FileRecyclerViewAdapter(listFile);
        fileRecyclerView.setAdapter(fileAdapter);
        fileRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }
}