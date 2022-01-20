package com.example.projeto.Models

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.projeto.ui.calendario.CalendarUtils
import java.time.LocalDate
import java.time.LocalTime
import java.util.ArrayList

class Event(var id: String, var name: String, var desc: String, var date: LocalDate, var time: LocalTime) {

    companion object {

        @RequiresApi(Build.VERSION_CODES.O)
        fun eventsForDate(date: LocalDate?): ArrayList<Event> {

            val events = ArrayList<Event>()

            for (event in CalendarUtils.eventsList) {

                if (event.date == date)
                    events.add(event)
            }

            return events
        }
    }
}