package com.example.myfirebase99.home

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
import androidx.core.view.isVisible
import com.example.myfirebase99.DBKey.Companion.DB_ARTICLES
import com.example.myfirebase99.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class AddArticleActivity : AppCompatActivity() {

    private var selectedUri: Uri? = null
    private var new: Int ?= 1 // 새로 작성이면 1, 수정이면 0
    private var articleKey: Long ?= null  // 수정에 필요한 문서 키

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private val storage: FirebaseStorage by lazy {
        Firebase.storage
    }

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_article)

        new = intent.getIntExtra("new", 1)

        findViewById<Button>(R.id.imageAddButton).setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    startContentProvider()
                }

                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    showPermissionContextPopup()
                }

                else -> {
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        1010
                    )
                }

            }
        }
        findViewById<Button>(R.id.submitButton).setOnClickListener {
            if (new == 1) {
                val title = findViewById<EditText>(R.id.titleEditText).text.toString().orEmpty()
                val price = findViewById<EditText>(R.id.priceEditText).text.toString().orEmpty()
                val sellerId = auth.currentUser?.uid.orEmpty()

                showProgress()
                if (selectedUri != null) {

                    val PhotoUri = selectedUri ?: return@setOnClickListener
                    uploadPhoto(
                        PhotoUri,
                        successHandler = { uri ->
                            uploadArticle(sellerId, title, price, uri)
                        },
                        errorHandler = {
                            Toast.makeText(this, "사진 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()

                            hideProgress()

                        }
                    )
                } else {
                    uploadArticle(sellerId, title, price, "")
                }
            } else {
                articleKey = intent.getLongExtra("itemId", 0)
                Log.d("my log", ""+ articleKey )

                val title = findViewById<EditText>(R.id.titleEditText).text.toString().orEmpty()
                val price = findViewById<EditText>(R.id.priceEditText).text.toString().orEmpty()
                val sellerId = auth.currentUser?.uid.orEmpty()

                if (selectedUri != null) {

                    val PhotoUri = selectedUri ?: return@setOnClickListener
                    uploadPhoto(
                        PhotoUri,
                        successHandler = { uri ->
                            uploadArticle(sellerId, title, price, uri)
                        },
                        errorHandler = {
                            Toast.makeText(this, "사진 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()

                            hideProgress()

                        }
                    )
                } else {
                    modArticle(articleKey!!, sellerId, title, price, "")
                }
            }
        }

        if (new != 1) {
            articleKey = intent.getLongExtra("itemId", 0)
            Log.d("my log", ""+ articleKey )

            firestore.collection("Articles")
                //.document(articleKey!!)
                .whereEqualTo("timestamp", articleKey)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.documents.isNotEmpty()) {
                        val documentSnapshot = querySnapshot.documents[0]
                        val title = documentSnapshot.getString("title")
                        val price = documentSnapshot.getString("price")
                        val swit1Checked = documentSnapshot.getBoolean("swit1Checked") ?: false

                        // 이후에 필요한 처리를 진행
                        val numericPart = price?.replace(Regex("[^\\d]"), "")

                        findViewById<EditText>(R.id.titleEditText).setText(title)
                        findViewById<EditText>(R.id.priceEditText).setText(numericPart)
                        findViewById<Switch>(R.id.switch1).isChecked = swit1Checked
                    } else {
                        Log.d("my log", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("my log", "Error getting documents: ", exception)
                }
        }

    }

    private fun uploadPhoto(uri: Uri, successHandler: (String) -> Unit, errorHandler: () -> Unit) {

        val fileName = "${System.currentTimeMillis()}.png"


        storage.reference.child("article/photo").child(fileName)
            .putFile(uri)
            .addOnCompleteListener {
                if (it.isSuccessful) {

                    storage.reference.child("article/photo").child(fileName)
                        .downloadUrl
                        .addOnSuccessListener { uri ->
                            successHandler(uri.toString())
                        }
                        .addOnFailureListener {
                            errorHandler()
                        }
                } else {
                    errorHandler()
                }
            }
    }

    // 아이템을 업로드하는 부분 중 일부
    private fun uploadArticle(sellerId: String, title: String, price: String, imageUrl: String) {
        val swit1Checked = findViewById<Switch>(R.id.switch1).isChecked
        
        val article = hashMapOf(
            "sellerId" to sellerId,
            "title" to title,
            "timestamp" to System.currentTimeMillis(),
            "price" to "$price 원",
            "imageUrl" to imageUrl,
            "swit1Checked" to swit1Checked
        )

        firestore.collection("Articles")
            .add(article)
            .addOnSuccessListener {
                Toast.makeText(this, "상품이 등록되었습니다.", Toast.LENGTH_SHORT).show()
                Log.d("my log", " 성공")
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "상품 등록 실패: 모든 내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
                Log.d("my log", " 실패")
            }
        hideProgress()

        finish()
    }

    private fun modArticle(id: Long, sellerId: String, title: String, price: String, imageUrl: String) {
        val swit1Checked = findViewById<Switch>(R.id.switch1).isChecked

        val article = hashMapOf<String, Any>(
            "sellerId" to sellerId,
            "title" to title,
            "timestamp" to System.currentTimeMillis(),
            "price" to "$price 원",
            "imageUrl" to imageUrl,
            "swit1Checked" to swit1Checked
        )

        firestore.collection("Articles")
           
            .whereEqualTo("timestamp", id)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.documents.isNotEmpty()) {
                    val documentSnapshot = querySnapshot.documents[0]
                    // 해당 문서를 찾았으면 해당 문서를 업데이트
                    firestore.collection("Articles")
                        .document(documentSnapshot.id)
                        .update(article)
                        .addOnSuccessListener {
                            Toast.makeText(this, "상품이 수정되었습니다.", Toast.LENGTH_SHORT).show()
                            Log.d("my log", " 성공")
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "상품 수정 실패: 모든 내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
                            Log.d("my log", " 실패")
                        }
                } else {
                    Log.d("my log", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("my log", "Error getting documents: ", exception)
            }

        hideProgress()

        finish()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1010 ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startContentProvider()
                } else {
                    Toast.makeText(this, "권한을 거부하셨습니다", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun startContentProvider() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 2020)
    }

    private fun showProgress(){
        findViewById<ProgressBar>(R.id.progressBar).isVisible = true

    }
    private fun hideProgress(){
        findViewById<ProgressBar>(R.id.progressBar).isVisible = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            2020 -> {
                val uri = data?.data
                if (uri != null) {
                    findViewById<ImageView>(R.id.photoImageView).setImageURI(uri)
                    selectedUri = uri
                } else {
                    Toast.makeText(this, "사진을 가져오지 못했습니다", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(this, "사진을 가져오지 못했습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("사진을 가져오기 위해 필요합니다")
            .setPositiveButton("동의") { _, _ ->
                requestPermissions(
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    1010
                )
            }
            .create()
            .show()

    }
}
