package com.example.mrapp

import android.content.Context
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import java.util.concurrent.Executors

class HandTrackingHelper(
    private val context: Context,
    private val overlayView: HandOverlayView,   // <-- было HandTrackingHelper.OverlayView
    private val onResult: (HandLandmarkerResult) -> Unit
) {
    private var handLandmarker: HandLandmarker? = null
    private val executor = Executors.newSingleThreadExecutor()

    fun initialize() {
        val baseOptions = com.google.mediapipe.tasks.core.BaseOptions.builder()
            .setModelAssetPath("hand_landmarker.task")
            .build()
        val options = HandLandmarker.HandLandmarkerOptions.builder()
            .setBaseOptions(baseOptions)
            .setNumHands(2)
            .setRunningMode(com.google.mediapipe.tasks.vision.core.RunningMode.LIVE_STREAM)
            .setResultListener { result, _ ->
                overlayView.setResults(result)
                onResult(result)
            }
            .setErrorListener { error -> error.printStackTrace() }
            .build()
        handLandmarker = HandLandmarker.createFromOptions(context, options)
    }

    fun processFrame(imageProxy: ImageProxy) {
        val bitmap = imageProxy.toBitmap()
        val mpImage = BitmapImageBuilder(bitmap).build()
        handLandmarker?.detectAsync(mpImage, System.currentTimeMillis())
        imageProxy.close()
    }
}
