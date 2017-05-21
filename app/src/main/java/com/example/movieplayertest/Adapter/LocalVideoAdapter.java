package com.example.movieplayertest.Adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.example.movieplayertest.R;
import com.example.movieplayertest.domain.MediaItem;
import com.example.movieplayertest.utils.Utils;

import java.util.ArrayList;

/**
 * Created by chenyuelun on 2017/5/21.
 */

public class LocalVideoAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<MediaItem> datas;
    private Utils utils;

    public LocalVideoAdapter(Context context, ArrayList<MediaItem> mediaItems) {
        this.context = context;
        this.datas = mediaItems;
        utils = new Utils();
    }



    @Override
    public int getCount() {
        return datas == null ? 0 :datas.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = View.inflate(context, R.layout.item_local_video,null);
            viewHolder = new ViewHolder();
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name33);
            viewHolder.tv_duration = (TextView) convertView.findViewById(R.id.tv_duration);
            viewHolder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
            convertView.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MediaItem item = datas.get(position);
        viewHolder.tv_name.setText(item.getName());
        viewHolder.tv_duration.setText(utils.stringForTime((int) item.getDuration()));
        viewHolder.tv_size.setText(Formatter.formatFileSize(context,item.getSize()));


        return convertView;
    }

    static class ViewHolder{
        TextView tv_name;
        TextView tv_duration;
        TextView tv_size;

    }
}
