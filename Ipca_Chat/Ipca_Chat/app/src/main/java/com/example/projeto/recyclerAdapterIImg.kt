package com.example.projeto


import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.example.projeto.Models.Mensagens
import com.google.firebase.auth.FirebaseAuth
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import com.google.android.material.imageview.ShapeableImageView
import de.hdodenhof.circleimageview.CircleImageView


class recyclerAdapterIImg(val messageImg: Mensagens, val context: Context) : Item(){
    override fun getLayout(): Int {

        if(messageImg.idemissor == FirebaseAuth.getInstance().currentUser!!.uid){
            return R.layout.row_image_chat_eu
        }
        else{
            return R.layout.row_image_chat_individual
        }

    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        if(messageImg.idemissor == FirebaseAuth.getInstance().currentUser!!.uid) {
            var img = viewHolder.itemView.findViewById<ShapeableImageView>(R.id.textView)
            Glide.with(context).load(messageImg.textomensagem).into(img)

        }
        if(messageImg.idemissor != FirebaseAuth.getInstance().currentUser!!.uid) {
            var img2 = viewHolder.itemView.findViewById<ShapeableImageView>(R.id.textView2)
            Glide.with(context).load(messageImg.textomensagem).into(img2)
            var foto = viewHolder.itemView.findViewById<CircleImageView>(R.id.imageView2)
            Glide.with(context).load(fotoPessoa).into(foto)
        }
        var context = viewHolder.itemView.getContext();

        viewHolder.itemView.setOnClickListener(){
            val intent = Intent(context, VerFoto::class.java)
            intent.putExtra("LinkFoto", messageImg.textomensagem)
            context.startActivity(intent)
        }
    }


}