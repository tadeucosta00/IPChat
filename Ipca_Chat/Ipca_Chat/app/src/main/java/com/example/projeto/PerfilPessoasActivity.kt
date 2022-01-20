package com.example.projeto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PerfilPessoasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_pessoas)

        var foto = findViewById<CircleImageView>(R.id.profile)
        var nome = findViewById<TextView>(R.id.name)
        var cursotxt = findViewById<TextView>(R.id.curso)
        var emailtxt = findViewById<TextView>(R.id.email)
        var anotxt = findViewById<TextView>(R.id.ano)


        supportActionBar?.title = nomePessoa

        nome.text = nomePessoa
        emailtxt.text = email
        anotxt.text = ano.toString() + "º ano"

        Picasso.get().load(fotoPessoa).into(foto)

        for (index in  0 until curso.size) {
            if(idCurso == curso[index].idcurso)
            {
                cursotxt.text = curso[index].nome
            }
        }
    }
}