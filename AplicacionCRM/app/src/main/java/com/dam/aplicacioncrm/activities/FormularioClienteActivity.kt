package com.dam.aplicacioncrm.activities

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dam.aplicacioncrm.R
import com.dam.aplicacioncrm.database.DatabaseHelper
import com.dam.aplicacioncrm.models.Cliente
import com.google.android.material.textfield.TextInputEditText

/**
 * Activity para crear o editar un cliente
 */
class FormularioClienteActivity : AppCompatActivity() {

    private lateinit var etNombre: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etTelefono: TextInputEditText
    private lateinit var btnGuardar: Button
    private lateinit var btnCancelar: Button
    private lateinit var btnEliminar: Button
    private lateinit var tvTitulo: TextView
    private lateinit var dbHelper: DatabaseHelper

    private var clienteId: Int? = null
    private var modoEdicion = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_cliente)

        // Inicializar base de datos
        dbHelper = DatabaseHelper(this)

        // Inicializar vistas
        inicializarVistas()

        // Verificar si estamos editando un cliente existente
        verificarModoEdicion()

        // Configurar botones
        configurarBotones()
    }

    /**
     * Inicializa todas las vistas del layout
     */
    private fun inicializarVistas() {
        etNombre = findViewById(R.id.etNombre)
        etEmail = findViewById(R.id.etEmail)
        etTelefono = findViewById(R.id.etTelefono)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnCancelar = findViewById(R.id.btnCancelar)
        btnEliminar = findViewById(R.id.btnEliminar)
        tvTitulo = findViewById(R.id.tvTituloFormulario)
    }

    /**
     * Verifica si estamos en modo edición o creación
     */
    private fun verificarModoEdicion() {
        val extras = intent.extras

        if (extras != null && extras.containsKey("CLIENTE_ID")) {
            // Modo edición
            modoEdicion = true
            clienteId = extras.getInt("CLIENTE_ID")

            // Cargar datos del cliente
            etNombre.setText(extras.getString("CLIENTE_NOMBRE"))
            etEmail.setText(extras.getString("CLIENTE_EMAIL"))
            etTelefono.setText(extras.getString("CLIENTE_TELEFONO"))

            // Cambiar título y mostrar botón eliminar
            tvTitulo.text = "Editar Cliente"
            btnEliminar.visibility = Button.VISIBLE
        } else {
            // Modo creación
            modoEdicion = false
            tvTitulo.text = "Nuevo Cliente"
            btnEliminar.visibility = Button.GONE
        }
    }

    /**
     * Configura los listeners de los botones
     */
    private fun configurarBotones() {
        btnGuardar.setOnClickListener {
            guardarCliente()
        }

        btnCancelar.setOnClickListener {
            finish()
            // Transición al volver atrás
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        btnEliminar.setOnClickListener {
            confirmarEliminacion()
        }
    }

    /**
     * Valida y guarda el cliente en la base de datos
     */
    private fun guardarCliente() {
        // Obtener valores
        val nombre = etNombre.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val telefono = etTelefono.text.toString().trim()

        // Crear objeto cliente temporal para validación
        val cliente = Cliente(
            id = clienteId ?: 0,
            nombre = nombre,
            email = email,
            telefono = telefono
        )

        // Validar campos
        if (!validarCliente(cliente)) {
            return
        }

        // Guardar en base de datos
        val exitoso = if (modoEdicion) {
            dbHelper.actualizarCliente(cliente) > 0
        } else {
            dbHelper.insertarCliente(cliente) > 0
        }

        // Verificar resultado
        if (exitoso) {
            val mensaje = if (modoEdicion) "Cliente actualizado" else "Cliente guardado"
            mostrarMensaje(mensaje)
            finish()
        } else {
            mostrarMensaje("Error al guardar el cliente")
        }
    }

    /**
     * Valida los datos del cliente
     */
    private fun validarCliente(cliente: Cliente): Boolean {
        // Verificar campos vacíos
        if (!cliente.esValido()) {
            mostrarMensaje("Todos los campos son obligatorios")
            return false
        }

        // Validar email
        if (!cliente.emailEsValido()) {
            mostrarMensaje("El formato del email no es válido")
            etEmail.error = "Email inválido"
            etEmail.requestFocus()
            return false
        }

        // Validar teléfono
        if (!cliente.telefonoEsValido()) {
            mostrarMensaje("El teléfono debe tener al menos 9 dígitos")
            etTelefono.error = "Mínimo 9 dígitos"
            etTelefono.requestFocus()
            return false
        }

        return true
    }

    /**
     * Muestra confirmación antes de eliminar
     */
    private fun confirmarEliminacion() {
        AlertDialog.Builder(this)
            .setTitle("Eliminar cliente")
            .setMessage("¿Estás seguro de que deseas eliminar este cliente?")
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarCliente()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Elimina el cliente de la base de datos
     */
    private fun eliminarCliente() {
        clienteId?.let { id ->
            val resultado = dbHelper.eliminarCliente(id)

            if (resultado > 0) {
                mostrarMensaje("Cliente eliminado correctamente")
                finish()
            } else {
                mostrarMensaje("Error al eliminar el cliente")
            }
        }
    }

    /**
     * Muestra un mensaje Toast
     */
    private fun mostrarMensaje(mensaje: String) {
        android.widget.Toast.makeText(this, mensaje, android.widget.Toast.LENGTH_SHORT).show()
    }
}