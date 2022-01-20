package com.example.projeto.ui.conversas

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projeto.*
import com.example.projeto.Models.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import java.text.SimpleDateFormat
import java.util.*


class ConversasFragment : Fragment() {


    val secretKey = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
    val salt = "QWlGNHNhMTJTQWZ2bGhpV3U="
    val iv = "bVQzNFNhRkQ1Njc4UUFaWA=="
    private lateinit var messagesSection : Section
    private  var shouldInitRecyclerView = true
    lateinit var botao : FloatingActionButton
    lateinit var recyclerUltimasMsgs : RecyclerView
    private lateinit var utilizadorConversa : pessoaUltimaconversa
    private val db = Firebase.firestore
    //private val adapter =  GroupAdapter<ViewHolder>()
    val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document("utilizadores/${FirebaseAuth.getInstance().currentUser?.uid ?: throw NullPointerException("UID is null.")}")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val rootView = inflater.inflate(R.layout.conversas_fragment, container, false)

        botao = rootView.findViewById(R.id.floatingActionButton)

        recyclerUltimasMsgs = rootView.findViewById(R.id.mensagens)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        novasMsg(this::updateRecyclerView)

        botao.setOnClickListener(){
            val intent = Intent (getActivity(), ListaPessoasActivity::class.java)
            getActivity()?.startActivity(intent)
        }


    }


    fun novasMsg(onListen : (List<Item>) -> Unit){

        currentUserDocRef.collection("ultimasMsg").orderBy("time", Query.Direction.DESCENDING).addSnapshotListener() { querySnapshot, firebaseFirestoreException ->
            val items = mutableListOf<Item>()
            querySnapshot!!.documents.forEach {
                items.add(NovasMsg(it.toObject(Ultimasconversas::class.java)!!, requireContext()))
            }
            onListen(items)
        }
    }


    private fun updateRecyclerView(messages: List<Item>) {
        fun init(){
            recyclerUltimasMsgs.apply {
                layoutManager = LinearLayoutManager(context)
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

        recyclerUltimasMsgs.scrollToPosition(recyclerUltimasMsgs.adapter!!.itemCount - 1)

    }

}


