package com.nothing.mynote.activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.nothing.mynote.R
import kotlinx.android.synthetic.main.settings_activity.*

class SettingsActivity : AppCompatActivity() {

    companion object{
        val PREF_NAME = "MODEDARK"
        private var sharedPreferences: SharedPreferences? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        sharedPreferences = getSharedPreferences(PREF_NAME, 0)
        val modeDark =sharedPreferences!!.getBoolean("darkMode",false)
        switch_mode.isChecked = modeDark
           if (modeDark){
               AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
           }else{
               AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
           }

        switch_mode!!.setOnCheckedChangeListener { buttonView, isChecked ->
        if (switch_mode.isChecked){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            restartApp()

        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            restartApp()
//            setTheme(R.style.LightMode)
        }
            val sharedPreferences= getSharedPreferences(PREF_NAME,0)
            val editor = sharedPreferences.edit()
            editor.putBoolean("darkMode",isChecked)
            editor.apply()

        }

    }

    fun restartApp(){
        val i = Intent(applicationContext,SettingsActivity::class.java)
        startActivity(i)
        finish()
    }
}