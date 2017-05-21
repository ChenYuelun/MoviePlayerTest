package com.example.movieplayertest;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.example.movieplayertest.fragment.BaseFragment;
import com.example.movieplayertest.pager.LocalAudioPager;
import com.example.movieplayertest.pager.LocalVideoPager;
import com.example.movieplayertest.pager.NetAudioPager;
import com.example.movieplayertest.pager.NetVideoPager;

import java.util.ArrayList;

import static com.example.movieplayertest.R.id.rb_local_audio;
import static com.example.movieplayertest.R.id.rb_local_video;
import static com.example.movieplayertest.R.id.rb_net_audio;
import static com.example.movieplayertest.R.id.rb_net_video;

public class MainActivity extends AppCompatActivity {

    private FrameLayout framlayout;
    private RadioGroup radiogroup;
    private int position;
    private ArrayList<BaseFragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        framlayout = (FrameLayout) findViewById(R.id.framlayout);
        radiogroup = (RadioGroup) findViewById(R.id.radiogroup);
        initFragments();
        isGrantExternalRW(this);
        radiogroup.setOnCheckedChangeListener(new OnCheckedChangeListener());
        radiogroup.check(rb_local_video);
    }

    private void initFragments() {
        fragments = new ArrayList<>();
        fragments.add(new LocalVideoPager());
        fragments.add(new LocalAudioPager());
        fragments.add(new NetVideoPager());
        fragments.add(new NetAudioPager());
    }

    private class OnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case rb_local_video:
                    position = 0;
                    break;
                case rb_local_audio:
                    position = 1;
                    break;
                case rb_net_video:
                    position = 2;
                    break;
                case rb_net_audio:
                    position = 3;
                    break;
            }
            BaseFragment fragment = fragments.get(position);
            addFragment(fragment);
        }
    }

    private BaseFragment tempFragment;

    private void addFragment(BaseFragment fragment) {
        if (tempFragment != fragment) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if(!fragment.isAdded()) {
                if(tempFragment != null) {
                    ft.hide(tempFragment);
                }
                ft.add(R.id.framlayout,fragment);
            }else {
                if(tempFragment != null) {
                    ft.hide(tempFragment);
                }
                ft.show(fragment);
            }

            ft.commit();
            tempFragment = fragment;

        }

    }

    /**
     * 解决安卓6.0以上版本不能读取外部存储权限的问题
     * @param activity
     * @return
     */
    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

            return false;
        }

        return true;
    }
}
