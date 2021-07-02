package com.example.qzy.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.qzy.myapplication.R;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by QZY on 2017/12/27.
 */

public class SplashActivity extends AppCompatActivity{

    protected Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();

    }

    private void initView(){
        TextView tvCopyright = (TextView) findViewById(R.id.tv_copyright);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        tvCopyright.setText("Copyright © 2017-" + year);
        final Intent intent = new Intent(SplashActivity.this, SearchActivity.class);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 2000);//2秒后执行TimeTask的run方法
    }


}
