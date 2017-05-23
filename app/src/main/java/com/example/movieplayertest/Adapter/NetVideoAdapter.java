package com.example.movieplayertest.Adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.movieplayertest.R;
import com.example.movieplayertest.domain.MovieInfo;
import com.example.movieplayertest.utils.Utils;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

/**
 * Created by chenyuelun on 2017/5/21.
 */

public class NetVideoAdapter extends BaseAdapter {
    private final Context context;
    private final List<MovieInfo.TrailersBean> datas;
    private Utils utils;
    private ImageOptions options;


    public NetVideoAdapter(Context context, List<MovieInfo.TrailersBean> trailers) {
        this.context = context;
        this.datas = trailers;
        utils = new Utils();
        options = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setFailureDrawableId(R.drawable.ic_topbanner_logo)
                .setLoadingDrawableId(R.drawable.ic_topbanner_logo)
                .build();

    }


    @Override
    public int getCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public MovieInfo.TrailersBean getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_net_video, null);
            viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.iv_icon2);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name2);
            viewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_content2);
            viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time2);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MovieInfo.TrailersBean bean = datas.get(position);
        viewHolder.tv_name.setText(bean.getMovieName());
        viewHolder.tv_content.setText(bean.getSummary());
        viewHolder.tv_time.setText(utils.stringForTime( bean.getVideoLength() * 1000));

        x.image().bind(viewHolder.icon, bean.getCoverImg(), options);

        return convertView;
    }

    static class ViewHolder {
        ImageView icon;
        TextView tv_name;
        TextView tv_content;
        TextView tv_time;

    }
}
