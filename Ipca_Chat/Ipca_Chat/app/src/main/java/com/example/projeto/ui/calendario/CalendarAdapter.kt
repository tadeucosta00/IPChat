package com.example.projeto.ui.calendario

import android.content.ContentValues
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.projeto.R
import com.example.projeto.dados
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.ArrayList

class CalendarAdapter(private val days: ArrayList<LocalDate?>, private val onItemListener: OnItemListener) : RecyclerView.Adapter<CalendarViewHolder>() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.calendar_cell, parent, false)
        val layoutParams = view.layoutParams

        if (days.size > 15)
            layoutParams.height = (parent.height * 0.166666666).toInt()
        else
            layoutParams.height = parent.height

        return CalendarViewHolder(view, onItemListener, days)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val date = days[position]

        val db = Firebase.firestore

        db.collection("calendario").document(dados.uid).collection("eventos")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {

                    val dateFB = document.data["date"].toString()

                    Log.d(ContentValues.TAG, "TESTE -> " + dateFB)

                    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    val dateFormated = LocalDate.parse(dateFB, dateFormatter)

                    if (date == dateFormated) {
                        holder.imageEvent.visibility = View.VISIBLE
                    }

                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents: ", exception)
            }

        if (date == null)
            holder.dayOfMonth.text = ""
        else {

            holder.dayOfMonth.text = date.dayOfMonth.toString()

            if (date == CalendarUtils.selectedDate) {
                holder.dayOfMonth.setBackgroundResource(R.drawable.circle_date_today)
            }

            if (date == CalendarUtils.todayDate) {
                holder.dayOfMonth.setBackgroundResource(R.drawable.circle_date_today)
                holder.dayOfMonth.setTextColor(Color.rgb(76, 175, 80))
            }

        }

        if(date!!.month.equals(CalendarUtils.selectedDate!!.month))
            holder.dayOfMonth.setTextColor(Color.BLACK);
        else
            holder.dayOfMonth.setTextColor(Color.LTGRAY);
    }

    override fun getItemCount(): Int {
        return days.size
    }

    interface OnItemListener {
        fun onItemClick(position: Int, date: LocalDate?)
    }
}