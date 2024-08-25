package com.ahmetmuhittingurkan.fotografpaylasmafirebase.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.ahmetmuhittingurkan.fotografpaylasmafirebase.R
import com.ahmetmuhittingurkan.fotografpaylasmafirebase.databinding.FragmentKullaniciBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.rpc.context.AttributeContext.Auth
import java.lang.Exception

class KullaniciFragment : Fragment() {

    private lateinit var binding: FragmentKullaniciBinding
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth=Firebase.auth
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding=FragmentKullaniciBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonKayit.setOnClickListener { kayitOl(it) }
        binding.buttonGiris.setOnClickListener { girisYap(it) }

        val guncelKullanici=auth.currentUser
        if(guncelKullanici!=null){
            val gecis=KullaniciFragmentDirections.actionKullaniciFragmentToFeedFragment()
            Navigation.findNavController(view).navigate(gecis)
        }
    }

    fun kayitOl(view:View){

        val email=binding.emailText.text.toString()
        val password=binding.passwordText.text.toString()

        if(email.isNotBlank() || password.isNotBlank()){
            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task->
                if(task.isSuccessful){ // Kullanıcı oluşturuldu demek
                    val gecis=KullaniciFragmentDirections.actionKullaniciFragmentToFeedFragment()
                    Navigation.findNavController(view).navigate(gecis)
                }
            } .addOnFailureListener{ exception->
                Toast.makeText(requireContext(),exception.localizedMessage,Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun girisYap(view:View){
        val email=binding.emailText.text.toString()
        val password=binding.passwordText.text.toString()

        if(email.isNotBlank() || password.isNotBlank()){
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener {
                 // Kullanıcı oluşturuldu demek
                    val gecis=KullaniciFragmentDirections.actionKullaniciFragmentToFeedFragment()
                    Navigation.findNavController(view).navigate(gecis)

            }.addOnFailureListener {exception->
                Toast.makeText(requireContext(),exception.localizedMessage,Toast.LENGTH_SHORT).show()
            }
        }

    }
}


