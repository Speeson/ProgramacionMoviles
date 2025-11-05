package com.dam.aplicacioncrm.models

/**
 * Clase que representa un cliente en el sistema CRM
 * @property id Identificador único del cliente en la base de datos
 * @property nombre Nombre completo del cliente
 * @property email Correo electrónico del cliente
 * @property telefono Número de teléfono del cliente
 */
data class Cliente(
    val id: Int = 0,
    val nombre: String,
    val email: String,
    val telefono: String
) {
    /**
     * Valida que todos los campos obligatorios estén completos
     * @return true si todos los campos son válidos
     */
    fun esValido(): Boolean {
        return nombre.isNotBlank() &&
                email.isNotBlank() &&
                telefono.isNotBlank()
    }

    /**
     * Valida el formato del email
     * @return true si el email tiene un formato válido
     */
    fun emailEsValido(): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
        return email.matches(emailPattern.toRegex())
    }

    /**
     * Valida que el teléfono tenga al menos 9 dígitos
     * @return true si el teléfono es válido
     */
    fun telefonoEsValido(): Boolean {
        val soloDigitos = telefono.replace(Regex("[^0-9]"), "")
        return soloDigitos.length >= 9
    }
}