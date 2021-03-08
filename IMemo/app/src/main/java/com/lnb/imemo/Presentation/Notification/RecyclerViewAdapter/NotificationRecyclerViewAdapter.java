package com.lnb.imemo.Presentation.Notification.RecyclerViewAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.C;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lnb.imemo.Model.Diary;
import com.lnb.imemo.Model.Notification;
import com.lnb.imemo.Model.PersonProfile;
import com.lnb.imemo.R;
import com.lnb.imemo.Utils.DateHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class NotificationRecyclerViewAdapter extends RecyclerView.Adapter<NotificationRecyclerViewAdapter.NotificationReyclerViewHolder> {
    private static final String TAG = "NotificationRecyclerVie";
    private ArrayList<Notification> listNotifications = new ArrayList<>();
    private Gson gson = new GsonBuilder().create();
    private Context mContext;
    private PublishSubject<Pair<Diary, String>> recyclerViewObserver;
    {
        if (recyclerViewObserver == null) {
            recyclerViewObserver = PublishSubject.create();
        }
    }
    private PublishSubject<Pair<String, Integer>> markReadObserver;
    {
        if (markReadObserver == null) {
            markReadObserver = PublishSubject.create();
        }
    }
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

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull NotificationReyclerViewHolder holder, int position) {
        JsonObject jsonObject = new JsonParser().parse(listNotifications.get(position).getData()).getAsJsonObject();
        JsonObject diaryObject = jsonObject.getAsJsonObject("diary");
        JsonObject user = diaryObject.getAsJsonObject("user");
        PersonProfile personProfile = gson.fromJson(user, PersonProfile.class);
        holder.notificationTextView.setText("Bạn được chia sẻ một memo từ " +personProfile.getName());
        Glide.with(mContext)
                .load(personProfile.getPicture())
                .into(holder.userImage);
        Diary diary = gson.fromJson(diaryObject, Diary.class);
        listDiary.add(diary);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewObserver.onNext(new Pair<>(listDiary.get(position), personProfile.getName()));
                if (!listNotifications.get(position).getSeen()) {
                    markReadObserver.onNext(new Pair<>(listNotifications.get(position).getId(), position));
                }
            }
        });

        if (listNotifications.get(position).getSeen()) {
            holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#e7f3ff"));
        }

        Single.fromCallable((Callable<Object>) () -> DateHelper.dateConverter(listNotifications.get(position).getCreatedAt())).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> holder.time.setText(String.valueOf(o)));

    }

    @Override
    public int getItemCount() {
        return Math.min(listNotifications.size(), 30);
    }



    public PublishSubject<Pair<Diary, String>> getRecyclerViewObserver() {
        return recyclerViewObserver;
    }

    public PublishSubject<Pair<String, Integer>> getMarkReadObserver() {
        return markReadObserver;
    }

    public void addNotification(Notification notification) {
        this.listNotifications.add(0, notification);
        if (listNotifications.size() > 30) {
            listNotifications.remove(listNotifications.size() - 1);
        }
        notifyItemInserted(0);
    }

    public void updateItem(int currentReadNotificationPosition) {
        listNotifications.get(currentReadNotificationPosition).setSeen(true);
        notifyItemChanged(currentReadNotificationPosition);
    }

    class NotificationReyclerViewHolder extends RecyclerView.ViewHolder {
        TextView notificationTextView;
        CircleImageView userImage;
        private TextView time;
        public NotificationReyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationTextView = itemView.findViewById(R.id.content_notification);
            userImage = itemView.findViewById(R.id.user_image);
            time = itemView.findViewById(R.id.time_notification);
        }
    }
}
