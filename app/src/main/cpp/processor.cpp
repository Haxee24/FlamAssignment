#include "processor.h"
#include <android/log.h>
#include <cstring>

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "EdgeProcessor", __VA_ARGS__)

Processor::Processor() {
    LOGI("Processor ctor");
}

Processor::~Processor() {
    LOGI("Processor dtor");
}

// input: pointer to Y-plane data with size width*height
// out: pointer to single-channel output (width*height)
void Processor::processFrame(uint8_t* input, uint8_t* out, int width, int height) {
    int size = width * height;
    // simple copy for now (Y-plane -> output)
    memcpy(out, input, size);
    // FUTURE: apply Canny/OpenCV here
}
