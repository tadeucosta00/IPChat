package com.example.projeto

import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.*
import com.example.projeto.Models.Dados
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern
import com.google.firebase.firestore.DocumentSnapshot

import com.google.android.gms.tasks.OnSuccessListener

import com.google.firebase.firestore.DocumentReference
import org.w3c.dom.Text


lateinit var dados : Dados
class Login : AppCompatActivity() {

    lateinit var uid :  String
    val db = Firebase.firestore



    val EMAIL_ADDRESS_PATTERN = Pattern.compile(
        "[a0-9]{1,256}@alunos.ipca.pt"
    )
    val EMAIL_ADDRESS_PATTERN1 = Pattern.compile(
        "[a-z]{1,256}@ipca.pt"
    )

    fun isValidString(str: String): Boolean{
        return EMAIL_ADDRESS_PATTERN.matcher(str.toString()).matches() || EMAIL_ADDRESS_PATTERN1.matcher(str.toString()).matches()
    }

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)

        supportActionBar?.hide()

        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val botao = findViewById<Button>(R.id.button)
        val botaorecuperar = findViewById<TextView>(R.id.recuperar)

        auth = Firebase.auth

        botaorecuperar.setOnClickListener(){
            val intent2 = Intent(this,RecuperarPass::class.java)
            startActivity(intent2)
        }


        botao.setOnClickListener(){
            val emails = arrayOf<String>(email.text.toString())

            if (email.text.toString().isEmpty() || password.text.toString().isEmpty()){
                Toast.makeText(this, "Por favor preencha todos os campos!",Toast.LENGTH_LONG).show()
            }

            emails.forEach {
                Log.d("MainActivity", "is valid email $it => ${isValidString(it)}")
                if(isValidString(it) == true){
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email.text.toString(),password.text.toString())
                        .addOnCompleteListener{
                            if (it.isSuccessful){
                                uid = FirebaseAuth.getInstance().uid.toString()
                                guardarDados()
                                loginConcluido()
                            }
                        }
                        .addOnFailureListener{
                            Toast.makeText(this, "As creedencias inseridas nao são validas!",Toast.LENGTH_LONG).show()
                        }
                    }
                else if(isValidString(it) == false){
                    Toast.makeText(this, "O email inserido não pertence a esta instituição!",Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun guardarDados(){
        val dataRef = db.collection("utilizadores").document(uid)
        dataRef.get().addOnSuccessListener { documentSnapshot ->
            dados = documentSnapshot.toObject(Dados::class.java)!!
        }
    }

    fun loginConcluido(){
        val progressDialog = ProgressDialog(this@Login)
        val intent = Intent(this,PaginaInicial::class.java)
        progressDialog.setTitle("A carregar mensagens...")
        progressDialog.setMessage("A carregar mensagens, Por favor aguarde...")
        progressDialog.show()
        val timer = object: CountDownTimer(2000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                progressDialog.hide()
                startActivity(intent)
                finish()
            }
        }
        timer.start()
    }
}