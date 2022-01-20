package com.example.projeto


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Base64
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projeto.Models.Ultimasconversas
import com.example.projeto.Models.pessoaUltimaconversa
import com.example.projeto.ui.conversas.ConversasFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
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

class NovasMsg(val ultimasMsg: Ultimasconversas, val context: Context) : Item(){
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
          var fotoonline = viewHolder.itemView.findViewById<ImageView>(R.id.online2)
          var textData = viewHolder.itemView.findViewById<TextView>(R.id.textView6)
          var imagemvista = viewHolder.itemView.findViewById<ImageView>(R.id.vista)


        val sdf = SimpleDateFormat("HH:mm dd/MM", Locale.FRENCH)
        val dateStr: String = sdf.format(ultimasMsg.time)
        textData.text = dateStr

        val uid = FirebaseAuth.getInstance().uid

         val db = Firebase.firestore
         var PessoaUltimaconversa : pessoaUltimaconversa

         if(uid == ultimasMsg.idrecetor){
           db.collection("utilizadores").document(ultimasMsg.idemissor).get().addOnSuccessListener { documentSnapshot ->
               PessoaUltimaconversa = documentSnapshot.toObject(pessoaUltimaconversa::class.java)!!
                textNome.text = PessoaUltimaconversa.username
               Glide.with(context).load(PessoaUltimaconversa.profileImageurl).into(foto)
               if (PessoaUltimaconversa.atividade == true){
                   fotoonline.visibility = View.VISIBLE
               }
               else if(PessoaUltimaconversa.atividade == false){
                   fotoonline.visibility = View.INVISIBLE
               }
           }
          }
        else if(uid == ultimasMsg.idemissor){
          db.collection("utilizadores").document(ultimasMsg.idrecetor).get().addOnSuccessListener { documentSnapshot ->
                PessoaUltimaconversa = documentSnapshot.toObject(pessoaUltimaconversa::class.java)!!
                textNome.text = PessoaUltimaconversa.username
                Glide.with(context).load(PessoaUltimaconversa.profileImageurl).into(foto)
                if (PessoaUltimaconversa.atividade == true){
                    fotoonline.visibility = View.VISIBLE
                }
                else if(PessoaUltimaconversa.atividade == false){
                    fotoonline.visibility = View.INVISIBLE
                }
          }
        }


        if(ultimasMsg.tipo == "TEXT"){
            var string1 :String

            val ivParameterSpec =  IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val spec =  PBEKeySpec(secretKey.toCharArray(), Base64.decode(salt, Base64.DEFAULT), 10000, 256)
            val tmp = factory.generateSecret(spec);
            val secretKey =  SecretKeySpec(tmp.encoded, "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            string1 = String(cipher.doFinal(Base64.decode(ultimasMsg.textomensagem, Base64.DEFAULT)))


            if (ultimasMsg.idemissor == uid){
                if (ultimasMsg.vista == true){
                    imagemvista.setImageResource(R.drawable.icons8_double_tick_48);
                }
                else if(ultimasMsg.vista == false){
                    imagemvista.setImageResource(R.drawable.icons8_check_64);
                }
            }
            else if (ultimasMsg.idrecetor == uid){
                if (ultimasMsg.vista == true){
                    imagemvista.setVisibility(View.INVISIBLE);
                }
            }

            if (ultimasMsg.idrecetor == uid){
                 if(ultimasMsg.vista == false){
                     textMsg.setTextColor(Color.BLACK)
                     textData.setTextColor(Color.BLACK)
                     imagemvista.setImageResource(R.drawable.mensagem);
                 }
            }


            if (uid == ultimasMsg.idemissor){
                textMsg.text = "Tu: " + string1
            }
            else if(uid != ultimasMsg.idemissor){
                textMsg.text = string1
            }

        }
        else if(ultimasMsg.tipo == "IMAGEM"){
            if (uid == ultimasMsg.idemissor){
                textMsg.text = "Enviaste uma foto."
            }
            else if(uid != ultimasMsg.idemissor){
                textMsg.text = "Recebeste uma foto."
            }
        }
        viewHolder.itemView.setOnClickListener(){

            var PessoaUltimaconversa : pessoaUltimaconversa

            if(uid == ultimasMsg.idrecetor){
                db.collection("utilizadores").document(ultimasMsg.idemissor).get().addOnSuccessListener { documentSnapshot ->
                    PessoaUltimaconversa = documentSnapshot.toObject(pessoaUltimaconversa::class.java)!!
                    val intent = Intent(context, SalaChat::class.java)
                    intent.putExtra("UTILIZADOR", PessoaUltimaconversa.uid)
                    intent.putExtra("UTILIZADORNOME", PessoaUltimaconversa.username)
                    intent.putExtra("UTILIZADORTOKEN", PessoaUltimaconversa.token)
                    intent.putExtra("UTILIZADORFOTO", PessoaUltimaconversa.profileImageurl)
                    intent.putExtra("UTILIZADORCURSO", PessoaUltimaconversa.idcurso)
                    intent.putExtra("UTILIZADORATIVIDADE", PessoaUltimaconversa.atividade)
                    intent.putExtra("UTILIZADOREMAIL", PessoaUltimaconversa.email)
                    intent.putExtra("UTILIZADORANO", PessoaUltimaconversa.ano)

                    context.startActivity(intent)

                }
            }
            else if(uid == ultimasMsg.idemissor){
                db.collection("utilizadores").document(ultimasMsg.idrecetor).get().addOnSuccessListener { documentSnapshot ->
                    PessoaUltimaconversa = documentSnapshot.toObject(pessoaUltimaconversa::class.java)!!
                    val intent = Intent(context, SalaChat::class.java)
                    intent.putExtra("UTILIZADOR", PessoaUltimaconversa.uid)
                    intent.putExtra("UTILIZADORNOME", PessoaUltimaconversa.username)
                    intent.putExtra("UTILIZADORTOKEN", PessoaUltimaconversa.token)
                    intent.putExtra("UTILIZADORFOTO", PessoaUltimaconversa.profileImageurl)
                    intent.putExtra("UTILIZADORCURSO", PessoaUltimaconversa.idcurso)
                    intent.putExtra("UTILIZADORATIVIDADE", PessoaUltimaconversa.atividade)
                    intent.putExtra("UTILIZADOREMAIL", PessoaUltimaconversa.email)
                    intent.putExtra("UTILIZADORANO", PessoaUltimaconversa.ano)

                    context.startActivity(intent)

                }
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