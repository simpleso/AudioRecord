package com.andnux.audiorecord;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.File;

public class MainActivity extends AppCompatActivity implements SuperAudioRecord.SuperAudioRecordListener {

    private  SuperAudioRecord mSuperAudioRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSuperAudioRecord = new SuperAudioRecord();
        mSuperAudioRecord.setFileName(Environment.getExternalStorageDirectory()+ File.separator + System.currentTimeMillis()+".mp3");
        mSuperAudioRecord.setSuperAudioRecordListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void start(View view) {
        mSuperAudioRecord.start();
    }

    public void pause(View view) {
        mSuperAudioRecord.pause();
    }

    public void stop(View view) {
        mSuperAudioRecord.stop();
    }

    @Override
    public void initError() {

    }

    @Override
    public void recordErroor() {

    }

    @Override
    public void onSuccess(String path) {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(path));
        intent.setDataAndType(uri,"audio/*");
        startActivity(intent);
    }
}
