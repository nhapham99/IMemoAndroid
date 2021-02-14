package com.lnb.imemo.Presentation.Home.TabFragment.AudioRecyclerView;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lnb.imemo.Model.Resource;
import com.lnb.imemo.R;
import com.lnb.imemo.Utils.Utils;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AudioRecyclerViewAdapter extends RecyclerView.Adapter<AudioRecyclerViewAdapter.AudioRecyclerViewHolder> {
    private static final String TAG = "AudioRecyclerViewAdapte";
    private ArrayList<Resource> listAudio;
    private ArrayList<MediaPlayer> mediaPlayers;
    private ArrayList<Handler> handlers;
    private ArrayList<Runnable> runnables;
    private CompositeDisposable disposable = new CompositeDisposable();
    private Context mContext;

    public AudioRecyclerViewAdapter(ArrayList<Resource> listAudio) {
        this.listAudio = listAudio;
        mediaPlayers = new ArrayList<>(this.listAudio.size());
        handlers = new ArrayList<>(this.listAudio.size());
        for (int i = 0; i < this.listAudio.size(); i++) {
            mediaPlayers.add(new MediaPlayer());
            handlers.add(new Handler());
        }
        runnables = new ArrayList<>(this.listAudio.size());
    }

    @NonNull
    @Override
    public AudioRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.memo_audio_item, parent, false);
        mContext = parent.getContext();
        return new AudioRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioRecyclerViewHolder holder, int position) {
        holder.name.setText(listAudio.get(position).getName());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                updateSeekBar(holder, position);
                long currentDuration = mediaPlayers.get(position).getCurrentPosition();
                holder.currentTime.setText(milliSecondsToTimer(currentDuration));
            }
        };
        runnables.add(position, runnable);

        holder.playerSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                SeekBar seekBar = (SeekBar) view;
                int playPosition = (mediaPlayers.get(position).getDuration() / 100) * seekBar.getProgress();
                mediaPlayers.get(position).seekTo(playPosition);
                holder.currentTime.setText(milliSecondsToTimer(mediaPlayers.get(position).getCurrentPosition()));
                return false;
            }
        });


        holder.playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayers.get(position).isPlaying()) {
                    handlers.get(position).removeCallbacks(runnables.get(position));
                    mediaPlayers.get(position).pause();
                    holder.playPause.setImageResource(R.drawable.ic_play);
                    Log.d(TAG, "onClick: ");
                } else {
                    if (mediaPlayers.get(position).getDuration() > 0) {
                        Log.d(TAG, "onClick: loaded");
                        mediaPlayers.get(position).start();
                        holder.playPause.setImageResource(R.drawable.ic_pause);
                        updateSeekBar(holder, position);
                    } else {
                        Log.d(TAG, "onClick: loading");
                        holder.playPause.setVisibility(View.INVISIBLE);
                        holder.progressBar.setVisibility(View.VISIBLE);
                        Observable<Boolean> loadAudio = Observable.create(new ObservableOnSubscribe<Boolean>() {
                            @Override
                            public void subscribe(@io.reactivex.annotations.NonNull ObservableEmitter<Boolean> emitter) throws Exception {
                                emitter.onNext(prepareMediaPlayer(holder, position));
                            }
                        })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());

                        loadAudio.subscribe(new Observer<Boolean>() {
                            @Override
                            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                                disposable.add(d);
                            }

                            @Override
                            public void onNext(@io.reactivex.annotations.NonNull Boolean isLoaded) {
                                Log.d(TAG, "accept: " + isLoaded);
                                if (isLoaded) {
                                    holder.playerSeekBar.setEnabled(true);
                                    holder.playPause.setVisibility(View.VISIBLE);
                                    holder.progressBar.setVisibility(View.INVISIBLE);
                                    mediaPlayers.get(position).start();
                                    holder.playPause.setImageResource(R.drawable.ic_pause);
                                    updateSeekBar(holder, position);
                                    holder.totalTime.setText(milliSecondsToTimer(mediaPlayers.get(position).getDuration()));
                                } else {
                                    holder.playPause.setVisibility(View.VISIBLE);
                                    holder.progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(mContext, "Có lỗi xảy ra. Xin vui lòng thử lại", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(@io.reactivex.annotations.NonNull Throwable e) {}

                            @Override
                            public void onComplete() {}
                        });
                    }

                }
            }
        });


    }

    private Boolean prepareMediaPlayer(AudioRecyclerViewHolder holder, int position) {
        try {
            mediaPlayers.get(position).setDataSource(Utils.storeUrl + listAudio.get(position).getUrl());
            mediaPlayers.get(position).prepare();
            return true;
        } catch (Exception exception) {
            Log.d(TAG, "prepareMediaPlayer: " + exception.getMessage());
            return false;
        }
    }

    private void updateSeekBar(AudioRecyclerViewHolder holder, int position) {
        if (mediaPlayers.get(position).isPlaying()) {
            holder.playerSeekBar.setProgress((int) (((float) mediaPlayers.get(position).getCurrentPosition() / mediaPlayers.get(position).getDuration()) * 100));
            handlers.get(position).postDelayed(runnables.get(position), 1000);
        }
    }

    private String milliSecondsToTimer(long milliSeconds) {
        String timerString = "";
        String secondsString = "";
        int hours = (int) (milliSeconds / (1000 * 60 * 60));
        int minutes = (int) (milliSeconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliSeconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if (hours > 0) {
            timerString = hours + ":";
        }

        if (seconds < 0) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        timerString = timerString + minutes + ":" + secondsString;
        return timerString;
    }

    @Override
    public int getItemCount() {
        return listAudio.size();
    }

    class AudioRecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView playPause;
        TextView name, currentTime, totalTime;
        SeekBar playerSeekBar;
        ProgressBar progressBar;

        public AudioRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            playPause = itemView.findViewById(R.id.memo_audio_play_button);
            name = itemView.findViewById(R.id.memo_audio_name);
            currentTime = itemView.findViewById(R.id.memo_audio_currentTime);
            totalTime = itemView.findViewById(R.id.memo_audio_totalTime);
            progressBar = itemView.findViewById(R.id.memo_audio_progressBar);
            playerSeekBar = itemView.findViewById(R.id.memo_audio_seekBar);
            playerSeekBar.setMax(100);
            playerSeekBar.setEnabled(false);
        }
    }
}
