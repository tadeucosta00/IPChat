package com.example.projeto.ui.calendario

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.projeto.Models.EventFB
import com.example.projeto.R
import com.example.projeto.dados
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class CreateEventActivity : AppCompatActivity() {

    private var eventNameET: EditText? = null
    private var eventDescET: EditText? = null
    private var eventDateTV: TextView? = null
    private var eventTimeTV: TextView? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        supportActionBar!!.title = "Criar Evento"

        initWidgets()
    }

    private fun initWidgets() {
        eventNameET = findViewById(R.id.eventNameET)
        eventDescET = findViewById(R.id.eventDescricaoET)
        eventDateTV = findViewById(R.id.eventDateTV)
        eventTimeTV = findViewById(R.id.eventTimeTV)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun chooseDate(view: View) {
        datePicker()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun chooseTime(view: View) {
        timePicker()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun datePicker() {

        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDate(calendar)
        }

        val dateDialog = DatePickerDialog(this, R.style.MyPickerStyle, datePicker, calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        dateDialog.datePicker.minDate = calendar.timeInMillis

        dateDialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun timePicker() {
        val calendar = Calendar.getInstance()
        val timePicker = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            updateTime(calendar)
        }

        TimePickerDialog(this, R.style.MyPickerStyle, timePicker, calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE), true).show()
    }

    private fun updateDate(calendar: Calendar) {
        val format = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(format)
        eventDateTV!!.text = sdf.format(calendar.time)
    }

    private fun updateTime(calendar: Calendar) {
        val format = "HH:mm"
        val sdf = SimpleDateFormat(format)
        eventTimeTV!!.text = sdf.format(calendar.time)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveEventAction(view: View?) {

        val db = Firebase.firestore

        val eventId = db.collection("calendario").document(dados.uid).collection("eventos").document()
        val eventName = eventNameET!!.text.toString()
        val eventDesc = eventDescET!!.text.toString()
        val eventDate = eventDateTV!!.text.toString()
        val eventTime = eventTimeTV!!.text.toString()

        //val eventFB = hashMapOf(
        //    "name" to eventName,
        //    "desc" to eventDesc,
        //    "date" to eventDate,
        //    "time" to eventTime
        //)

        val eventFB = EventFB(
            eventId.id,
            eventName,
            eventDesc,
            eventDate,
            eventTime
        )

        Log.d(ContentValues.TAG, "AQUI -> ${CalendarUtils.formattedDate(CalendarUtils.selectedDate)}")

        val timer = object: CountDownTimer(250, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                finish()
            }
        }

        if (eventName != "" && eventDate != "Selecionar Data") {

            db.collection("calendario").document(dados.uid).collection("eventos").document(eventFB.id)
                .set(eventFB)
                .addOnSuccessListener { documentReference ->
                    Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${eventFB.id}")
                    CalendarUtils.loadEventsFromFB()
                    timer.start()
                }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, "Error adding document", e)
                    Toast.makeText(this,"Erro ao adicionar evento!", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Preencha os campos obrigatórios!", Toast.LENGTH_SHORT).show()
        }


        /*db.collection("calendario").document(dados.uid).collection("eventos")
            .add(eventFB)
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                loadEventsFromFB()
                timer.start()
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
                Toast.makeText(this,"Erro ao adicionar evento!", Toast.LENGTH_SHORT).show()
            }*/

    }
}