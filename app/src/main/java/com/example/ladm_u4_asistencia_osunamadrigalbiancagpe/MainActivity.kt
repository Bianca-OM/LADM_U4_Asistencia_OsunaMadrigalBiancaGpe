package com.example.ladm_u4_asistencia_osunamadrigalbiancagpe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.example.ladm_u4_asistencia_osunamadrigalbiancagpe.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnTest.setOnClickListener {
            val vtnTest = Intent(this, MainActivity2::class.java)
            startActivity(vtnTest)
        }

        binding.btnPasar.setOnClickListener {
            val vtnPasar = Intent(this, MainActivity5::class.java)
            startActivity(vtnPasar)
        }

        binding.btnGestionar.setOnClickListener {
            val vtnGestionar = Intent(this, MainActivity3::class.java)
            startActivity(vtnGestionar)
        }

        binding.btnInsertar.setOnClickListener {
            val vtnInsertar = Intent(this, MainActivity4::class.java)
            startActivity(vtnInsertar)
        }
    }
}