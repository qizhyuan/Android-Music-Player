package com.example.qzy.myapplication.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.qzy.myapplication.R;
import com.example.qzy.myapplication.database.MySQLite;
import com.example.qzy.myapplication.model.SearchMusic;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by QZY on 2017/12/28.
 */
public class FavorMusicAdapter extends BaseAdapter {
    private List<SearchMusic.Song> mData = new ArrayList<SearchMusic.Song>();
    private Context context;
    private static int PLAY_POSITION = -1;
    private static boolean isChoose = false;

    public void setPlayPosition(int position){
        PLAY_POSITION = position;
    }

    public int getPlayPosition(){
        return PLAY_POSITION;
    }

    public void setChoose(boolean flag){
        isChoose = flag;
    }

    public FavorMusicAdapter(Context context) {
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

    public void updateFromDatabase(){

        MySQLite mySQLite = new MySQLite(context);
        String[] songName = mySQLite.getArray("songName");
        String[] artistName = mySQLite.getArray("artistName");
        String[] songId = mySQLite.getArray("songId");
        if (!mData.isEmpty()){
            mData.clear();
        }
        for(int i = 0; i< songName.length; ++i){
            SearchMusic.Song song = new SearchMusic.Song();
            song.setSongName(songName[i]);
            song.setArtistName(artistName[i]);
            song.setSongId(songId[i]);
            mData.add(song);
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.favor_list_item, parent, false);
            holder = new ViewHolder(convertView);
            holder.tvTitle.setTextColor(Color.parseColor("#000000"));
            holder.tvArtist.setTextColor(Color.parseColor("#9E9E9E"));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            if(isChoose && position == PLAY_POSITION){
                holder.tvTitle.setTextColor(Color.parseColor("#F44336"));
                holder.tvArtist.setTextColor(Color.parseColor("#4CF44336"));
            }
        }

        holder.tvTitle.setText(mData.get(position).getSongName());
        holder.tvArtist.setText(mData.get(position).getArtistName());

        return convertView;
    }

    private static class ViewHolder {


        private TextView tvTitle;
        private TextView tvArtist;

        ViewHolder(View view) {
            tvTitle = view.findViewById(R.id.favor_list_item_song);
            tvArtist = view.findViewById(R.id.favor_list_item_singer);
        }
    }
}
