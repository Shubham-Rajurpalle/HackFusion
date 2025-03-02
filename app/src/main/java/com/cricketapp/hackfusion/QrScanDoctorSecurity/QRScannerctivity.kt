package com.cricketapp.hackfusion.QrScanDoctorSecurity
import org.json.JSONObject
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
import com.cricketapp.hackfusion.ConductVoilate.FillCaughtDetails
import com.cricketapp.hackfusion.databinding.ActivityQrScannerBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QRScannerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQrScannerBinding
    private lateinit var cameraExecutor: ExecutorService
    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQrScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        cameraExecutor = Executors.newSingleThreadExecutor()

        requestCameraPermission()

        binding.btnScan.setOnClickListener {
            startCamera()
        }
    }

    private fun requestCameraPermission() {
        val permission = Manifest.permission.CAMERA
        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                startCamera()
            }
            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startCamera()
        } else {
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
                .addOnCompleteListener { imageProxy.close() }
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

                // Stop Camera
                stopCamera()

                // Navigate to FillCaughtDetailsActivity
                val intent = Intent(this, FillCaughtDetails::class.java)
                intent.putExtra("QR_CODE_DATA", qrCodeValue) // Pass QR data
                startActivity(intent)
                finish()
            }
        }
    }

//    private fun checkUserRole(studentId: String) {
//        db.collection("users").document(studentId)
//            .get()
//            .addOnSuccessListener { document ->
//                if (document.exists()) {
//                    val role = document.getString("role")
//                    Log.d("Firestore", "User Role: $role")
//                    if (role == "Student") {
//                        saveToFirestore(studentId)
//                    } else {
//                        Toast.makeText(this, "Only students can be caught!", Toast.LENGTH_SHORT).show()
//                    }
//                } else {
//                    Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show()
//                }
//            }
//            .addOnFailureListener { e ->
//                Log.e("Firestore", "Error fetching user role: ${e.message}", e)
//                Toast.makeText(this, "Error fetching user role: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//    }

    private fun saveToFirestore(studentDataJson: JSONObject) {
        val studentData = mapOf(
            "studentId" to studentDataJson.getString("reg_no"),
            "name" to studentDataJson.getString("name"),
            "role" to studentDataJson.getString("role"),
            "self_phone" to studentDataJson.getString("self_phone"),
            "self_mail" to studentDataJson.getString("self_mail"),
            "parent_mail" to studentDataJson.getString("parent_mail"),
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("studentsCaught")
            .add(studentData)
            .addOnSuccessListener {
                Log.d("Firestore", "Student added successfully: ${studentDataJson.getString("reg_no")}")
                Toast.makeText(this, "Student caught successfully!", Toast.LENGTH_SHORT).show()
                stopCamera()
                finish()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error adding student: ${e.message}", e)
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun stopCamera() {
        cameraProvider?.unbindAll()
        cameraExecutor.shutdown()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        cameraProvider?.unbindAll()
    }
}