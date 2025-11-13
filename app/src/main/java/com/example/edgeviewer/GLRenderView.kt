package com.example.edgeviewer

import android.content.Context
import android.opengl.GLSurfaceView

class GLRenderView(context: Context) : GLSurfaceView(context) {

    val renderer: GLRenderer

    init {
        setEGLContextClientVersion(2)
        renderer = GLRenderer(context)
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY
    }
}
