package com.example.projeto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class EditarPerfil : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_perfil)

        supportActionBar?.hide()

        val foto = findViewById<CircleImageView>(R.id.profile)

        Picasso.get().load(dados.profileImageurl).into(foto)


    }
}