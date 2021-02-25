package com.lnb.imemo.Presentation.Home.TabFragment.FileRecyclerViewAdapter;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lnb.imemo.Model.Resource;
import com.lnb.imemo.R;
import com.lnb.imemo.Utils.Utils;

import java.util.ArrayList;

public class FileRecyclerViewAdapter extends RecyclerView.Adapter<FileRecyclerViewAdapter.FileRecyclerViewHolder> {
    private static final String TAG = "FileRecyclerViewAdapter";
    private ArrayList<Resource> listFile = new ArrayList<>();
    private Context mContext;

    public FileRecyclerViewAdapter(ArrayList<Resource> listFile) {
        this.listFile = listFile;
    }

    @NonNull
    @Override
    public FileRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_recycler_view_item, parent, false);
        return new FileRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileRecyclerViewHolder holder, int position) {
        holder.fileName.setText(listFile.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDownloadFile(position);
            }
        });
    }

    private void startDownloadFile(int position) {
        String url = listFile.get(position).getUrl();
        if (!url.contains("https")) {
            url = Utils.storeUrl + url;
        }
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle(listFile.get(position).getName());
        request.setDescription("Downloading " + listFile.get(position).getName());
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "" + listFile.get(position).getName());


        DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
    }


    @Override
    public int getItemCount() {
        return listFile.size();
    }

    class FileRecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView fileName;
        public FileRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.file_name);
        }
    }
}
