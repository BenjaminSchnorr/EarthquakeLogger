package com.example.final_project_435

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar


class DateFragment : DialogFragment(), DatePickerDialog.OnDateSetListener{

    interface OnDateSetListener{
        fun onDateSelected(year: Int, month: Int, day: Int)
    }

    private lateinit var listener: OnDateSetListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(requireActivity(), activity as DatePickerDialog.OnDateSetListener, year, month, day)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_date, container, false)

    }


    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int){
        listener.onDateSelected(year, month, day)
    }
}