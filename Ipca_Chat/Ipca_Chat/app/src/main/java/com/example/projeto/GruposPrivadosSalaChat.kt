package com.example.projeto

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projeto.*
import com.example.projeto.Models.*
import com.example.projeto.notifications.FirebaseService
import com.example.projeto.ui.grupos.*
import com.example.projeto.util.StorageUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.ArrayList


private lateinit var ChatGrupoNome:String
private lateinit var ChatGrupoImagem:String
private lateinit var ChatGrupoidAdmin : String
private lateinit var ChatGrupoid : String

class GruposPrivadosSalaChat : AppCompatActivity() {
    private var userIds : ArrayList<String> = ArrayList()
    val TAG = "SalaChat"
    val adapter = GroupAdapter<ViewHolder>()
    private  var shouldInitRecyclerView = true
    private lateinit var messagesSection : Section
    val secretKey = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
    val salt = "QWlGNHNhMTJTQWZ2bGhpV3U="
    val iv = "bVQzNFNhRkQ1Njc4UUFaWA=="
    private val RC_SELECT_IMAGE = 2
    val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    val chatChannelsCollectionRef = firestoreInstance.collection("GruposPrivados")
    lateinit var reyclermensagens: RecyclerView
    lateinit var Editxtmensagem: EditText

    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document("utilizadores/${FirebaseAuth.getInstance().currentUser?.uid
            ?: throw NullPointerException("UID is null.")}")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grupos_privados_sala_chat)

        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        var bundle :Bundle ?=intent.extras

        ChatGrupoNome = bundle!!.getString("GrupoNome").toString()
        ChatGrupoImagem = bundle!!.getString("GrupoImagem").toString()
        ChatGrupoidAdmin = bundle!!.getString("GrupoIdAdmin").toString()
        userIds = bundle!!.getStringArrayList("userIds")!!
        ChatGrupoid = bundle!!.getString("GrupoId")!!

        supportActionBar?.setDisplayShowCustomEnabled(true)
        val inflator = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v: View = inflator.inflate(R.layout.fotoactionbargrupo,null)
        val foto = v.findViewById<CircleImageView>(R.id.imageView4)
        Picasso.get().load(ChatGrupoImagem).into(foto)

        val textActionBar = v.findViewById<TextView>(R.id.textViewActionbar)

        textActionBar.text = ChatGrupoNome
        supportActionBar?.setCustomView(v)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val enviar = findViewById<Button>(R.id.enviar)
        val enviarfoto = findViewById<Button>(R.id.enviarfoto)

        reyclermensagens = findViewById<RecyclerView>(R.id.RecyclerViewMensagens)
        Editxtmensagem = findViewById<EditText>(R.id.mensagem)


        currentUserDocRef.collection("ultimasgruposprivados").document(ChatGrupoid).get().addOnSuccessListener{ documents ->
                    val ultimaconversa = documents.toObject(UltimasConversasGrupos::class.java)
                    val ultimaconversa1 = UltimasConversasGrupos(
                        ultimaconversa!!.idGrupo,
                        ultimaconversa.textomensagem,
                        ultimaconversa.idemissor,
                        ultimaconversa.time,
                        true,
                        ultimaconversa.tipo)

                    val uid = FirebaseAuth.getInstance().uid
                    if(uid != ultimaconversa.idemissor) {
                        currentUserDocRef.collection("ultimasgruposprivados").document(ChatGrupoid)
                            .set(ultimaconversa1)
                        firestoreInstance.collection("utilizadores").document(ultimaconversa.idemissor)
                            .collection("ultimasgruposprivados")
                            .document(ChatGrupoid).set(ultimaconversa1)
                    }
        }


        foto.setOnClickListener(){
            val intent = Intent(this, DetalhesGruposPrivados::class.java)

            intent.putExtra("GrupoNome", ChatGrupoNome)
            intent.putExtra("GrupoImagem", ChatGrupoImagem)
            intent.putExtra("GrupoIdAdmin", ChatGrupoidAdmin)
            intent.putExtra("GrupoId", ChatGrupoid)
            intent.putExtra("userIds", userIds)

            startActivity(intent)
        }

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
                    ChatGrupoid,
                    imagePath,
                    uid.toString(),
                    date,
                    false,
                    "IMAGEM")
                chatChannelsCollectionRef.document(ChatGrupoid)
                    .collection("mensagens").add(chatMessage)
            }
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
            ChatGrupoid,
            string1,
            meuID.toString(),
            data,
            false,
            "TEXT")

        if(Editxtmensagem.text.isEmpty()){
            Toast.makeText(this,"Mensagem sem conteudo!", Toast.LENGTH_LONG).show()
        }
        else {
            val db = Firebase.firestore
            chatChannelsCollectionRef.document(ChatGrupoid).collection("mensagens")
                .add(chatMessage).addOnSuccessListener {
                    Editxtmensagem.text.clear()
                }
            userIds.forEachIndexed{ index, userId ->
                db.collection("utilizadores").document(userId).collection("ultimasgruposprivados").document(ChatGrupoid).set(chatMessage)
            }
        }
    }

    fun atualizaMensagens(onListen : (List<Item>) -> Unit){

        chatChannelsCollectionRef.document(ChatGrupoid).collection("mensagens").orderBy("time")
            .addSnapshotListener{ querySnapshot, firebaseFirestoreException ->
                val items = mutableListOf<Item>()
                querySnapshot!!.documents.forEach {
                    if (it["tipo"] == "TEXT")
                        items.add(recyclerAdapterMsgGrupo(it.toObject(MensagensGrupos::class.java)!!,this@GruposPrivadosSalaChat))
                    else if(it["tipo"] == "IMAGEM")
                        items.add(RecyclerAdapterImgGrupo(it.toObject(MensagensGrupos::class.java)!!,this@GruposPrivadosSalaChat))
                }
                onListen(items)
            }
    }

    private fun updateRecyclerView(messages: List<Item>) {
        fun init(){
            reyclermensagens.apply {
                layoutManager = LinearLayoutManager(this@GruposPrivadosSalaChat)
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