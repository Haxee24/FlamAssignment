#include <jni.h>
#include <android/log.h>
#include "ImageProcessor.h"

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "JNI", __VA_ARGS__)

extern "C" {

JNIEXPORT void JNICALL
Java_com_example_edgeviewer_NativeBridge_processFrame(
        JNIEnv* env,
        jclass,
        jbyteArray yuvArr,
        jint width,
        jint height,
        jbyteArray outArr
) {
    jbyte* yuvData = env->GetByteArrayElements(yuvArr, nullptr);
    jbyte* outData = env->GetByteArrayElements(outArr, nullptr);

    ImageProcessor::processFrame(
        reinterpret_cast<uint8_t*>(yuvData),
        width,
        height,
        reinterpret_cast<uint8_t*>(outData)
    );

    env->ReleaseByteArrayElements(yuvArr, yuvData, JNI_ABORT);
    env->ReleaseByteArrayElements(outArr, outData, 0);
}
}
