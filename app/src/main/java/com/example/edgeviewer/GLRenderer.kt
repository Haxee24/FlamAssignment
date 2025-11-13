package com.example.edgeviewer

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.graphics.Bitmap
import android.graphics.BitmapFactory

import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.egl.EGLConfig

class GLRenderer(private val ctx: Context) : GLSurfaceView.Renderer {

    private var program = 0
    private var textureId = 0

    private val vertices = floatArrayOf(
        -1f, -1f,
         1f, -1f,
        -1f,  1f,
         1f,  1f
    )

    private val texCoords = floatArrayOf(
        0f, 1f,
        1f, 1f,
        0f, 0f,
        1f, 0f
    )

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
        drawQuad()
    }

    private fun createTexture(): Int {
        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        val id = textures[0]

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

        // TEMP: load placeholder image from mipmap for rendering
        val bmp = BitmapFactory.decodeResource(ctx.resources, android.R.drawable.ic_menu_gallery)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0)
        bmp.recycle()

        return id
    }

    private fun drawQuad() {
        val posLoc = GLES20.glGetAttribLocation(program, "a_Position")
        val texLoc = GLES20.glGetAttribLocation(program, "a_TexCoord")
        val samplerLoc = GLES20.glGetUniformLocation(program, "u_Texture")

        GLES20.glEnableVertexAttribArray(posLoc)
        GLES20.glVertexAttribPointer(posLoc, 2, GLES20.GL_FLOAT, false, 0, BufferUtils.floatToBuffer(vertices))

        GLES20.glEnableVertexAttribArray(texLoc)
        GLES20.glVertexAttribPointer(texLoc, 2, GLES20.GL_FLOAT, false, 0, BufferUtils.floatToBuffer(texCoords))

        GLES20.glUniform1i(samplerLoc, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
    }
}
