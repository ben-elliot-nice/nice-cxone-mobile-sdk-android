package com.nice.cxonechat.sample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val invisibleButton: Button = findViewById(R.id.invisible_button)
        invisibleButton.setOnClickListener {
            Log.d("SIMPLE_ACTIVITY", "Invisible button clicked. Launching StoreActivity.")
            val intent = Intent(this@HomeActivity, StoreActivity::class.java)
            startActivity(intent)
        }
    }
}