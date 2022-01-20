package com.example.projeto.Models

import java.util.*

class MensagensGrupos(val idGrupo : String,val textomensagem: String, val idemissor: String, val time : Date, val vista : Boolean, val tipo: String) {
    constructor() : this("","", "", Date(0), false, "")
}