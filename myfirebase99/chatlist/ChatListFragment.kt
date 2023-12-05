package com.example.myfirebase99.chatlist

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirebase99.DBKey.Companion.CHILD_CHAT
import com.example.myfirebase99.DBKey.Companion.DB_USERS
import com.example.myfirebase99.R
import com.example.myfirebase99.databinding.FragmentChatlistBinding

import com.example.myfirebase99.chatdetail.ChatRoomActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatListFragment:Fragment(R.layout.fragment_chatlist) {

    private var binding: FragmentChatlistBinding? = null
    private lateinit var chatListAdapter: ChatListAdapter
    private val chatRoomList = mutableListOf<ChatListItem>()

    private lateinit var chatDB: DatabaseReference

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentChatlistBinding = FragmentChatlistBinding.bind(view)
        binding = fragmentChatlistBinding

        chatListAdapter = ChatListAdapter(onItemClicked = { ChatRoom ->
            // todo 채팅방으로 이동하는 코드

            val intent = Intent(requireContext(), ChatRoomActivity::class.java)
            intent.putExtra("chatKey", ChatRoom.time)
            startActivity(intent)

        })

        chatRoomList.clear()

        fragmentChatlistBinding.chatListRecyclerView.adapter = chatListAdapter
        fragmentChatlistBinding.chatListRecyclerView.layoutManager = LinearLayoutManager(context)

        if (auth.currentUser == null){
            return
        }

        chatDB = Firebase.database.reference.child(DB_USERS).child(auth.currentUser!!.uid).child(CHILD_CHAT)

        chatDB.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                // todo 서버에서 데이터를 가져오는 것에 성공하면 호출


                // snapshot.children에 Data model들을 담은 하나의 리스트가 내려옴
                // 이 리스트에서 Data model들을 하나씩 분리하는 작업이 필요 ( forEach )
                snapshot.children.forEach{
                    val model = it.getValue(ChatListItem::class.java)
                    model ?: return

                    chatRoomList.add(model)
                }

                chatListAdapter.submitList(chatRoomList)
                chatListAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // TODO 서버에서 데이터를 가져오는 것에 실패했을 경우 호출
            }
        })

    }

    override fun onResume() {
        super.onResume()

        chatListAdapter.notifyDataSetChanged()
    }
}