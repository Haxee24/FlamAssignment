package com.example.edgeviewer

import android.content.Context
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer

class GLRenderView(context: Context) : GLSurfaceView(context) {

    val renderer: GLRenderer

    init {
        setEGLContextClientVersion(2)
        renderer = GLRenderer(context)
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    /**
     * Thread-safe helper: queue a texture update on the GL thread.
     * The provided ByteBuffer should be a direct buffer slice with remaining() == width*height.
     */
    fun queueTextureUpdate(buffer: ByteBuffer, width: Int, height: Int) {
        queueEvent {
            renderer.updateTexture(buffer, width, height)
        }
    }
}
