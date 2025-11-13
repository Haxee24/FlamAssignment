package com.example.edgeviewer

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import java.nio.ByteBuffer
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.egl.EGLConfig

class GLRenderer(private val ctx: Context) : GLSurfaceView.Renderer {

    private var program = 0
    private var textureId = 0

    private var frameBuffer: ByteArray? = null
    private var frameWidth = 0
    private var frameHeight = 0
    @Volatile private var hasNewFrame = false

    override fun onSurfaceCreated(unused: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)

        val vs = ctx.resources.openRawResource(R.raw.vertex_shader)
            .bufferedReader().readText()
        val fs = ctx.resources.openRawResource(R.raw.fragment_shader)
            .bufferedReader().readText()

        program = ShaderUtils.createProgram(vs, fs)
        GLES20.glUseProgram(program)

        textureId = createTexture()
    }

    override fun onSurfaceChanged(unused: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(unused: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        // 🔥 Update texture if a new processed frame is ready
        if (hasNewFrame && frameBuffer != null) {
            updateTexture(frameBuffer!!, frameWidth, frameHeight)
            hasNewFrame = false
        }

        drawQuad()
    }

    private fun updateTexture(rgba: ByteArray, width: Int, height: Int) {
        val bb = ByteBuffer.wrap(rgba)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D,
            0,
            GLES20.GL_RGBA,
            width,
            height,
            0,
            GLES20.GL_RGBA,
            GLES20.GL_UNSIGNED_BYTE,
            bb
        )
    }

    fun updateFrame(rgba: ByteArray, width: Int, height: Int) {
        this.frameBuffer = rgba
        this.frameWidth = width
        this.frameHeight = height
        this.hasNewFrame = true
    }

    /* createTexture(), drawQuad() stay same */
}
