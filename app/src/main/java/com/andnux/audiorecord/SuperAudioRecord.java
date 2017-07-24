package com.andnux.audiorecord;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by Shenbin on 17/4/30.
 */

public class SuperAudioRecord {

    private static final int STATE_RECORFING = 1; //正在录音
    private static final int STATE_PAUSE = 2; //暂停录音
    private static final int STATE_STOP = 3; //停止录音
    public static final int MAX_BUFFER = 2048;
    private ExecutorService mExecutorService;
    private String mFileName;
    private String mTempFeleName;
    private File mTempFele;
    private byte [] mBuffer;
    private AudioRecord mAudioRecord;
    private SuperAudioRecordListener mListener;
    private volatile int state; //录音状态
    private Handler mHandler;

    interface  SuperAudioRecordListener{
        void  initError();
        void  recordErroor();
        void  onSuccess(String path);
    }

    public void setSuperAudioRecordListener(SuperAudioRecordListener listener) {
        mListener = listener;
    }

    public SuperAudioRecord() {
        mHandler = new Handler(Looper.getMainLooper());
        mBuffer = new byte[MAX_BUFFER];
        mTempFeleName = Environment.getExternalStorageDirectory()+File.separator+"temp.pcm";
        mExecutorService = Executors.newSingleThreadExecutor();
        try {
            mTempFele = new File(mTempFeleName);
            if (mTempFele.exists()){
                mTempFele.delete();
            }
            mTempFele.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String fileName) {
        mFileName = fileName;
    }


    public void start() {
        Log.e("andnux","开始录音");
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                RandomAccessFile file = null;
                try {
                    file = new RandomAccessFile(mTempFeleName,"rwd");
                    file.seek(file.length());
                    int audioSource = MediaRecorder.AudioSource.MIC;
                    int sampleRateInHz = 44100;
                    int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
                    int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
                    int bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
                    mAudioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);
                    if (mAudioRecord.getState() == AudioRecord.STATE_UNINITIALIZED) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mListener!=null){
                                    mListener.initError();
                                }
                            }
                        });
                        return;
                    }
                    mAudioRecord.startRecording();
                    state = STATE_RECORFING;
                    while (state == STATE_RECORFING){
                            int read = mAudioRecord.read(mBuffer,0,MAX_BUFFER);
                            if (read > 0){
                                file.write(mBuffer,0,read);
                            }else {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mListener!=null){
                                            mListener.recordErroor();
                                        }
                                    }
                                });
                            }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if (file!=null){
                        try {
                            file.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (mAudioRecord !=null){
                        mAudioRecord.release();
                    }
                }
            }
        });
    }

    public void pause() {
        if (state == STATE_RECORFING){
            state = STATE_PAUSE;
            Log.e("andnux","暂停");
            mAudioRecord.stop();
        }
    }

    public void stop() {
        if (state == STATE_RECORFING){
            mAudioRecord.stop();
        }
        state = STATE_STOP;
        Log.e("andnux","停止录音");
        mExecutorService.execute(new Runnable() {
           @Override
           public void run() {
               try {
                  LameNative.convert(mTempFeleName,mFileName);
                   new File(mTempFeleName).delete();
                   mHandler.post(new Runnable() {
                       @Override
                       public void run() {
                           if (mListener!=null){
                               mListener.onSuccess(mFileName);
                           }
                       }
                   });
               } catch (Exception e) {
                   e.printStackTrace();
               }
           }
        });
    }

    public  void destroy(){
        mExecutorService.shutdown();
    }
}
