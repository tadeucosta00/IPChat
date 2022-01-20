package com.example.projeto.ui.grupos

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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder

class GruposFragment : Fragment() {

    lateinit var botao : FloatingActionButton

    val secretKey = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
    val salt = "QWlGNHNhMTJTQWZ2bGhpV3U="
    val iv = "bVQzNFNhRkQ1Njc4UUFaWA=="
    private lateinit var messagesSection : Section
    private  var shouldInitRecyclerView = true
    lateinit var recyclerUltimasMsgs : RecyclerView
    private lateinit var utilizadorConversa : pessoaUltimaconversa
    private val db = Firebase.firestore
    //private val adapter =  GroupAdapter<ViewHolder>()
    val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.grupos_fragment, container, false)
        recyclerUltimasMsgs = rootView.findViewById(R.id.mensagens)
        botao = rootView.findViewById(R.id.floatingActionButton2)
        return rootView
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        novasMsg(this::updateRecyclerView)

        botao.setOnClickListener(){
            val intent = Intent (getActivity(), GruposActivity::class.java)
            getActivity()?.startActivity(intent)
        }

    }

    fun novasMsg(onListen : (List<com.xwray.groupie.kotlinandroidextensions.Item>) -> Unit){
        val db = Firebase.firestore
        db.collection("utilizadores").document(dados.uid).collection("ultimasgruposprivados")
            .orderBy("time", Query.Direction.DESCENDING).addSnapshotListener() { querySnapshot, firebaseFirestoreException ->
                val items = mutableListOf<com.xwray.groupie.kotlinandroidextensions.Item>()
            querySnapshot!!.documents.forEach {
                items.add(NovasMsgGrupos(it.toObject(UltimasConversasGrupos::class.java)!!, requireContext(),))
            }
            onListen(items)
        }
    }

    private fun updateRecyclerView(messages: List<com.xwray.groupie.kotlinandroidextensions.Item>) {
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