package com.example.projeto.ui.calendario

import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projeto.Models.Event
import com.example.projeto.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate
import java.util.ArrayList

class WeekViewFragment : Fragment(), CalendarAdapter.OnItemListener {

    lateinit var botao: FloatingActionButton
    private var monthYearText: TextView? = null
    private var calendarRecyclerView: RecyclerView? = null
    lateinit var botaoBack: Button
    lateinit var botaoForward: Button
    private var eventListView: ListView? = null
    private var positionList: Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        setWeekView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_week_view, container, false)

        botaoBack = rootView.findViewById(R.id.buttonBack)
        botaoForward = rootView.findViewById(R.id.buttonForward)
        botao = rootView.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        calendarRecyclerView = rootView.findViewById<RecyclerView>(R.id.calendarRecyclerView)
        monthYearText = rootView.findViewById<TextView>(R.id.monthYearTV)
        eventListView = rootView.findViewById(R.id.eventListView)

        setWeekView()

        return rootView
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        botaoBack.setOnClickListener {
            previousWeekAction()
            setWeekView()
        }

        botaoForward.setOnClickListener {
            nextWeekAction()
            setWeekView()
        }

        botao.setOnClickListener{
            startActivity(Intent(context, CreateEventActivity::class.java))
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setWeekView() {
        Log.d(ContentValues.TAG, "WEEKVIEW")
        monthYearText!!.text = CalendarUtils.monthYearFromDate(CalendarUtils.selectedDate!!)

        val days: ArrayList<LocalDate?> =
            CalendarUtils.daysInWeekArray(CalendarUtils.selectedDate!!)
        val calendarAdapter = CalendarAdapter(days, this)
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(requireActivity().applicationContext, 7)

        calendarRecyclerView!!.layoutManager = layoutManager
        calendarRecyclerView!!.adapter = calendarAdapter

        setEventAdapter()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun previousWeekAction() {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate!!.minusWeeks(1)
        //setWeekView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun nextWeekAction() {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate!!.plusWeeks(1)
        //setWeekView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemClick(position: Int, date: LocalDate?) {
        Log.d(ContentValues.TAG, "ITEMCLICK")
        positionList = position
        CalendarUtils.selectedDate = date
        //setWeekView()
        replaceFragment(WeekViewFragment())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setEventAdapter() {
        Log.d(ContentValues.TAG, "EVENTADAPTER")
        val dailyEvents: ArrayList<Event> = Event.eventsForDate(CalendarUtils.selectedDate)!!
        val eventAdapter = EventAdapter(requireActivity().applicationContext, dailyEvents)

        eventListView!!.adapter = eventAdapter
    }

    private fun replaceFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_layout, fragment)
            .commit()
    }
}