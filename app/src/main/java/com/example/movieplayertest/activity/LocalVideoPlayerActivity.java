package com.example.movieplayertest.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.movieplayertest.R;
import com.example.movieplayertest.domain.MediaItem;
import com.example.movieplayertest.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LocalVideoPlayerActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PROGRESS = 1;
    private static final int NEWTIME = 2;
    private VideoView vv;
    private ArrayList<MediaItem> mediaItems;
    private int position;

    private LinearLayout llTop;
    private TextView tvName;
    private ImageView ivBattery;
    private TextView tvSystemTime;
    private Button btnVoice;
    private SeekBar seekbarVoice;
    private Button btnSwitchPlayer;
    private LinearLayout llBottom;
    private TextView tvCurrentTime;
    private SeekBar seekbarVideo;
    private TextView tvDuration;
    private Button btnExit;
    private Button btnPre;
    private Button btnStartPause;
    private Button btnNext;
    private Button btnSwitchScreen;
    private int duration;
    private Utils utils;
    private MyBroadCastReceiver receiver;

    private GestureDetector detector;


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-05-21 21:35:27 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_local_video_player);
        vv = (VideoView) findViewById(R.id.vv);
        llTop = (LinearLayout) findViewById(R.id.ll_top);
        tvName = (TextView) findViewById(R.id.tv_name);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
        tvSystemTime = (TextView) findViewById(R.id.tv_system_time);
        btnVoice = (Button) findViewById(R.id.btn_voice);
        seekbarVoice = (SeekBar) findViewById(R.id.seekbar_voice);
        btnSwitchPlayer = (Button) findViewById(R.id.btn_switch_player);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        tvCurrentTime = (TextView) findViewById(R.id.tv_current_time);
        seekbarVideo = (SeekBar) findViewById(R.id.seekbar_video);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        btnExit = (Button) findViewById(R.id.btn_exit);
        btnPre = (Button) findViewById(R.id.btn_pre);
        btnStartPause = (Button) findViewById(R.id.btn_start_pause);
        btnNext = (Button) findViewById(R.id.btn_next);
        btnSwitchScreen = (Button) findViewById(R.id.btn_switch_screen);


        btnVoice.setOnClickListener(this);
        btnSwitchPlayer.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnPre.setOnClickListener(this);
        btnStartPause.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnSwitchScreen.setOnClickListener(this);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2017-05-21 21:35:27 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == btnVoice) {
            // Handle clicks for btnVoice
        } else if (v == btnSwitchPlayer) {
            // Handle clicks for btnSwitchPlayer
        } else if (v == btnExit) {
            finish();
            // Handle clicks for btnExit
        } else if (v == btnPre) {
            playPreVideo();

            // Handle clicks for btnPre
        } else if (v == btnStartPause) {
            playOrPauseVideo();
            // Handle clicks for btnStartPause
        } else if (v == btnNext) {
            playNextVideo();
            // Handle clicks for btnNext
        } else if (v == btnSwitchScreen) {
            // Handle clicks for btnSwitchScreen
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViews();
        getDatas();
        initData();

        setListener();
        setDatas();


    }





    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PROGRESS :
                    int currentPosition = vv.getCurrentPosition();
                    seekbarVideo.setProgress(currentPosition);
                    tvCurrentTime.setText(utils.stringForTime(currentPosition));
                    sendEmptyMessageDelayed(PROGRESS,1000);

                    break;

                case NEWTIME:
                    tvSystemTime.setText(getSystemTime());
                    handler.sendEmptyMessageDelayed(NEWTIME, 60000);
                    break;
            }

        }
    };




    private void setDatas() {
        if (mediaItems != null && mediaItems.size() > 0) {

            MediaItem mediaItem = mediaItems.get(position);
            vv.setVideoPath(mediaItem.getData());
            handler.sendEmptyMessage(NEWTIME);

        }
    }

    //获取视频数据
    private void getDatas() {
        mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("videoList");
        tvName.setText(mediaItems.get(position).getName());
        position = getIntent().getIntExtra("position", 0);

    }

    private void initData() {
        utils = new Utils();
        //注册监听电量信息的广播
        receiver = new MyBroadCastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver,filter);

        detector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                hideOrShowMediaController();
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                return super.onDoubleTap(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                playOrPauseVideo();
                super.onLongPress(e);
            }
        });

    }
    private boolean isShowMediaController = true;

    private void hideOrShowMediaController() {
        if(isShowMediaController) {
            llBottom.setVisibility(View.GONE);
            llTop.setVisibility(View.GONE);
            isShowMediaController = false;
        }else {
            llBottom.setVisibility(View.VISIBLE);
            llTop.setVisibility(View.VISIBLE);
            isShowMediaController = true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }



    //设置监听事件
    private void setListener() {
        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                duration = vv.getDuration();
                seekbarVideo.setMax(duration);
                seekbarVideo.setMax(duration);
                tvDuration.setText(utils.stringForTime(duration));
                vv.start();
                setButtonStatus();
                handler.sendEmptyMessage(PROGRESS);

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

                    playNextVideo();


            }
        });

        seekbarVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    vv.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void playNextVideo() {
        position++;
        if(position<mediaItems.size()) {
            MediaItem mediaItem = mediaItems.get(position);
            vv.setVideoPath(mediaItem.getData());
            tvName.setText(mediaItem.getName());
            setButtonStatus();
        }else {
            Toast.makeText(LocalVideoPlayerActivity.this, "视频列表已播放完毕", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void playPreVideo() {
        position--;
        if(position>=0) {
            MediaItem mediaItem = mediaItems.get(position);
            vv.setVideoPath(mediaItem.getData());
            tvName.setText(mediaItem.getName());
            setButtonStatus();
        }
    }

    private void setButtonStatus() {
        if(mediaItems!= null && mediaItems.size() > 0) {
            setEnable(true);
            if(position ==0) {
                btnPre.setBackgroundResource(R.drawable.btn_pre_gray);
                btnPre.setEnabled(false);
            }

            if(position == mediaItems.size()-1) {
                btnNext.setBackgroundResource(R.drawable.btn_next_gray);
                btnNext.setEnabled(false);
            }
        }
    }

    private void setEnable(boolean b) {
        if(b) {
            btnPre.setBackgroundResource(R.drawable.btn_pre_selector);
            btnNext.setBackgroundResource(R.drawable.btn_next_selector);
        }else {
            btnPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btnNext.setBackgroundResource(R.drawable.btn_next_gray);
        }
        btnPre.setEnabled(b);
        btnNext.setEnabled(b);
    }

    private String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(new Date());
    }

    public class MyBroadCastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);//主线程
            Log.e("TAG","level=="+level);
            setBatteryView(level);
        }
    }
    private void setBatteryView(int level) {
        if(level <=0){
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        }else if(level <= 10){
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        }else if(level <=20){
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        }else if(level <=40){
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        }else if(level <=60){
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        }else if(level <=80){
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        }else if(level <=100){
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }else {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }
    }
    private void playOrPauseVideo() {
        if (vv.isPlaying()) {
            vv.pause();
            btnStartPause.setBackgroundResource(R.drawable.btn_start_selector);
        } else {
            vv.start();
            btnStartPause.setBackgroundResource(R.drawable.btn_pause_selector);
        }
    }


    @Override
    protected void onDestroy() {
        if(handler != null){
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }

        if(receiver != null){
            unregisterReceiver(receiver);
            receiver = null;
        }
        super.onDestroy();
    }
}
