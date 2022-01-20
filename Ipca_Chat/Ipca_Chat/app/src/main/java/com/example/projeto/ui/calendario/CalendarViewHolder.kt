package com.example.projeto.ui.calendario

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projeto.R
import java.time.LocalDate
import java.util.ArrayList

class CalendarViewHolder(itemView: View, onItemListener: CalendarAdapter.OnItemListener, days: ArrayList<LocalDate?>) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private val days: ArrayList<LocalDate?>
    val parentView: View
    val dayOfMonth: TextView
    val imageEvent: ImageView
    private val onItemListener: CalendarAdapter.OnItemListener

    override fun onClick(view: View) {
        onItemListener.onItemClick(adapterPosition, days[adapterPosition])
    }

    init {
        parentView = itemView.findViewById(R.id.parentView)
        imageEvent = itemView.findViewById(R.id.imageEvent)
        dayOfMonth = itemView.findViewById(R.id.cellDayText)
        this.onItemListener = onItemListener
        itemView.setOnClickListener(this)
        this.days = days
    }
}