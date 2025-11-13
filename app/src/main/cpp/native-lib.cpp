#include <jni.h>
#include "processor.h"

static Processor* processor = nullptr;

extern "C" JNIEXPORT void JNICALL
Java_com_example_edgeviewer_NativeBridge_initNative(JNIEnv*, jobject) {
    if (!processor)
        processor = new Processor();
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_edgeviewer_NativeBridge_processFrame(
        JNIEnv* env,
        jobject,
        jobject buffer,
        jint width,
        jint height
) {
    if (!processor) return;

    uint8_t* bytes = (uint8_t*) env->GetDirectBufferAddress(buffer);
    processor->processFrame(bytes, width, height);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_edgeviewer_NativeBridge_releaseNative(JNIEnv*, jobject) {
    if (processor) {
        delete processor;
        processor = nullptr;
    }
}
