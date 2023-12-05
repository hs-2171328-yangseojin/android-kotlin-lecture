package com.example.myfirebase99.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirebase99.DBKey.Companion.CHILD_CHAT
import com.example.myfirebase99.DBKey.Companion.DB_ARTICLES
import com.example.myfirebase99.DBKey.Companion.DB_USERS
import com.example.myfirebase99.R
import com.example.myfirebase99.chatlist.ChatListItem
import com.example.myfirebase99.databinding.FragmentHomeBinding

import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore

import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment(R.layout.fragment_home) {

    private var binding: FragmentHomeBinding? = null
    private var itemId: String ?= null
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var articleDB: DatabaseReference
    private lateinit var userDB: DatabaseReference

    private val articleList = mutableListOf<ArticleModel>()

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val listener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

            val articleModel = snapshot.getValue(ArticleModel::class.java)
            articleModel ?: return

            articleList.add(articleModel)
        }
        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onChildRemoved(snapshot: DataSnapshot) {}
        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onCancelled(error: DatabaseError) {}
    }

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentHomeBinding = FragmentHomeBinding.bind(view)
        binding = fragmentHomeBinding
        articleList.clear()

        firestore.collection("Articles")
            .get()
            .addOnSuccessListener { result ->
                val itemList = mutableListOf<ArticleModel>()
                for(document in result) {
                    val item = document.toObject(ArticleModel::class.java)
                    //documentId = "${document.id}"
                    itemList.add(item)
                }
                fragmentHomeBinding.articleRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                fragmentHomeBinding.articleRecyclerView.adapter = ArticleAdapter(itemList,
                    onItemClicked = { articleModel->

                        if(auth.currentUser != null) { // 로그인 상태
                            if(auth.currentUser!!.uid != articleModel.sellerId) {
                                val intent = Intent(requireContext(), ArticleDetailActivity::class.java)
                                intent.putExtra("itemId", articleModel.timestamp)
                                requireContext().startActivity(intent)
                            }
                            else {
                                val intent = Intent(requireContext(), AddArticleActivity::class.java)
                                intent.putExtra("itemId", articleModel.timestamp)
                                intent.putExtra("new", 0)

                                requireContext().startActivity(intent)
                            }
                        }
                        else { //로그인하지 않은 상태
                            Snackbar.make(view, "회원만 이용 가능합니다.", Snackbar.LENGTH_LONG).show()
                        }
                    })
            }
        fragmentHomeBinding.addFloatingButton.setOnClickListener{

            if(auth.currentUser != null){
                val intent = Intent(requireContext(), AddArticleActivity::class.java)
                startActivity(intent)
            }else{

                Snackbar.make(view,"로그인 후 사용해주세요", Snackbar.LENGTH_LONG).show()
            }
        }

     
    }

    override fun onResume() {
        super.onResume()



       
    }


//        articleDB.removeEventListener(listener)
//    }
}
