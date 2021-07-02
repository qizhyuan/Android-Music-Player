package com.example.qzy.myapplication.okhttp;

import android.support.annotation.NonNull;

import com.example.qzy.myapplication.model.DownloadInfo;
import com.example.qzy.myapplication.model.SearchMusic;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.IOException;

import okhttp3.Call;

/**
 * Created by QZY on 2017/12/27.
 */
public class HttpClient {
    private static final String BASE_URL = "http://tingapi.ting.baidu.com/v1/restserver/ting";
    private static final String METHOD_DOWNLOAD_MUSIC = "baidu.ting.song.play";
    private static final String METHOD_SEARCH_MUSIC = "baidu.ting.search.catalogSug";
    // private static final String METHOD_LRC = "baidu.ting.song.lry";
    private static final String PARAM_METHOD = "method";
    private static final String PARAM_SONG_ID = "songid";
    private static final String PARAM_QUERY = "query";

    public static void getMusicDownloadInfo(String songId, @NonNull final HttpCallback<DownloadInfo> callback) {
        OkHttpUtils.get().url(BASE_URL)
                .addParams(PARAM_METHOD, METHOD_DOWNLOAD_MUSIC)
                .addParams(PARAM_SONG_ID, songId)
                .build()
                .execute(new JsonCallback<DownloadInfo>(DownloadInfo.class) {
                    @Override
                    public void onResponse(DownloadInfo response, int id) {
                        try {
                            callback.onSuccess(response);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void searchMusic(String keyword, @NonNull final HttpCallback<SearchMusic> callback) {
        OkHttpUtils.get().url(BASE_URL)
                .addParams(PARAM_METHOD, METHOD_SEARCH_MUSIC)
                .addParams(PARAM_QUERY, keyword)
                .build()
                .execute(new JsonCallback<SearchMusic>(SearchMusic.class) {
                    @Override
                    public void onResponse(SearchMusic response, int id) {
                        try {
                            callback.onSuccess(response);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }


}
