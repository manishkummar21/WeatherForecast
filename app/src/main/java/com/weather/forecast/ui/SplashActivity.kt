package com.weather.forecast.ui

import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.weather.forecast.R
import com.weather.forecast.ui.dashboard.activities.DashBoardActivity

class SplashActivity : AppCompatActivity(R.layout.activity_splash) {


    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, DashBoardActivity::class.java))
            finish()
        },3000)
    }
}