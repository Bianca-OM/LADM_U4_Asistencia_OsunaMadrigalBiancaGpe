package com.example.ladm_u4_asistencia_osunamadrigalbiancagpe

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDatos(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE DATOS(NOMBRE VARCHAR(200) PRIMARY KEY, MATERIA VARCHAR(50))")
        db.execSQL("CREATE TABLE ASISTENCIA(NOCTRL VARCHAR(8), FECHA VARCHAR(10), HORA VARCHAR(2), MINUTOS VARCHAR(2), NOMBRE VARCHAR(200), PRIMARY KEY (NOCTRL, FECHA))")
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        //
    }
}