package com.example.qzy.myapplication.okhttp;

import android.os.Build;
import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by QZY on 2017/12/27.
 */
public class HttpInterceptor implements Interceptor {
    private static final String UA = "User-Agent";

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request()
                .newBuilder()
                .addHeader(UA, makeUA())
                .build();
        return chain.proceed(request);
    }

    private String makeUA() {
        return Build.BRAND + "/" + Build.MODEL + "/" + Build.VERSION.RELEASE;
    }
}
