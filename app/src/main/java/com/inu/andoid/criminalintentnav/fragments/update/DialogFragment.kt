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
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.inu.andoid.criminalintentnav.R
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
        photoUri = FileProvider.getUriForFile(
            requireActivity(),
           "com.inu.andoid.criminalintentnav.fileprovider",
            mCrimeViewModel.getPhotoFile(crime)
        )

        try {
            val instream: InputStream? = resolver?.openInputStream(photoUri)
            var imgBitmap = BitmapFactory.decodeStream(instream)
         //   var imgBitmap2 = getScaledBitmap(photoFile.path, requireActivity()) // ?????????
            imgBitmap = rotateImage(imgBitmap, 90F)
            photoView.setImageBitmap(imgBitmap) // ????????? ????????? ??????????????? ???
            instream?.close() // ????????? ????????????
            Log.d("photo: ","?????? ???????????? ??????")
        } catch (e: java.lang.Exception) {
            Log.d("photo: ","?????? ???????????? ??????, ${photoUri} \n" +
                    "")
        }
    }


}

