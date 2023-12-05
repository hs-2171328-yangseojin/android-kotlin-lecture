package com.example.myfirebase99.home

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myfirebase99.databinding.ItemArticleBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
//import com.example.aop_part3_chapter14.databinding.ItemArticleBinding
import java.text.SimpleDateFormat
import java.util.*

class ArticleAdapter(private var items: List<ArticleModel>, val onItemClicked: (ArticleModel) -> Unit)
    : RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {
//(val onItemClicked: (ArticleModel) -> Unit) : ListAdapter<ArticleModel, ArticleAdapter.ViewHolder>(diffUtil) {
    private var seller: String ?= null

    // ViewBinding을 통해 레이아웃에서 가져옴
    inner class ViewHolder(private val binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root) {
//        init {
//            binding.root.setOnClickListener {
//                val userid = Firebase.auth.currentUser!!.uid
//                val clickedItem = items[adapterPosition]
//                val itemId = clickedItem.sellerId
//
//                Log.d("my log1", ""+userid)
//                Log.d("my log2", ""+clickedItem)
//                Log.d("my log2", ""+itemId)
//
//                val intent = Intent(binding.root.context, AddArticleActivity::class.java)
//                intent.putExtra("itemId", itemId)
//                intent.putExtra("new", 0)
//
//                if (userid == itemId) {
//                    binding.root.context.startActivity(intent)
//                } else {
//
//                }
//            }
//        }

        fun bind(articleModel: ArticleModel) {
            val format = SimpleDateFormat("MM월 dd일")
            val date = Date(articleModel.createdAt!!)

            binding.titleTextView.text = articleModel.title
            binding.dateTextView.text = format.format(date).toString()
            binding.priceTextView.text = articleModel.price

            // SWIT2 스위치의 상태를 SWIT1의 상태로 설정
            binding.switch4.isChecked = articleModel.swit1Checked

            if (articleModel.imageUrl!!.isNotEmpty()) {
                Glide.with(binding.thumbnailImageView)
                    .load(articleModel.imageUrl)
                    .into(binding.thumbnailImageView)
            }

            binding.root.setOnClickListener{
                onItemClicked(articleModel)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemArticleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateData(newItems: List<ArticleModel>){
        items = newItems
        notifyDataSetChanged()
    }

//    companion object {
//        val diffUtil = object : DiffUtil.ItemCallback<ArticleModel>() {
//
//            // 현재 노출되고 있는 아이템과 새로운 아이템이 같은지 확인 ㅡ, 새로운 아이템이 들어오면 호출됨
//            // 일반적으로 키값을 통해 구분하게 됨
//            override fun areItemsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
//                return oldItem.createdAt == newItem.createdAt
//            }
//
//            // 현재 아이템과 새로운 아이탬의 = 여부를 확인
//            override fun areContentsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
//                return oldItem == newItem
//            }
//        }
//    }
}