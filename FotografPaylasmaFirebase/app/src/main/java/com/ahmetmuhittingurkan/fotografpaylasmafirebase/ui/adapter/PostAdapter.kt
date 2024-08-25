package com.ahmetmuhittingurkan.fotografpaylasmafirebase.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahmetmuhittingurkan.fotografpaylasmafirebase.databinding.FragmentFeedBinding
import com.ahmetmuhittingurkan.fotografpaylasmafirebase.databinding.RecyclerRowBinding
import com.ahmetmuhittingurkan.fotografpaylasmafirebase.model.Post
import com.squareup.picasso.Picasso


class PostAdapter(var postList:ArrayList<Post>) : RecyclerView.Adapter<PostAdapter.PostHolder>() {

    inner class PostHolder(var binding:RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
       val binding= RecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent,false)
       return PostHolder(binding)
    }


    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.binding.recyclerEmailText.text=postList[position].email
        holder.binding.recyclerCommentText.text=postList[position].comment
        Picasso.get().load(postList[position].downloadUrl).into(holder.binding.recyclerImageView)

    }

    override fun getItemCount(): Int {
        return postList.size
    }


}