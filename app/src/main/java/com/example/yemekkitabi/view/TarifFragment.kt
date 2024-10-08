package com.example.yemekkitabi.view

import android.Manifest
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.yemekkitabi.databinding.FragmentTarifBinding
import com.example.yemekkitabi.model.Tarif
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream
import java.io.IOException

class TarifFragment : Fragment() {

    private var _binding: FragmentTarifBinding? = null
    private val binding get() = _binding!!
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var secilenGorsel:Uri?=null
    private var secilenBitmap:Bitmap?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTarifBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageView.setOnClickListener { gorselSec(it) }
        binding.KaydetButton.setOnClickListener { kaydet(it) }
        binding.SilButton.setOnClickListener { kaydet(it) }

        arguments?.let {
            val bilgi=TarifFragmentArgs.fromBundle(it).bilgi

            if (bilgi=="yeni"){
                //yenitarifeklenecel
                binding.SilButton.isEnabled=false
                binding.KaydetButton.isEnabled=true
                binding.IsimText.setText("")
                binding.MalzemeText.setText("")
            }
            else{
                //varolantarifgosterilecek
                binding.SilButton.isEnabled=true
                binding.KaydetButton.isEnabled=false
            }
        }


    }

    fun kaydet(view: View){
        val isim = binding.IsimText.text.toString()
        val malzeme = binding.MalzemeText.text.toString()

        if (secilenBitmap != null){
            val kucukBitmap = kucukBitmapOlustur(secilenBitmap!!,300)
            val outputStream = ByteArrayOutputStream()
            kucukBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val byteDizisi = outputStream.toByteArray()

            val tarif = Tarif(isim, malzeme,byteDizisi)


        }

    }
    fun sil(view: View){

    }
    fun gorselSec(view: View){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_MEDIA_IMAGES)!=PackageManager.PERMISSION_GRANTED){
                //izin verilmemiş istemek lazım
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_MEDIA_IMAGES)){
                    Snackbar.make(view,"Galeriye ulaşıp görsel seçmemiz lazım!",Snackbar.LENGTH_INDEFINITE).setAction("İzin ver",View.OnClickListener {
                        //izin isticez
                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    } ).show()
                }
                else{
                    //izinverildi
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            }
            else{
                //izin verilmiş
                val intentToGallery= Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }
        else{
            if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                //izin verilmemiş istemek lazım
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view,"Galeriye ulaşıp görsel seçmemiz lazım!",Snackbar.LENGTH_INDEFINITE).setAction("İzin ver",View.OnClickListener {
                        //izin isticez
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    } ).show()
                }
                else{
                    //izinverildi
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
            else{
                //izin verilmiş
                val intentToGallery= Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }


    }

    private fun registerLauncher(){
        activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
            if (result.resultCode==AppCompatActivity.RESULT_OK){
                val intentFromResult=result.data
                if (intentFromResult!=null){
                    secilenGorsel= intentFromResult.data

                    try {
                        if(Build.VERSION.SDK_INT>=28){
                            val source= ImageDecoder.createSource(requireActivity().contentResolver,secilenGorsel!!)
                            secilenBitmap=ImageDecoder.decodeBitmap(source)
                            binding.imageView.setImageBitmap(secilenBitmap)
                        }
                        else {
                            secilenBitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, secilenGorsel)
                            binding.imageView.setImageBitmap(secilenBitmap)
                        }
                    }
                    catch (e: IOException){
                        println(e.localizedMessage)
                    }


                }
            }

        }

        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){ result->
            if (result){
                //izinverildi
                val intentToGallery= Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
            else{
                Toast.makeText(requireContext(),"İzin verilmedi!",Toast.LENGTH_LONG).show()
            }

        }

    }

    private fun kucukBitmapOlustur(kullanicininSectigiBitmap : Bitmap, maximumBoyut: Int): Bitmap {
        var width = kullanicininSectigiBitmap.width
        var height = kullanicininSectigiBitmap.height
        val bitmapOrani : Double = width.toDouble()/height.toDouble()

        if (bitmapOrani>1){
            //gorsel yatay
            width=maximumBoyut
            val kisaltilmisYukseklik = width/bitmapOrani
            height=kisaltilmisYukseklik.toInt()
        }
        else{
            //gorsel dikey
            height=maximumBoyut
            val kisaltilmisGenislik= height/bitmapOrani
            width=kisaltilmisGenislik.toInt()
        }

        return Bitmap.createScaledBitmap(kullanicininSectigiBitmap,width,height,true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

//deneme