package com.example.projeto

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import com.example.projeto.Models.GroupChannel
import com.example.projeto.Models.Utilizadores
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.ArrayList

class CriarGrupo2Activity : AppCompatActivity() {
    private var userIds : ArrayList<String> = ArrayList()

    var selectedPhotoUri: Uri? = null
    var linkfoto : String = ""
    val db = Firebase.firestore
    lateinit var uploadfoto: CircleImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_criar_grupo2)

        supportActionBar?.title = "Criar Grupo"
        var bundle :Bundle ?=intent.extras
        userIds = bundle!!.getStringArrayList("userIds")!!
        userIds!!.add(dados.uid)

        val botaocriar = findViewById<Button>(R.id.button3)
        uploadfoto = findViewById<CircleImageView>(R.id.circleImageView2)

        val nomegrupo1 = findViewById<EditText>(R.id.nomegrupo)

        botaocriar.setOnClickListener(){
            if(linkfoto == ""){
                val nomegrupo = nomegrupo1.text.toString()
                val novoGrupo = db.collection("GruposPrivados").document()
                val groupChannel = GroupChannel(novoGrupo.id,nomegrupo, userIds, "https://firebasestorage.googleapis.com/v0/b/projeto-a51e7.appspot.com/o/imagens%2F74577.png?alt=media&token=f3b7535a-a5e6-4969-a49c-7e8eac484e82", dados.uid)
                novoGrupo.set(groupChannel).addOnSuccessListener {
                    userIds.forEachIndexed{ index, userId ->
                        println("userId = $userId")
                        if(userId == dados.uid)
                        {
                            db.collection("utilizadores")
                                .document(userId)
                                .collection("gruposprivados")
                                .document(novoGrupo.id)
                                .set(mapOf("chatid" to novoGrupo.id))
                        }
                        else
                        {
                            db.collection("utilizadores")
                                .document(userId)
                                .collection("gruposprivados")
                                .document(novoGrupo.id)
                                .set(mapOf("chatid" to novoGrupo.id))
                        }
                    }
                    val intent = Intent(this,PaginaInicial::class.java)
                    finish()
                    startActivity(intent)
                }
            }
            else{
                val nomegrupo = nomegrupo1.text.toString()
                val novoGrupo = db.collection("GruposPrivados").document()
                val groupChannel = GroupChannel(novoGrupo.id,nomegrupo, userIds, linkfoto, dados.uid)
                novoGrupo.set(groupChannel).addOnSuccessListener {
                    userIds.forEachIndexed{ index, userId ->
                        println("userId = $userId")
                        if(userId == dados.uid)
                        {
                            db.collection("utilizadores")
                                .document(userId)
                                .collection("gruposprivados")
                                .document(novoGrupo.id)
                                .set(mapOf("chatid" to novoGrupo.id))
                        }
                        else
                        {
                            db.collection("utilizadores")
                                .document(userId)
                                .collection("gruposprivados")
                                .document(novoGrupo.id)
                                .set(mapOf("chatid" to novoGrupo.id))
                        }
                    }
                    val intent = Intent(this,PaginaInicial::class.java)
                    startActivity(intent)
                }
            }

        }
        uploadfoto.setOnClickListener(){
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

            uploadfoto.setImageBitmap(bitmap)

            uploadfoto.alpha = 0f

            uploadImageToFirebaseStorage()
        }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/imagens/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    linkfoto = it.toString()
                }
            }
            .addOnFailureListener {
            }
    }
}