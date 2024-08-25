package com.ahmetmuhittingurkan.fotografpaylasmafirebase.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmetmuhittingurkan.fotografpaylasmafirebase.R
import com.ahmetmuhittingurkan.fotografpaylasmafirebase.databinding.FragmentFeedBinding
import com.ahmetmuhittingurkan.fotografpaylasmafirebase.model.Post
import com.ahmetmuhittingurkan.fotografpaylasmafirebase.ui.adapter.PostAdapter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

class FeedFragment : Fragment(), PopupMenu.OnMenuItemClickListener {

    private lateinit var binding: FragmentFeedBinding
    private lateinit var popUp: PopupMenu
    private lateinit var auth: FirebaseAuth
    private lateinit var db:FirebaseFirestore
    var postList: ArrayList<Post> = arrayListOf()

    private var adapter:PostAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth=Firebase.auth
        db=Firebase.firestore

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding=FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fab.setOnClickListener{fabTikla(it)}

        popUp = PopupMenu(requireContext(), binding.fab)
        val inflater = popUp.menuInflater
        inflater.inflate(R.menu.my_popup_menu, popUp.menu)
        popUp.setOnMenuItemClickListener(this)

        fireStoreVerileriAl()
        adapter=PostAdapter(postList)
        binding.feedRecyclerView.layoutManager=LinearLayoutManager(requireContext())
        binding.feedRecyclerView.adapter=adapter

    }

    fun fabTikla(view:View){
        popUp.show()
    }

    private fun fireStoreVerileriAl(){
        db.collection("Posts").orderBy("date",Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            if(error!=null){
                Toast.makeText(requireContext(),error.localizedMessage,Toast.LENGTH_LONG).show()
            } else {
                if(value!=null){
                    if(!value.isEmpty){
                        postList.clear()
                        val documents=value.documents
                        for(document in documents){
                            val comment= document.get("comment") as String
                            val email=document.get("email") as String
                            val downloadUrl=document.get("downloadUrl") as String

                            val post=Post(email,comment,downloadUrl)
                            postList.add(post)
                        }
                        adapter?.notifyDataSetChanged()
                    }
                }
            }

        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if(item?.itemId==R.id.yuklemeItem){
            val gecis=FeedFragmentDirections.actionFeedFragmentToYuklemeFragment()
            Navigation.findNavController(requireView()).navigate(gecis)

        } else if(item?.itemId==R.id.cikisItem){
            auth.signOut()
            val gecis=FeedFragmentDirections.actionFeedFragmentToKullaniciFragment2()
            Navigation.findNavController(requireView()).navigate(gecis)
        }
        return true

    }
}
