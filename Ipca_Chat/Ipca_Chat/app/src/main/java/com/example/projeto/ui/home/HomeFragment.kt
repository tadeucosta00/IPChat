package com.example.projeto.ui.home

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import de.hdodenhof.circleimageview.CircleImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.projeto.*
import com.example.projeto.Models.Dados
import com.example.projeto.Models.Mensagens
import com.example.projeto.Models.Utilizadores
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import example.javatpoint.com.kotlintablayoutexample.adapter

lateinit var viewPager : ViewPager
lateinit var tabLayout : TabLayout

class HomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayShowCustomEnabled(true)

        val inflater = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val v: View = inflater.inflate(R.layout.actionbar,null)
        val foto = v.findViewById<CircleImageView>(R.id.imageView4)

        Picasso.get().load(dados.profileImageurl).into(foto)

        val textActionBar = v.findViewById<TextView>(R.id.textViewActionbar)
        textActionBar.text = "Conversas"

        (activity as AppCompatActivity?)!!.supportActionBar?.setCustomView(v)

        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayShowTitleEnabled(false)

        val intent = Intent(activity, VerPerfil::class.java)
        foto.setOnClickListener(){
            startActivity(intent)
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        viewPager = rootView.findViewById(R.id.viewPager)
        tabLayout = rootView.findViewById(R.id.tabLayout)
        viewPager.adapter = adapter((activity as AppCompatActivity).supportFragmentManager)


        return rootView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabLayout.setupWithViewPager(viewPager)

    }



}


