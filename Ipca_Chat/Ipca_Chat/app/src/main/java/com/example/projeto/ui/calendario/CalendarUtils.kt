package com.example.projeto.ui.calendario

import android.content.ContentValues
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.projeto.Models.Event
import com.example.projeto.dados
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

object CalendarUtils {
    var selectedDate: LocalDate? = null
    var eventsList = ArrayList<Event>()
    var todayDate: LocalDate? = null

    @RequiresApi(Build.VERSION_CODES.O)
    fun formattedDate(date: LocalDate?): String {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return date!!.format(formatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formattedTime(time: LocalTime?): String {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return time!!.format(formatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun monthYearFromDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return date.format(formatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun daysInMonthArray(): ArrayList<LocalDate?> {
        val daysInMonthArray = ArrayList<LocalDate?>()
        val yearMonth = YearMonth.from(selectedDate)
        val daysInMonth = yearMonth.lengthOfMonth()

        val prevMonth = selectedDate!!.minusMonths(1)
        val nextMonth = selectedDate!!.plusMonths(1)

        val prevYearMonth = YearMonth.from(prevMonth)
        val prevDaysInMonth = prevYearMonth.lengthOfMonth()

        val firstOfMonth = selectedDate!!.withDayOfMonth(1)
        val dayOfWeek = firstOfMonth.dayOfWeek.value

        for (i in 1..42) {

            if (i <= dayOfWeek)
                daysInMonthArray.add(LocalDate.of(prevMonth.year, prevMonth.month, prevDaysInMonth + i - dayOfWeek))
            else if (i > daysInMonth + dayOfWeek)
                daysInMonthArray.add(LocalDate.of(nextMonth.year, nextMonth.month,i - dayOfWeek - daysInMonth));

            else
                daysInMonthArray.add(LocalDate.of(selectedDate!!.year, selectedDate!!.month, i - dayOfWeek))
        }

        return daysInMonthArray
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun daysInWeekArray(selectedDate: LocalDate): ArrayList<LocalDate?> {
        val days = ArrayList<LocalDate?>()
        var current = sundayForDate(selectedDate)
        val endDate = current!!.plusWeeks(1)

        while (current!!.isBefore(endDate)) {
            days.add(current)
            current = current.plusDays(1)
        }

        return days
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sundayForDate(current: LocalDate): LocalDate? {
        var current = current
        val oneWeekAgo = current.minusWeeks(1)

        while (current.isAfter(oneWeekAgo)) {
            if (current.dayOfWeek == DayOfWeek.SUNDAY) return current
            current = current.minusDays(1)
        }

        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadEventsFromFB() {
        eventsList.clear()

        Log.d(ContentValues.TAG, "LOADEVENTS")

        val db = Firebase.firestore

        db.collection("calendario").document(dados.uid).collection("eventos")
            .addSnapshotListener { snapshot, e ->

                if (e != null) {
                    Log.d(ContentValues.TAG, "Listen Failed " + e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {

                    for (document in snapshot!!) {

                        val id = document.data["id"].toString()
                        val name = document.data["name"].toString()
                        val desc = document.data["desc"].toString()
                        val date = document.data["date"].toString()
                        val time = document.data["time"].toString()

                        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        val dateFormated = LocalDate.parse(date, dateFormatter)
                        val timeFormated: LocalTime
                        val timeFormatter: DateTimeFormatter

                        if (time != "Selecionar Hora") {
                            timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
                            timeFormated = LocalTime.parse(time, timeFormatter)
                        }
                        else {
                            timeFormated = LocalTime.of(0, 0)
                        }

                        val event = Event(id ,name, desc, dateFormated, timeFormated)

                        eventsList.add(event)

                        Log.d(ContentValues.TAG, "Evento: ${event.id}, ${event.name}, ${event.date}, ${event.time}")
                    }

                }
            }

        /*db.collection("calendario").document(dados.uid).collection("eventos")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {


                    val name = document.data["name"].toString()
                    val desc = document.data["desc"].toString()
                    val date = document.data["date"].toString()
                    val time = document.data["time"].toString()

                    println("GGG" + name)

                    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    val dateFormated = LocalDate.parse(date, dateFormatter)

                    val timeFormated: LocalTime
                    val timeFormatter: DateTimeFormatter

                    if (time != "Selecionar Hora") {

                        timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
                        timeFormated = LocalTime.parse(time, timeFormatter)
                    }
                    else {
                        timeFormated = LocalTime.of(0, 0)
                    }

                    val event = Event(name, desc, dateFormated, timeFormated)

                    eventsList.add(event)

                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                    Log.d(ContentValues.TAG, "Evento: ${event.name}, ${event.date}, ${event.time}")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents: ", exception)
            }*/
    }
}