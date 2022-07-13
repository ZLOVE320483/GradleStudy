package com.zlove.gradle.study

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showToast()
    }

    private fun showToast() {
        Toast.makeText(this, "Test Toast", Toast.LENGTH_SHORT).show()
    }

}