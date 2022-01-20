package com.example.projeto.ui.calendario

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.projeto.Models.Event
import com.example.projeto.R
import java.time.LocalTime

class EventAdapter(context: Context, events: List<Event>?) : ArrayAdapter<Event?>(context, 0, events!!) {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val event: Event? = getItem(position)

        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.event_cell, parent, false)

        val eventNameTV = convertView!!.findViewById<TextView>(R.id.eventNameTV)
        val eventDescTV = convertView!!.findViewById<TextView>(R.id.eventDescTV)
        val eventDateTV = convertView!!.findViewById<TextView>(R.id.eventDateTV)
        val eventTimeTV = convertView!!.findViewById<TextView>(R.id.eventTimeTV)

        val date = CalendarUtils.formattedDate(event!!.date)
        val time = CalendarUtils.formattedTime(event!!.time)

        val eventName: String = event!!.name
        val eventDesc: String = event!!.desc

        eventNameTV.text = eventName
        eventDescTV.text = eventDesc
        eventDateTV.text = "Dia: " + date

        if (time != "00:00") {
            eventTimeTV.text = " ás: " + time
        } else {
            eventTimeTV.text = " "
        }


        return convertView
    }
}