package com.example.qzy.myapplication.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by QZY on 2017/12/27.
 */
public class SearchMusic {
    @SerializedName("song")
    private List<Song> song;

    public List<Song> getSong() {
        return song;
    }

    public void setSong(List<Song> song) {
        this.song = song;
    }

    public static class Song {
        @SerializedName("songname")
        private String songName;
        @SerializedName("artistname")
        private String artistName;
        @SerializedName("songid")
        private String songId;

        public String getSongName() {
            return songName;
        }

        public void setSongName(String songName) {
            this.songName = songName;
        }

        public String getArtistName() {
            return artistName;
        }

        public void setArtistName(String artistName) {
            this.artistName = artistName;
        }

        public String getSongId() {
            return songId;
        }

        public void setSongId(String songId) {
            this.songId = songId;
        }
    }
}
