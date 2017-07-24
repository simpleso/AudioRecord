package com.andnux.audiorecord;

/**
 * Created by Shenbin on 17/7/23.
 */

public class LameNative {

    static {
        System.loadLibrary("lame");
    }

    public static native void convert(String input, String output);

    public static native void init(int samplerate,int channels,int brate,int quality);

    public static  native  void destroy();

    public static  native  byte[] encode(short []lbuffer,short[]rbuffer,int len);

    public static  native  String getVersion();

}
