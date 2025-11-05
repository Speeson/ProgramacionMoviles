package com.dam.aplicacioncrm.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.dam.aplicacioncrm.models.Cliente

/**
 * Clase helper para gestionar la base de datos SQLite
 * Implementa las operaciones CRUD para la entidad Cliente
 */
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        // Nombre y versión de la base de datos
        private const val DATABASE_NAME = "clientes.db"
        private const val DATABASE_VERSION = 1

        // Nombre de la tabla y columnas
        private const val TABLE_CLIENTES = "clientes"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NOMBRE = "nombre"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_TELEFONO = "telefono"
    }

    /**
     * Se ejecuta cuando se crea la base de datos por primera vez
     */
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = """
            CREATE TABLE $TABLE_CLIENTES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOMBRE TEXT NOT NULL,
                $COLUMN_EMAIL TEXT NOT NULL,
                $COLUMN_TELEFONO TEXT NOT NULL
            )
        """.trimIndent()

        db?.execSQL(createTable)

        // Insertar datos de ejemplo
        insertarDatosEjemplo(db)
    }

    /**
     * Inserta datos de ejemplo para demostración
     */
    private fun insertarDatosEjemplo(db: SQLiteDatabase?) {
        val clientesEjemplo = listOf(
            Triple("Juan Pérez García", "juan.perez@empresa.com", "612345678"),
            Triple("María López Martínez", "maria.lopez@gmail.com", "623456789"),
            Triple("Carlos Rodríguez Sánchez", "carlos.rodriguez@hotmail.com", "634567890"),
            Triple("Ana Fernández Ruiz", "ana.fernandez@outlook.com", "645678901"),
            Triple("David González Torres", "david.gonzalez@yahoo.com", "656789012"),
            Triple("Laura Martín Jiménez", "laura.martin@empresa.com", "667890123"),
            Triple("Pedro Sánchez Moreno", "pedro.sanchez@gmail.com", "678901234"),
            Triple("Carmen Díaz Álvarez", "carmen.diaz@hotmail.com", "689012345"),
            Triple("Miguel Romero Castro", "miguel.romero@outlook.com", "690123456"),
            Triple("Isabel Navarro Gil", "isabel.navarro@empresa.com", "601234567"),
            Triple("Roberto Jiménez Morales", "roberto.jimenez@empresa.com", "611223344"),
            Triple("Sofía Herrera Vega", "sofia.herrera@gmail.com", "622334455"),
            Triple("Francisco Ortiz Ramírez", "francisco.ortiz@hotmail.com", "633445566"),
            Triple("Elena Vargas Medina", "elena.vargas@outlook.com", "644556677"),
            Triple("Antonio Castillo Ramos", "antonio.castillo@yahoo.com", "655667788"),
            Triple("Lucía Iglesias Molina", "lucia.iglesias@empresa.com", "666778899"),
            Triple("Javier Domínguez Cruz", "javier.dominguez@gmail.com", "677889900"),
            Triple("Marta Rubio Delgado", "marta.rubio@hotmail.com", "688990011"),
            Triple("Sergio Pascual Blanco", "sergio.pascual@outlook.com", "699001122"),
            Triple("Cristina Vidal Serrano", "cristina.vidal@empresa.com", "600112233")
        )

        clientesEjemplo.forEach { (nombre, email, telefono) ->
            val values = ContentValues().apply {
                put(COLUMN_NOMBRE, nombre)
                put(COLUMN_EMAIL, email)
                put(COLUMN_TELEFONO, telefono)
            }
            db?.insert(TABLE_CLIENTES, null, values)
        }
    }

    /**
     * Se ejecuta cuando se actualiza la versión de la base de datos
     */
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_CLIENTES")
        onCreate(db)
    }

    /**
     * Inserta un nuevo cliente en la base de datos
     * @param cliente El cliente a insertar
     * @return El ID del cliente insertado, o -1 si hay error
     */
    fun insertarCliente(cliente: Cliente): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOMBRE, cliente.nombre)
            put(COLUMN_EMAIL, cliente.email)
            put(COLUMN_TELEFONO, cliente.telefono)
        }

        val id = db.insert(TABLE_CLIENTES, null, values)
        db.close()
        return id
    }

    /**
     * Obtiene todos los clientes de la base de datos
     * @return Lista con todos los clientes
     */
    fun obtenerTodosLosClientes(): List<Cliente> {
        val listaClientes = mutableListOf<Cliente>()
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_CLIENTES ORDER BY $COLUMN_NOMBRE", null)

        if (cursor.moveToFirst()) {
            do {
                val cliente = Cliente(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)),
                    email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    telefono = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TELEFONO))
                )
                listaClientes.add(cliente)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return listaClientes
    }

    /**
     * Actualiza los datos de un cliente existente
     * @param cliente El cliente con los datos actualizados
     * @return Número de filas afectadas
     */
    fun actualizarCliente(cliente: Cliente): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOMBRE, cliente.nombre)
            put(COLUMN_EMAIL, cliente.email)
            put(COLUMN_TELEFONO, cliente.telefono)
        }

        val filasActualizadas = db.update(
            TABLE_CLIENTES,
            values,
            "$COLUMN_ID = ?",
            arrayOf(cliente.id.toString())
        )

        db.close()
        return filasActualizadas
    }

    /**
     * Elimina un cliente de la base de datos
     * @param id El ID del cliente a eliminar
     * @return Número de filas eliminadas
     */
    fun eliminarCliente(id: Int): Int {
        val db = this.writableDatabase
        val filasEliminadas = db.delete(
            TABLE_CLIENTES,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )

        db.close()
        return filasEliminadas
    }

    /**
     * Busca clientes por nombre o email
     * @param query Texto de búsqueda
     * @return Lista de clientes que coinciden con la búsqueda
     */
    fun buscarClientes(query: String): List<Cliente> {
        val listaClientes = mutableListOf<Cliente>()
        val db = this.readableDatabase

        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM $TABLE_CLIENTES WHERE $COLUMN_NOMBRE LIKE ? OR $COLUMN_EMAIL LIKE ? ORDER BY $COLUMN_NOMBRE",
            arrayOf("%$query%", "%$query%")
        )

        if (cursor.moveToFirst()) {
            do {
                val cliente = Cliente(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)),
                    email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    telefono = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TELEFONO))
                )
                listaClientes.add(cliente)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return listaClientes
    }

    /**
     * Cuenta el total de clientes en la base de datos
     * @return Número total de clientes
     */
    fun contarClientes(): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_CLIENTES", null)
        var count = 0

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }

        cursor.close()
        db.close()
        return count
    }
}