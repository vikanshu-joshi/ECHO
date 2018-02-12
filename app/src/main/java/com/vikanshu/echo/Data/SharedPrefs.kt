package com.vikanshu.echo.Data

import android.content.Context


class SharedPrefs(context: Context){

    val context = context
    val pref = "Shared Preferences"
    val position = "position"
    val frag = "frag"
    val shuffle = "shuffle"
    val loop = "loop"
    val shaky = "shaky"
    val exclude = "exclude"
    val time = "time"
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
    fun settings(value: Boolean){
        val editor = preferences.edit()
        editor.putBoolean(shaky,value)
        editor.apply()
    }

    fun readSetting(): Boolean{
        val state = preferences.getBoolean(shaky,false)
        return state
    }

    fun getExcludeSettings(): Boolean{
        val state = preferences.getBoolean(exclude,false)
        return state
    }

    fun setExcludeSettings(value: Boolean){
        val editor = preferences.edit()
        editor.putBoolean(exclude,value)
        editor.apply()
    }

    fun setExcludeTime(value: Int){
        val editor = preferences.edit()
        editor.putInt(time,value)
        editor.apply()
    }

    fun getExcludeTime(): Int{
        val time = preferences.getInt(time,10)
        return time
    }
}