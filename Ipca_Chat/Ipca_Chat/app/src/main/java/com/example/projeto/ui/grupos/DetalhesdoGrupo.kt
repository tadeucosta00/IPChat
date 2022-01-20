package com.example.projeto.ui.grupos

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projeto.ListaPessoasActivity
import com.example.projeto.Models.ChatDisciplina
import com.example.projeto.Models.MensagensGrupos
import com.example.projeto.Models.Utilizadores
import com.example.projeto.R
import com.example.projeto.dados
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class DetalhesdoGrupo : AppCompatActivity() {

    private lateinit var ChatDisciplinaId:String
    private lateinit var ChatDisciplinaNome:String
    private lateinit var ChatDisciplinaImagem:String
    private var ChatDisciplinaAno : Int = 0
    private lateinit var ChatDisciplinaIdAdmin: String
    var selectedPhotoUri: Uri? = null
    var linkfoto : String = ""
    lateinit var editarimagem: CircleImageView
    private val adapter = GroupAdapter<ViewHolder>()
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhesdo_grupo)

        var bundle :Bundle ?=intent.extras

        ChatDisciplinaId = bundle!!.getString("ChatDisciplinaId").toString()
        ChatDisciplinaNome = bundle!!.getString("ChatDisciplinaNome").toString()
        ChatDisciplinaImagem = bundle!!.getString("ChatDisciplinaImagem").toString()
        ChatDisciplinaAno = bundle!!.getInt("ChatDisciplinaAno")
        ChatDisciplinaIdAdmin = bundle!!.getString("ChatDisciplinaIdAdmin").toString()

        supportActionBar?.title = ChatDisciplinaNome

        val foto = findViewById<CircleImageView>(R.id.profile)
        val textViewano = findViewById<TextView>(R.id.nome)
        val recycler = findViewById<RecyclerView>(R.id.RecyclerViewMembros)
        editarimagem = findViewById<CircleImageView>(R.id.editar)

        Glide.with(this).load(ChatDisciplinaImagem).into(foto)
        textViewano.text = ChatDisciplinaNome

        recycler.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler.setLayoutManager(layoutManager)
        recycler.adapter = adapter

        db.collection("utilizadores").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    var utilizador = document.toObject(Utilizadores::class.java)!!
                    if(utilizador.idcurso == dados.idcurso && utilizador.ano == dados.ano && utilizador.cargo == "Aluno"){
                        adapter.add(Users(utilizador))
                    }
                    else if (utilizador.cargo == "Professor" && utilizador.uid == ChatDisciplinaIdAdmin){
                        adapter.add(Users(utilizador))
                    }
                }
            }

        if(ChatDisciplinaIdAdmin == dados.uid){
               editarimagem.visibility = View.VISIBLE
        }

       // editarimagem.setOnClickListener(){
         //   val intent = Intent(Intent.ACTION_PICK)
           // intent.type = "image/*"
          //  startActivityForResult(intent, 0)
       // }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            editarimagem.setImageBitmap(bitmap)

            editarimagem.alpha = 0f

            uploadImageToFirebaseStorage()
        }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/imagens/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    linkfoto = it.toString()
                     val grupodados = ChatDisciplina(ChatDisciplinaNome, linkfoto, ChatDisciplinaAno, ChatDisciplinaId, ChatDisciplinaIdAdmin)
                        db.collection("GruposChatsCurriculares").document(dados.idcurso).collection("Disciplinas")
                        .document(ChatDisciplinaId).set(grupodados)
                }
            }
            .addOnFailureListener {
            }
    }

    class Users(val utilizador: Utilizadores) : Item<ViewHolder>() {

        override fun getLayout() = R.layout.row_pessoas

        override fun bind(viewHolder: ViewHolder, position: Int) {
            var textNome = viewHolder.itemView.findViewById<TextView>(R.id.textViewPessoas)
            textNome.text = utilizador.username
            var imagem = viewHolder.itemView.findViewById<CircleImageView>(R.id.imageView)
            Picasso.get().load(utilizador.profileImageurl).into(imagem)
        }

    }
}