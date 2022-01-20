package com.example.projeto

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.example.projeto.Models.Cursos
import com.example.projeto.Models.Dados
import com.example.projeto.Models.Utilizadores
import com.example.projeto.databinding.ActivityMainpBinding
import com.example.projeto.ui.calendario.CalendarioFragment
import com.example.projeto.ui.home.HomeFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

var curso : MutableList<Cursos> = arrayListOf()

class PaginaInicial : AppCompatActivity() {

    var online : Boolean = true
    val db = Firebase.firestore

    private lateinit var binding: ActivityMainpBinding
    private val CHANNEL_ID = "channel_id_example_01"
    private val notificationId = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        replaceFragment(HomeFragment())

        navView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.navigation_notifications -> {
                    replaceFragment(CalendarioFragment())
                    true
                }
                else -> false
            }
        }

        var uid = dados.uid

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }

             var token = task.result


        if(dados.token == ""){

                val user = Utilizadores(uid,dados.username, dados.profileImageurl,false, dados.idcurso,token,dados.ano,dados.email,dados.cargo)
                db.collection("utilizadores").document(uid).set(user)
                val dataRef = db.collection("utilizadores").document(uid)
                dataRef.get().addOnSuccessListener { documentSnapshot ->
                    dados = documentSnapshot.toObject(Dados::class.java)!!
                }

        }
        else if(token != dados.token){
            val user = Utilizadores(uid,dados.username, dados.profileImageurl,false, dados.idcurso,token,dados.ano,dados.email,dados.cargo)
            db.collection("utilizadores").document(uid).set(user)
            val dataRef = db.collection("utilizadores").document(uid)
            dataRef.get().addOnSuccessListener { documentSnapshot ->
                dados = documentSnapshot.toObject(Dados::class.java)!!
            }
        }

    })

        val user = Utilizadores(uid,dados.username, dados.profileImageurl,true, dados.idcurso, dados.token,dados.ano,dados.email,dados.cargo)

        db.collection("utilizadores").document(uid).set(user)
            .addOnSuccessListener {
                println("Online")
            }

        var cursos : Cursos

        db.collection("cursos").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    cursos = document.toObject(Cursos::class.java)
                    curso.add(Cursos(cursos.idcurso, cursos.nome))
                }
            }



        createNotificationChannel()

    }

    //botao envia uma notificacao para o nosso telemovel
    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Notification Title"
            val descriptionText = "Notification Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(){
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ipca_logo_rgb_v2)
            .setContentTitle("Example Title")
            .setContentText("Example Description")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)){
            notify(notificationId, builder.build())
        }
    }

    override fun onPause() {
        super.onPause()
        val uid = dados.uid
        val user = Utilizadores(uid,dados.username, dados.profileImageurl,false,dados.idcurso, dados.token,dados.ano,dados.email,dados.cargo)

        db.collection("utilizadores").document(uid).set(user)
            .addOnSuccessListener {
                println("Offline")
            }
    }
    override fun onResume() {
        super.onResume()
        val uid = dados.uid
        val user = Utilizadores(uid,dados.username, dados.profileImageurl,true,dados.idcurso, dados.token,dados.ano,dados.email,dados.cargo)
        db.collection("utilizadores").document(uid).set(user)
            .addOnSuccessListener {
                println("Offline")
            }
    }
    

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_layout, fragment)
            .commit()
    }

}
