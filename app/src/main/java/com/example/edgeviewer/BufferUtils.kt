package com.example.edgeviewer

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

object BufferUtils {
    fun floatToBuffer(array: FloatArray): FloatBuffer {
        val bb = ByteBuffer.allocateDirect(array.size * 4)
        bb.order(ByteOrder.nativeOrder())
        val fb = bb.asFloatBuffer()
        fb.put(array)
        fb.position(0)
        return fb
    }
}
