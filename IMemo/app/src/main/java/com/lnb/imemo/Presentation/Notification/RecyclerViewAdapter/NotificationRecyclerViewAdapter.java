package com.lnb.imemo.Presentation.Notification.RecyclerViewAdapter;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lnb.imemo.Model.Diary;
import com.lnb.imemo.Model.Notification;
import com.lnb.imemo.Model.PersonProfile;
import com.lnb.imemo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.subjects.PublishSubject;

public class NotificationRecyclerViewAdapter extends RecyclerView.Adapter<NotificationRecyclerViewAdapter.NotificationReyclerViewHolder> {
    private static final String TAG = "NotificationRecyclerVie";
    private ArrayList<Notification> listNotifications = new ArrayList<>();
    private Gson gson = new GsonBuilder().create();
    private Context mContext;
    private PublishSubject<Pair<Diary, String>> recyclerViewObserver = PublishSubject.create();
    private ArrayList<Diary> listDiary = new ArrayList<>();

    public NotificationRecyclerViewAdapter(ArrayList<Notification> listNotifications) {
        this.listNotifications = listNotifications;
    }

    public NotificationRecyclerViewAdapter() {
    }

    public void setListNotifications(ArrayList<Notification> listNotifications) {
        this.listNotifications = listNotifications;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationReyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_notification_layout, parent, false);
        return new NotificationReyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationReyclerViewHolder holder, int position) {
        JsonObject jsonObject = new JsonParser().parse(listNotifications.get(position).getData()).getAsJsonObject();
        Log.d(TAG, "onBindViewHolder: " + jsonObject.toString());
        JsonObject diaryObject = jsonObject.getAsJsonObject("diary");
        JsonObject user = diaryObject.getAsJsonObject("user");
        Log.d(TAG, "onBindViewHolder: " + user.toString());
        PersonProfile personProfile = gson.fromJson(user, PersonProfile.class);
        holder.notificationTextView.setText("Bạn được chia sẻ một memo từ " +personProfile.getName());
        Glide.with(mContext).load(personProfile.getPicture()).into(holder.userImage);
        Diary diary = gson.fromJson(diaryObject, Diary.class);
        listDiary.add(diary);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewObserver.onNext(new Pair<>(listDiary.get(position), personProfile.getName()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return listNotifications.size();
    }

    public PublishSubject<Pair<Diary, String>> getRecyclerViewObserver() {
        return recyclerViewObserver;
    }

    class NotificationReyclerViewHolder extends RecyclerView.ViewHolder {
        TextView notificationTextView;
        CircleImageView userImage;
        public NotificationReyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationTextView = itemView.findViewById(R.id.content_notification);
            userImage = itemView.findViewById(R.id.user_image);
        }
    }
}
