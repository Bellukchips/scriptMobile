package com.nothing.mynote.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nothing.mynote.R

class AboutActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_about)
        title = getString(R.string.about)
    }
}