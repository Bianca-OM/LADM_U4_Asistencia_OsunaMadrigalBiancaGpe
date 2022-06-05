package com.example.ladm_u4_asistencia_osunamadrigalbiancagpe

import android.R
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import com.example.ladm_u4_asistencia_osunamadrigalbiancagpe.databinding.ActivityMain3Binding
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity3 : AppCompatActivity() {
    lateinit var binding: ActivityMain3Binding

    val vector = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lblDate.text = fechaActual()

        llenarLista()

        binding.btnDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnGuardar.setOnClickListener {
            if(vector.size>0) {
                guardarLista()
            } else{
                AlertDialog.Builder(this)
                    .setTitle("LISTA VACIA")
                    .setMessage("¿Desea guardar lista de todos modos?")
                    .setPositiveButton("GUARDAR",{d,i->
                        guardarLista()
                    })
                    .setNegativeButton("CANCELAR",{d,i->d.cancel()})
                    .show()
            }
        }

        binding.btnConsultar.setOnClickListener {
            leerLista()
        }

        binding.btnRegresar.setOnClickListener {
            finish()
        }

        binding.spnHora.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                llenarLista()
            }
        }
    }

    fun llenarLista(){
        vector.clear()
        val fecha = binding.lblDate.text.toString()
        val hrSelected = binding.spnHora.selectedItem.toString().split(":")
        val hora = hrSelected[0]

        val lista = Asistencia(this).buscarLista(fecha,hora)

        if(lista.size>0) {
            (0..lista.size - 1).forEach {
                val asist = lista[it]
                vector.add(
                    "Noctrl: ${asist.noctrl}\n" +
                            "Nombre: ${asist.nombre}\n" +
                            "Hora: ${asist.hora}:${asist.minutos}"
                )
            }
        }

        binding.lvAsistencia.adapter = ArrayAdapter<String>(this,
            R.layout.simple_list_item_1, vector)

        binding.lvAsistencia.setOnItemClickListener { adapterView, view, position, id ->
            AlertDialog.Builder(this)
                .setTitle("ELIMINAR ASISTENCIA")
                .setMessage("¿Desea eliminar la asistencia de " +
                        "${lista[position].nombre} el día ${binding.lblDate.text}?")
                .setPositiveButton("ELIMINAR",{d,i->
                    val asistEliminar = lista[position]
                    if(asistEliminar.eliminarAsistencia()){
                        AlertDialog.Builder(this)
                            .setMessage("ASISTENCIA ELIMINADA")
                            .show()
                    }
                    llenarLista()
                    d.dismiss()
                })
                .setNegativeButton("CANCELAR",{d,i->d.cancel()})
                .show()
        }
    }

    fun guardarLista(){
        try {
            val archivo = OutputStreamWriter(openFileOutput(
                "lista(${binding.lblDate.text})" +
                        "_hora(${binding.spnHora.selectedItem}).txt",
                MODE_PRIVATE))
            var cadena = ""

            (0..vector.size - 1).forEach {
                cadena = cadena + vector[it] + ","
            }

            archivo.write(cadena)
            archivo.flush()
            archivo.close()
            AlertDialog.Builder(this).setMessage("SE GUARDÓ").show()
        }catch (e:Exception){
            AlertDialog.Builder(this).setMessage(e.message).show()
        }
    }

    fun leerLista(){
        var cadena = ""
        try {
            cadena = BufferedReader(
                InputStreamReader(this.openFileInput(
                    "lista(${binding.lblDate.text})" +
                            "_hora(${binding.spnHora.selectedItem}).txt"))
            ).readText()
            AlertDialog.Builder(this)
                .setTitle("lista(${binding.lblDate.text})" +
                    "_hora(${binding.spnHora.selectedItem}).txt")
                .setMessage(cadena).show()
        }catch (e:Exception){
            AlertDialog.Builder(this).setTitle("ERROR").setMessage(e.message).show()
        }
    }

    fun showDatePicker(){
        val dateSelected = binding.lblDate.text.toString().split("-")
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
            binding.lblDate.text = "0${day}-0${m}-${year}"
        } else if(m<10) {
            binding.lblDate.text = "${day}-0${m}-${year}"
        } else if(day<10) {
            binding.lblDate.text = "0${day}-${m}-${year}"
        } else {
            binding.lblDate.text = "${day}-${m}-${year}"
        }
        binding.spnHora.setSelection(0)
        llenarLista()
    }

    @SuppressLint("NewApi")
    fun fechaActual(): String{
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val formatted = current.format(formatter)
        return formatted
    }
}