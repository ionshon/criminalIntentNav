package com.inu.andoid.criminalintentnav.fragments.update

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
import com.inu.andoid.criminalintentnav.R
import com.inu.andoid.criminalintentnav.model.Crime
import com.inu.andoid.criminalintentnav.viewmodel.CrimeViewModel
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.fragment_add.view.*
import kotlinx.android.synthetic.main.fragment_update.*
import kotlinx.android.synthetic.main.fragment_update.view.*
import java.util.*

class UpdateFragment : Fragment() {

    private val args by navArgs<UpdateFragmentArgs>()
    private lateinit var mCrimeViewModel : CrimeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_update, container, false)
        view.updateTitle_et.setText(args.currentCrime.title)
        /*view.updateDate_et.setOnClickListener {
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("일자를 골라주세요")
                    .build()

            datePicker.show(childFragmentManager, "date_picker")
            datePicker.addOnPositiveButtonClickListener {
                updateDate_et.text = datePicker.headerText
            }
        }*/
        mCrimeViewModel = ViewModelProvider(this).get(CrimeViewModel::class.java)

        view.update_button.setOnClickListener {
            updateItem()
        }

        // Add menu
        setHasOptionsMenu(true)

        return view
    }

    private fun updateItem() {
        val title = updateTitle_et.text.toString()
        val date = updateDate_et.text.toString()
        val isChecked = updateIsSolved_CheckBox.isChecked
        val content = updateContents_et.text.toString()

    //    Toast.makeText(requireContext(),"${title}", Toast.LENGTH_SHORT).show()

        if (inputCheck(title, content)) {
            val crime = Crime(args.currentCrime.id, title, Date(), isChecked)
            mCrimeViewModel.updateCrime(crime)
            Toast.makeText(requireContext(), "${args.currentCrime.id}: ${title}, Successfully updated!", Toast.LENGTH_SHORT).show()
            // Navigate Back
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }else
            Toast.makeText(requireContext(), "fill out update!", Toast.LENGTH_SHORT).show()

    }

    private fun inputCheck(title: String, content: String): Boolean {
        return !(TextUtils.isEmpty(title) && TextUtils.isEmpty(content))
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
}



