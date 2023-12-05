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

// Firebase RealTime Database에서 Model Class를 통해 데이터를 주고 받고 싶을 떄는
// 반드시 위와 같이 빈 생성자를 정의해줘야 한다. -> ( 아마 null 예외처리 때문인 듯 함 )