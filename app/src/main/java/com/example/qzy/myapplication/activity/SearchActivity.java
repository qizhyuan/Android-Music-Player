package com.example.qzy.myapplication.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qzy.myapplication.R;
import com.example.qzy.myapplication.adapter.FavorMusicAdapter;
import com.example.qzy.myapplication.adapter.SearchMusicAdapter;
import com.example.qzy.myapplication.database.MySQLite;
import com.example.qzy.myapplication.model.DownloadInfo;
import com.example.qzy.myapplication.model.SearchMusic;
import com.example.qzy.myapplication.okhttp.HttpCallback;
import com.example.qzy.myapplication.okhttp.HttpClient;
import com.example.qzy.myapplication.okhttp.HttpInterceptor;
import com.example.qzy.myapplication.service.MusicService;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by QZY on 2017/12/28.
 */

public class SearchActivity extends AppCompatActivity{
    private EditText editText;
    private Button search;
    private ImageView play;
    private ImageView next;
    private ImageView previous;
    private ListView listView;
    private ListView favorList;
    private TextView song;
    private TextView singer;
    private TextView findNone;
    private List<SearchMusic.Song> mSearchMusicList = new ArrayList<>();
    private SearchMusicAdapter mAdapter = new SearchMusicAdapter(mSearchMusicList,this);
    private FavorMusicAdapter favorMusicAdapter = new FavorMusicAdapter(this);
    private MySQLite mySQLite;
    private FloatingActionButton listSwitch;
    private static TextView timeLeft;
    private static TextView timeRight;
    private static SeekBar seekBar;
    private static int PLAY_LIST_STATE = 0;  //1网络列表，2本地列表
    private static SimpleDateFormat time = new SimpleDateFormat("mm:ss");

    private static int code = 1;

    private int nextSong;
    private int previousSong;

    public static MusicService musicService = new MusicService();



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initOkHttpUtils();
        initView();
        setClickListener();
        mThread.start();
    }

    private void initView(){
        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
        bindService(intent, sc, BIND_AUTO_CREATE);

        editText = (EditText) findViewById(R.id.search_content);
        search = (Button)findViewById(R.id.search_button);

        timeLeft = (TextView)findViewById(R.id.seek_bar_begin);
        timeRight = (TextView)findViewById(R.id.seek_bar_end);
        seekBar =(SeekBar)findViewById(R.id.seek_bar);

        play = (ImageView) findViewById(R.id.iv_play_bar_play);
        next = (ImageView) findViewById(R.id.iv_play_bar_next);
        previous = (ImageView) findViewById(R.id.iv_play_bar_previous);

        song = (TextView) findViewById(R.id.tv_play_bar_title);
        singer = (TextView)findViewById(R.id.tv_play_bar_artist);
        findNone = (TextView)findViewById(R.id.find_none);
        listView = (ListView)findViewById(R.id.search_list_view);
        listView.setAdapter(mAdapter);

        favorList = (ListView)findViewById(R.id.favor_music);
        favorList.setAdapter(favorMusicAdapter);

        favorMusicAdapter.updateFromDatabase();

        listSwitch = (FloatingActionButton) findViewById(R.id.list_switch);
        listSwitch.setTag(0);

    }

    private void setClickListener(){
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = editText.getText().toString();
                if (!key.isEmpty()){
                    searchMusic(key);
                    mAdapter.setChoose(false);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                playMusic(i,true);
                PLAY_LIST_STATE = 1;
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(musicService.getState() == 3){
                    view.setSelected(false);
                    musicService.pause();
                }else if (musicService.getState() == 2 || musicService.getState() == 4){
                    view.setSelected(true);
                    musicService.start();
                }

            }
        });


        listSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listSwitch.getTag().toString().equals("0")){
                    listSwitch.setTag(1);
                    listView.setVisibility(View.GONE);
                    findNone.setVisibility(View.GONE);
                    favorList.setVisibility(View.VISIBLE);
                    favorMusicAdapter.updateFromDatabase();
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.search);
                    listSwitch.setImageBitmap(bitmap);
                }else if(listSwitch.getTag().toString().equals("1")){
                    listSwitch.setTag(0);
                    listView.setVisibility(View.VISIBLE);
                    favorList.setVisibility(View.GONE);
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.favorite);
                    listSwitch.setImageBitmap(bitmap);
                }
            }
        });

        favorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                playMusic(i,false);
                PLAY_LIST_STATE = 2;
            }
        });

        favorList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                mySQLite = new MySQLite(SearchActivity.this);
                if(favorMusicAdapter != null){
                    if(favorMusicAdapter.getPlayPosition()!=-1){//判断收藏列表是否有歌被选定
                        String currentSongId = favorMusicAdapter.getItem(favorMusicAdapter.getPlayPosition()).getSongId();
                        String deleteSongId = favorMusicAdapter.getItem(position).getSongId();
                        mySQLite.delete(deleteSongId);
                        if(deleteSongId.equals(currentSongId)){//判断当前删除歌曲是不是正在播放
                            favorMusicAdapter.updateFromDatabase();
                            favorMusicAdapter.setChoose(false);
                        }else{
                            favorMusicAdapter.setPlayPosition(mySQLite.getPosition(currentSongId));//设置播放歌曲列表位置
                            favorMusicAdapter.updateFromDatabase();
                            favorMusicAdapter.setChoose(true);
                        }
                        favorMusicAdapter.notifyDataSetChanged();
                    }else{
                        String deleteSongId = favorMusicAdapter.getItem(position).getSongId();
                        mySQLite.delete(deleteSongId);
                        favorMusicAdapter.updateFromDatabase();
                        favorMusicAdapter.notifyDataSetChanged();
                    }
                    Toast.makeText(SearchActivity.this,"成功删除歌曲", Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(PLAY_LIST_STATE == 2){//播放收藏列表歌曲时
                    mySQLite = new MySQLite(SearchActivity.this);
                    //收藏列表有当前选定的歌曲时（即该歌没有被收藏-》播放-》删除）
                    if(favorMusicAdapter != null && mySQLite.isExist(musicService.getSongId())){
                        nextSong = mySQLite.getNext(musicService.getSongId());
                        playMusic(nextSong,false);
                    }
                }else if(PLAY_LIST_STATE == 1){//播放在线搜索列表歌曲时
                    if(mAdapter != null && mAdapter.isChoose()){
                        //线搜索列表下一首（具有循环功能）
                        nextSong = (mAdapter.getPlayPosition() + 1) % mAdapter.getCount();
                        playMusic(nextSong,true);
                    }
                }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(PLAY_LIST_STATE == 2){
                    mySQLite = new MySQLite(SearchActivity.this);
                    if(favorMusicAdapter != null && mySQLite.isExist(musicService.getSongId())){
                        previousSong = mySQLite.getPrevious(musicService.getSongId());
                        playMusic(previousSong,false);
                    }
                }else if(PLAY_LIST_STATE == 1){
                        if(mAdapter != null && mAdapter.isChoose()){
                            previousSong = (mAdapter.getPlayPosition() + mAdapter.getCount() - 1) % mAdapter.getCount();
                            playMusic(previousSong,true);
                        }
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    seekBar.setProgress(progress);
                    code = 2;
                    timeLeft.setText(time.format(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                musicService.setProgress(progress);
                code = 1;
            }
        });

    }

    private void initOkHttpUtils() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(new HttpInterceptor())
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }

    private void searchMusic(String keyword) {
        HttpClient.searchMusic(keyword, new HttpCallback<SearchMusic>() {
            @Override
            public void onSuccess(SearchMusic response) {
                if(mSearchMusicList.size() != 0){
                    mSearchMusicList.clear();
                }
                if(response != null && response.getSong() != null){
                    findNone.setVisibility(View.INVISIBLE);
                    mSearchMusicList.addAll(response.getSong());
                }else{
                    findNone.setVisibility(View.VISIBLE);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(Exception e) {
                Toast.makeText(SearchActivity.this,e.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d("service","connected");
            MusicService.MyBinder myBinder = (MusicService.MyBinder) iBinder;
            musicService = myBinder.getService();
        }
        @Override

        public void onServiceDisconnected(ComponentName componentName) {
            sc = null;
            Log.d("service","disconnected");
        }
    };

    private void playMusic(final int where, final boolean which ){//true 搜索列表， false 收藏列表

        final String songId = which ? mAdapter.getItem(where).getSongId() : favorMusicAdapter.getItem(where).getSongId();
        final String songName = which ? mAdapter.getItem(where).getSongName() : favorMusicAdapter.getItem(where).getSongName();
        final String artistName = which? mAdapter.getItem(where).getArtistName() : favorMusicAdapter.getItem(where).getArtistName();
        HttpClient.getMusicDownloadInfo(songId, new HttpCallback<DownloadInfo>() {
            @Override
            public void onSuccess(DownloadInfo response) throws IOException {
                if (response == null || response.getBitrate() == null) {
                    onFail(null);
                    return;
                }
                play.setSelected(true);
                musicService.setSource(response.getBitrate().getFile_link());
                musicService.start();
                musicService.setSongId(songId);

                song.setText(songName);
                singer.setText(artistName);

                Toast.makeText(SearchActivity.this,"正在为您播放 "+ songName,Toast.LENGTH_SHORT).show();

                if(which){
                    mAdapter.setPlayPosition(where);
                    mAdapter.setChoose(true);
                    favorMusicAdapter.setChoose(false);
                }else{
                    favorMusicAdapter.setPlayPosition(where);
                    favorMusicAdapter.setChoose(true);
                    mAdapter.setChoose(false);
                }

                favorMusicAdapter.notifyDataSetChanged();
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(Exception e) {
                Log.v("err", e.getMessage());
            }
        });
    }

    @SuppressLint("HandlerLeak")
    final static Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    timeRight.setText(time.format(musicService.getDuration()));
                    timeLeft.setText(time.format(musicService.getProgress()));
                    seekBar.setMax(musicService.getDuration());
                    seekBar.setProgress(musicService.getProgress());
                case 2:
                    timeRight.setText(time.format(musicService.getDuration()));

                case 3:

            }
        }
    };

     Thread mThread = new Thread(){
        @Override
        public void run(){
            while(true){
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(sc != null){
                    mHandler.obtainMessage(code).sendToTarget();
                }
            }
        }
    };

}
