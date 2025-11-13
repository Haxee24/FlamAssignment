#pragma once
#include <vector>

class Processor {
public:
    Processor();
    ~Processor();

    // Process a frame (RGBA)
    void processFrame(uint8_t* data, int width, int height);

private:
    // Future: TensorFlow Lite / ONNX / custom model
};
