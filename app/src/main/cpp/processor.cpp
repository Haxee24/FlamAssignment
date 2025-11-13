#include "processor.h"
#include <android/log.h>

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "EdgeAI", __VA_ARGS__)

Processor::Processor() {
    LOGI("Processor created");
}

Processor::~Processor() {
    LOGI("Processor destroyed");
}

void Processor::processFrame(uint8_t* data, int width, int height) {
    // Placeholder for edge processing
    // Currently no-op
    LOGI("Processing frame %dx%d", width, height);
}
