package com.inu.andoid.criminalintentnav.fragments.add

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.inu.andoid.criminalintentnav.R
import com.inu.andoid.criminalintentnav.model.Crime
import com.inu.andoid.criminalintentnav.viewmodel.CrimeViewModel
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.fragment_add.view.*
import java.text.DateFormat
import java.text.ParseException
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


private const val DIALOG_DATE = "DialogDate"
class AddFragment : Fragment() {

   // private lateinit var crime: Crime

    private lateinit var mCrimeViewModel: CrimeViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false)
// 포맷변경 ( 년월일 시분초) SimpleDateFormat
        val sdformat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        view.addDate_button.setOnClickListener {
            val cal = Calendar.getInstance()    //캘린더뷰 만들기val date = LocalDateTime.now()
            val dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            // Getting zone id

            val dateSetListener = DatePickerDialog.OnDateSetListener {
                    datePicker, year, month, dayOfMonth ->
                val resultDate : Date = GregorianCalendar(year, month, dayOfMonth).time
                // Getting the timezone
                val tz: TimeZone = cal.getTimeZone()
                // Getting zone id
                val zoneId: ZoneId = tz.toZoneId()
                val localDateTime: LocalDateTime = LocalDateTime.ofInstant(resultDate.toInstant(), zoneId)
                val nowString = localDateTime.format(dtf)
                addDate_text_old.text = "${nowString}" //"날짜/시간 : "+ dateString // + " / " + timeString
            }
            DatePickerDialog(requireContext(), dateSetListener, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show()
        /*
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("일자를 골라주세요")
                    .build()

            datePicker.show(childFragmentManager, "date_picker")
            datePicker.addOnPositiveButtonClickListener {
                addDate_et_old.text = datePicker.headerText
            }*/
        }
        view.addTime_button.setOnClickListener {
            val cal = Calendar.getInstance()
            cal.setTime(stringToDate(addDate_text_old.text.toString())) //(addDate_et_old.text) //"${addDate_et_old.text}" + " " + timeString

            val timeSetListener = TimePickerDialog.OnTimeSetListener { datepicker, hourOfDay, minute ->
                cal.add(Calendar.HOUR, hourOfDay)
                cal.add(Calendar.MINUTE, minute)
                val today = sdformat.format(cal.getTime())
                addDate_text_old.text = today //"${addDate_et_old.text}" + " " + timeString


            }
            Log.d("date time:",  "${addDate_text_old.text}")
            TimePickerDialog(requireContext(), timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),true).show()
        }

        mCrimeViewModel = ViewModelProvider(this).get(CrimeViewModel::class.java)
        view.add_button.setOnClickListener {
            insertDataToDatabase()
        }
        return view
    }

    private fun insertDataToDatabase() {

        val title = addTitle_et.text.toString()
        val date = addDate_text_old.text.toString()
        val content = addContents_et.text.toString()
        val isChecked = addIsSolved_CheckBox.isChecked
        val suspect = add_suspect_button.text.toString()

        Log.d("date check", "${date}, ${stringToDate(date)}")

        if (inputCheck(title, content, date)){
            //Creat Crime Object
            val crime = stringToDate(date)?.let {
                Crime(0, title, it, isChecked, suspect)
            }
            // Add Data to Database
            if (crime != null) {
                mCrimeViewModel.addCrime(crime)
            }
            Toast.makeText(requireContext(), "Successfully added!", Toast.LENGTH_SHORT).show()
            // Navigate Back
            findNavController().navigate(R.id.action_addFragment_to_listFragment)
        }else{
            Toast.makeText(requireContext(), "Please fill out!", Toast.LENGTH_SHORT).show()

        }
    }

    private fun inputCheck(title: String, content: String, date: String): Boolean {
        return !((TextUtils.isEmpty(title) && TextUtils.isEmpty(content))  || TextUtils.isEmpty(date))

    }

    private fun isPackageExpired(date: String): Boolean {
        var isExpired = false
        val expiredDate = stringToDate(date)
        if (Date().after(expiredDate)) isExpired = true
        return isExpired
    }

    private fun stringToDate(aDate: String?): Date? {
        if (aDate == null) return null
        val pos = ParsePosition(0)
        val simpledateformat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss") //""EEE MMM d HH:mm:ss zz yyyy")
        return simpledateformat.parse(aDate, pos)
    }

    private fun convertStringToDate(data: String): Date? {
        val format: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        var date: Date? = null
        try {
            date = format.parse(data)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date
    }


}