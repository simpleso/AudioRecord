LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := lame
LOCAL_CLFAGS := -std=c99
LOCAL_LDLIBS := -llog

LOCAL_SRC_FILES := lame/VbrTag.c  \
                   lame/fft.c  \
                   lame/lame.c \
                   lame/presets.c  \
                   lame/quantize_pvt.c  \
                   lame/tables.c  \
                   lame/vbrquantize.c \
                   lame/bitstream.c  \
                   lame/gain_analysis.c  \
                   lame/mpglib_interface.c \
                   lame/psymodel.c   \
                   lame/reservoir.c   \
                   lame/takehiro.c   \
                   lame/version.c	\
                   lame/encoder.c   \
                   lame/id3tag.c  \
                   lame/newmdct.c  \
                   lame/quantize.c \
                   lame/set_get.c \
                   lame/util.c \
                   lame.c

include $(BUILD_SHARED_LIBRARY)