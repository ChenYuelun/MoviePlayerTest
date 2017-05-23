package com.example.movieplayertest.pager;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.movieplayertest.Adapter.NetVideoAdapter;
import com.example.movieplayertest.R;
import com.example.movieplayertest.activity.LocalVideoPlayerActivity;
import com.example.movieplayertest.domain.MovieInfo;
import com.example.movieplayertest.fragment.BaseFragment;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

import static com.example.movieplayertest.R.id.lv_local_video;


/**
 * Created by chenyuelun on 2017/5/21.
 */

public class NetVideoPager extends BaseFragment {
    private ListView lv_net_video;
    private TextView tv_nodata;
    private NetVideoAdapter adapter;
    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.net_video_item, null);
        lv_net_video = (ListView) view.findViewById(R.id.lv_net_video2);
        tv_nodata = (TextView) view.findViewById(R.id.tv_nodata2);

        lv_net_video.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, LocalVideoPlayerActivity.class);
                intent.setDataAndType(Uri.parse(adapter.getItem(position).getUrl()),"video/*");
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
    }


}
