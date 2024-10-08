package com.example.yemekkitabi

import android.Manifest
import android.app.Instrumentation
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.yemekkitabi.databinding.FragmentListeBinding
import com.example.yemekkitabi.databinding.FragmentTarifBinding
import com.google.android.material.snackbar.Snackbar

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

    }
    fun sil(view: View){

    }
    fun gorselSec(view: View){
        if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            //izin verilmemiş istemek lazım
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Galeriye ulaşıp görsel seçmemiz lazım",Snackbar.LENGTH_INDEFINITE).setAction("İzin ver",View.OnClickListener {
                    //izin isticez
                } ).show()
            }
            else{

            }
        }
        else{
            //izin verilmiş
        }
    }

    private fun registerLauncher(){
        activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
            if (result.resultCode==AppCompatActivity.RESULT_OK){
                val intentFromResult=result.data
                if (intentFromResult!=null){
                    intentFromResult.data



                }
            }

        }

        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){ result->
            if (result){
                //izinverildi
            }
            else{
                Toast.makeText(requireContext(),"İzin verilmedi!",Toast.LENGTH_LONG).show()
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

//deneme