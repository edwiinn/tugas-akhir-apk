package com.edwiinn.digitalsignature

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("activity", "hello")
        documents_btn.setOnClickListener {
            redirectToDocumentsActivity()
        }
    }

    private fun redirectToDocumentsActivity(){
        val documentActivityIntent = Intent(this@MainActivity, DocumentsActivity::class.java)
        startActivity(documentActivityIntent)
    }
}