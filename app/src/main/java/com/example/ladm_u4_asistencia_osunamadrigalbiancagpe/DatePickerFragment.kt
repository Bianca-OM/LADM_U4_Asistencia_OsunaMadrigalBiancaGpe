package com.example.ladm_u4_asistencia_osunamadrigalbiancagpe

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerFragment (val listener: (day: Int, month: Int, year: Int) -> Unit) :
    DialogFragment(), DatePickerDialog.OnDateSetListener {

    var anio = 2022
    var mes = 5
    var dia = 10

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        listener(day, month, year)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        /*val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)*/
        var picker = DatePickerDialog(activity as Context, this, anio, mes, dia)
        c.add(Calendar.YEAR, 0)
        picker.datePicker.maxDate = c.timeInMillis
        //c.add(Calendar.YEAR, -5)
        //picker.datePicker.minDate = c.timeInMillis
        return picker
    }
}