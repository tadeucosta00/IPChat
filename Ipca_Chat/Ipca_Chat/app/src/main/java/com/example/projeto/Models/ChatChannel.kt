package com.example.projeto.Models

data class ChatChannel(val userIds: MutableList<String>) {
    constructor() : this(mutableListOf())
}