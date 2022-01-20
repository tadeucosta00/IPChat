package com.example.projeto.ui.grupos

import android.app.PendingIntent.getActivity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projeto.*
import com.example.projeto.Models.GroupChannel
import com.example.projeto.Models.Utilizadores
import com.example.projeto.Models.idChat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView

class GruposActivity : AppCompatActivity() {

    lateinit var recyclerView : RecyclerView
    lateinit var textView: TextView
    lateinit var foto : CircleImageView
    lateinit var relative : RelativeLayout
    private val adapter = GroupAdapter<ViewHolder>()
    val db = Firebase.firestore
    lateinit var botao : FloatingActionButton
    val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grupos)

        supportActionBar?.title = "Grupos"

        botao = findViewById(R.id.floatingActionButton2)
        textView = findViewById(R.id.textViewPessoas)
        foto = findViewById(R.id.imageView)
        relative = findViewById(R.id.curso)

        recyclerView = findViewById(R.id.mensagens)
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.setLayoutManager(layoutManager)
        recyclerView.adapter = adapter

        db.collection("GruposChatsCurriculares").document(dados.idcurso).get().addOnSuccessListener{ documentSnapshot ->
            var grupocurricular = documentSnapshot.toObject(com.example.projeto.Models.GruposCurriculares::class.java)!!
            textView.text = grupocurricular.NomeChat
            Picasso.get().load(grupocurricular.GrupoImagem).into(foto)
        }

        db.collection("utilizadores").document(dados.uid).collection("gruposprivados").get().addOnSuccessListener { documents ->
            for (document in documents) {
                val id = document.id
                db.collection("GruposPrivados").document(id).get().addOnSuccessListener { documents ->
                    var grupo = documents.toObject(GroupChannel::class.java)!!
                    adapter.add(Grupos(grupo))
                }
            }
            adapter.setOnItemClickListener{ item, view ->
                val chatescolhido = item as Grupos
                val intent = Intent(view.context, GruposPrivadosSalaChat::class.java)
                intent.putExtra("GrupoId", chatescolhido.grupos.idgrupo)
                intent.putExtra("GrupoNome", chatescolhido.grupos.nomegrupo)
                intent.putExtra("GrupoImagem", chatescolhido.grupos.fotogrupo)
                intent.putExtra("GrupoIdAdmin", chatescolhido.grupos.idadmin)
                val bundle = Bundle()
                bundle.putStringArrayList("userIds", chatescolhido.grupos.userIds)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }

        botao.setOnClickListener(){
            val intent = Intent (this, CriarGrupos::class.java)
            startActivity(intent)
        }
        relative.setOnClickListener(){
            val intent = Intent (this, GruposCurriculares::class.java)
            startActivity(intent)
        }
    }
    class Grupos(val grupos: GroupChannel) : Item<ViewHolder>() {

        override fun getLayout() = R.layout.row_pessoas

        override fun bind(viewHolder: ViewHolder, position: Int) {
            var textNome = viewHolder.itemView.findViewById<TextView>(R.id.textViewPessoas)
            textNome.text = grupos.nomegrupo
            var imagem = viewHolder.itemView.findViewById<CircleImageView>(R.id.imageView)
            Picasso.get().load(grupos.fotogrupo).into(imagem)
        }
    }
}