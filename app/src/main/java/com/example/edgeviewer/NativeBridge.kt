package com.example.edgeviewer

class NativeBridge {

    external fun initNative()
    external fun processFrame(inputBuffer: java.nio.ByteBuffer, outputBuffer: java.nio.ByteBuffer, width: Int, height: Int)
    external fun releaseNative()

    companion object {
        init {
            System.loadLibrary("edge_native")
        }
    }
}
