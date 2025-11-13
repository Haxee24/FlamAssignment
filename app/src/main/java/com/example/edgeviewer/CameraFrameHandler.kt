package com.example.edgeviewer

import android.media.Image
import android.util.Log

class CameraFrameHandler(private val onFrameReady: (ByteArray, Int, Int) -> Unit) {

    fun process(image: Image) {
        val width = image.width
        val height = image.height

        val y = image.planes[0].buffer.toByteArray()
        val u = image.planes[1].buffer.toByteArray()
        val v = image.planes[2].buffer.toByteArray()

        val rgba = EdgePipeline.processFrame(y, u, v, width, height)

        onFrameReady(rgba, width, height)
    }

    private fun java.nio.ByteBuffer.toByteArray(): ByteArray {
        val byteArray = ByteArray(remaining())
        get(byteArray)
        return byteArray
    }
}
