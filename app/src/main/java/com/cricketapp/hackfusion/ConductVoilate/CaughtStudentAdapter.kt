package com.cricketapp.hackfusion.ConductVoilate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cricketapp.hackfusion.R
import com.cricketapp.hackfusion.Student

class CaughtStudentAdapter(private var studentList: List<Student>) :
    RecyclerView.Adapter<CaughtStudentAdapter.StudentViewHolder>() {

    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.studentName)
        val regNo: TextView = itemView.findViewById(R.id.registrationNumber)
        val reason: TextView = itemView.findViewById(R.id.reason)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_caught_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = studentList[position]
        holder.name.text = student.name
        holder.regNo.text = "Reg. No: ${student.regNo}"
        holder.reason.text = student.reason
    }

    override fun getItemCount(): Int = studentList.size

    fun updateList(newList: List<Student>) {
        studentList = newList
        notifyDataSetChanged()
    }
}
