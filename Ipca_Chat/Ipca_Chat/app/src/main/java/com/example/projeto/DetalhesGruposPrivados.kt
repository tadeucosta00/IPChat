package com.example.projeto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projeto.Models.Utilizadores
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView


private lateinit var NomeChat:String
private lateinit var ImagemChat:String
private lateinit var idAdmin:String
private lateinit var idgrupo : String
private var userIds : ArrayList<String> = ArrayList()

class DetalhesGruposPrivados : AppCompatActivity() {

    private val adapter = GroupAdapter<ViewHolder>()
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_grupos_privados)

        var bundle :Bundle ?=intent.extras

        NomeChat = bundle!!.getString("GrupoNome").toString()
        ImagemChat = bundle!!.getString("GrupoImagem").toString()
        idAdmin = bundle!!.getString("GrupoIdAdmin").toString()
        idgrupo = bundle!!.getString("GrupoId").toString()
        userIds = bundle!!.getStringArrayList("userIds")!!

        supportActionBar?.title = NomeChat

        val foto = findViewById<CircleImageView>(R.id.profile)
        val textViewano = findViewById<TextView>(R.id.nome)
        val recycler = findViewById<RecyclerView>(R.id.RecyclerViewMembros)

        Glide.with(this).load(ImagemChat).into(foto)
        textViewano.text = NomeChat

        recycler.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler.setLayoutManager(layoutManager)
        recycler.adapter = adapter

        userIds.forEachIndexed{ index, userId ->
        db.collection("utilizadores").document(userId).get()
            .addOnSuccessListener { documents ->
                var utilizador = documents.toObject(Utilizadores::class.java)!!
                adapter.add(Users(utilizador))
            }
        }
    }
    class Users(val utilizador: Utilizadores) : Item<ViewHolder>() {

        override fun getLayout() = R.layout.rowdetalhesgrupos

        override fun bind(viewHolder: ViewHolder, position: Int) {
            var textNome = viewHolder.itemView.findViewById<TextView>(R.id.textViewPessoas)
            textNome.text = utilizador.username
            var imagem = viewHolder.itemView.findViewById<CircleImageView>(R.id.imageView)
            var admin = viewHolder.itemView.findViewById<TextView>(R.id.textViewPessoas2)

            var uid = FirebaseAuth.getInstance().uid
            Picasso.get().load(utilizador.profileImageurl).into(imagem)
            if(utilizador.uid == idAdmin){
                admin.visibility = View.VISIBLE
            }
        }
    }
}