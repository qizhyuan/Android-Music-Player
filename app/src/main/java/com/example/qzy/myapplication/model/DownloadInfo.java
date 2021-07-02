package com.example.qzy.myapplication.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by QZY on 2017/12/27.
 */
public class DownloadInfo {
    @SerializedName("bitrate")
    private Bitrate bitrate;

    public Bitrate getBitrate() {
        return bitrate;
    }

    public static class Bitrate {
        @SerializedName("file_link")
        private String file_link;

        public String getFile_link() {
            return file_link;
        }

    }
}
