package com.example.navigationcomponent


import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class AboutUs : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)
    }
    fun rateMe (View: View) = try {
            startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + "vc.ntrc.ccyrus.vincywifi")))
        } catch (e: ActivityNotFoundException) {
            startActivity( Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=$packageName")))
        }
}