package com.example.movieplayertest.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.VideoView;

import com.example.movieplayertest.R;
import com.example.movieplayertest.domain.MediaItem;

import java.util.ArrayList;

public class LocalVideoPlayerActivity extends AppCompatActivity {
    private VideoView vv;
    private ArrayList<MediaItem> mediaItems;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_video_player);
        vv = (VideoView)findViewById(R.id.vv);
        getDatas();
        setListener();
        setDatas();


    }

    private void setDatas() {
        if (mediaItems != null && mediaItems.size() > 0) {

            MediaItem mediaItem = mediaItems.get(position);
            vv.setVideoPath(mediaItem.getData());

        }
    }

    //获取视频数据
    private void getDatas() {
        mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("videoList");
        position = getIntent().getIntExtra("position", 0);

    }


    //设置监听事件
    private void setListener() {
        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                vv.start();
            }
        });

        vv.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });

        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

            }
        });
    }


}
