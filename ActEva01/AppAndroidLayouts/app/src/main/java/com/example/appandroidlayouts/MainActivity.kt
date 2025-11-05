package com.example.appandroidlayouts  // ← Debe ser TU paquete

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnIrPresentacion = findViewById<Button>(R.id.btnIrPresentacion)
        btnIrPresentacion.setOnClickListener {
            val intent = Intent(this, PresentacionActivity::class.java)
            startActivity(intent)
        }
    }
}