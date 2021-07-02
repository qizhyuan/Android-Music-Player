package com.example.qzy.myapplication.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qzy.myapplication.R;
import com.example.qzy.myapplication.database.MySQLite;
import com.example.qzy.myapplication.model.SearchMusic;

import java.util.List;


/**
 * Created by QZY on 2017/12/28.
 */
public class SearchMusicAdapter extends BaseAdapter {
    private List<SearchMusic.Song> mData;
    private Context context;
    private static int PLAY_POSITION = 0;
    private static boolean isChoose = false;

    public void setPlayPosition(int position){
        PLAY_POSITION = position;
    }

    public int getPlayPosition(){
        return PLAY_POSITION;
    }

    public boolean isChoose(){
        return isChoose;
    }

    public void setChoose(boolean flag){
        isChoose = flag;
    }

    public SearchMusicAdapter(List<SearchMusic.Song> data, Context context) {
        mData = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public SearchMusic.Song getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvTitle.setText(mData.get(position).getSongName());
        holder.tvArtist.setText(mData.get(position).getArtistName());
        holder.btFavor.setOnClickListener(new MyListener(position));

        if(isChoose && position == PLAY_POSITION){
            holder.tvTitle.setTextColor(Color.parseColor("#F44336"));
            holder.tvArtist.setTextColor(Color.parseColor("#4CF44336"));
            holder.btFavor.setBackgroundResource(R.drawable.favorite_add_red);
        }else{
            holder.tvTitle.setTextColor(Color.parseColor("#000000"));
            holder.tvArtist.setTextColor(Color.parseColor("#9E9E9E"));
            holder.btFavor.setBackgroundResource(R.drawable.favorite_add);
        }

        return convertView;
    }

    private boolean isShowDivider(int position) {
        return position != mData.size() - 1;
    }


    private static class ViewHolder {

        private TextView tvTitle;
        private TextView tvArtist;
        private Button btFavor;

        ViewHolder(View view) {
           tvTitle = view.findViewById(R.id.search_list_item_song);
           tvArtist = view.findViewById(R.id.search_list_item_singer);
           btFavor = view.findViewById(R.id.add_favor_button);
        }
    }

    private class MyListener implements View.OnClickListener {
        int mPosition;
        MyListener(int position){
            mPosition = position;
        }
        @Override
        public void onClick(View view) {
            MySQLite mySQLite = new MySQLite(context);
            if(!mySQLite.isExist(mData.get(mPosition).getSongId())) {
                mySQLite.InsertAndUpdate(new String[]{mData.get(mPosition).getSongName(), mData.get(mPosition).getArtistName(),
                        mData.get(mPosition).getSongId()});
                Toast.makeText(context,"成功收藏歌曲 " + mData.get(mPosition).getSongName(), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context,"您已经添加过该歌曲了噢", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
