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
import java.util.*


private const val DIALOG_DATE = "DialogDate"
class AddFragment : Fragment() {

    var dateString = ""
    var timeString = ""

    private lateinit var mCrimeViewModel: CrimeViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        view.addDate_button.setOnClickListener {
            val cal = Calendar.getInstance()    //캘린더뷰 만들기
            val dateSetListener = DatePickerDialog.OnDateSetListener {
                    datePicker, year, month, dayOfMonth ->
                val resultDate : Date = GregorianCalendar(year, month, dayOfMonth).time
                dateString = "${year}년 ${month+1}월 ${dayOfMonth}일"
                addDate_et_old.text = "${resultDate}" //"날짜/시간 : "+ dateString // + " / " + timeString
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
            val timeSetListener = TimePickerDialog.OnTimeSetListener { datepicker, hourOfDay, minute ->
                timeString = "${hourOfDay}시 ${minute}분"
                addDate_et_old.text = "${addDate_et_old.text}" + " " + timeString
            }
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
        val date = addDate_et_old.text.toString()
        val content = addContents_et.text.toString()
        val isChecked = addIsSolved_CheckBox.isChecked
        Log.d("date check", "${date}, ${stringToDate(date, "EEE MMM d HH:mm:ss zz yyyy")}")

        if (inputCheck(title, content)){
            //Creat Crime Object
            val crime = stringToDate(date, "EEE MMM d HH:mm:ss zz yyyy")?.let {
                Crime(0, title, it, isChecked)
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

    private fun inputCheck(title: String, content: String): Boolean {
        return !(TextUtils.isEmpty(title) && TextUtils.isEmpty(content))
    }

    private fun isPackageExpired(date: String): Boolean {
        var isExpired = false
        val expiredDate = stringToDate(date, "EEE MMM d HH:mm:ss zz yyyy")
        if (Date().after(expiredDate)) isExpired = true
        return isExpired
    }

    private fun stringToDate(aDate: String?, aFormat: String): Date? {
        if (aDate == null) return null
        val pos = ParsePosition(0)
        val simpledateformat = SimpleDateFormat(aFormat)
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