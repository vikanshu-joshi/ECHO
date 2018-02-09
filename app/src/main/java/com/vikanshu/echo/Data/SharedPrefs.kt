package com.vikanshu.echo.Data

import android.content.Context


class SharedPrefs(context: Context){

    val context = context
    val pref = "Shared Preferences"
    val position = "position"
    val frag = "frag"
    val shuffle = "shuffle"
    val loop = "loop"
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

    fun getFragment(): Boolean{
        val f = preferences.getBoolean(frag,false)
        return f
    }

    fun setFragment(value: Boolean){
        val editor = preferences.edit()
        editor.putBoolean(frag,value)
        editor.apply()
    }

    fun setShuffleSettings(value: Boolean){
        val editor = preferences.edit()
        editor.putBoolean(shuffle,value)
        editor.apply()
    }

    fun getShuffleSettings(): Boolean{
        val s = preferences.getBoolean(shuffle,false)
        return s
    }

    fun setLoopSettings(value: Boolean){
        val editor = preferences.edit()
        editor.putBoolean(loop,value)
        editor.apply()
    }

    fun getLoopSettings(): Boolean{
        val s = preferences.getBoolean(loop,false)
        return s
    }

    fun settings(){
    }

    fun readSetting(){

    }
}