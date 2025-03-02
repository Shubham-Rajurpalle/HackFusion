package com.cricketapp.hackfusion.Budget


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.cricketapp.hackfusion.databinding.ActivityBudgetUploadBinding
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BudgetUploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBudgetUploadBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseStorage: FirebaseStorage
    private val selectedReceiptUris = ArrayList<Uri>()
    private val uploadedReceiptUrls = ArrayList<String>()

    // Register for activity result to handle image selection
    private val selectImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedReceiptUris.add(uri)
                updateSelectedFilesCount()
            }
            // For multiple image selection
            val clipData = result.data?.clipData
            if (clipData != null) {
                for (i in 0 until clipData.itemCount) {
                    selectedReceiptUris.add(clipData.getItemAt(i).uri)
                }
                updateSelectedFilesCount()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()

        setupCategorySpinner()
        setupListeners()
    }

    private fun setupCategorySpinner() {
        val categories = arrayOf("Event", "Department", "Mess")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter
    }

    private fun setupListeners() {
        // Choose receipt images
        binding.btnSelectReceipts.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
            selectImageLauncher.launch(intent)
        }

        // Upload budget data
        binding.btnSubmitBudget.setOnClickListener {
            if (validateInputs()) {
                uploadBudgetData()
            }
        }
    }

    private fun validateInputs(): Boolean {
        when {
            binding.editTextTitle.text.isNullOrBlank() -> {
                showToast("Please enter a title")
                return false
            }
            binding.editTextAmount.text.isNullOrBlank() -> {
                showToast("Please enter an amount")
                return false
            }
            binding.editTextDescription.text.isNullOrBlank() -> {
                showToast("Please enter a description")
                return false
            }
            selectedReceiptUris.isEmpty() -> {
                showToast("Please select at least one receipt")
                return false
            }
            else -> return true
        }
    }

    private fun updateSelectedFilesCount() {
        binding.tvSelectedFilesCount.text = "${selectedReceiptUris.size} files selected"
    }

    private fun uploadBudgetData() {
        binding.progressBar.visibility = android.view.View.VISIBLE
        binding.btnSubmitBudget.isEnabled = false

        // Create a unique ID for this budget entry
        val budgetId = UUID.randomUUID().toString()

        // Upload all receipt images first
        uploadReceiptImages(budgetId) { receiptsList ->
            // After all images are uploaded, save the budget data
            saveBudgetDataToFirebase(budgetId, receiptsList)
        }
    }

    private fun uploadReceiptImages(budgetId: String, onComplete: (List<Map<String, Any>>) -> Unit) {
        val receiptsData = mutableListOf<Map<String, Any>>()
        var uploadedCount = 0

        if (selectedReceiptUris.isEmpty()) {
            onComplete(receiptsData)
            return
        }

        selectedReceiptUris.forEachIndexed { index, uri ->
            val receiptId = UUID.randomUUID().toString()
            val storageRef = firebaseStorage.reference
                .child("receipts")
                .child(budgetId)
                .child("$receiptId.jpg")

            storageRef.putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUrl ->
                        // Create receipt data
                        val receiptData = mapOf(
                            "receipt_id" to receiptId,
                            "image_url" to downloadUrl.toString(),
                            "amount" to binding.editTextReceiptAmount.text.toString().toFloatOrNull() ?: 0f,
                            "vendor" to binding.editTextVendorName.text.toString(),
                            "date" to SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                            "description" to binding.editTextReceiptDescription.text.toString()
                        )

                        receiptsData.add(receiptData)
                        uploadedCount++

                        // Check if all uploads are complete
                        if (uploadedCount == selectedReceiptUris.size) {
                            onComplete(receiptsData)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    showToast("Failed to upload receipt: ${exception.message}")
                    binding.progressBar.visibility = android.view.View.GONE
                    binding.btnSubmitBudget.isEnabled = true
                }
        }
    }

    private fun saveBudgetDataToFirebase(budgetId: String, receiptsList: List<Map<String, Any>>) {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val budgetAmount = binding.editTextAmount.text.toString().toFloatOrNull() ?: 0f

        // Calculate total spent from receipts
        val totalSpent = receiptsList.sumOf {
            (it["amount"] as? Float ?: 0f).toDouble()
        }

        val budgetData = mapOf(
            "id" to budgetId,
            "title" to binding.editTextTitle.text.toString(),
            "category" to binding.spinnerCategory.selectedItem.toString(),
            "amount" to budgetAmount,
            "description" to binding.editTextDescription.text.toString(),
            "date" to currentDate,
            "uploaded_by" to "Dean", // Since it's directly pushed by the dean
            "receipts" to receiptsList,
            "total_spent" to totalSpent,
            "remaining" to (budgetAmount - totalSpent)
        )

        // Save to Firebase Realtime Database
        val dbRef = firebaseDatabase.getReference("budgets")
        dbRef.child(budgetId).setValue(budgetData)
            .addOnSuccessListener {
                showToast("Budget data uploaded successfully")
                binding.progressBar.visibility = android.view.View.GONE
                clearForm()
                finish() // Return to previous screen
            }
            .addOnFailureListener { exception ->
                showToast("Failed to save budget data: ${exception.message}")
                binding.progressBar.visibility = android.view.View.GONE
                binding.btnSubmitBudget.isEnabled = true
            }
    }

    private fun clearForm() {
        binding.editTextTitle.text.clear()
        binding.editTextAmount.text.clear()
        binding.editTextDescription.text.clear()
        binding.editTextVendorName.text.clear()
        binding.editTextReceiptAmount.text.clear()
        binding.editTextReceiptDescription.text.clear()
        selectedReceiptUris.clear()
        updateSelectedFilesCount()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}