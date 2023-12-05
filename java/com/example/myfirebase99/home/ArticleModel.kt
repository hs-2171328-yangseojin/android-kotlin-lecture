package com.example.myfirebase99.home

data class ArticleModel(
    val timestamp: Long,
    val sellerId: String,
    var title: String?= null,
    var createdAt: Long?= null,
    var price: String?= null,
    var imageUrl: String?= null,
    var swit1Checked: Boolean // 추가: SWIT1 스위치 상태를 저장하는 속성
) {
    constructor() : this(0, "", "", 0, "", "", false)
}
