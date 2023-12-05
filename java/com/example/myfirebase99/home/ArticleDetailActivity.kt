package com.example.myfirebase99.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.example.myfirebase99.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class ArticleDetailActivity : AppCompatActivity() {
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private val storage: FirebaseStorage by lazy {
        Firebase.storage
    }

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private var articleKey: Long ?= null
    private var sellerId: String ?= null
    private var title: String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_detail)

        articleKey = intent.getLongExtra("itemId", 0)

        val myId = auth.currentUser?.uid.orEmpty()

        firestore.collection("Articles")
            .whereEqualTo("timestamp", articleKey)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.documents.isNotEmpty()) {
                    val documentSnapshot = querySnapshot.documents[0]
                    title = documentSnapshot.getString("title")
                    val price = documentSnapshot.getString("price")
                    sellerId = documentSnapshot.getString("sellerId")

                    // 이후에 필요한 처리를 진행
                    val numericPart = price?.replace(Regex("[^\\d]"), "")

                    findViewById<TextView>(R.id.titleText).setText(title)
                    findViewById<TextView>(R.id.priceText).setText(numericPart)
                } else {
                    Log.d("my log", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("my log", "Error getting documents: ", exception)
            }

        findViewById<Button>(R.id.chatButton).setOnClickListener {
            upload(auth.currentUser!!.uid, sellerId!!, title!!)
        }
    }

    // 아이템을 업로드하는 부분 중 일부
    private fun upload(buyerId: String, sellerId: String, title: String) {

        val chat = hashMapOf(
            "buyerId" to buyerId,
            "sellerId" to sellerId,
            "itemTitle" to title,
            "time" to System.currentTimeMillis()
        )


        finish()
    }
}
