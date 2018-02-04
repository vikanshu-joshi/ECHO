package com.vikanshu.echo.Data

import android.content.Context


class SharedPrefs(context: Context){

    val context = context
    val pref = "Shared Preferences"
    val position = "position"
    val preferences = context.getSharedPreferences(pref,Context.MODE_PRIVATE)

    fun getSongInfo(): Int {
        val pos = preferences.getInt("position",0)
        return pos
    }

    fun setSongInfo(pos: Int){
        val editor = preferences.edit()
        editor.putInt(position,pos)
        editor.apply()
    }

    fun settings(){

    }

    fun readSetting(){

    }
}