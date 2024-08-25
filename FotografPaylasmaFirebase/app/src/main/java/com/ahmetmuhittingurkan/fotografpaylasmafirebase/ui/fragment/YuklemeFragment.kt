package com.ahmetmuhittingurkan.fotografpaylasmafirebase.ui.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.ahmetmuhittingurkan.fotografpaylasmafirebase.R
import com.ahmetmuhittingurkan.fotografpaylasmafirebase.databinding.FragmentYuklemeBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import java.util.UUID

class YuklemeFragment : Fragment() {

    private lateinit var binding:FragmentYuklemeBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    var secilenGorsel : Uri?= null
    var secilenBitmap: Bitmap?= null

    private lateinit var auth: FirebaseAuth
    private lateinit var storage:FirebaseStorage
    private lateinit var db:FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth= Firebase.auth
        storage=Firebase.storage
        db=Firebase.firestore

        registerLaunchers()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding=FragmentYuklemeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageView.setOnClickListener{gorselSec(it)}
        binding.yukleButton.setOnClickListener { yukleResim(it) }
    }

    fun gorselSec(view:View){

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU){

            if(ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.READ_MEDIA_IMAGES)!=PackageManager.PERMISSION_GRANTED)
                // Galeri izni yoksa demek bu kod.

                if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),android.Manifest.permission.READ_MEDIA_IMAGES)){
                    // İzin mantığını kullanıcıya göstermemiz lazım.
                    Snackbar.make(view,"Resim seçebilmek için izin vermeniz gerekiyor.",Snackbar.LENGTH_INDEFINITE)
                        .setAction("İzin ver",View.OnClickListener {
                            // İzin isteme kodu.
                            permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                        }) .show()
                } else{
                    // İzin istememiz lazım.
                    permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                }

                else{
                    // Galeri izni var. // Galeriye gitme kodu yazılır.
                val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }

        } else {
            if(ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
            // Galeri izni yoksa demek bu kod.

                if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                    // İzin mantığını kullanıcıya göstermemiz lazım.
                    Snackbar.make(view,"Resim seçebilmek için izin vermeniz gerekiyor.",Snackbar.LENGTH_INDEFINITE)
                        .setAction("İzin ver",View.OnClickListener {
                            // İzin isteme kodu.
                            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        }) .show()
                } else{
                    // İzin istememiz lazım.
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }

            else{
                // Galeri izni var. // Galeriye gitme kodu yazılır.
                val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }

        }
    }

    fun yukleResim(view:View){

        val uuid=UUID.randomUUID()
        val gorselAdi="${uuid}.jpg"

        val reference= storage.reference
        val gorselReferansi=reference.child("images").child(gorselAdi)
        if(secilenGorsel!=null){
            gorselReferansi.putFile(secilenGorsel!!).addOnSuccessListener {uploadTask->
                // urlyi alma işlemi yapılacak.
                gorselReferansi.downloadUrl.addOnSuccessListener {uri->
                    val downloadUrl=uri.toString()
                    // veri tabanına kayıt etmemiz lazım.

                    val postMap= hashMapOf<String,Any>()
                    postMap.put("comment",binding.commentText.text.toString())
                    postMap.put("email",auth.currentUser?.email.toString())
                    postMap.put("downloadUrl",downloadUrl)
                    postMap.put("date",Timestamp.now())

                    db.collection("Posts").add(postMap).addOnSuccessListener {documentReference->
                        val gecis=YuklemeFragmentDirections.actionYuklemeFragmentToFeedFragment()
                        Navigation.findNavController(view).navigate(gecis)

                    }.addOnFailureListener{exception->
                        Toast.makeText(requireContext(),exception.localizedMessage,Toast.LENGTH_LONG).show()
                    }

                }

            }.addOnFailureListener{exception->
                Toast.makeText(requireContext(),exception.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun registerLaunchers(){

        activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
            if(result.resultCode==RESULT_OK){
                val intentFromResult=result.data
                if(intentFromResult!=null){
                    secilenGorsel=intentFromResult.data
                    try{
                        if(Build.VERSION.SDK_INT>=28){
                            val source=ImageDecoder.createSource(requireActivity().contentResolver,secilenGorsel!!)
                            secilenBitmap=ImageDecoder.decodeBitmap(source)
                            binding.imageView.setImageBitmap(secilenBitmap)
                        } else{
                            secilenBitmap= MediaStore.Images.Media.getBitmap(requireActivity().contentResolver,secilenGorsel)
                            binding.imageView.setImageBitmap(secilenBitmap)
                        }
                    } catch(e:Exception){
                        e.printStackTrace()
                    }
                }
            }

        }

        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){ result->
            if(result) {
                // Galeri izni verildi. // Galeriye gitme kodu yazılcak.
                val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else{
                // Galeri izni reddedildi.
                Toast.makeText(requireContext(),"Galeriye gitmek için izin vermelisiniz!!",Toast.LENGTH_LONG).show()
            }
        }

    }


}