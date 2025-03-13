package com.nice.cxonechat.sample

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class TransactionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.transactions_home)


        val transactionsButton: Button = findViewById(R.id.case_button)
        transactionsButton.setOnClickListener {
            Log.d("SIMPLE_ACTIVITY", "Invisible button clicked. Launching TransactionsActivity.")
            val intent = Intent(this@TransactionsActivity, CaseActivity::class.java)
            startActivity(intent)
        }
    }
}