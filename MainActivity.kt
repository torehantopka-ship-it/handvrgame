package com.example.mrapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import io.github.sceneview.SceneView

class MainActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var sceneView: SceneView
    private lateinit var overlayView: HandOverlayView
    private lateinit var cameraManager: CameraManager
    private lateinit var handTrackingHelper: HandTrackingHelper
    private var isStereoMode = false

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) startCameraAndTracking()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        previewView = findViewById(R.id.previewView)
        sceneView = findViewById(R.id.sceneView)
        overlayView = findViewById(R.id.overlayView)
        val btnReset: Button = findViewById(R.id.btnReset)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startCameraAndTracking()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        btnReset.setOnClickListener {
            // Logic to reset physics objects (handled in VRRenderer / SceneView)
        }
    }

    private fun startCameraAndTracking() {
        cameraManager = CameraManager(this, previewView) { frame ->
            handTrackingHelper.processFrame(frame)
        }
        cameraManager.start()

        handTrackingHelper = HandTrackingHelper(this, overlayView) { result ->
            // Pass landmarks to 3D renderer for physics interaction
            // e.g., sceneView.updateHandPositions(result)
        }
        handTrackingHelper.initialize()
    }
}
