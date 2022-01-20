package com.example.projeto.ui.grupos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projeto.ListaPessoasActivity
import com.example.projeto.Models.ChatDisciplina
import com.example.projeto.R
import com.example.projeto.SalaChat
import com.example.projeto.dados
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView

class GruposCurriculares : AppCompatActivity() {

    lateinit var recyclerView : RecyclerView
    private val adapter = GroupAdapter<ViewHolder>()
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grupos_curriculares)

        supportActionBar?.title = "Grupos Curriculares"

        recyclerView = findViewById(R.id.lisViewPessoas)

        db.collection("GruposChatsCurriculares").document(dados.idcurso).collection("Disciplinas").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    var chatsdisciplinas = document.toObject(ChatDisciplina::class.java)!!
                    if (chatsdisciplinas.Ano == dados.ano && dados.cargo == "Aluno"){
                        adapter.add(ChatsDisciplinas(chatsdisciplinas))
                    }
                    else if (chatsdisciplinas.idadmin == dados.uid && dados.cargo == "Professor"){
                        adapter.add(ChatsDisciplinas(chatsdisciplinas))
                    }
                }
        }
        recyclerView.adapter = adapter


        adapter.setOnItemClickListener{ item, view ->
            val chatescolhido = item as ChatsDisciplinas
            val intent = Intent(view.context, SalaChatGruposCurriculares::class.java)
            intent.putExtra("ChatDisciplinaId",chatescolhido.gruposcurricular.id)
            intent.putExtra("ChatDisciplinaNome", chatescolhido.gruposcurricular.NomeChat)
            intent.putExtra("ChatDisciplinaImagem", chatescolhido.gruposcurricular.GrupoImagem)
            intent.putExtra("ChatDisciplinaAno", chatescolhido.gruposcurricular.Ano)
            intent.putExtra("ChatDisciplinaIdAdmin", chatescolhido.gruposcurricular.idadmin)

            startActivity(intent)
        }

    }

    class ChatsDisciplinas(val gruposcurricular: ChatDisciplina) : Item<ViewHolder>() {

        override fun getLayout() = R.layout.row_gruposcurriculares

        override fun bind(viewHolder: ViewHolder, position: Int) {
            var textNome = viewHolder.itemView.findViewById<TextView>(R.id.textViewNomesGrupos)
            textNome.text = gruposcurricular.NomeChat
            var imagem = viewHolder.itemView.findViewById<CircleImageView>(R.id.imageViewGruposCurriculares)
            Picasso.get().load(gruposcurricular.GrupoImagem).into(imagem)

        }
    }
}