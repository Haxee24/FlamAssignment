package com.example.edgeviewer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.ImageFormat
import android.media.Image
import android.media.ImageReader
import android.hardware.camera2.*
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import java.nio.ByteBuffer

class CameraController(
    private val activity: Activity,
    private val glView: GLRenderView,
    private val width: Int = 640,
    private val height: Int = 480,
) {
    private val TAG = "CameraController"

    private val cameraManager: CameraManager by lazy {
        activity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    private val cameraId: String by lazy {
        // pick back-facing camera, fallback to first
        cameraManager.cameraIdList.firstOrNull { id ->
            val desc = cameraManager.getCameraCharacteristics(id)
            val facing = desc[CameraCharacteristics.LENS_FACING]
            facing == CameraCharacteristics.LENS_FACING_BACK
        } ?: cameraManager.cameraIdList[0]
    }

    private var cameraDevice: CameraDevice? = null
    private var session: CameraCaptureSession? = null

    private val cameraThread = HandlerThread("CameraThread").apply { start() }
    private val cameraHandler = Handler(cameraThread.looper)

    private val reader: ImageReader = ImageReader.newInstance(width, height, ImageFormat.YUV_420_888, 2)
    private val nativeBridge = NativeBridge()

    // Input buffer holds YUV Y-plane copy (we'll copy Y-plane only to keep it simple)
    private val inputBuffer: ByteBuffer = ByteBuffer.allocateDirect(width * height)
    // Output buffer holds processed 1-byte per pixel image (width*height)
    private val outputBuffer: ByteBuffer = ByteBuffer.allocateDirect(width * height)

    @SuppressLint("MissingPermission")
    fun start() {
        nativeBridge.initNative()

        reader.setOnImageAvailableListener({ r ->
            var img: Image? = null
            try {
                img = r.acquireLatestImage() ?: return@setOnImageAvailableListener

                // Extract Y plane
                val planes = img.planes
                val yPlane = planes[0]
                val yBuffer = yPlane.buffer
                val rowStride = yPlane.rowStride
                val pixelStride = yPlane.pixelStride // usually 1 for Y plane

                inputBuffer.rewind()
                // If rowStride == width and pixelStride == 1 we can copy bulk
                if (rowStride == width && pixelStride == 1) {
                    // bulk copy
                    val len = width * height
                    if (yBuffer.remaining() >= len) {
                        val tmp = ByteArray(len)
                        yBuffer.get(tmp, 0, len)
                        inputBuffer.put(tmp)
                    } else {
                        // fallback safer per-row copy
                        for (row in 0 until height) {
                            val rowBytes = ByteArray(width)
                            yBuffer.get(rowBytes, 0, width)
                            inputBuffer.put(rowBytes)
                            // skip padding if any
                            val skip = rowStride - width
                            if (skip > 0) {
                                yBuffer.position(yBuffer.position() + skip)
                            }
                        }
                    }
                } else {
                    // rowStride has padding — copy row by row
                    val rowBytes = ByteArray(width)
                    val tmpRow = ByteArray(rowStride)
                    yBuffer.rewind()
                    for (row in 0 until height) {
                        yBuffer.get(tmpRow, 0, rowStride)
                        System.arraycopy(tmpRow, 0, rowBytes, 0, width)
                        inputBuffer.put(rowBytes)
                    }
                }

                inputBuffer.rewind()
                outputBuffer.rewind()

                // Call native processor (inplace outputBuffer will be filled)
                nativeBridge.processFrame(inputBuffer, outputBuffer, width, height)

                // Send processed bytes to GL on GL thread
                outputBuffer.rewind()
                // make a slice to avoid disturbing position
                val slice = outputBuffer.slice()
                glView.queueTextureUpdate(slice, width, height)

            } catch (e: Exception) {
                Log.e(TAG, "Image handling error", e)
            } finally {
                img?.close()
            }
        }, cameraHandler)

        // Open camera
        cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
            override fun onOpened(device: CameraDevice) {
                cameraDevice = device
                startCaptureSession()
            }

            override fun onDisconnected(device: CameraDevice) {
                device.close()
            }

            override fun onError(device: CameraDevice, error: Int) {
                device.close()
            }
        }, cameraHandler)
    }

    private fun startCaptureSession() {
        val device = cameraDevice ?: return
        val surface = reader.surface

        val reqBuilder = device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        reqBuilder.addTarget(surface)
        reqBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)

        device.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(session: CameraCaptureSession) {
                this@CameraController.session = session
                try {
                    session.setRepeatingRequest(reqBuilder.build(), null, cameraHandler)
                } catch (ex: CameraAccessException) {
                    // ignore
                }
            }

            override fun onConfigureFailed(session: CameraCaptureSession) {
                // handle
            }
        }, cameraHandler)
    }

    fun stop() {
        try {
            session?.close()
        } catch (_: Exception) {}
        try {
            cameraDevice?.close()
        } catch (_: Exception) {}
        try {
            reader.close()
        } catch (_: Exception) {}
        nativeBridge.releaseNative()
        cameraThread.quitSafely()
    }
}
