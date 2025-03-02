package com.cricketapp.hackfusion.Budget

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class BudgetAdapter(
    private val budgetList: List<BudgetItem>,
    private val onItemClick: (BudgetItem) -> Unit
) : RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val binding = ItemBudgetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BudgetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        holder.bind(budgetList[position])
    }

    override fun getItemCount(): Int = budgetList.size

    inner class BudgetViewHolder(private val binding: ItemBudgetBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(budget: BudgetItem) {
            // Format currency
            val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())

            binding.textViewTitle.text = budget.title
            binding.textViewCategory.text = budget.category
            binding.textViewDate.text = budget.date
            binding.textViewAmount.text = currencyFormat.format(budget.amount)

            // Calculate percentage spent
            val percentSpent = if (budget.amount > 0) {
                (budget.totalSpent / budget.amount.toDouble()) * 100
            } else {
                0.0
            }

            binding.textViewSpent.text = "${currencyFormat.format(budget.totalSpent)} (${String.format("%.1f", percentSpent)}%)"
            binding.textViewRemaining.text = currencyFormat.format(budget.remaining)

            // Set progress bar
            binding.progressBarSpent.progress = percentSpent.toInt().coerceIn(0, 100)

            // Set different color for progress bar based on percentage spent
            val context = binding.root.context
            val progressColor = when {
                percentSpent > 90 -> android.R.color.holo_red_light
                percentSpent > 75 -> android.R.color.holo_orange_light
                else -> android.R.color.holo_green_light
            }
            binding.progressBarSpent.progressTintList = android.content.res.ColorStateList.valueOf(
                androidx.core.content.ContextCompat.getColor(context, progressColor)
            )

            // Set receipt count
            binding.textViewReceiptCount.text = "${budget.receipts.size} receipts"

            // Set click listener
            binding.cardViewBudget.setOnClickListener {
                onItemClick(budget)
            }
        }
    }
}