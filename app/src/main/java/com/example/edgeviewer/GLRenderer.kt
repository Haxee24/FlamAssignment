package com.example.edgeviewer

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import java.nio.ByteBuffer
import java.nio.FloatBuffer
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

    private var vertexBuffer: FloatBuffer = BufferUtils.floatToBuffer(vertices)
    private var texBuffer: FloatBuffer = BufferUtils.floatToBuffer(texCoords)

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
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)

        // allocate a tiny placeholder 2x2 LUMINANCE image so texture is valid
        val empty = ByteArray(4) // 2x2
        val bb = ByteBuffer.allocateDirect(empty.size)
        bb.put(empty)
        bb.position(0)
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, 2, 2, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, bb)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        return id
    }

    /**
     * Called on GL thread. buffer must be direct and contain width*height bytes (single channel).
     */
    fun updateTexture(buffer: ByteBuffer, width: Int, height: Int) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)

        // Ensure buffer position is correct
        buffer.position(0)

        // Upload single-channel luminance bytes
        GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1)
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, width, height, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, buffer)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
    }

    private fun drawQuad() {
        val posLoc = GLES20.glGetAttribLocation(program, "a_Position")
        val texLoc = GLES20.glGetAttribLocation(program, "a_TexCoord")
        val samplerLoc = GLES20.glGetUniformLocation(program, "u_Texture")

        GLES20.glEnableVertexAttribArray(posLoc)
        GLES20.glVertexAttribPointer(posLoc, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        GLES20.glEnableVertexAttribArray(texLoc)
        GLES20.glVertexAttribPointer(texLoc, 2, GLES20.GL_FLOAT, false, 0, texBuffer)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glUniform1i(samplerLoc, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        GLES20.glDisableVertexAttribArray(posLoc)
        GLES20.glDisableVertexAttribArray(texLoc)
    }
}
