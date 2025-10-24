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