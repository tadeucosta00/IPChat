package com.example.projeto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projeto.Models.Dados
import com.example.projeto.Models.Mensagens
import com.example.projeto.Models.Utilizadores
import com.example.projeto.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.ArrayList


class ListaPessoasActivity : AppCompatActivity() {

    lateinit var recyclerView : RecyclerView
    private val adapter = GroupAdapter<ViewHolder>()
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_pessoas)
        supportActionBar?.title = "Pessoas"

        val searchBar = findViewById<SearchView>(R.id.searchView)
        searchBar.queryHint = "Pesquisar por Utilizador"


        recyclerView = findViewById(R.id.lisViewPessoas)
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.setLayoutManager(layoutManager)
        recyclerView.adapter = adapter

        val uid = FirebaseAuth.getInstance().uid

        db.collection("utilizadores").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    var utilizador = document.toObject(Utilizadores::class.java)!!
                    if(utilizador.uid != uid){
                        adapter.add(Users(utilizador))
                    }
                }
                adapter.setOnItemClickListener{ item, view ->
                val utilizadorEscolhido = item as Users
                val intent = Intent(view.context,SalaChat::class.java)
                intent.putExtra("UTILIZADOR", utilizadorEscolhido.utilizador.uid)
                intent.putExtra("UTILIZADORNOME", utilizadorEscolhido.utilizador.username)
                intent.putExtra("UTILIZADORTOKEN", utilizadorEscolhido.utilizador.token)
                intent.putExtra("UTILIZADOREMAIL", utilizadorEscolhido.utilizador.email)
                intent.putExtra("UTILIZADORANO", utilizadorEscolhido.utilizador.ano)
                intent.putExtra("UTILIZADORFOTO", utilizadorEscolhido.utilizador.profileImageurl)
                intent.putExtra("UTILIZADORCURSO", utilizadorEscolhido.utilizador.idcurso)
                intent.putExtra("UTILIZADORATIVIDADE", utilizadorEscolhido.utilizador.atividade)

                startActivity(intent)
            }
        }

    }


    class Users(val utilizador: Utilizadores) : Item<ViewHolder>() {

        override fun getLayout() = R.layout.row_pessoas

        override fun bind(viewHolder: ViewHolder, position: Int) {
            var textNome = viewHolder.itemView.findViewById<TextView>(R.id.textViewPessoas)
            textNome.text = utilizador.username
            var imagem = viewHolder.itemView.findViewById<CircleImageView>(R.id.imageView)
            Picasso.get().load(utilizador.profileImageurl).into(imagem)
            var imagemonline = viewHolder.itemView.findViewById<ImageView>(R.id.online)

            if(utilizador.atividade == true){
                imagemonline.visibility = View.VISIBLE
            }
            if(utilizador.atividade == false){
                imagemonline.visibility = View.INVISIBLE
            }
        }
    }
}
