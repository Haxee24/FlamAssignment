package com.example.edgeviewer

import android.os.Bundle
import android.view.TextureView
import androidx.appcompat.app.AppCompatActivity
import com.example.edgeviewer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), TextureView.SurfaceTextureListener {

    private lateinit var bind: ActivityMainBinding
    private var cameraController: CameraController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)

        bind.cameraTexture.surfaceTextureListener = this
    }

    override fun onSurfaceTextureAvailable(surface: android.graphics.SurfaceTexture, width: Int, height: Int) {
        cameraController = CameraController(this, surface)
        cameraController?.start()
    }

    override fun onSurfaceTextureSizeChanged(surface: android.graphics.SurfaceTexture, width: Int, height: Int) {}
    override fun onSurfaceTextureDestroyed(surface: android.graphics.SurfaceTexture): Boolean {
        cameraController?.stop()
        return true
    }
    override fun onSurfaceTextureUpdated(surface: android.graphics.SurfaceTexture) {}
}
