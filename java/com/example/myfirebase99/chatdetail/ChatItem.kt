package com.example.myfirebase99.chatdetail

data class ChatItem (
    val time: String,
    val senderId: String,
    val message: String
){
    constructor():this("","","")
}