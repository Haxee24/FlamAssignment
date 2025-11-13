package com.example.edgeviewer

import android.util.Log

object EdgePipeline {

    fun processFrame(y: ByteArray, u: ByteArray, v: ByteArray, width: Int, height: Int): ByteArray {
        val yuv = ByteArray(width * height * 3 / 2)

        System.arraycopy(y, 0, yuv, 0, y.size)
        System.arraycopy(u, 0, yuv, y.size, u.size)
        System.arraycopy(v, 0, yuv, y.size + u.size, v.size)

        val outRGBA = ByteArray(width * height * 4)

        NativeBridge.processFrame(yuv, width, height, outRGBA)

        return outRGBA
    }
}
