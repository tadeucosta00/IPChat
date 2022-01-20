package com.example.projeto

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projeto.Models.*
import com.example.projeto.notifications.FirebaseService
import com.example.projeto.notifications.NotificationData
import com.example.projeto.notifications.PushNotification
import com.example.projeto.notifications.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
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
import java.util.*
import com.example.projeto.util.StorageUtil
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

import java.io.ByteArrayOutputStream

lateinit var fotoPessoa:String
lateinit var nomePessoa:String
lateinit var idPessoa:String
lateinit var idCurso:String
lateinit var token:String
lateinit var email:String
var ano :Int = 0

var atividade:Boolean = false
const val TOPIC = "/topics/myTopic"

class SalaChat : AppCompatActivity() {

    val TAG = "SalaChat"
    val adapter = GroupAdapter<ViewHolder>()
    private  var shouldInitRecyclerView = true
    private lateinit var messagesSection : Section

    val secretKey = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
    val salt = "QWlGNHNhMTJTQWZ2bGhpV3U="
    val iv = "bVQzNFNhRkQ1Njc4UUFaWA=="

    private val RC_SELECT_IMAGE = 2

    val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    val chatChannelsCollectionRef = firestoreInstance.collection("chats")


    lateinit var reyclermensagens: RecyclerView
    lateinit var Editxtmensagem: EditText

    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document("utilizadores/${FirebaseAuth.getInstance().currentUser?.uid
            ?: throw NullPointerException("UID is null.")}")

    @SuppressLint("QueryPermissionsNeeded")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        atualizaMensagens(this::updateRecyclerView)

        reyclermensagens = findViewById<RecyclerView>(R.id.RecyclerViewMensagens)
        Editxtmensagem = findViewById<EditText>(R.id.mensagem)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        reyclermensagens.layoutManager = layoutManager
        reyclermensagens.adapter = adapter


        val enviar = findViewById<Button>(R.id.enviar)
        val enviarfoto = findViewById<Button>(R.id.enviarfoto)
        val enviaranexo = findViewById<Button>(R.id.enviaranexo)

        enviaranexo.setOnClickListener(){
            val intent = Intent(Intent.ACTION_PICK)
            intent.setType("application/pdf");
            startActivityForResult(Intent.createChooser(intent, "Selecione a um ficheiro"), RC_SELECT_IMAGE)
        }

        enviarfoto.setOnClickListener(){
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Selecione a foto"), RC_SELECT_IMAGE)
        }


        var bundle :Bundle ?=intent.extras

        idPessoa    = bundle!!.getString("UTILIZADOR").toString()
        fotoPessoa  = bundle.getString("UTILIZADORFOTO").toString()
        nomePessoa  = bundle.getString("UTILIZADORNOME").toString()
        atividade   = bundle.getBoolean("UTILIZADORATIVIDADE")
        idCurso     = bundle.getString("UTILIZADORCURSO").toString()
        token       = bundle.getString("UTILIZADORTOKEN").toString()
        email       = bundle.getString("UTILIZADOREMAIL").toString()
        ano         = bundle.getInt("UTILIZADORANO")

        supportActionBar?.setDisplayShowCustomEnabled(true)
        val inflator = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v: View = inflator.inflate(R.layout.fotoactionbar,null)
        val foto = v.findViewById<CircleImageView>(R.id.imageView4)
        Picasso.get().load(fotoPessoa).into(foto)


        val textActionBar = v.findViewById<TextView>(R.id.textViewActionbar)
        val textViewAtividade = v.findViewById<TextView>(R.id.textViewOnline)

        currentUserDocRef.collection("salasdechats").document(idPessoa.toString()).get().addOnSuccessListener{ documents ->
            val idChat = documents.toObject(idChat::class.java)
            if (idChat != null) {
                currentUserDocRef.collection("ultimasMsg").document(idChat.chatId).get().addOnSuccessListener{ documents ->
                    val ultimaconversa = documents.toObject(Ultimasconversas::class.java)
                    val ultimaconversa1 = Ultimasconversas(
                        ultimaconversa!!.textomensagem,
                        ultimaconversa.idemissor,
                        ultimaconversa.idrecetor,
                        ultimaconversa.time,
                        true,
                        ultimaconversa.tipo)

                     val uid = FirebaseAuth.getInstance().uid
                    if(uid != ultimaconversa.idemissor){
                        currentUserDocRef.collection("ultimasMsg").document(idChat.chatId).set(ultimaconversa1)
                        firestoreInstance.collection("utilizadores").document(idPessoa.toString()).collection("ultimasMsg")
                            .document(idChat.chatId).set(ultimaconversa1)
                    }
                }
            }
        }

        foto.setOnClickListener(){
            val intent = Intent(this,PerfilPessoasActivity::class.java)
            intent.putExtra("UTILIZADOR", idPessoa)
            intent.putExtra("UTILIZADORNOME", nomePessoa)
            intent.putExtra("UTILIZADORFOTO", fotoPessoa)
            intent.putExtra("UTILIZADORCURSO", curso.toString())
            intent.putExtra("UTILIZADORATIVIDADE", atividade)
            startActivity(intent)
        }

        textActionBar.text = nomePessoa
        if(atividade == true){
            textViewAtividade.text = "Online agora"
        }
        else{
            textViewAtividade.text = "Offline"
        }
        supportActionBar?.setCustomView(v)
        supportActionBar?.setDisplayShowTitleEnabled(false)



        enviar.setOnClickListener(){
            println("TOken" + token)
            val title = dados.username
            val message = Editxtmensagem.text.toString()
            val userPhoto = dados.profileImageurl
            val recipientToken = token

            enviarMsg()

            if(title.isNotEmpty() && message.isNotEmpty() && recipientToken.isNotEmpty()) {
                PushNotification(
                    NotificationData(title, message, userPhoto),
                    recipientToken
                ).also {
                    sendNotification(it)
                }
            }
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
                currentUserDocRef.collection("salasdechats").document(idPessoa.toString()).get()
                    .addOnSuccessListener { documents ->
                        val idChat = documents.toObject(idChat::class.java)
                        val chatMessage = Mensagens(
                            imagePath,
                            uid.toString(),
                            idPessoa.toString(),
                            date,
                            false,
                            "IMAGEM")
                        chatChannelsCollectionRef.document(idChat!!.chatId)
                            .collection("mensagens")
                            .add(chatMessage)

                        currentUserDocRef.collection("ultimasMsg").document(idChat.chatId).set(chatMessage)
                        firestoreInstance.collection("utilizadores").document(idPessoa.toString()).collection("ultimasMsg")
                            .document(idChat.chatId).set(chatMessage)

                    }
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
        var idPessoa = bundle!!.getString("UTILIZADOR")
        val chatMessage = Mensagens(
            string1,
            meuID.toString(),
            idPessoa.toString(),
            data,
            false,
            "TEXT")

        if(Editxtmensagem.text.isEmpty()){
            Toast.makeText(this,"Mensagem sem conteudo!",Toast.LENGTH_LONG).show()
        }
        else {
            currentUserDocRef.collection("salasdechats").document(idPessoa.toString()).get()
                .addOnSuccessListener{ documents ->
                    val idChat = documents.toObject(idChat::class.java)
                    currentUserDocRef.collection("salasdechats")
                        .document(idPessoa.toString()).get().addOnSuccessListener {
                            if (idChat != null) {
                                chatChannelsCollectionRef.document(idChat.chatId)
                                    .collection("mensagens")
                                    .add(chatMessage).addOnSuccessListener {
                                        Editxtmensagem.text.clear()
                                    }

                                currentUserDocRef.collection("ultimasMsg").document(idChat.chatId).set(chatMessage)
                                firestoreInstance.collection("utilizadores").document(idPessoa.toString()).collection("ultimasMsg")
                                    .document(idChat.chatId).set(chatMessage)

                            }
                            else{

                                val novoChat = chatChannelsCollectionRef.document()
                                novoChat.set(ChatChannel(mutableListOf(meuID.toString(), idPessoa.toString())))

                                chatChannelsCollectionRef.document(novoChat.id)
                                    .collection("mensagens")
                                    .add(chatMessage).addOnSuccessListener {
                                        Editxtmensagem.text.clear()
                                    }


                                currentUserDocRef.collection("ultimasMsg").document(novoChat.id).set(chatMessage)

                                firestoreInstance.collection("utilizadores").document(idPessoa.toString()).collection("ultimasMsg")
                                    .document(novoChat.id).set(chatMessage)


                                currentUserDocRef
                                    .collection("salasdechats")
                                    .document(idPessoa.toString())
                                    .set(mapOf("chatId" to novoChat.id))

                                firestoreInstance.collection("utilizadores").document(idPessoa.toString())
                                    .collection("salasdechats")
                                    .document(meuID.toString())
                                    .set(mapOf("chatId" to novoChat.id))
                            }
                        }
                    }
                atualizaMensagens(this::updateRecyclerView)
                }
            }

        fun atualizaMensagens(onListen : (List<Item>) -> Unit){

            var bundle: Bundle? = intent.extras
            var idPessoa = bundle!!.getString("UTILIZADOR")



            currentUserDocRef.collection("salasdechats").document(idPessoa.toString()).get()
                .addOnSuccessListener{ documents ->
                    val idChat = documents.toObject(idChat::class.java)
                    currentUserDocRef.collection("salasdechats")
                        .document(idPessoa.toString()).get().addOnSuccessListener {
                            if (idChat != null)
                            {
                                chatChannelsCollectionRef.document(idChat.chatId).collection("mensagens").orderBy("time")
                                    .addSnapshotListener{ querySnapshot, firebaseFirestoreException ->
                                        val items = mutableListOf<Item>()
                                        querySnapshot!!.documents.forEach {
                                            if (it["tipo"] == "TEXT")
                                                items.add(recyclerAdapterMsg(it.toObject(Mensagens::class.java)!!,this@SalaChat))
                                            else if(it["tipo"] == "IMAGEM")
                                                items.add(recyclerAdapterIImg(it.toObject(Mensagens::class.java)!!,this@SalaChat))
                                        }
                                        onListen(items)
                                    }
                            }
                }
            }

        }


    private fun updateRecyclerView(messages: List<Item>) {
        fun init(){
            reyclermensagens.apply {
                layoutManager = LinearLayoutManager(this@SalaChat)
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