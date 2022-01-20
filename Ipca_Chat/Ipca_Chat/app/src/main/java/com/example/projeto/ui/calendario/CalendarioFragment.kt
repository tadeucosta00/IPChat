package com.example.projeto.ui.calendario

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projeto.R
import com.example.projeto.VerPerfil
import com.example.projeto.dados
import com.example.projeto.ui.calendario.CalendarUtils.daysInMonthArray
import com.example.projeto.ui.calendario.CalendarUtils.loadEventsFromFB
import com.example.projeto.ui.calendario.CalendarUtils.monthYearFromDate
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.time.LocalDate
import java.util.ArrayList

class CalendarioFragment : Fragment(), CalendarAdapter.OnItemListener {

    lateinit var botao: FloatingActionButton
    private var monthYearText: TextView? = null
    private var calendarRecyclerView: RecyclerView? = null
    lateinit var botaoBack: Button
    lateinit var botaoForward: Button

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayShowCustomEnabled(true)

        val inflater = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val v: View = inflater.inflate(R.layout.actionbar,null)
        val foto = v.findViewById<CircleImageView>(R.id.imageView4)

        Picasso.get().load(dados.profileImageurl).into(foto)

        val textActionBar = v.findViewById<TextView>(R.id.textViewActionbar)
        textActionBar.text = "Calendário"

        (activity as AppCompatActivity?)!!.supportActionBar?.customView = v

        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayShowTitleEnabled(false)

        val intent = Intent(activity, VerPerfil::class.java)
        foto.setOnClickListener(){
            startActivity(intent)
        }

        loadEventsFromFB()
        CalendarUtils.selectedDate = LocalDate.now()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        setMonthView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_calendario, container, false)

        botaoBack = rootView.findViewById(R.id.buttonBack)
        botaoForward = rootView.findViewById(R.id.buttonForward)
        botao = rootView.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        calendarRecyclerView = rootView.findViewById<RecyclerView>(R.id.calendarRecyclerView)
        monthYearText = rootView.findViewById<TextView>(R.id.monthYearTV)

        setMonthView()

        return rootView
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        botaoBack.setOnClickListener {
            previousMonthAction()
            setMonthView()
        }

        botaoForward.setOnClickListener {
            nextMonthAction()
            setMonthView()
        }

        botao.setOnClickListener{
            startActivity(Intent(context, CreateEventActivity::class.java))
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setMonthView() {
        monthYearText!!.text = monthYearFromDate(CalendarUtils.selectedDate!!)
        //val daysInMonth: ArrayList<LocalDate?> = daysInMonthArray(CalendarUtils.selectedDate)
        val daysInMonth: ArrayList<LocalDate?> = daysInMonthArray()
        val calendarAdapter:CalendarAdapter = CalendarAdapter(daysInMonth, this)
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(requireActivity().applicationContext, 7)
        calendarRecyclerView!!.layoutManager = layoutManager
        calendarRecyclerView!!.adapter = calendarAdapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun previousMonthAction() {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate!!.minusMonths(1)
        //setMonthView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun nextMonthAction() {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate!!.plusMonths(1)
        //setMonthView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemClick(position: Int, date: LocalDate?) {
        if (date != null) {
            CalendarUtils.selectedDate = date
            //setMonthView()
            replaceFragment(WeekViewFragment())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_layout, fragment)
            .commit()
    }

}