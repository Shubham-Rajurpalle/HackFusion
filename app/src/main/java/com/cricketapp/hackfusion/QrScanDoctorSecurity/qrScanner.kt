package com.cricketapp.hackfusion.QrScanDoctorSecurity
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.cricketapp.hackfusion.LogInSignIn.signUp
import com.cricketapp.hackfusion.databinding.ActivityQrScannerBinding
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class qrScanner : AppCompatActivity() {

    private lateinit var binding: ActivityQrScannerBinding
    private lateinit var cameraExecutor: ExecutorService
    private var cameraProvider: ProcessCameraProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup View Binding
        binding = ActivityQrScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Camera Executor
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Check Permission & Start Camera
        requestCameraPermission()

        // Set Click Listener to Restart Camera
        binding.btnScan.setOnClickListener {
            Log.d("QRScanner", "Scan button clicked. Starting camera...")
            startCamera()
        }
    }

    private fun requestCameraPermission() {
        val permission = Manifest.permission.CAMERA
        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("QRScanner", "Camera permission granted. Starting camera...")
                startCamera()
            }
            else -> {
                Log.d("QRScanner", "Requesting camera permission...")
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("QRScanner", "Permission granted by user. Starting camera...")
            startCamera()
        } else {
            Log.e("QRScanner", "Camera Permission Denied")
            Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            binding.previewView.visibility = View.VISIBLE
            val preview = Preview.Builder()
                .build()
                .also { it.setSurfaceProvider(binding.previewView.surfaceProvider) }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { it.setAnalyzer(cameraExecutor) { processImage(it) } }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer
                )
                Log.d("QRScanner", "Camera started successfully!")
            } catch (exc: Exception) {
                Log.e("QRScanner", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun processImage(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val scanner = BarcodeScanning.getClient()

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        handleQRCode(barcode)
                    }
                }
                .addOnFailureListener { Log.e("QRScanner", "QR Code scanning failed", it) }
                .addOnCompleteListener { imageProxy.close() } // **Fix: Close ImageProxy**
        } else {
            imageProxy.close()
        }
    }

    private fun handleQRCode(barcode: Barcode) {
        val qrCodeValue = barcode.displayValue
        if (!qrCodeValue.isNullOrEmpty()) {
            runOnUiThread {
                binding.txtResult.text = "Scanned: $qrCodeValue"
                Toast.makeText(this, "Scanned: $qrCodeValue", Toast.LENGTH_LONG).show()

                Log.d("QRScanner", "Scanned QR Code: $qrCodeValue")

                // **STOP THE CAMERA**
                stopCamera()

                // Start SignUpActivity
                val intent = Intent(this, signUp::class.java)
                intent.putExtra("QR_CODE_DATA", qrCodeValue) // Pass QR data if needed
                startActivity(intent)
                finish() // Close the scanner activity
            }
        }
    }
    private fun stopCamera() {
        cameraProvider?.unbindAll() // Unbind all camera use cases
        cameraExecutor.shutdown()   // Shut down the executor
        Log.d("QRScanner", "Camera stopped.")
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        cameraProvider?.unbindAll()
    }
}