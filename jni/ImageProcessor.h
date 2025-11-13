#ifndef IMAGE_PROCESSOR_H
#define IMAGE_PROCESSOR_H

#include <opencv2/opencv.hpp>

class ImageProcessor {
public:
    static void processFrame(
        const uint8_t* yuvData,
        int width,
        int height,
        uint8_t* outRGBA
    ) {
        cv::Mat yuv(height + height/2, width, CV_8UC1, (void*)yuvData);

        cv::Mat rgba;
        cv::cvtColor(yuv, rgba, cv::COLOR_YUV420_8882RGBA);

        cv::Mat edges;
        cv::Canny(rgba, edges, 80, 150);

        cv::Mat edgesColor;
        cv::cvtColor(edges, edgesColor, cv::COLOR_GRAY2RGBA);

        memcpy(outRGBA, edgesColor.data, width * height * 4);
    }
};

#endif
