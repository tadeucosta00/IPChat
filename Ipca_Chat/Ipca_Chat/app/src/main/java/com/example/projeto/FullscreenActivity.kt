package com.example.projeto

import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.widget.LinearLayout
import android.widget.TextView
import com.example.projeto.databinding.ActivityFullscreenBinding
import android.R
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.example.projeto.Models.Dados
import com.example.projeto.Models.Utilizadores
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging


class FullscreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullscreenBinding
    val db = Firebase.firestore
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)

        binding = ActivityFullscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val intent = Intent(this,Login::class.java)
        val timer = object: CountDownTimer(1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                if(FirebaseAuth.getInstance().currentUser == null)
                {
                    startActivity(intent)
                    finish()
                }
                else{
                    guardarDados()
                    loginConcluido()
                }
            }
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
        })

        timer.start()
    }


    fun guardarDados(){
        val uid = FirebaseAuth.getInstance().uid.toString()
        val dataRef = db.collection("utilizadores").document(uid)
        dataRef.get().addOnSuccessListener { documentSnapshot ->
            dados = documentSnapshot.toObject(Dados::class.java)!!
        }

    }

    fun loginConcluido(){
        val intent = Intent(this,PaginaInicial::class.java)
        val timer = object: CountDownTimer(2000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                startActivity(intent)
                finish()
            }
        }
        timer.start()

    }

}