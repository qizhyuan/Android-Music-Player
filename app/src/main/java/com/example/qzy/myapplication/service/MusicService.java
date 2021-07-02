package com.example.qzy.myapplication.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;

/**
 * Created by QZY on 2017/12/27.
 */

public class MusicService extends Service {
    private final IBinder iBinder = new MyBinder();
    private static MediaPlayer mediaPlayer = new MediaPlayer();
    private static int STATE_FLAG = 0;
    private static String songId = ""; //当前音乐在列表中的位置

    public String getSongId(){
        return songId;
    }

    public void setSongId(String id){
         songId = id;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    public void stop(){
         if(STATE_FLAG == 3 || STATE_FLAG == 4){
             mediaPlayer.stop();
             STATE_FLAG = 5;
         }

    }

    public void start(){
        if(STATE_FLAG == 2 || STATE_FLAG == 4){
            mediaPlayer.start();
            STATE_FLAG=3;
        }
    }

    public void pause(){
        if(STATE_FLAG == 3){
            mediaPlayer.pause();
            STATE_FLAG = 4;
        }
    }

    public void setSource(String url){
        if(STATE_FLAG != 0){
            stop();
            mediaPlayer.reset();
            STATE_FLAG = 0;
        }
        setDataSource(url);
        prepare();
    }

    public void setDataSource(String url){
        if(STATE_FLAG == 0){
            try {
                mediaPlayer.setDataSource(url);
                STATE_FLAG = 1;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void prepare(){
        if(STATE_FLAG == 1){
            try {
                mediaPlayer.prepare();
                STATE_FLAG = 2;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int getDuration(){
        int duration = 0;
        if(mediaPlayer != null){
            if(STATE_FLAG ==2 || STATE_FLAG == 3 || STATE_FLAG == 4){
                duration = mediaPlayer.getDuration();
            }
        }
        return duration;
    }

    public int getProgress(){
        int progress = 0;
        if(mediaPlayer != null){
            if(STATE_FLAG == 3 || STATE_FLAG == 4){
                progress = mediaPlayer.getCurrentPosition();
            }
        }
        return progress;
    }

    public void setProgress(int progress){
        if(mediaPlayer != null){
            if(STATE_FLAG == 3 || STATE_FLAG == 4){
                mediaPlayer.seekTo(progress);
            }
        }
    }


    public int getState(){
        return STATE_FLAG;
    }


    public  class  MyBinder extends Binder{
        public MusicService getService(){
            return MusicService.this;
        }
    }
}
