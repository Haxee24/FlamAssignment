package com.example.edgeviewer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    private lateinit var glView: GLRenderView
    private lateinit var cameraHandler: CameraHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        glView = GLRenderView(this)
        setContentView(glView)

        cameraHandler = CameraHandler(this) { image ->
            frameProcessor.process(image)
        }

        frameProcessor = CameraFrameHandler { rgba, w, h ->
            glView.updateFrame(rgba, w, h)
        }
    }

    override fun onResume() {
        super.onResume()
        glView.onResume()
        cameraHandler.start()
    }

    override fun onPause() {
        super.onPause()
        cameraHandler.stop()
        glView.onPause()
    }
}
