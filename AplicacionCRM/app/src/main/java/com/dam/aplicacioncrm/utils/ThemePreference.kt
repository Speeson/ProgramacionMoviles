package com.dam.aplicacioncrm.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

/**
 * Gestor de preferencias del tema de la aplicación
 */
object ThemePreference {

    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_THEME = "theme_mode"
    private const val KEY_HAND_MODE = "hand_mode"

    // Modos de tema
    const val MODE_LIGHT = 0
    const val MODE_DARK = 1
    const val MODE_SYSTEM = 2

    // Modos de mano
    const val HAND_RIGHT = 0  // Diestro (botón a la derecha)
    const val HAND_LEFT = 1   // Zurdo (botón a la izquierda)

    /**
     * Obtiene las SharedPreferences
     */
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Guarda el modo de tema seleccionado
     */
    fun saveThemeMode(context: Context, mode: Int) {
        getPreferences(context).edit().putInt(KEY_THEME, mode).apply()
        applyTheme(mode)
    }

    /**
     * Obtiene el modo de tema guardado (por defecto: seguir sistema)
     */
    fun getThemeMode(context: Context): Int {
        return getPreferences(context).getInt(KEY_THEME, MODE_SYSTEM)
    }

    /**
     * Aplica el tema según el modo seleccionado
     */
    fun applyTheme(mode: Int) {
        when (mode) {
            MODE_LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            MODE_DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            MODE_SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    /**
     * Verifica si el tema actual es oscuro
     */
    fun isDarkMode(context: Context): Boolean {
        val mode = getThemeMode(context)
        return when (mode) {
            MODE_DARK -> true
            MODE_LIGHT -> false
            else -> {
                // Si es MODE_SYSTEM, verificar configuración del sistema
                val nightMode = context.resources.configuration.uiMode and
                        android.content.res.Configuration.UI_MODE_NIGHT_MASK
                nightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES
            }
        }
    }

    /**
     * Guarda el modo de mano seleccionado
     */
    fun saveHandMode(context: Context, mode: Int) {
        getPreferences(context).edit().putInt(KEY_HAND_MODE, mode).apply()
    }

    /**
     * Obtiene el modo de mano guardado (por defecto: zurdo - izquierda)
     */
    fun getHandMode(context: Context): Int {
        return getPreferences(context).getInt(KEY_HAND_MODE, HAND_LEFT)
    }

    /**
     * Verifica si el modo es para zurdo
     */
    fun isLeftHanded(context: Context): Boolean {
        return getHandMode(context) == HAND_LEFT
    }
}