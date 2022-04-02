package com.inu.andoid.criminalintentnav.fragments.update

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.text.format.DateFormat
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.inu.andoid.criminalintentnav.BuildConfig
import com.inu.andoid.criminalintentnav.R
import com.inu.andoid.criminalintentnav.getScaledBitmap
import com.inu.andoid.criminalintentnav.model.Crime
import com.inu.andoid.criminalintentnav.viewmodel.CrimeViewModel
import kotlinx.android.synthetic.main.fragment_update.*
import kotlinx.android.synthetic.main.fragment_update.view.*
import java.io.File
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


private const val DATE_FORMAT = "yyyy년 M월 d일 H시 m분, E요일"
private const val REQUEST_PHOTO = 2
class UpdateFragment : Fragment() {

    private val args by navArgs<UpdateFragmentArgs>()
    private lateinit var mCrimeViewModel : CrimeViewModel
    private val sdformat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    lateinit var crime: Crime
    private lateinit var reportButton: Button
    private lateinit var suspectButton: Button
    private lateinit var getResult: ActivityResultLauncher<Intent>
    private lateinit var getResultPhoto: ActivityResultLauncher<Intent>
    private lateinit var photoButton: Button
    private lateinit var photoView: ImageView
    // permissionLauncher 선언
    private lateinit var  permissionLauncher: ActivityResultLauncher<String>
    private lateinit var photoFile: File
    private lateinit var photoUri: Uri
    val fileDir =  context?.applicationContext?.filesDir
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val storageDir: File? = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val file = File.createTempFile(
        "JPEG_${timeStamp}_",
        ".jpg",
        storageDir
    )
    var filePath = file.absolutePath

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        crime = Crime()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_update, container, false)

        reportButton = view.update_report_button as Button
        suspectButton = view.update_suspect_button as Button
        photoButton = view.update_camera_button as Button
        photoView = view.update_photo_iv as ImageView


        view.updateTitle_et.setText(args.currentCrime.title)
        // Date 출력 형식 변경
        val cal = Calendar.getInstance()
        val dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val tz: TimeZone = cal.getTimeZone()
        // Getting zone id
        val zoneId: ZoneId = tz.toZoneId()
        val localDateTime: LocalDateTime =
            LocalDateTime.ofInstant(args.currentCrime.date.toInstant(), zoneId)
        val nowString = localDateTime.format(dtf)
        view.updateDate_Text.setText(nowString)
        view.updateIsSolved_CheckBox.isChecked= args.currentCrime.isSolved
        view.update_suspect_button.setText("용의자 : " + args.currentCrime.suspect)

        view.updateDate_button.setOnClickListener {
            val cal = Calendar.getInstance()    //캘린더뷰 만들기

            val dateSetListener =
                DatePickerDialog.OnDateSetListener { datePicker, year, month, dayOfMonth ->
                    cal.time = stringToDate(updateDate_Text.text.toString())
                    cal.set(year , month, dayOfMonth)
                    val getDay = sdformat.format(cal.getTime())
                    Log.d("date update: ", getDay)
                    updateDate_Text.text = "${getDay}" //"날짜/시간 : "+ dateString // + " / " + timeString

                }
            DatePickerDialog(
                requireContext(),
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

            mCrimeViewModel = ViewModelProvider(this).get(CrimeViewModel::class.java)

            view.update_button.setOnClickListener {
                updateItem()
                updatePhotoView()
            }

        reportButton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
            }.also { intent ->
                // startActivity(intent)
                val chooserIntent = Intent.createChooser(intent, getString(R.string.send_report))
                startActivity(chooserIntent)
            }
        }


        getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { it ->
            val intent: Intent? = it.data
            when {
                it.resultCode != RESULT_OK -> {
                    Toast.makeText(requireContext(), "suspect no ok", Toast.LENGTH_SHORT).show()
                }
                it.resultCode == RESULT_OK -> {
                    val contactUri: Uri = intent?.data!!
                    // 쿼리에서 값으로 반환할 필드를 지정한다
                    val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
                    // 쿼리를 수행한다
                    val cursor = requireActivity().contentResolver.query(contactUri, queryFields, null, null, null)
                    cursor?.use {
                        // 쿼리 결과 데이터가 있는지 확인한다
                        if (it.count == 0) {
                        }
                        // 첫 번째 데이터 행의 첫 번째 열의 값을 가져온다
                        // 이값이 용의자의 이름이다
                        it.moveToFirst()
                        val suspect = it.getString(0)
                        crime.suspect = suspect
                      //  mCrimeViewModel.updateCrime(crime)
                        suspectButton.text = suspect
                    }
                 //   updatePhotoView()
                }
            }
        }

        suspectButton.setOnClickListener {
            val pickContactIntent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            getResult.launch(pickContactIntent)
        }

        getResultPhoto = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            when {
                it.resultCode != RESULT_OK -> {
                    Toast.makeText(requireContext(), "photo no ok", Toast.LENGTH_SHORT).show()
                }
                it.resultCode == RESULT_OK -> {
                    Log.d( "getResultPhoto","photo  ok")
             //       requireActivity().revokeUriPermission(photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    updatePhotoView()
                }
            }
        }
        photoButton.apply {
            val packageManager: PackageManager = requireActivity().packageManager

       /*     val resolvedActivity: ResolveInfo? = packageManager.resolveActivity(
                captureImage, PackageManager.MATCH_DEFAULT_ONLY)*/
            photoFile = file //mCrimeViewModel.getPhotoFile(crime)
            photoUri = FileProvider.getUriForFile(requireActivity(),
                "com.inu.andoid.criminalintentnav.fileprovider",file)
            val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)

           /* if (resolvedActivity == null) {
                Log.d("photo","cameraActivity")
                isEnabled = false
            }*/
            setOnClickListener {
                if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // 권한 있는 경우 실행할 코드...
                    getResultPhoto.launch(captureImage)
                    Log.d("photo permissions :", "ok")
                } else {// 권한 없는 경우, 권한 요청
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    Log.d("phto permissions :", "no")
                }

              /*  val cameraActivities: List<ResolveInfo> =
                    packageManager.queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY)

                for (cameraActivity in cameraActivities) {
                    requireActivity().grantUriPermission(cameraActivity.activityInfo.packageName,
                    photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                }*/
              //  getResultPhoto.launch(captureImage)
            }
        }

        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // 권한 있는 경우 실행할 코드...
            } else {
                AlertDialog.Builder(requireContext())
                    .setTitle("미디어 접근 권한")
                    .setMessage("미디어를 첨부하시려면, 앱 접근 권한을 허용해 주세요.")
                    .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri: Uri = Uri.fromParts(
                            "package",
                            BuildConfig.APPLICATION_ID, null
                        )
                        intent.data = uri
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    })
                    .create()
                    .show()
            }
        }
        // Add menu
        setHasOptionsMenu(true)

        return view
    }

    override fun onDetach() {
        super.onDetach()
        requireActivity().revokeUriPermission(photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    }

    private fun updatePhotoView(){
        if (photoFile.exists()){
            val bitmap = getScaledBitmap(photoFile.path, requireActivity())
            photoView.setImageBitmap(bitmap)
            Log.d("photofile: ", "exist!")
        }else{
            photoView.setImageDrawable(null)

            Log.d("photofile: ", "no exist!, ${photoFile.path}")
        }
    }
    private fun updateItem() {
        val title = updateTitle_et.text.toString()
        val date = updateDate_Text.text.toString()
        val isChecked = updateIsSolved_CheckBox.isChecked
        val content = updateContents_et.text.toString()
        val suspcct = update_suspect_button.text.toString()

        if (inputCheck(title, content, date)) {

            Log.d("date updateItem: ", date)
            crime = stringToDate(date)?.let {
                Crime(args.currentCrime.id, title, it, isChecked, suspcct)
            }!!
            if (crime != null) {
                mCrimeViewModel.updateCrime(crime)
            }
            Toast.makeText(requireContext(), "${args.currentCrime.id}: ${title}, Successfully updated!", Toast.LENGTH_SHORT).show()
            // Navigate Back
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        } else
            Toast.makeText(requireContext(), "fill out update!", Toast.LENGTH_SHORT).show()

    }

    private fun stringToDate(aDate: String?): Date? {
        if (aDate == null) return null
        val pos = ParsePosition(0)
        val simpledateformat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return simpledateformat.parse(aDate, pos)
    }

    private fun inputCheck(title: String, content: String, date: String): Boolean {
        return !((TextUtils.isEmpty(title) && TextUtils.isEmpty(content))  || TextUtils.isEmpty(date))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delete_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_delete)
            deleteUser()
        return super.onOptionsItemSelected(item)
    }

    private fun deleteUser() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") {_, _ ->
            mCrimeViewModel.deleteCrime(args.currentCrime)
            Toast.makeText(requireContext(), "Succesfully removed: ${args.currentCrime.title}",
            Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }
        builder.setNegativeButton("No") {_, _ -> }
        builder.setTitle("Delete ${args.currentCrime.title}?")
        builder.setMessage("Are You sure you want to delete message ${args.currentCrime.title}")
        builder.create().show()
    }

    private fun getCrimeReport(): String{
        val solvedString = if (args.currentCrime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }
        val dateString = DateFormat.format(
            com.inu.andoid.criminalintentnav.fragments.update.DATE_FORMAT,
            args.currentCrime.date).toString()
        val suspect = if (args.currentCrime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, args.currentCrime.suspect)
        }
        return getString(R.string.crime_report,
            args.currentCrime.title, dateString, solvedString, suspect)
    }
}



