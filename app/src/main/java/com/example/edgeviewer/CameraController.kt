package com.example.edgeviewer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.os.Handler
import android.os.HandlerThread
import android.util.Size
import android.view.Surface

class CameraController(
    private val activity: Activity,
    private val surfaceTexture: SurfaceTexture
) {

    private lateinit var cameraDevice: CameraDevice
    private lateinit var captureSession: CameraCaptureSession

    private var cameraThread: HandlerThread = HandlerThread("CameraThread")
    private lateinit var cameraHandler: Handler

    private val cameraManager: CameraManager by lazy {
        activity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    private val cameraId: String by lazy {
        cameraManager.cameraIdList[0]     // Use back camera
    }

    @SuppressLint("MissingPermission")
    fun start() {
        cameraThread.start()
        cameraHandler = Handler(cameraThread.looper)

        cameraManager.openCamera(
            cameraId,
            object : CameraDevice.StateCallback() {
                override fun onOpened(device: CameraDevice) {
                    cameraDevice = device
                    startPreview()
                }
                override fun onDisconnected(device: CameraDevice) {
                    device.close()
                }
                override fun onError(device: CameraDevice, error: Int) {
                    device.close()
                }
            },
            cameraHandler
        )
    }

    private fun startPreview() {
        val texture = surfaceTexture
        texture.setDefaultBufferSize(1280, 720)

        val surface = Surface(texture)

        val previewRequest = cameraDevice.createCaptureRequest(
            CameraDevice.TEMPLATE_PREVIEW
        ).apply {
            addTarget(surface)
            set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
        }

        cameraDevice.createCaptureSession(
            listOf(surface),
            object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    captureSession = session
                    captureSession.setRepeatingRequest(
                        previewRequest.build(),
                        null,
                        cameraHandler
                    )
                }
                override fun onConfigureFailed(session: CameraCaptureSession) { }
            },
            cameraHandler
        )
    }

    fun stop() {
        try {
            captureSession.close()
        } catch (_: Exception) {}

        try {
            cameraDevice.close()
        } catch (_: Exception) {}

        cameraThread.quitSafely()
    }
}
