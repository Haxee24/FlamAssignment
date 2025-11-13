package com.example.edgeviewer

object NativeBridge {

    init {
        System.loadLibrary("edgeprocessor")
    }

    external fun processFrame(
        yuvData: ByteArray,
        width: Int,
        height: Int,
        outRGBA: ByteArray
    )
}
