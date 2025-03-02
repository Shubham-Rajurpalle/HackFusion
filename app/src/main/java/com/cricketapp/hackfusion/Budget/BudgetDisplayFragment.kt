package com.cricketapp.hackfusion.Budget


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BudgetDisplayFragment : Fragment() {

    private var _binding: FragmentBudgetDisplayBinding? = null
    private val binding get() = _binding!!

    private lateinit var budgetAdapter: BudgetAdapter
    private val budgetList = mutableListOf<BudgetItem>()
    private lateinit var firebaseDatabase: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetDisplayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase
        firebaseDatabase = FirebaseDatabase.getInstance()

        // Setup RecyclerView
        setupRecyclerView()

        // Load budget data
        loadBudgetData()

        // Setup swipe refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            budgetList.clear()
            budgetAdapter.notifyDataSetChanged()
            loadBudgetData()
        }

        // Setup filter buttons
        setupFilterButtons()
    }

    private fun setupRecyclerView() {
        budgetAdapter = BudgetAdapter(budgetList) { budgetItem ->
            // Handle item click - navigate to detail view
            navigateToBudgetDetail(budgetItem)
        }

        binding.recyclerViewBudgets.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = budgetAdapter
        }
    }

    private fun loadBudgetData() {
        binding.progressBar.visibility = View.VISIBLE

        val budgetRef = firebaseDatabase.getReference("budgets")
        budgetRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                budgetList.clear()

                for (budgetSnapshot in snapshot.children) {
                    try {
                        val id = budgetSnapshot.child("id").getValue(String::class.java) ?: ""
                        val title = budgetSnapshot.child("title").getValue(String::class.java) ?: ""
                        val category = budgetSnapshot.child("category").getValue(String::class.java) ?: ""
                        val amount = budgetSnapshot.child("amount").getValue(Float::class.java) ?: 0f
                        val description = budgetSnapshot.child("description").getValue(String::class.java) ?: ""
                        val date = budgetSnapshot.child("date").getValue(String::class.java) ?: ""
                        val totalSpent = budgetSnapshot.child("total_spent").getValue(Double::class.java) ?: 0.0
                        val remaining = budgetSnapshot.child("remaining").getValue(Double::class.java) ?: 0.0

                        // Create receipt list
                        val receiptsList = mutableListOf<ReceiptItem>()
                        val receiptsSnapshot = budgetSnapshot.child("receipts")

                        for (receiptSnapshot in receiptsSnapshot.children) {
                            val receiptId = receiptSnapshot.child("receipt_id").getValue(String::class.java) ?: ""
                            val imageUrl = receiptSnapshot.child("image_url").getValue(String::class.java) ?: ""
                            val receiptAmount = receiptSnapshot.child("amount").getValue(Float::class.java) ?: 0f
                            val vendor = receiptSnapshot.child("vendor").getValue(String::class.java) ?: ""
                            val receiptDate = receiptSnapshot.child("date").getValue(String::class.java) ?: ""
                            val receiptDesc = receiptSnapshot.child("description").getValue(String::class.java) ?: ""

                            receiptsList.add(
                                ReceiptItem(
                                    receiptId,
                                    imageUrl,
                                    receiptAmount,
                                    vendor,
                                    receiptDate,
                                    receiptDesc
                                )
                            )
                        }

                        // Add budget item to list
                        budgetList.add(
                            BudgetItem(
                                id,
                                title,
                                category,
                                amount,
                                description,
                                date,
                                receiptsList,
                                totalSpent,
                                remaining
                            )
                        )
                    } catch (e: Exception) {
                        // Skip invalid entries
                        continue
                    }
                }

                budgetAdapter.notifyDataSetChanged()
                binding.progressBar.visibility = View.GONE
                binding.swipeRefreshLayout.isRefreshing = false

                // Update UI based on data
                updateEmptyStateVisibility()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load budgets: ${error.message}", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
                binding.swipeRefreshLayout.isRefreshing = false
            }
        })
    }

    private fun updateEmptyStateVisibility() {
        if (budgetList.isEmpty()) {
            binding.emptyStateLayout.visibility = View.VISIBLE
        } else {
            binding.emptyStateLayout.visibility = View.GONE
        }
    }

    private fun setupFilterButtons() {
        binding.chipAll.setOnClickListener { filterBudgets(null) }
        binding.chipEvents.setOnClickListener { filterBudgets("Event") }
        binding.chipDepartments.setOnClickListener { filterBudgets("Department") }
        binding.chipMess.setOnClickListener { filterBudgets("Mess") }
    }

    private fun filterBudgets(category: String?) {
        val budgetRef = firebaseDatabase.getReference("budgets")

        if (category == null) {
            // Load all budgets
            loadBudgetData()
        } else {
            // Apply filter
            binding.progressBar.visibility = View.VISIBLE
            budgetList.clear()

            budgetRef.orderByChild("category").equalTo(category)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        budgetList.clear()

                        for (budgetSnapshot in snapshot.children) {
                            try {
                                val id = budgetSnapshot.child("id").getValue(String::class.java) ?: ""
                                val title = budgetSnapshot.child("title").getValue(String::class.java) ?: ""
                                val budgetCategory = budgetSnapshot.child("category").getValue(String::class.java) ?: ""
                                val amount = budgetSnapshot.child("amount").getValue(Float::class.java) ?: 0f
                                val description = budgetSnapshot.child("description").getValue(String::class.java) ?: ""
                                val date = budgetSnapshot.child("date").getValue(String::class.java) ?: ""
                                val totalSpent = budgetSnapshot.child("total_spent").getValue(Double::class.java) ?: 0.0
                                val remaining = budgetSnapshot.child("remaining").getValue(Double::class.java) ?: 0.0

                                // Create receipt list
                                val receiptsList = mutableListOf<ReceiptItem>()
                                val receiptsSnapshot = budgetSnapshot.child("receipts")

                                for (receiptSnapshot in receiptsSnapshot.children) {
                                    val receiptId = receiptSnapshot.child("receipt_id").getValue(String::class.java) ?: ""
                                    val imageUrl = receiptSnapshot.child("image_url").getValue(String::class.java) ?: ""
                                    val receiptAmount = receiptSnapshot.child("amount").getValue(Float::class.java) ?: 0f
                                    val vendor = receiptSnapshot.child("vendor").getValue(String::class.java) ?: ""
                                    val receiptDate = receiptSnapshot.child("date").getValue(String::class.java) ?: ""
                                    val receiptDesc = receiptSnapshot.child("description").getValue(String::class.java) ?: ""

                                    receiptsList.add(
                                        ReceiptItem(
                                            receiptId,
                                            imageUrl,
                                            receiptAmount,
                                            vendor,
                                            receiptDate,
                                            receiptDesc
                                        )
                                    )
                                }

                                // Add budget item to list
                                budgetList.add(
                                    BudgetItem(
                                        id,
                                        title,
                                        budgetCategory,
                                        amount,
                                        description,
                                        date,
                                        receiptsList,
                                        totalSpent,
                                        remaining
                                    )
                                )
                            } catch (e: Exception) {
                                // Skip invalid entries
                                continue
                            }
                        }

                        budgetAdapter.notifyDataSetChanged()
                        binding.progressBar.visibility = View.GONE
                        updateEmptyStateVisibility()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(requireContext(), "Failed to filter budgets: ${error.message}", Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.GONE
                    }
                })
        }
    }

    private fun navigateToBudgetDetail(budgetItem: BudgetItem) {
        // Navigate to detail fragment/activity with the selected budget
        val bundle = Bundle().apply {
            putString("budget_id", budgetItem.id)
        }

        // Use Navigation Component or start a new activity
        // Navigation.findNavController(requireView()).navigate(R.id.action_to_budget_detail, bundle)

        // Or use traditional fragment transaction
        // val fragment = BudgetDetailFragment().apply { arguments = bundle }
        // parentFragmentManager.beginTransaction()
        //    .replace(R.id.fragment_container, fragment)
        //    .addToBackStack(null)
        //    .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}