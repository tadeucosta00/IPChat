package com.example.projeto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso

class VerFoto : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_foto)

        var bundle :Bundle ?=intent.extras
        var foto = bundle!!.getString("LinkFoto").toString()
        val imagem = findViewById<ImageView>(R.id.imageView5)

        supportActionBar?.hide()


        Glide.with(this).load(foto).into(imagem)

    }
}