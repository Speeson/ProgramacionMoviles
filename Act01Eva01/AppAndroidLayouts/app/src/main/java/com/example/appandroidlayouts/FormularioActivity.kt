package com.example.appandroidlayouts  // ← Debe ser TU paquete

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class FormularioActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etMensaje: EditText
    private lateinit var btnEnviar: Button
    private lateinit var tvConfirmacion: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario)

        etNombre = findViewById(R.id.etNombre)
        etCorreo = findViewById(R.id.etCorreo)
        etMensaje = findViewById(R.id.etMensaje)
        btnEnviar = findViewById(R.id.btnEnviar)
        tvConfirmacion = findViewById(R.id.tvConfirmacion)

        btnEnviar.setOnClickListener {
            validarYEnviarFormulario()
        }
    }

    private fun validarYEnviarFormulario() {
        val nombre = etNombre.text.toString().trim()
        val correo = etCorreo.text.toString().trim()
        val mensaje = etMensaje.text.toString().trim()

        if (nombre.isEmpty()) {
            etNombre.error = "El nombre es obligatorio"
            etNombre.requestFocus()
            return
        }

        if (correo.isEmpty()) {
            etCorreo.error = "El correo es obligatorio"
            etCorreo.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etCorreo.error = "Formato de correo inválido"
            etCorreo.requestFocus()
            return
        }

        if (mensaje.isEmpty()) {
            etMensaje.error = "El mensaje es obligatorio"
            etMensaje.requestFocus()
            return
        }

        mostrarConfirmacion(nombre, correo, mensaje)
    }

    private fun mostrarConfirmacion(nombre: String, correo: String, mensaje: String) {
        Toast.makeText(this, "Formulario enviado correctamente", Toast.LENGTH_LONG).show()

        val textoConfirmacion = """
            ✓ Datos recibidos correctamente:
            
            Nombre: $nombre
            Correo: $correo
            Mensaje: $mensaje
        """.trimIndent()

        tvConfirmacion.text = textoConfirmacion
        tvConfirmacion.visibility = android.view.View.VISIBLE

        limpiarFormulario()
    }

    private fun limpiarFormulario() {
        etNombre.text.clear()
        etCorreo.text.clear()
        etMensaje.text.clear()
    }
}