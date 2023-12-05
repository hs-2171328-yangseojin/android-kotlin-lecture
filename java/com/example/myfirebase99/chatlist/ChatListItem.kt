package com.example.myfirebase99.chatlist


data class ChatListItem(
    val buyerId: String,
    val sellerId : String,
    val itemTitle: String,
    val time: Long
){

    constructor(): this("","","",0)
}