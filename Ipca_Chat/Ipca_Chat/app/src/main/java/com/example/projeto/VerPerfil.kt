package com.example.projeto

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.projeto.Models.Dados
import com.example.projeto.Models.Utilizadores
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class VerPerfil : AppCompatActivity() {

    var selectedPhotoUri: Uri? = null
    var linkfoto : String = ""
    val db = Firebase.firestore
    lateinit var editarfoto : CircleImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_perfil)

        supportActionBar?.title = "Eu"

        val foto = findViewById<CircleImageView>(R.id.circleImageView)
        editarfoto = findViewById<CircleImageView>(R.id.circleImageVieweditar)
        val nome = findViewById<TextView>(R.id.textView4)

        val verperfil = findViewById<RelativeLayout>(R.id.verperfil)
        val terminarsessao = findViewById<RelativeLayout>(R.id.terminarsessao)
        val verplataforma = findViewById<RelativeLayout>(R.id.verplataforma)

        nome.text = dados.username
        Glide.with(this).load(dados.profileImageurl).into(foto)

        verplataforma.setOnClickListener(){
            //val intent = Intent(this, PlataformaActivity::class.java)
            //startActivity(intent)
            val url = "https://portal.ipca.pt/Intranet/Home/PublicIndex"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        println("fdssfsd" + dados.cargo)
        verperfil.setOnClickListener(){
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
        }
        terminarsessao.setOnClickListener(){
            FirebaseAuth.getInstance().signOut()
            finish()
            var intent = Intent(this, Login::class.java).apply {
            }
            startActivity(intent)
        }
        editarfoto.setOnClickListener(){
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            editarfoto.setImageBitmap(bitmap)

            editarfoto.alpha = 0f

            uploadImageToFirebaseStorage()

        }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/imagens/$filename")
        val uid = dados.uid

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {

                ref.downloadUrl.addOnSuccessListener {
                    linkfoto = it.toString()

                    val user = Utilizadores(uid, dados.username, linkfoto, true, dados.idcurso, dados.token,dados.ano,dados.email,dados.cargo)
                    db.collection("utilizadores").document(uid).set(user)
                        .addOnSuccessListener {
                            println("Foto atualizada")
                        }
                }
            }
            .addOnFailureListener {

            }
    }


}