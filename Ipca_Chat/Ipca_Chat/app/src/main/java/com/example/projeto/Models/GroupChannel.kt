package com.example.projeto.Models

class GroupChannel(val idgrupo :String, val nomegrupo: String, val userIds: ArrayList<String>?, val fotogrupo: String, val idadmin:String) {
    constructor() : this("","", null,"","")
}