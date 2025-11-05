package com.dam.aplicacioncrm.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dam.aplicacioncrm.R
import com.dam.aplicacioncrm.models.Cliente
import com.google.android.material.button.MaterialButton

/**
 * Adaptador para mostrar la lista de clientes en un RecyclerView
 * @property listaClientes Lista de clientes a mostrar
 * @property onItemClick Callback para cuando se hace clic en un item
 * @property onItemLongClick Callback para cuando se mantiene presionado un item
 */
class ClienteAdapter(
    private var listaClientes: List<Cliente>,
    private val onItemClick: (Cliente) -> Unit,
    private val onItemLongClick: (Cliente) -> Boolean
) : RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder>() {

    private var lastPosition = -1

    /**
     * ViewHolder que contiene las referencias a las vistas de cada item
     */
    inner class ClienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        val tvTelefono: TextView = itemView.findViewById(R.id.tvTelefono)
        val btnEmail: MaterialButton = itemView.findViewById(R.id.btnEmail)
        val btnLlamar: MaterialButton = itemView.findViewById(R.id.btnLlamar)

        /**
         * Vincula los datos del cliente con las vistas
         */
        fun bind(cliente: Cliente) {
            tvNombre.text = cliente.nombre
            tvEmail.text = cliente.email
            tvTelefono.text = cliente.telefono

            // Click normal para editar
            itemView.setOnClickListener {
                onItemClick(cliente)
            }

            // Long click para opciones adicionales
            itemView.setOnLongClickListener {
                onItemLongClick(cliente)
            }

            // Botón Email: Abre la app de email
            btnEmail.setOnClickListener {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:${cliente.email}")
                    putExtra(Intent.EXTRA_SUBJECT, "Contacto desde CRM")
                }

                // Verificar que hay una app de email disponible
                if (intent.resolveActivity(itemView.context.packageManager) != null) {
                    itemView.context.startActivity(intent)
                } else {
                    // Si no hay app de email, mostrar mensaje
                    android.widget.Toast.makeText(
                        itemView.context,
                        "No hay aplicación de email instalada",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            }

            // Botón Llamar: Abre el marcador telefónico
            btnLlamar.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${cliente.telefono}")
                }

                // Verificar que hay una app de teléfono disponible
                if (intent.resolveActivity(itemView.context.packageManager) != null) {
                    itemView.context.startActivity(intent)
                } else {
                    // Si no hay app de teléfono, mostrar mensaje
                    android.widget.Toast.makeText(
                        itemView.context,
                        "No se puede realizar la llamada",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cliente, parent, false)
        return ClienteViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        holder.bind(listaClientes[position])

        // Aplicar animación de entrada
        setAnimation(holder.itemView, position)
    }

    override fun getItemCount(): Int = listaClientes.size

    /**
     * Actualiza la lista de clientes y notifica al RecyclerView
     * @param nuevaLista Nueva lista de clientes
     */
    fun actualizarLista(nuevaLista: List<Cliente>) {
        listaClientes = nuevaLista
        lastPosition = -1 // Resetear para animar todos los items
        notifyDataSetChanged()
    }

    /**
     * Aplica animación de deslizamiento a los items
     */
    private fun setAnimation(viewToAnimate: View, position: Int) {
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(viewToAnimate.context, android.R.anim.slide_in_left)
            animation.duration = 300
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }
}