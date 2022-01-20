package com.example.projeto.Models

import java.util.*

class Ultimasconversas(val textomensagem: String, val idemissor: String, val idrecetor: String,val time : Date,val vista : Boolean, val tipo : String) {
    constructor() : this("", "", "", Date(0), false, "")
}