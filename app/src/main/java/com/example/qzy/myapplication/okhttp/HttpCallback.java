package com.example.qzy.myapplication.okhttp;

import java.io.IOException;

/**
 * Created by QZY on 2017/12/27.
 */
public abstract class HttpCallback<T> {
    public abstract void onSuccess(T t) throws IOException;

    public abstract void onFail(Exception e);

    void onFinish() {
    }
}
