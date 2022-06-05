package com.example.ladm_u4_asistencia_osunamadrigalbiancagpe

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.example.ladm_u4_asistencia_osunamadrigalbiancagpe.databinding.ActivityMain4Binding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity4 : AppCompatActivity() {
    lateinit var binding: ActivityMain4Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain4Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lblFecha.text = fechaActual()

        binding.btnFecha.setOnClickListener {
            showDatePicker()
        }

        binding.btnInsertar.setOnClickListener {
            insertar()
        }

        binding.btnRegresar.setOnClickListener {
            finish()
        }
    }

    fun insertar(){
        val nc = binding.txtNctrl.text.toString()
        val nombre = binding.txtNombre.text.toString()
        val fecha = binding.lblFecha.text.toString()
        val hora = binding.spnHora.selectedItem.toString()
        val min = binding.spnMin.selectedItem.toString()

        if(nc == "" || nombre == ""){
            AlertDialog.Builder(this)
                .setTitle("CAMPOS VACIOS")
                .setMessage("Favor de verificar campos")
                .show()
        }else if(nc.length>8 || nombre.length>200){
            AlertDialog.Builder(this)
                .setTitle("LONGITUD EXCEDIDA")
                .setMessage("No. control no puede contener más de 8 caracteres\n" +
                        "Nombre no puede contener más de 200 caracteres")
                .show()
        }else{
            val asist = Asistencia(this)
            asist.noctrl = nc
            asist.fecha = fecha
            asist.hora = hora
            asist.minutos = min
            asist.nombre = nombre
            if(asist.insertar()) {
                AlertDialog.Builder(this).setMessage("ASISTENCIA GUARDADA").show()
                reset()
            }
        }
    }

    fun reset(){
        binding.txtNctrl.setText("")
        binding.txtNombre.setText("")
        binding.lblFecha.text = fechaActual()
        binding.spnHora.setSelection(0)
        binding.spnMin.setSelection(0)
    }

    fun showDatePicker(){
        val dateSelected = binding.lblFecha.text.toString().split("-")
        val d = dateSelected[0].toInt()
        val m = dateSelected[1].toInt()
        val y = dateSelected[2].toInt()

        val datePicker = DatePickerFragment{
                day, month, year -> onDateSelected(day, month, year)
        }
        datePicker.dia = d
        datePicker.mes = m-1
        datePicker.anio = y
        datePicker.show(supportFragmentManager, "datePicker")
    }

    fun onDateSelected(day: Int, month: Int, year: Int) {
        val m = month+1
        if(m<10 && day<10){
            binding.lblFecha.text = "0${day}-0${m}-${year}"
        } else if(m<10) {
            binding.lblFecha.text = "${day}-0${m}-${year}"
        } else if(day<10) {
            binding.lblFecha.text = "0${day}-${m}-${year}"
        } else {
            binding.lblFecha.text = "${day}-${m}-${year}"
        }
    }

    @SuppressLint("NewApi")
    fun fechaActual(): String{
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val formatted = current.format(formatter)
        return formatted
    }

    @SuppressLint("NewApi")
    fun horaActual(): String{
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val formatted = current.format(formatter)
        return formatted
    }
}