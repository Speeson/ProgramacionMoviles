package com.dam.aplicacioncrm.activities

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.dam.aplicacioncrm.R

/**
 * Activity de bienvenida con animación que se muestra al iniciar la app
 */
class SplashActivity : AppCompatActivity() {

    private lateinit var ivLogo: ImageView
    private lateinit var tvAppName: TextView
    private lateinit var tvSubtitle: TextView

    companion object {
        private const val SPLASH_DURATION = 2500L // 2.5 segundos
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Inicializar vistas
        ivLogo = findViewById(R.id.ivSplashLogo)
        tvAppName = findViewById(R.id.tvAppName)
        tvSubtitle = findViewById(R.id.tvSubtitle)

        // Aplicar animaciones
        animarSplash()

        // Navegar a MainActivity después del delay
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            // Transición suave entre activities
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, SPLASH_DURATION)
    }

    /**
     * Anima los elementos del splash screen
     */
    private fun animarSplash() {
        // Animación del logo: escala desde 0 a tamaño normal
        ivLogo.scaleX = 0f
        ivLogo.scaleY = 0f
        ivLogo.alpha = 0f

        ObjectAnimator.ofFloat(ivLogo, View.SCALE_X, 0f, 1f).apply {
            duration = 800
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }

        ObjectAnimator.ofFloat(ivLogo, View.SCALE_Y, 0f, 1f).apply {
            duration = 800
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }

        ObjectAnimator.ofFloat(ivLogo, View.ALPHA, 0f, 1f).apply {
            duration = 800
            start()
        }

        // Animación del texto: fade in desde abajo
        tvAppName.translationY = 50f
        tvAppName.alpha = 0f

        ObjectAnimator.ofFloat(tvAppName, View.TRANSLATION_Y, 50f, 0f).apply {
            duration = 800
            startDelay = 400
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }

        ObjectAnimator.ofFloat(tvAppName, View.ALPHA, 0f, 1f).apply {
            duration = 800
            startDelay = 400
            start()
        }

        // Animación del subtítulo
        tvSubtitle.translationY = 30f
        tvSubtitle.alpha = 0f

        ObjectAnimator.ofFloat(tvSubtitle, View.TRANSLATION_Y, 30f, 0f).apply {
            duration = 800
            startDelay = 600
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }

        ObjectAnimator.ofFloat(tvSubtitle, View.ALPHA, 0f, 1f).apply {
            duration = 800
            startDelay = 600
            start()
        }
    }
}