package com.example.projeto.Models

class pessoasGrupo(
    val uid: String, val username: String, val profileImageurl: String, val atividade: Boolean, val idcurso:String, val token : String, val ano : Int, val email : String,val cargo:String
) {
    constructor() : this("", "", "",false,"","",0,"","")
}