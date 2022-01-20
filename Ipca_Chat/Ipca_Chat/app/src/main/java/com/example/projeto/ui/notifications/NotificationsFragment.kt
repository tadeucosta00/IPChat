package com.example.projeto.ui.notifications

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.projeto.*
import com.google.android.material.floatingactionbutton.FloatingActionButton


class NotificationsFragment : Fragment() {

    lateinit var botao:FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayShowCustomEnabled(true)
        val inflater = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val v: View = inflater.inflate(R.layout.actionbar,null)
        val foto = v.findViewById<CircleImageView>(R.id.imageView4)

        Picasso.get().load(dados.profileImageurl).into(foto)

        val textActionBar = v.findViewById<TextView>(R.id.textViewActionbar)
        textActionBar.text = "Calendario"

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
        val rootView = inflater.inflate(R.layout.fragment_notifications, container, false)

        botao = rootView.findViewById<FloatingActionButton>(R.id.floatingActionButton)

        return rootView
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        botao.setOnClickListener(){
            val intent = Intent (getActivity(), Adiconarevento::class.java)
            getActivity()?.startActivity(intent)
        }

    }
}