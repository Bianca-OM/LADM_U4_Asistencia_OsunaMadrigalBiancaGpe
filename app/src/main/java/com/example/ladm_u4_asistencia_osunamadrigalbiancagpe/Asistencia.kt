package com.example.ladm_u4_asistencia_osunamadrigalbiancagpe

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteException

class Asistencia (e: Context) {
    private val este = e
    private val base = "asistencialistas"
    var noctrl = ""
    var fecha = ""
    var hora = ""
    var minutos = ""
    var nombre = ""
    private var err = ""

    fun insertar() : Boolean{
        val basedatos = BaseDatos(este, base, null, 1)
        err = ""
        try {
            val tabla = basedatos.writableDatabase
            val datos = ContentValues()

            datos.put("NOCTRL", noctrl)
            datos.put("FECHA", fecha)
            datos.put("HORA", hora)
            datos.put("MINUTOS", minutos)
            datos.put("NOMBRE", nombre)

            val respuesta = tabla.insert("ASISTENCIA", null, datos)
            if(respuesta == -1L){
                AlertDialog.Builder(este)
                    .setTitle("Error")
                    .setMessage("Respuesta = -1\n" +
                            "Verifique que el registro no se repita")
                    .show()
                return false
            }
        } catch (err: SQLiteException) {
            this.err = err.message!!
            AlertDialog.Builder(este)
                .setMessage("Error: ${this.err}")
                .show()
            return false
        } finally {
            basedatos.close()
        }
        return true
    }

    fun mostrarTodos() : ArrayList<Asistencia> {
        val basedatos = BaseDatos(este, base, null, 1)
        err = ""
        val lista = ArrayList<Asistencia>()

        try{
            val tabla = basedatos.readableDatabase
            val SQLSELECT = "SELECT * FROM ASISTENCIA"

            val cursor = tabla.rawQuery(SQLSELECT, null)
            if(cursor.moveToFirst()){
                do {
                    val asist = Asistencia(este)
                    asist.noctrl = cursor.getString(0)
                    asist.fecha = cursor.getString(1)
                    asist.hora = cursor.getString(2)
                    asist.minutos = cursor.getString(3)
                    asist.nombre = cursor.getString(4)
                    lista.add(asist)
                }while (cursor.moveToNext())
            }
        } catch (err:SQLiteException) {
            this.err = err.message!!
            AlertDialog.Builder(este)
                .setMessage("Error: ${this.err}")
                .show()
        } finally {
            basedatos.close()
        }
        return lista
    }

    fun buscarLista(fechaBuscar: String, horaBuscar: String) : ArrayList<Asistencia> {
        val basedatos = BaseDatos(este, base, null, 1)
        err = ""
        val lista = ArrayList<Asistencia>()
        val fb = fechaBuscar
        val hb = horaBuscar

        try {
            val tabla = basedatos.readableDatabase
            val SQLSELECT = "SELECT * FROM ASISTENCIA WHERE FECHA=? AND HORA=?"

            val cursor = tabla.rawQuery(SQLSELECT, arrayOf(fb,hb))
            if(cursor.moveToFirst()){
                do {
                    val asist = Asistencia(este)
                    asist.noctrl = cursor.getString(0)
                    asist.fecha = cursor.getString(1)
                    asist.hora = cursor.getString(2)
                    asist.minutos = cursor.getString(3)
                    asist.nombre = cursor.getString(4)
                    lista.add(asist)
                }while (cursor.moveToNext())
            }

        } catch (err:SQLiteException) {
            this.err = err.message!!
            AlertDialog.Builder(este)
                .setMessage("Error: ${this.err}")
                .show()
        } finally {
            basedatos.close()
        }
        return lista
    }

    fun buscarAsistencia(ncBuscar: String, fechaBuscar: String) : Asistencia {
        val basedatos = BaseDatos(este, base, null, 1)
        err = ""
        val ncb = ncBuscar
        val fb = fechaBuscar
        val asist = Asistencia(este)

        try {
            val tabla = basedatos.readableDatabase
            val SQLSELECT = "SELECT * FROM ASISTENCIA WHERE NOCTRL=? AND FECHA=?"

            val cursor = tabla.rawQuery(SQLSELECT, arrayOf(ncb,fb))
            if(cursor.moveToFirst()){
                asist.noctrl = cursor.getString(0)
                asist.fecha = cursor.getString(1)
                asist.hora = cursor.getString(2)
                asist.minutos = cursor.getString(3)
                asist.nombre = cursor.getString(4)
            }

        } catch (err:SQLiteException) {
            this.err = err.message!!
            AlertDialog.Builder(este)
                .setMessage("Error: ${this.err}")
                .show()
        } finally {
            basedatos.close()
        }
        return asist
    }

    fun actualizar() : Boolean {
        val basedatos = BaseDatos(este, base, null, 1)
        err = ""
        try {
            val tabla = basedatos.writableDatabase
            val datosActualizados = ContentValues()

            datosActualizados.put("NOMBRE", nombre)

            val respuesta = tabla.update("ASISTENCIA", datosActualizados,
                "NOCTRL=?", arrayOf(noctrl))

            if(respuesta == 0){
                AlertDialog.Builder(este)
                    .setTitle("Error")
                    .setMessage("Respuesta = 0")
                    .show()
                return false
            }

        } catch (err:SQLiteException) {
            this.err = err.message!!
            AlertDialog.Builder(este)
                .setMessage("Error: ${this.err}")
                .show()
            return false
        } finally {
            basedatos.close()
        }
        return true
    }

    fun eliminarAsistencia(): Boolean{
        val basedatos = BaseDatos(este, base, null, 1)
        try {
            val tabla = basedatos.writableDatabase
            val respuesta = tabla.delete("ASISTENCIA","NOCTRL=? AND FECHA=?",arrayOf(noctrl,fecha))

            if(respuesta == 0){
                AlertDialog.Builder(este)
                    .setTitle("Error")
                    .setMessage("Respuesta = 0")
                    .show()
                return false
            }

        } catch (err:SQLiteException) {
            this.err = err.message!!
            AlertDialog.Builder(este)
                .setMessage("Error: ${this.err}")
                .show()
            return false
        } finally {
            basedatos.close()
        }
        return true
    }

    fun eliminarAlumno(): Boolean{
        val basedatos = BaseDatos(este, base, null, 1)
        try {
            val tabla = basedatos.writableDatabase
            val respuesta = tabla.delete("ASISTENCIA","NOCTRL=?",arrayOf(noctrl))

            if(respuesta == 0){
                AlertDialog.Builder(este)
                    .setTitle("Error")
                    .setMessage("Respuesta = 0")
                    .show()
                return false
            }

        } catch (err:SQLiteException) {
            this.err = err.message!!
            AlertDialog.Builder(este)
                .setMessage("Error: ${this.err}")
                .show()
            return false
        } finally {
            basedatos.close()
        }
        return true
    }
}