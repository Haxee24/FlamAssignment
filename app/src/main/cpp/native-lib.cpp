#include <jni.h>
#include <android/log.h>
#include "processor.h"

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "EdgeNative", __VA_ARGS__)

static Processor* processor = nullptr;

extern "C"
JNIEXPORT void JNICALL
Java_com_example_edgeviewer_NativeBridge_initNative(JNIEnv* env, jobject /* this */) {
    if (!processor) {
        processor = new Processor();
        LOGI("Processor created from JNI");
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_edgeviewer_NativeBridge_processFrame(
        JNIEnv* env,
        jobject /* this */,
        jobject inputBuffer,
        jobject outputBuffer,
        jint width,
        jint height
) {
    if (!processor) return;
    if (inputBuffer == nullptr || outputBuffer == nullptr) return;

    uint8_t* in = reinterpret_cast<uint8_t*>(env->GetDirectBufferAddress(inputBuffer));
    uint8_t* out = reinterpret_cast<uint8_t*>(env->GetDirectBufferAddress(outputBuffer));
    if (!in || !out) return;

    // delegate to processor
    processor->processFrame(in, out, width, height);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_edgeviewer_NativeBridge_releaseNative(JNIEnv* env, jobject /* this */) {
    if (processor) {
        delete processor;
        processor = nullptr;
        LOGI("Processor released from JNI");
    }
}

