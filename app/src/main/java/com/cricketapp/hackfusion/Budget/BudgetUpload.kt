package com.cricketapp.hackfusion.Budget

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.cricketapp.hackfusion.databinding.ActivityBudgetUploadBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class BudgetUpload : AppCompatActivity() {

    private lateinit var binding: ActivityBudgetUploadBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val selectedReceiptUris = ArrayList<Uri>()

    private val selectImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedReceiptUris.add(uri)
                updateSelectedFilesCount()
            }
            result.data?.clipData?.let { clipData ->
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
        binding.btnSelectReceipts.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
            selectImageLauncher.launch(intent)
        }

        binding.btnSubmitBudget.setOnClickListener {
            if (validateInputs()) {
                uploadBudgetData()
            }
        }
    }

    private fun validateInputs(): Boolean {
        return when {
            binding.editTextTitle.text.isNullOrBlank() -> {
                showToast("Please enter a title"); false
            }
            binding.editTextAmount.text.isNullOrBlank() -> {
                showToast("Please enter an amount"); false
            }
            binding.editTextDescription.text.isNullOrBlank() -> {
                showToast("Please enter a description"); false
            }
            selectedReceiptUris.isEmpty() -> {
                showToast("Please select at least one receipt"); false
            }
            else -> true
        }
    }

    private fun updateSelectedFilesCount() {
        binding.tvSelectedFilesCount.text = "${selectedReceiptUris.size} files selected"
    }

    private fun uploadBudgetData() {
        binding.progressBar.visibility = android.view.View.VISIBLE
        binding.btnSubmitBudget.isEnabled = false

        val budgetId = UUID.randomUUID().toString()
        uploadReceiptImages(budgetId) { receiptsList ->
            saveBudgetDataToFirestore(budgetId, receiptsList)
        }
    }

    private fun uploadReceiptImages(budgetId: String, onComplete: (List<Map<String, Any>>) -> Unit) {
        val receiptsData = mutableListOf<Map<String, Any>>()
        var uploadedCount = 0

        if (selectedReceiptUris.isEmpty()) {
            onComplete(receiptsData)
            return
        }

        selectedReceiptUris.forEach { uri ->
            val receiptId = UUID.randomUUID().toString()
            val storageRef = storage.reference.child("receipts/$budgetId/$receiptId.jpg")

            storageRef.putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUrl ->
                        val receiptData = mapOf(
                            "receipt_id" to receiptId,
                            "image_url" to downloadUrl.toString(),
                            "amount" to (binding.editTextAmount.text.toString().toFloatOrNull() ?: 0f),
                            "vendor" to binding.editTextVendorName.text.toString(),
                            "date" to SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                            "description" to binding.editTextReceiptDescription.text.toString()
                        )
                        receiptsData.add(receiptData)
                        uploadedCount++

                        if (uploadedCount == selectedReceiptUris.size) {
                            onComplete(receiptsData)
                        }
                    }
                }
                .addOnFailureListener {
                    showToast("Failed to upload receipt: ${it.message}")
                    binding.progressBar.visibility = android.view.View.GONE
                    binding.btnSubmitBudget.isEnabled = true
                }
        }
    }

    private fun saveBudgetDataToFirestore(budgetId: String, receiptsList: List<Map<String, Any>>) {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val budgetAmount = binding.editTextAmount.text.toString().toFloatOrNull() ?: 0f

        val totalSpent = receiptsList.sumOf { (it["amount"] as? Float ?: 0f).toDouble() }
        val remaining = budgetAmount - totalSpent

        val budgetData = mapOf(
            "id" to budgetId,
            "title" to binding.editTextTitle.text.toString(),
            "category" to binding.spinnerCategory.selectedItem.toString(),
            "amount" to budgetAmount,
            "description" to binding.editTextDescription.text.toString(),
            "date" to currentDate,
            "uploaded_by" to "Dean",
            "receipts" to receiptsList,
            "total_spent" to totalSpent,
            "remaining" to remaining
        )

        firestore.collection("budgets")
            .document(budgetId)
            .set(budgetData)
            .addOnSuccessListener {
                showToast("Budget data uploaded successfully!")
                binding.progressBar.visibility = android.view.View.GONE
                clearForm()
                finish()
            }
            .addOnFailureListener {
                showToast("Failed to save budget data: ${it.message}")
                binding.progressBar.visibility = android.view.View.GONE
                binding.btnSubmitBudget.isEnabled = true
            }
    }

    private fun clearForm() {
        binding.editTextTitle.text?.clear()
        binding.editTextAmount.text?.clear()
        binding.editTextDescription.text?.clear()
        binding.editTextVendorName.text?.clear()
        binding.editTextReceiptAmount.text?.clear()
        binding.editTextReceiptDescription.text?.clear()
        selectedReceiptUris.clear()
        updateSelectedFilesCount()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
