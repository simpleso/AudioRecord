#include <jni.h>
#include <stdint.h>
#include <malloc.h>
#include "lame/lame.h"

JNIEXPORT void JNICALL
Java_com_andnux_audiorecord_LameNative_convert(JNIEnv *env, jclass instance, jstring input_,
                                               jstring output_) {
    const char *inputfile = (*env)->GetStringUTFChars(env, input_, 0);
    const char *outputfile = (*env)->GetStringUTFChars(env, output_, 0);

    FILE *pcm = fopen(inputfile, "rb");
    FILE *mp3 = fopen(outputfile, "wb");
    int read, write;
    const int PCM_SIZE = 8192;
    const int MP3_SIZE = 8192;
    short int pcm_buffer[PCM_SIZE*2];
    unsigned char mp3_buffer[MP3_SIZE];
    //初始化lame解码器
    lame_t lame = lame_init();
    // 设置音频的采样率
    lame_set_in_samplerate(lame, 44100);
    // 设置lame编码器的声道
    lame_set_num_channels(lame,2);
    lame_set_quality(lame,5);   //中等的
    //初始化lame的编码器
    lame_init_params(lame);
    do {
        read = fread(pcm_buffer, 2*sizeof(short int), PCM_SIZE, pcm);
        if (read == 0)
            write = lame_encode_flush(lame, mp3_buffer, MP3_SIZE);
        else
            write = lame_encode_buffer_interleaved(lame, pcm_buffer, read, mp3_buffer, MP3_SIZE);
        fwrite(mp3_buffer, 1, write, mp3);
    } while (read != 0);
    lame_close(lame);
    fclose(mp3);
    fclose(pcm);
    (*env)->ReleaseStringUTFChars(env, input_, inputfile);
    (*env)->ReleaseStringUTFChars(env, output_, outputfile);
}

lame_t *lame;

JNIEXPORT void JNICALL
Java_com_andnux_audiorecord_LameNative_init(JNIEnv *env, jclass type, jint samplerate,
                                            jint channels,jint brate,jint quality) {
    lame = lame_init();
    // 设置音频的采样率
    lame_set_in_samplerate(lame, samplerate);
    // 设置lame编码器的声道
    lame_set_num_channels(lame,channels);
    lame_set_quality(lame,quality);   //中等的
    lame_set_brate(lame,brate);
    lame_init_params(lame);
}

JNIEXPORT void JNICALL
Java_com_andnux_audiorecord_LameNative_destroy(JNIEnv *env, jclass type) {

  if (lame != NULL){
      lame_close(lame);
  }
}

JNIEXPORT jbyteArray JNICALL
Java_com_andnux_audiorecord_LameNative_encode(JNIEnv *env, jclass type, jshortArray buffer_l,jshort buffer_r,
                                              jint len) {
    jshort *lbuffer = (*env)->GetShortArrayElements(env, buffer_l, NULL);
    jshort *rbuffer = (*env)->GetShortArrayElements(env, buffer_r, NULL);
    const int MP3_SIZE = 8192;
    unsigned char mp3_buffer[MP3_SIZE];
    const  int  nsamples = len / 2;
    int size = lame_encode_buffer(lame,lbuffer,rbuffer,nsamples,mp3_buffer,MP3_SIZE);
    lame_encode_flush(lame,mp3_buffer,size);
    (*env)->ReleaseShortArrayElements(env, buffer_l, lbuffer, 0);
    (*env)->ReleaseShortArrayElements(env, buffer_r, rbuffer, 0);
}

JNIEXPORT jstring JNICALL
Java_com_andnux_audiorecord_LameNative_getVersion(JNIEnv *env, jclass type) {
    return (*env)->NewStringUTF(env, get_lame_version());
}

