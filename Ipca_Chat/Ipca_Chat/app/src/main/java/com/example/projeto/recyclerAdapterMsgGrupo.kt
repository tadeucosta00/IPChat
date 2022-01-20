package com.example.projeto

import android.content.Context
import android.util.Base64
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projeto.Models.MensagensGrupos
import com.example.projeto.Models.pessoaUltimaconversa
import com.example.projeto.Models.pessoasGrupo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.kotlinandroidextensions.Item
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

private val secretKey = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
private val salt = "QWlGNHNhMTJTQWZ2bGhpV3U="
private val iv = "bVQzNFNhRkQ1Njc4UUFaWA=="

class recyclerAdapterMsgGrupo(val message: MensagensGrupos, val context: Context) : Item(){
    override fun getLayout(): Int {

        if(message.idemissor == FirebaseAuth.getInstance().currentUser!!.uid){
            return R.layout.row_eu
        }
        else{
            return R.layout.rowchat_ele
        }

    }
    override fun bind(
        viewHolder: com.xwray.groupie.kotlinandroidextensions.ViewHolder,
        position: Int
    ) {
        if(message.idemissor == FirebaseAuth.getInstance().currentUser!!.uid){
            var text = viewHolder.itemView.findViewById<TextView>(R.id.textView)

            var string1 :String

            val ivParameterSpec =  IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val spec =  PBEKeySpec(secretKey.toCharArray(), Base64.decode(salt, Base64.DEFAULT), 10000, 256)
            val tmp = factory.generateSecret(spec);
            val secretKey =  SecretKeySpec(tmp.encoded, "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            string1 = String(cipher.doFinal(Base64.decode(message.textomensagem, Base64.DEFAULT)))

            text.text = string1

        }
        else{
            var text1 = viewHolder.itemView.findViewById<TextView>(R.id.textView)

            var string1 :String

            val ivParameterSpec =  IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val spec =  PBEKeySpec(secretKey.toCharArray(), Base64.decode(salt, Base64.DEFAULT), 10000, 256)
            val tmp = factory.generateSecret(spec);
            val secretKey =  SecretKeySpec(tmp.encoded, "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            string1 = String(cipher.doFinal(Base64.decode(message.textomensagem, Base64.DEFAULT)))

            text1.text = string1

            val db = Firebase.firestore
            var pessoasgrupo : pessoasGrupo

            db.collection("utilizadores").document(message.idemissor).get().addOnSuccessListener { documentSnapshot ->
                pessoasgrupo = documentSnapshot.toObject(pessoasGrupo::class.java)!!
                var imagem = viewHolder.itemView.findViewById<CircleImageView>(R.id.imageView2)
                Glide.with(context).load(pessoasgrupo.profileImageurl).into(imagem)
                var nome =  viewHolder.itemView.findViewById<TextView>(R.id.nome)
                nome.text = pessoasgrupo.username
            }

        }

        setTimeText(viewHolder)
    }


    private fun setTimeText(viewHolder: RecyclerView.ViewHolder){
        val dateFormat = SimpleDateFormat
            .getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)

    }
}