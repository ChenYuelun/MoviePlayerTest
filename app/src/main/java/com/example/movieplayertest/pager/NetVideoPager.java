package com.example.movieplayertest.pager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.movieplayertest.Adapter.NetVideoAdapter;
import com.example.movieplayertest.R;
import com.example.movieplayertest.activity.LocalVideoPlayerActivity;
import com.example.movieplayertest.activity.VitamioVideoPlayerActivity;
import com.example.movieplayertest.domain.MediaItem;
import com.example.movieplayertest.domain.MovieInfo;
import com.example.movieplayertest.fragment.BaseFragment;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.internal.Primitives;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import static android.media.CamcorderProfile.get;
import static com.example.movieplayertest.R.id.lv_local_video;


/**
 * Created by chenyuelun on 2017/5/21.
 */

public class NetVideoPager extends BaseFragment {
    private ListView lv_net_video;
    private TextView tv_nodata;
    private NetVideoAdapter adapter;
    private ArrayList<MediaItem> mediaItems;
    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.net_video_item, null);
        lv_net_video = (ListView) view.findViewById(R.id.lv_net_video2);
        tv_nodata = (TextView) view.findViewById(R.id.tv_nodata2);

        lv_net_video.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(context,LocalVideoPlayerActivity.class);
                if(mediaItems != null && mediaItems.size()>0) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("videoList",mediaItems);
                    intent.putExtra("position",position);
                    intent.putExtras(bundle);
                }
                startActivity(intent);

            }
        });

        return view;

    }


    @Override
    public void initDatas() {
        super.initDatas();
        getDataFromNet();
    }

    private void getDataFromNet() {
        RequestParams request = new RequestParams("http://api.m.mtime.cn/PageSubArea/TrailerList.api");
        x.http().get(request, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG","联网成功");
                setData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAG","联网失败");

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void setData(String json) {
        MovieInfo movieInfo = new Gson().fromJson(json, MovieInfo.class);
        List<MovieInfo.TrailersBean> trailers = movieInfo.getTrailers();

        if(trailers!= null && trailers.size()>0) {
            tv_nodata.setVisibility(View.GONE);
            adapter = new NetVideoAdapter(context,trailers);
            lv_net_video.setAdapter(adapter);
        }else {
            tv_nodata.setVisibility(View.VISIBLE);
        }

        mediaItems = new ArrayList<>();
//        for(int i = 0; i < trailers.size(); i++) {
////            private String name;
////            private long duration;
////            private long size;
////            private String data;
//            MovieInfo.TrailersBean bean = trailers.get(i);
//
//            String name = bean.getMovieName();
//            long duraition = bean.getVideoLength()*1000;
//            long size = 0;
//            String data = bean.getUrl();
//            mediaItems.add(new MediaItem(name,duraition,size,data));
//            Log.e("TAG",data);
//
//        }

        try {
            JSONObject jsonObject = new JSONObject(json);
            String json2 = jsonObject.getString("trailers");
            JSONArray array = new JSONArray(json2);
            for(int i = 0; i < array.length(); i++) {
                JSONObject jsonObject2 = array.getJSONObject(i);
                String name = jsonObject2.getString("movieName");
                long duraition = jsonObject2.getInt("videoLength")*1000;
                long size = 0;
                String data = jsonObject2.getString("url");
                mediaItems.add(new MediaItem(name,duraition,size,data));

            }



        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


}
