package com.example.edgeviewer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var glView: GLRenderView
    private var cameraController: CameraController? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            // permission denied - show message or close
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        glView = GLRenderView(this)
        setContentView(glView)

        // Check camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            startCamera()
        }
    }

    private fun startCamera() {
        cameraController = CameraController(this, glView, 640, 480)
        cameraController?.start()
    }

    override fun onPause() {
        super.onPause()
        cameraController?.stop()
        glView.onPause()
    }

    override fun onResume() {
        super.onResume()
        glView.onResume()
    }
}
