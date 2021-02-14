package com.lnb.imemo.Presentation.Home.TabFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lnb.imemo.Model.Link;
import com.lnb.imemo.Presentation.Home.TabFragment.LinkRecyclerView.LinkRecyclerViewAdapter;
import com.lnb.imemo.R;

import java.util.ArrayList;

public class LinkFragment extends Fragment {
    private static final String TAG = "LinkFragment";
    // ui
    private RecyclerView linkRecyclerView;

    // var
    private ArrayList<Link> listLinks;

    public LinkFragment(ArrayList<Link> listLinks) {
        this.listLinks = listLinks;
        Log.d(TAG, "LinkFragment: " + listLinks.size());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view =  inflater.inflate(R.layout.fragment_link, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        linkRecyclerView = view.findViewById(R.id.link_recyclerView);
        LinkRecyclerViewAdapter adapter = new LinkRecyclerViewAdapter(listLinks);
        linkRecyclerView.setAdapter(adapter);
        linkRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }
}