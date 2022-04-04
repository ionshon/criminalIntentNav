package com.inu.andoid.criminalintentnav.fragments.update

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.inu.andoid.criminalintentnav.R
import com.inu.andoid.criminalintentnav.getScaledBitmap
import com.inu.andoid.criminalintentnav.model.Crime
import com.inu.andoid.criminalintentnav.rotateImage
import com.inu.andoid.criminalintentnav.viewmodel.CrimeViewModel
import kotlinx.android.synthetic.main.fragment_dialog.view.*
import java.io.File
import java.io.InputStream

class DialogFragment : Fragment() {

    private val args by navArgs<DialogFragmentArgs>()
    private lateinit var photoView: ImageView
    private lateinit var mCrimeViewModel : CrimeViewModel
    private lateinit var photoFile: File
    private lateinit var photoUri: Uri
    private lateinit var crime: Crime

  /*  override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
    }*/
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dialog, container, false)

        photoView = view.dialog_photo_iv as ImageView

        crime = args.dialogCrime

        readPhotoView()



        return view
    }


    private fun readPhotoView() {
        val resolver = context?.contentResolver
        mCrimeViewModel = ViewModelProvider(this)[CrimeViewModel::class.java]
        photoFile = File(context?.cacheDir, mCrimeViewModel.getPhotoFile(crime).toString())
        photoUri = FileProvider.getUriForFile(requireActivity(),
           "com.inu.andoid.criminalintentnav.fileprovider", photoFile)

        try {
            val instream: InputStream? = resolver?.openInputStream(photoUri)
            var imgBitmap = BitmapFactory.decodeStream(instream)
         //   var imgBitmap2 = getScaledBitmap(photoFile.path, requireActivity()) // 썸네일
            imgBitmap = rotateImage(imgBitmap, 90F)
            photoView.setImageBitmap(imgBitmap) // 선택한 이미지 이미지뷰에 셋
            instream?.close() // 스트림 닫아주기
            Log.d("photo: ","파일 불러오기 성공")
        } catch (e: java.lang.Exception) {
            Log.d("photo: ","파일 불러오기 실패, ${crime.photoFileName} \n" +
                    "")
        }
    }


}

