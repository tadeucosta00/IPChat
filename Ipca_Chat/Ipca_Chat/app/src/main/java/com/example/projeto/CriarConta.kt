package com.example.projeto


import android.R.attr
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.projeto.Models.Utilizadores
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.R.attr.x


class CriarConta : AppCompatActivity() {
    lateinit var email : EditText
    lateinit var password : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_criar_conta)
        supportActionBar?.hide()

        email = findViewById<EditText>(R.id.email)
        password = findViewById<EditText>(R.id.password)

        val botao = findViewById<Button>(R.id.button)

        botao.setOnClickListener(){
        if (email.text.toString().isEmpty() || password.text.toString().isEmpty()){
            Toast.makeText(this, "Por favor preencha todos os campos!",Toast.LENGTH_LONG).show()
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.text.toString(),password.text.toString())
            .addOnCompleteListener{
                if (it.isSuccessful){
                    guardar()
                }
            }
            .addOnFailureListener{
                Toast.makeText(this, "Não foi possivel criar conta",Toast.LENGTH_LONG).show()

            }
        }
    }

    private fun guardar(){
        var atividade = false
        val db = Firebase.firestore
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val intent = Intent(this,Login::class.java)
        val username = findViewById<EditText>(R.id.username)
        val ano1 = findViewById<EditText>(R.id.username2)

        val n: Int = ano1.getText().toString().toInt()


        val user = Utilizadores(uid,username.text.toString(),"https://firebasestorage.googleapis.com/v0/b/projeto-a51e7.appspot.com/o/imagens%2Fdownload.png?alt=media&token=a05b2b39-6349-47e8-ad78-1764f9b32688",false,"Não Atribuido","",n, email.text.toString(),""
        )

        db.collection("utilizadores").document(uid).set(user)
            .addOnSuccessListener {
                Toast.makeText(this,"Conta criada com sucesso!!",Toast.LENGTH_SHORT).show()
                //startActivity(intent)
                //finish()
                ano1.text.clear()
                username.text.clear()
                email.text.clear()
                password.text.clear()
            }
            .addOnFailureListener {
                Toast.makeText(this,"Erro!!",Toast.LENGTH_SHORT).show()
            }

    }

}
