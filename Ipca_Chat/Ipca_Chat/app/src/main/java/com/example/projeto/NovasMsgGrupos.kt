package com.example.projeto


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Base64
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projeto.Models.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.kotlinandroidextensions.Item
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


private val secretKey = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
private val salt = "QWlGNHNhMTJTQWZ2bGhpV3U="
private val iv = "bVQzNFNhRkQ1Njc4UUFaWA=="

class NovasMsgGrupos(val grupos: UltimasConversasGrupos, val context: Context) : Item(){
    override fun getLayout(): Int {
        return R.layout.row_ultimasmsgs
    }
    override fun bind(
        viewHolder: com.xwray.groupie.kotlinandroidextensions.ViewHolder,
        position: Int
    ) {
        var textNome = viewHolder.itemView.findViewById<TextView>(R.id.textView2)
        var textMsg = viewHolder.itemView.findViewById<TextView>(R.id.textView3)
        var foto = viewHolder.itemView.findViewById<CircleImageView>(R.id.imageView3)
        var textData = viewHolder.itemView.findViewById<TextView>(R.id.textView6)
        var imagemvista = viewHolder.itemView.findViewById<ImageView>(R.id.vista)

        val db = Firebase.firestore


        db.collection("GruposPrivados").document(grupos.idGrupo).get().addOnSuccessListener { document ->
            var informaçoesGrupos = document.toObject(GroupChannel::class.java)!!
            textNome.text = informaçoesGrupos.nomegrupo
            Glide.with(context).load(informaçoesGrupos.fotogrupo).into(foto)
        }

        var string1 :String

        val ivParameterSpec =  IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val spec =  PBEKeySpec(secretKey.toCharArray(), Base64.decode(salt, Base64.DEFAULT), 10000, 256)
        val tmp = factory.generateSecret(spec);
        val secretKey =  SecretKeySpec(tmp.encoded, "AES")

        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        string1 = String(cipher.doFinal(Base64.decode(grupos.textomensagem, Base64.DEFAULT)))

        val sdf = SimpleDateFormat("HH:mm dd/MM", Locale.FRENCH)
        val dateStr: String = sdf.format(grupos.time)
        textData.text = dateStr

        val uid = FirebaseAuth.getInstance().uid


        if(grupos.tipo == "TEXT") {
            if (uid == grupos.idemissor) {
                textMsg.text = "Tu: " + string1
                if (grupos.vista == true){
                    imagemvista.setImageResource(R.drawable.icons8_double_tick_48);
                }
            } else if (uid != grupos.idemissor) {
                textMsg.text = string1
            }

            else if(grupos.vista == false){
                imagemvista.setImageResource(R.drawable.icons8_check_64);
            }
            else if (grupos.idemissor != uid){
                if (grupos.vista == true){
                    imagemvista.setVisibility(View.INVISIBLE);
                }
            }
            if (grupos.idemissor != uid){
                if(grupos.vista == false){
                    textMsg.setTextColor(Color.BLACK)
                    textData.setTextColor(Color.BLACK)
                    imagemvista.setImageResource(R.drawable.mensagem);
                }
            }

        }
        else if(grupos.tipo == "IMAGEM"){
            if (uid == grupos.idemissor){
                textMsg.text = "Enviaste uma foto."
            }
            else if(uid != grupos.idemissor){
                textMsg.text = "Recebeste uma foto."
            }
        }


        viewHolder.itemView.setOnClickListener(){
            db.collection("GruposPrivados").document(grupos.idGrupo).get().addOnSuccessListener { documentSnapshot ->
                    var informaçoesGrupos = documentSnapshot.toObject(GroupChannel::class.java)!!
                    val intent = Intent(context, GruposPrivadosSalaChat::class.java)
                    intent.putExtra("GrupoNome", informaçoesGrupos.nomegrupo)
                    intent.putExtra("GrupoImagem", informaçoesGrupos.fotogrupo)
                    intent.putExtra("GrupoIdAdmin", informaçoesGrupos.idadmin)
                    intent.putExtra("GrupoId", informaçoesGrupos.idgrupo)
                    intent.putExtra("userIds", informaçoesGrupos.userIds)
                    context.startActivity(intent)
            }
        }
        setTimeText(viewHolder)
    }


    interface OnItemClickListener {
        fun onClick(view: View?, position: Int)
    }


    private fun setTimeText(viewHolder: RecyclerView.ViewHolder){
        val dateFormat = SimpleDateFormat
            .getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)

    }
}