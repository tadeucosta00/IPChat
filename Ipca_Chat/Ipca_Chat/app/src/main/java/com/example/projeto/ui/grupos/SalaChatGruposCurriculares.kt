package com.example.projeto.ui.grupos

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projeto.*
import com.example.projeto.Models.*
import com.example.projeto.notifications.FirebaseService
import com.example.projeto.notifications.PushNotification
import com.example.projeto.notifications.RetrofitInstance
import com.example.projeto.util.StorageUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

private lateinit var ChatDisciplinaId:String
private lateinit var ChatDisciplinaNome:String
private lateinit var ChatDisciplinaImagem:String
private var ChatDisciplinaAno : Int = 0
private lateinit var ChatDisciplinaIdAdmin: String


class SalaChatGruposCurriculares : AppCompatActivity() {

    val TAG = "SalaChat"
    val adapter = GroupAdapter<ViewHolder>()
    private  var shouldInitRecyclerView = true
    private lateinit var messagesSection : Section
    val secretKey = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
    val salt = "QWlGNHNhMTJTQWZ2bGhpV3U="
    val iv = "bVQzNFNhRkQ1Njc4UUFaWA=="
    private val RC_SELECT_IMAGE = 2

    val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    val chatChannelsCollectionRef = firestoreInstance.collection("GruposChatsCurriculares")


    lateinit var reyclermensagens: RecyclerView
    lateinit var Editxtmensagem: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sala_chat_grupos_curriculares)

        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        var bundle :Bundle ?=intent.extras

        ChatDisciplinaId = bundle!!.getString("ChatDisciplinaId").toString()
        ChatDisciplinaNome = bundle!!.getString("ChatDisciplinaNome").toString()
        ChatDisciplinaImagem = bundle!!.getString("ChatDisciplinaImagem").toString()
        ChatDisciplinaAno = bundle!!.getInt("ChatDisciplinaAno")
        ChatDisciplinaIdAdmin = bundle!!.getString("ChatDisciplinaIdAdmin").toString()

        println("FDsfs" + ChatDisciplinaId)

        reyclermensagens = findViewById<RecyclerView>(R.id.RecyclerViewMensagens)
        Editxtmensagem = findViewById<EditText>(R.id.mensagem)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        reyclermensagens.layoutManager = layoutManager
        reyclermensagens.adapter = adapter

        val enviar = findViewById<Button>(R.id.enviar)
        val enviarfoto = findViewById<Button>(R.id.enviarfoto)

        supportActionBar?.setDisplayShowCustomEnabled(true)
        val inflator = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v: View = inflator.inflate(R.layout.fotoactionbargrupo,null)
        val foto = v.findViewById<CircleImageView>(R.id.imageView4)
        Picasso.get().load(ChatDisciplinaImagem).into(foto)

        foto.setOnClickListener(){
            val intent = Intent(this, DetalhesdoGrupo::class.java)

            intent.putExtra("ChatDisciplinaId",ChatDisciplinaId)
            intent.putExtra("ChatDisciplinaNome", ChatDisciplinaNome)
            intent.putExtra("ChatDisciplinaImagem", ChatDisciplinaImagem)
            intent.putExtra("ChatDisciplinaAno", ChatDisciplinaAno)
            intent.putExtra("ChatDisciplinaIdAdmin", ChatDisciplinaIdAdmin)

            startActivity(intent)
        }

        val textActionBar = v.findViewById<TextView>(R.id.textViewActionbar)


        textActionBar.text = ChatDisciplinaNome

        supportActionBar?.setCustomView(v)
        supportActionBar?.setDisplayShowTitleEnabled(false)


        atualizaMensagens(this::updateRecyclerView)

        enviarfoto.setOnClickListener(){
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Selecione a foto"), RC_SELECT_IMAGE)
        }

        enviar.setOnClickListener(){

            enviarMsg()
            //if(title.isNotEmpty() && message.isNotEmpty() && recipientToken.isNotEmpty()) {
              //  PushNotification(
                //    NotificationData(title, message, userPhoto),
                  //  recipientToken
                //).also {
                    //sendNotification(it)
                //}
            //}
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater : MenuInflater = getMenuInflater()
        inflater.inflate(R.menu.menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val tipo = "IMAGEM"
        val date = Calendar.getInstance().time
        val uid = FirebaseAuth.getInstance().uid

        if (requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK &&
            data != null && data.data != null) {
            val selectedImagePath = data.data

            val selectedImageBmp = MediaStore.Images.Media.getBitmap(contentResolver, selectedImagePath)

            val outputStream = ByteArrayOutputStream()

            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            val selectedImageBytes = outputStream.toByteArray()

            StorageUtil.uploadMessageImage(selectedImageBytes) { imagePath ->
                        val chatMessage = MensagensGrupos(
                            dados.idcurso,
                            imagePath,
                            uid.toString(),
                            date,
                            false,
                            "IMAGEM")
                chatChannelsCollectionRef.document(dados.idcurso)
                    .collection("Disciplinas").document(ChatDisciplinaId)
                        .collection("mensagens").add(chatMessage)

            }
        }
    }


    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d(TAG, "Response: ${Gson().toJson(response)}")
            } else {
                Log.e(TAG, response.errorBody().toString())
            }
        } catch(e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

    private fun enviarMsg(){
        Editxtmensagem = findViewById<EditText>(R.id.mensagem)
        val tipo = "TEXT"

        var id : MutableList<idMensagem> = arrayListOf()
        val data = Calendar.getInstance().time

        var string : String
        var string1: String

        string = Editxtmensagem.getText().toString()


        val ivParameterSpec = IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val spec =  PBEKeySpec(secretKey.toCharArray(), Base64.decode(salt, Base64.DEFAULT), 10000, 256)
        val tmp = factory.generateSecret(spec)
        val secretKey =  SecretKeySpec(tmp.encoded, "AES")

        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)
        string1 = Base64.encodeToString(cipher.doFinal(string.toByteArray(Charsets.UTF_8)), Base64.DEFAULT)


        var bundle: Bundle? = intent.extras
        val meuID = FirebaseAuth.getInstance().uid
        val chatMessage = MensagensGrupos(
            dados.idcurso,
            string1,
            meuID.toString(),
            data,
            false,
            "TEXT")

        if(Editxtmensagem.text.isEmpty()){
            Toast.makeText(this,"Mensagem sem conteudo!", Toast.LENGTH_LONG).show()
        }
        else {
            chatChannelsCollectionRef.document(dados.idcurso).collection("Disciplinas").document(ChatDisciplinaId).collection("mensagens")
                .add(chatMessage).addOnSuccessListener {
                    Editxtmensagem.text.clear()
                }
        }
    }

    fun atualizaMensagens(onListen : (List<Item>) -> Unit){

        chatChannelsCollectionRef.document(dados.idcurso).collection("Disciplinas").document(ChatDisciplinaId).collection("mensagens").orderBy("time")
        .addSnapshotListener{ querySnapshot, firebaseFirestoreException ->
             val items = mutableListOf<Item>()
             querySnapshot!!.documents.forEach {
             if (it["tipo"] == "TEXT")
                  items.add(recyclerAdapterMsgGrupo(it.toObject(MensagensGrupos::class.java)!!,this@SalaChatGruposCurriculares))
             else if(it["tipo"] == "IMAGEM")
                  items.add(RecyclerAdapterImgGrupo(it.toObject(MensagensGrupos::class.java)!!,this@SalaChatGruposCurriculares))
             }
             onListen(items)
        }
    }

    private fun updateRecyclerView(messages: List<Item>) {
        fun init(){
            reyclermensagens.apply {
                layoutManager = LinearLayoutManager(this@SalaChatGruposCurriculares)
                adapter=GroupAdapter<ViewHolder>().apply {
                    messagesSection= Section(messages)
                    this.add(messagesSection)
                }
                shouldInitRecyclerView = false
            }
        }

        fun updateItems()=messagesSection.update(messages)

        if(shouldInitRecyclerView)
            init()
        else
            updateItems()

        reyclermensagens.scrollToPosition(reyclermensagens.adapter!!.itemCount - 1)
    }

}