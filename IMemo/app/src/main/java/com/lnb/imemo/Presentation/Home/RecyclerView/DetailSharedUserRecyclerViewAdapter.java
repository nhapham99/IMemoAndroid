package com.lnb.imemo.Presentation.Home.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lnb.imemo.Data.Repository.Model.SharedUser;
import com.lnb.imemo.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailSharedUserRecyclerViewAdapter extends RecyclerView.Adapter<DetailSharedUserRecyclerViewAdapter.DetailSharedUserViewHolder> {
    private List<SharedUser> listSharedUser;
    private Context mContext;

    public DetailSharedUserRecyclerViewAdapter(List<SharedUser> listSharedUser) {
        this.listSharedUser = listSharedUser;
    }

    @NonNull
    @Override
    public DetailSharedUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shared_user_item, parent, false);
        mContext = parent.getContext();
        return new DetailSharedUserViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DetailSharedUserViewHolder holder, int position) {
        SharedUser sharedUser = listSharedUser.get(position);
        holder.userEmail.setText(sharedUser.getReceiver().getName() + " (" + sharedUser.getReceiver().getEmail() + ")");
        Glide.with(mContext).load(sharedUser.getReceiver().getPicture()).into(holder.userAvatar);
    }

    @Override
    public int getItemCount() {
        return listSharedUser.size();
    }

    static class DetailSharedUserViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userAvatar;
        TextView userEmail;
        public DetailSharedUserViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.user_image);
            userEmail = itemView.findViewById(R.id.user_email);
        }
    }
}
