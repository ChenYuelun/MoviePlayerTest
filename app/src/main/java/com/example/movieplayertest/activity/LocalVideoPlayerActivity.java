package com.example.movieplayertest.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movieplayertest.R;
import com.example.movieplayertest.domain.MediaItem;
import com.example.movieplayertest.utils.Utils;
import com.example.movieplayertest.view.VideoView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.R.attr.width;

public class LocalVideoPlayerActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PROGRESS = 1;
    private static final int NEWTIME = 2;
    private static final int HIDEMEDIACONTROLLER = 3;
    private static final int DEFAULT_SCREEN = 4;
    private static final int FULL_SCREEN = 5;
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
    private boolean isShowMediaController = true;
    private GestureDetector detector;

    private int screenWidth;
    private int screenHeight;

    private int videoWidth;
    private int videoHeight;
    private boolean isFullScreen = true;


    private int maxVoice;
    private int currentVoice;
    private boolean isMute = false;
    private AudioManager am;


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
            if(!isMute) {
                am.setStreamVolume(AudioManager.STREAM_MUSIC,0,0);
                seekbarVoice.setProgress(0);
                isMute =true;
            }else {
                am.setStreamVolume(AudioManager.STREAM_MUSIC,currentVoice,0);
                seekbarVoice.setProgress(currentVoice);
                isMute =false;
            }
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
            setVideoScreenType();
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

                case HIDEMEDIACONTROLLER:

                    hideOrShowMediaController();
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

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenHeight = metrics.heightPixels;
        screenWidth =metrics.widthPixels;
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        maxVoice =am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVoice = am.getStreamVolume(AudioManager.STREAM_MUSIC);

        detector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                hideOrShowMediaController();
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                setVideoScreenType();
                return super.onDoubleTap(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                playOrPauseVideo();
                super.onLongPress(e);
            }
        });

    }


    private float startY;
    private float touchRang;
    private int curVoice;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN :
                startY = event.getY();
                touchRang =Math.min(screenWidth, screenHeight);
                curVoice = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                handler.removeMessages(HIDEMEDIACONTROLLER);
                break;
            case MotionEvent.ACTION_MOVE :
                float newY = event.getY();
                float diatanceY = startY - newY;
                int change = (int) ((diatanceY/touchRang)*maxVoice);
                int newVoice = Math.min(Math.max(curVoice + change,0),maxVoice);
                if(newVoice != 0) {
                    updataCurrentVoice(newVoice);
                }
                break;
            case MotionEvent.ACTION_UP :
                handler.sendEmptyMessageDelayed(HIDEMEDIACONTROLLER,5000);
                break;
        }
        return super.onTouchEvent(event);
    }

    //设置监听事件
    private void setListener() {
        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoWidth = mp.getVideoWidth();
                videoHeight = mp.getVideoHeight();
                duration = vv.getDuration();
                seekbarVideo.setMax(duration);
                seekbarVoice.setMax(maxVoice);
                seekbarVoice.setProgress(currentVoice);
                tvDuration.setText(utils.stringForTime(duration));
                vv.start();
                setVideoScreenType();
                setButtonStatus();
                handler.sendEmptyMessage(PROGRESS);
                hideOrShowMediaController();

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
                handler.removeMessages(HIDEMEDIACONTROLLER);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.sendEmptyMessageDelayed(HIDEMEDIACONTROLLER,5000);
            }
        });

        seekbarVoice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    updataCurrentVoice(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeMessages(HIDEMEDIACONTROLLER);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.sendEmptyMessageDelayed(HIDEMEDIACONTROLLER,5000);
            }
        });
    }

    private void updataCurrentVoice(int progress) {

        currentVoice =progress;
        am.setStreamVolume(AudioManager.STREAM_MUSIC,currentVoice,0);
        seekbarVoice.setProgress(currentVoice);

        if(currentVoice == 0) {
            isMute = true;
        }else {
            isMute = false;
        }


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



    private void hideOrShowMediaController() {
        if(isShowMediaController) {
            handler.removeMessages(HIDEMEDIACONTROLLER);
            llBottom.setVisibility(View.GONE);
            llTop.setVisibility(View.GONE);

            isShowMediaController = false;
        }else {
            llBottom.setVisibility(View.VISIBLE);
            llTop.setVisibility(View.VISIBLE);
            isShowMediaController = true;
            handler.sendEmptyMessageDelayed(HIDEMEDIACONTROLLER,5000);
        }
    }


    private void setVideoScreenType() {
        if(isFullScreen) {
            setScreenType(DEFAULT_SCREEN);
            isFullScreen =false;
        }else {
            setScreenType(FULL_SCREEN);
            isFullScreen =true;
        }
    }

    private void setScreenType(int screenTYpe) {
        switch (screenTYpe) {
            case  DEFAULT_SCREEN:
                int mVideoWidth = videoWidth;
                int mVideoHeight= videoHeight;
                int height = screenHeight;
                int width = screenWidth;


                // for compatibility, we adjust size based on aspect ratio
                if (mVideoWidth * height < width * mVideoHeight) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }
                vv.setVideoSize(width, height);
                btnSwitchScreen.setBackgroundResource(R.drawable.btn_switch_screen_full_selector);
                break;
            case  FULL_SCREEN:
                vv.setVideoSize(screenWidth,screenHeight);
                btnSwitchScreen.setBackgroundResource(R.drawable.btn_switch_screen_default_selector);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            currentVoice--;
            updataCurrentVoice(currentVoice);
            handler.removeMessages(HIDEMEDIACONTROLLER);
            handler.sendEmptyMessageDelayed(HIDEMEDIACONTROLLER, 5000);
            return true;
        }else if(keyCode ==KeyEvent.KEYCODE_VOLUME_UP){
            currentVoice++;
            updataCurrentVoice(currentVoice);
            handler.removeMessages(HIDEMEDIACONTROLLER);
            handler.sendEmptyMessageDelayed(HIDEMEDIACONTROLLER, 5000);
            return true;
        }

        return super.onKeyDown(keyCode, event);
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
