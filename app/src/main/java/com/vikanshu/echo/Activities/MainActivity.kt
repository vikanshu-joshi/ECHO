/*
    ECHO
    Copyright (C) 2018  VIKANSHU
    
    This file is part of ECHO
    
    ECHO is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    ECHO is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. 
    
    contacting owner :- vikanshu2016@gmail.com
*/
package com.vikanshu.echo.Activities

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.vikanshu.echo.Activities.MainActivity.statified.mediaPlayer
import com.vikanshu.echo.Activities.MainActivity.statified.notificationManager
import com.vikanshu.echo.Activities.MainActivity.statified.notificationTrack
import com.vikanshu.echo.Data.SharedPrefs
import com.vikanshu.echo.Fragments.*
import com.vikanshu.echo.MyReceiver
import com.vikanshu.echo.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(){

    object statified{
        var mediaPlayer = MediaPlayer()
        var notificationManager: NotificationManager ?= null
        var notificationTrack: Notification ?= null
    }
    lateinit var preferences: SharedPrefs
    lateinit var reciever: MyReceiver
    lateinit var filter: IntentFilter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        reciever = MyReceiver()
        filter = IntentFilter()
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL)
        filter.addAction(Intent.ACTION_HEADSET_PLUG)
        registerReceiver(reciever,filter)
        preferences = SharedPrefs(this)
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        val allsongs = AllSongsFragment()
        val bundle = Bundle()
        bundle.putString("where","Main")
        allsongs.arguments = bundle
        preferences.setFragment(false)
        this.supportFragmentManager
                .beginTransaction()
                .add(R.id.frag_holder_main,allsongs)
                .commit()
        all_songs.setBackgroundColor(resources.getColor(R.color.gray))
        favourites.setBackgroundColor(resources.getColor(R.color.white))
        settings.setBackgroundColor(resources.getColor(R.color.white))
        about.setBackgroundColor(resources.getColor(R.color.white))
        val intent = Intent(this,MainActivity::class.java)
        val pIntent = PendingIntent.getActivity(this,1024,intent,0)
        notificationTrack = Notification.Builder(this).setSmallIcon(R.mipmap.ic_launcher_round).setContentTitle("Playing Music in Background")
                .setContentIntent(pIntent).setOngoing(true).setAutoCancel(true).build()

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    fun all_songs(v: View){
        preferences.setFragment(false)
        all_songs.setBackgroundColor(resources.getColor(R.color.gray))
        favourites.setBackgroundColor(resources.getColor(R.color.white))
        settings.setBackgroundColor(resources.getColor(R.color.white))
        about.setBackgroundColor(resources.getColor(R.color.white))
        val all = AllSongsFragment()
        val bund = Bundle()
        bund.putString("where","Nav")
        all.arguments = bund
        this.supportFragmentManager
                .beginTransaction()
                .replace(R.id.frag_holder_main,all)
                .commit()
        drawer_layout.closeDrawers()
    }
    fun favourites(v: View){
        preferences.setFragment(true)
        all_songs.setBackgroundColor(resources.getColor(R.color.white))
        favourites.setBackgroundColor(resources.getColor(R.color.gray))
        settings.setBackgroundColor(resources.getColor(R.color.white))
        about.setBackgroundColor(resources.getColor(R.color.white))
        this.supportFragmentManager
                .beginTransaction()
                .replace(R.id.frag_holder_main,FavouritesFragment())
                .commit()
        drawer_layout.closeDrawers()
    }
    fun settings(v: View){
        preferences.setFragment(true)
        all_songs.setBackgroundColor(resources.getColor(R.color.white))
        favourites.setBackgroundColor(resources.getColor(R.color.white))
        settings.setBackgroundColor(resources.getColor(R.color.gray))
        about.setBackgroundColor(resources.getColor(R.color.white))
        this.supportFragmentManager
                .beginTransaction()
                .replace(R.id.frag_holder_main,SettingsFragment())
                .commit()
        drawer_layout.closeDrawers()
    }
    fun about(v: View){
//        preferences.setFragment(true)
//        all_songs.setBackgroundColor(resources.getColor(R.color.white))
//        favourites.setBackgroundColor(resources.getColor(R.color.white))
//        settings.setBackgroundColor(resources.getColor(R.color.white))
//        about.setBackgroundColor(resources.getColor(R.color.gray))
//        this.supportFragmentManager
//                .beginTransaction()
//                .replace(R.id.frag_holder_main, AboutFragment())
//                .commit()
//        drawer_layout.closeDrawers()
    }
    override fun onDestroy() {
        unregisterReceiver(reciever)
        preferences.setThread(false)
        super.onDestroy()
    }
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        }else if (preferences.getFragment()){
            preferences.setFragment(false)
            this.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frag_holder_main,AllSongsFragment())
                    .commit()
            all_songs.setBackgroundColor(resources.getColor(R.color.gray))
            favourites.setBackgroundColor(resources.getColor(R.color.white))
            settings.setBackgroundColor(resources.getColor(R.color.white))
            about.setBackgroundColor(resources.getColor(R.color.white))
        }else {
            super.onBackPressed()
        }
    }

    override fun onStop() {
        super.onStop()
        if (mediaPlayer.isPlaying) {
            notificationManager?.notify(1978, notificationTrack)
        }
    }

    override fun onResume() {
        super.onResume()
        notificationManager?.cancel(1978)
    }
}
