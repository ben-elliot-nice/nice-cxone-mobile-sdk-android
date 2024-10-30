package com.nice.cxonechat.sample

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        Log.e("Debug", "Before trying to get shared preferences")
        ClearPreferencesTask(this).execute()
        Log.e("Debug", "Before trying to remove shared preferences")
//        sharedPreferences.edit().clear().apply()
        Log.e("Debug", "After trying to remove shared preferences")


        val invisibleButton: Button = findViewById(R.id.invisible_button)
        invisibleButton.setOnClickListener {
            Log.d("SIMPLE_ACTIVITY", "Invisible button clicked. Launching StoreActivity.")
            val intent = Intent(this@HomeActivity, StoreActivity::class.java)
            startActivity(intent)
        }
    }
}

class ClearPreferencesTask(val context: Context) : AsyncTask<Void, Void, Void>() {
    override fun doInBackground(vararg params: Void?): Void? {
        Log.e("Debug", "While trying to remove shared preferences")
        val sharedPreferences = context.getSharedPreferences("com.nice.cxonechat.storefront", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
        return null
    }
}