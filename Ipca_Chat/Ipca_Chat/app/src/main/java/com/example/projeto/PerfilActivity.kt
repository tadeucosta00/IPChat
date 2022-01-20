package com.example.projeto

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.TextView
import com.example.projeto.Models.Dados
import com.example.projeto.Models.Utilizadores
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class PerfilActivity : AppCompatActivity() {

    lateinit var botaofoto : ImageView
    var selectedPhotoUri: Uri? = null
    var linkfoto : String = ""
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        val textViewNome = findViewById<TextView>(R.id.name)
        val textViewcurso = findViewById<TextView>(R.id.curso)
        val textViewano = findViewById<TextView>(R.id.ano)
        val textViewemail = findViewById<TextView>(R.id.email)

        val foto = findViewById<CircleImageView>(R.id.profile)


        textViewemail.text = dados.email
        supportActionBar?.hide()

        //botaofoto = findViewById<ImageView>(R.id.edit)
        //val botaoterminarSessao = findViewById<Button>(R.id.button2)


       // botaoterminarSessao.setOnClickListener(){
         //   logout()
       // }

        textViewNome.text = dados.username
        textViewano.text = dados.ano.toString() +  "º Ano"
        Picasso.get().load(dados.profileImageurl).into(foto)

        for (index in  0 until curso.size) {
            if(dados.idcurso == curso[index].idcurso)
            {
                textViewcurso.text = curso[index].nome
            }
        }


      //  botaofoto.setOnClickListener(){
        //    val intent = Intent(Intent.ACTION_PICK)
          //  intent.type = "image/*"
          //  startActivityForResult(intent, 0)
       // }

    }



}