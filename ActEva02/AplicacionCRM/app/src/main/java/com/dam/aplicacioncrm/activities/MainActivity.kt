package com.dam.aplicacioncrm.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dam.aplicacioncrm.R
import com.dam.aplicacioncrm.adapters.ClienteAdapter
import com.dam.aplicacioncrm.database.DatabaseHelper
import com.dam.aplicacioncrm.models.Cliente
import com.dam.aplicacioncrm.utils.ThemePreference
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText

/**
 * Activity principal que muestra la lista de clientes
 */
class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ClienteAdapter
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var etBuscar: TextInputEditText
    private lateinit var tvContador: TextView
    private lateinit var fabAgregar: FloatingActionButton
    private lateinit var toolbar: Toolbar

    // Switches del panel de configuración
    private lateinit var switchTema: SwitchCompat
    private lateinit var switchMano: SwitchCompat

    private var listaClientes = mutableListOf<Cliente>()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Aplicar el tema guardado antes de setContentView
        ThemePreference.applyTheme(ThemePreference.getThemeMode(this))

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar base de datos
        dbHelper = DatabaseHelper(this)

        // Inicializar vistas
        inicializarVistas()

        // Configurar toolbar
        setSupportActionBar(toolbar)

        // Configurar switches del panel
        configurarSwitches()

        // Configurar RecyclerView
        configurarRecyclerView()

        // Configurar búsqueda
        configurarBusqueda()

        // Cargar clientes
        cargarClientes()

        // Aplicar posición inicial del FAB según preferencia
        actualizarPosicionFAB()

        // Configurar botón flotante
        fabAgregar.setOnClickListener {
            abrirFormulario()
        }
    }

    /**
     * Inicializa todas las vistas del layout
     */
    private fun inicializarVistas() {
        recyclerView = findViewById(R.id.recyclerViewClientes)
        etBuscar = findViewById(R.id.etBuscar)
        tvContador = findViewById(R.id.tvContador)
        fabAgregar = findViewById(R.id.fabAgregarCliente)
        toolbar = findViewById(R.id.toolbar)

        // Switches del panel
        switchTema = findViewById(R.id.switchTema)
        switchMano = findViewById(R.id.switchMano)
    }

    /**
     * Configura los switches del panel de configuración
     */
    private fun configurarSwitches() {
        // Configurar switch de tema
        switchTema.isChecked = ThemePreference.isDarkMode(this)

        switchTema.setOnCheckedChangeListener { _, isChecked ->
            val newMode = if (isChecked) {
                ThemePreference.MODE_DARK
            } else {
                ThemePreference.MODE_LIGHT
            }

            ThemePreference.saveThemeMode(this, newMode)
            recreate()
        }

        // Configurar switch de modo mano
        // isChecked = true cuando es diestro (R), false cuando es zurdo (L)
        val esZurdo = ThemePreference.isLeftHanded(this)
        switchMano.isChecked = !esZurdo  // Si es zurdo (true), switch debe estar apagado (false)

        switchMano.setOnCheckedChangeListener { _, isChecked ->
            // isChecked = true significa switch hacia la derecha (R) = Diestro
            // isChecked = false significa switch hacia la izquierda (L) = Zurdo
            val newMode = if (isChecked) {
                ThemePreference.HAND_RIGHT  // Switch a la derecha = Diestro
            } else {
                ThemePreference.HAND_LEFT   // Switch a la izquierda = Zurdo
            }

            ThemePreference.saveHandMode(this, newMode)
            actualizarPosicionFAB()
        }
    }

    /**
     * Actualiza la posición del FloatingActionButton según preferencia
     */
    private fun actualizarPosicionFAB() {
        val layoutParams = fabAgregar.layoutParams as androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams

        if (ThemePreference.isLeftHanded(this)) {
            // Modo zurdo: botón a la izquierda
            layoutParams.gravity = android.view.Gravity.BOTTOM or android.view.Gravity.START
        } else {
            // Modo diestro: botón a la derecha
            layoutParams.gravity = android.view.Gravity.BOTTOM or android.view.Gravity.END
        }

        fabAgregar.layoutParams = layoutParams
    }

    /**
     * Configura el RecyclerView con su adapter y layout manager
     */
    private fun configurarRecyclerView() {
        adapter = ClienteAdapter(
            listaClientes,
            onItemClick = { cliente ->
                // Al hacer click normal: editar cliente
                abrirFormulario(cliente)
            },
            onItemLongClick = { cliente ->
                // Al mantener presionado: mostrar opciones
                mostrarOpcionesCliente(cliente)
                true
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    /**
     * Configura el buscador en tiempo real
     */
    private fun configurarBusqueda() {
        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                buscarClientes(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    /**
     * Carga todos los clientes de la base de datos
     */
    private fun cargarClientes() {
        listaClientes = dbHelper.obtenerTodosLosClientes().toMutableList()
        adapter.actualizarLista(listaClientes)
        actualizarContador()
    }

    /**
     * Busca clientes por nombre o email
     */
    private fun buscarClientes(query: String) {
        if (query.isEmpty()) {
            cargarClientes()
        } else {
            val resultados = dbHelper.buscarClientes(query)
            listaClientes = resultados.toMutableList()
            adapter.actualizarLista(listaClientes)
            actualizarContador()
        }
    }

    /**
     * Actualiza el contador de clientes
     */
    private fun actualizarContador() {
        val total = dbHelper.contarClientes()
        tvContador.text = "Total de clientes: $total"
    }

    /**
     * Abre el formulario para crear o editar un cliente
     */
    private fun abrirFormulario(cliente: Cliente? = null) {
        val intent = Intent(this, FormularioClienteActivity::class.java)

        // Si hay un cliente, se pasan sus datos para editar
        cliente?.let {
            intent.putExtra("CLIENTE_ID", it.id)
            intent.putExtra("CLIENTE_NOMBRE", it.nombre)
            intent.putExtra("CLIENTE_EMAIL", it.email)
            intent.putExtra("CLIENTE_TELEFONO", it.telefono)
        }

        startActivity(intent)
        // Transición suave al abrir el formulario
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }

    /**
     * Muestra un diálogo con opciones para el cliente
     */
    private fun mostrarOpcionesCliente(cliente: Cliente) {
        val opciones = arrayOf("Editar", "Eliminar")

        AlertDialog.Builder(this)
            .setTitle(cliente.nombre)
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> abrirFormulario(cliente) // Editar
                    1 -> confirmarEliminacion(cliente) // Eliminar
                }
            }
            .show()
    }

    /**
     * Muestra un diálogo de confirmación antes de eliminar
     */
    private fun confirmarEliminacion(cliente: Cliente) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar cliente")
            .setMessage("¿Estás seguro de que deseas eliminar a ${cliente.nombre}?")
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarCliente(cliente)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Elimina un cliente de la base de datos
     */
    private fun eliminarCliente(cliente: Cliente) {
        val resultado = dbHelper.eliminarCliente(cliente.id)

        if (resultado > 0) {
            cargarClientes()
            mostrarMensaje("Cliente eliminado correctamente")
        } else {
            mostrarMensaje("Error al eliminar el cliente")
        }
    }

    /**
     * Muestra un mensaje Toast
     */
    private fun mostrarMensaje(mensaje: String) {
        android.widget.Toast.makeText(this, mensaje, android.widget.Toast.LENGTH_SHORT).show()
    }

    /**
     * Se ejecuta cuando se vuelve a esta activity
     * Recarga los datos por si hubo cambios
     */
    override fun onResume() {
        super.onResume()
        cargarClientes()
    }
}