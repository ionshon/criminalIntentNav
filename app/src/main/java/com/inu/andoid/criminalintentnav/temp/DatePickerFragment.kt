package com.inu.andoid.criminalintentnav.temp

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*

private const val ARG_DATE = "date"
class DatePickerFragment : DialogFragment() {

    interface Callbacks {
        fun onDateSelected(date: Date)
    }
       override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

           val dateLitener =
               DatePickerDialog.OnDateSetListener { datePicker, year: Int, month: Int, day: Int ->
                   val resultDate: Date = GregorianCalendar(year, month, day).time
                   targetFragment?.let { fragment ->
                       (fragment as Callbacks).onDateSelected(resultDate)
                   }
               }

           val date = arguments?.getSerializable(ARG_DATE) as Date
           val calender = Calendar.getInstance()
           calender.time = date
           val calendar = Calendar.getInstance()
           val initialYear = calendar.get(Calendar.YEAR)
           val initialMonth = calendar.get(Calendar.MONTH)
           val initialDay = calendar.get(Calendar.DAY_OF_MONTH)

           return DatePickerDialog(
               requireContext(),
               null,
               initialYear,
               initialMonth,
               initialDay
           )
           /*  override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_date_picker, container, false)
    }*/
       }
}