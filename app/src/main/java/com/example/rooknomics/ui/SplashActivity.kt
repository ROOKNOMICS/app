package com.example.rooknomics.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.rooknomics.MainActivity
import com.example.rooknomics.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Initiate cinematic branding animation
        val splashContent = findViewById<android.view.View>(R.id.splash_content)
        val revealAnim = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.anim_fade_scale)
        splashContent.startAnimation(revealAnim)

        lifecycleScope.launch {
            delay(2800) // Adjusted delay slightly to allow 1.6s animation to breathe
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
    }
}
