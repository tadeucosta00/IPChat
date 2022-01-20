package com.example.projeto

import android.content.Context
import android.content.Intent
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.projeto.Models.MensagensGrupos
import com.example.projeto.Models.pessoasGrupo
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView


class RecyclerAdapterImgGrupo(val messageImg: MensagensGrupos, val context: Context) : Item(){
        override fun getLayout(): Int {

            if(messageImg.idemissor == FirebaseAuth.getInstance().currentUser!!.uid){
                return R.layout.row_image_chat_eu
            }
            else{
                return R.layout.row_image_chat_ele
            }

        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            if(messageImg.idemissor == FirebaseAuth.getInstance().currentUser!!.uid) {
                var img = viewHolder.itemView.findViewById<ShapeableImageView>(R.id.textView)
                Glide.with(context).load(messageImg.textomensagem).into(img)

            }
            if(messageImg.idemissor != FirebaseAuth.getInstance().currentUser!!.uid) {
                val db = Firebase.firestore
                var pessoasgrupo : pessoasGrupo
                db.collection("utilizadores").document(messageImg.idemissor).get().addOnSuccessListener { documentSnapshot ->
                    pessoasgrupo = documentSnapshot.toObject(pessoasGrupo::class.java)!!

                    var nome =  viewHolder.itemView.findViewById<TextView>(R.id.nome)
                    nome.text = pessoasgrupo.username

                    var img2 = viewHolder.itemView.findViewById<ShapeableImageView>(R.id.textView2)
                    Glide.with(context).load(messageImg.textomensagem).into(img2)

                    var foto = viewHolder.itemView.findViewById<CircleImageView>(R.id.imageView2)
                    Glide.with(context).load(pessoasgrupo.profileImageurl).into(foto)
                }
            }
            var context = viewHolder.itemView.getContext();

            viewHolder.itemView.setOnClickListener(){
                val intent = Intent(context, VerFoto::class.java)
                intent.putExtra("LinkFoto", messageImg.textomensagem)
                context.startActivity(intent)
            }
        }
}