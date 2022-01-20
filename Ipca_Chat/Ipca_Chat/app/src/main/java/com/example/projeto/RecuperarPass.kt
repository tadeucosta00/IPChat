package com.example.projeto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class RecuperarPass : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_pass)

        val email = findViewById<EditText>(R.id.email)
        val botao = findViewById<Button>(R.id.button)

        botao.setOnClickListener(){
            mAuth = FirebaseAuth.getInstance()
            mAuth!!.sendPasswordResetEmail(email.text.toString()) .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this,"Email enviado com sucesso!!", Toast.LENGTH_SHORT).show()
                    val intent2 = Intent(this,Login::class.java)
                    finish()
                    startActivity(intent2)
                }
                else {
                    Toast.makeText(this,"Algo correu mal!!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}