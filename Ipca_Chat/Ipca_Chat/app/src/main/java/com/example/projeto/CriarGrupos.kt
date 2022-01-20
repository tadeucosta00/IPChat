package com.example.projeto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projeto.Models.Utilizadores
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView
import android.R.string
import android.widget.Button
import com.google.firebase.database.MutableData
import com.google.firebase.firestore.Query
import pl.droidsonroids.gif.GifImageView


class CriarGrupos : AppCompatActivity() {

    private val userIds : ArrayList<String> = ArrayList()
    lateinit var recyclerView : RecyclerView
    private val adapter = GroupAdapter<ViewHolder>()
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_criar_grupos)

        supportActionBar?.title = "Criar Grupo"

        recyclerView = findViewById(R.id.lisViewPessoas)
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.setLayoutManager(layoutManager)
        recyclerView.adapter = adapter

        val botao = findViewById<Button>(R.id.button2)

        botao.visibility = View.INVISIBLE

        val uid = FirebaseAuth.getInstance().uid

        db.collection("utilizadores").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val utilizador = document.toObject(Utilizadores::class.java)!!
                    if(utilizador.uid != uid){
                        adapter.add(Users(utilizador, userIds))
                    }
                }
            }

        adapter.setOnItemClickListener { item, view ->
            val row = item as Users
            if(row.utilizador.uid in userIds){
                userIds.remove(row.utilizador.uid)
                if(userIds.size < 3)
                    botao.visibility = View.INVISIBLE
            }
            else{
                userIds.add(row.utilizador.uid!!)
                if(userIds.size >= 3)
                    botao.visibility = View.VISIBLE
            }
            adapter.notifyDataSetChanged()
            println("id" + row.utilizador.uid!!)
        }

        botao.setOnClickListener(){
            val bundle = Bundle()
            bundle.putStringArrayList("userIds", userIds)
            val intent = Intent(this, CriarGrupo2Activity::class.java)
            intent.putExtras(bundle)
            finish()
            startActivity(intent)
        }
    }

    class Users(val utilizador: Utilizadores, val userids : ArrayList<String>) : Item<ViewHolder>() {

        override fun bind(viewHolder: ViewHolder, position: Int) {
            var textNome = viewHolder.itemView.findViewById<TextView>(R.id.textViewPessoas)
            textNome.text = utilizador.username
            var imagem = viewHolder.itemView.findViewById<CircleImageView>(R.id.imageView)
            var imagemvista = viewHolder.itemView.findViewById<GifImageView>(R.id.imageViewverficado)
            Picasso.get().load(utilizador.profileImageurl).into(imagem)

            if(utilizador.uid in userids)
            {
                imagemvista.visibility = View.VISIBLE
                imagemvista.setImageResource(R.drawable.icons8_check_circle)
            }
            else
            {
                imagemvista.visibility = View.INVISIBLE
            }
        }
        override fun getLayout() = R.layout.row_criargrupo
    }

}