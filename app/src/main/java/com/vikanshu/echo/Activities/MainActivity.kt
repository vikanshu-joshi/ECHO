package com.vikanshu.echo.Activities

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.SeekBar
import com.vikanshu.echo.Activities.MainActivity.statified.mediaPlayer
import com.vikanshu.echo.Data.SharedPrefs
import com.vikanshu.echo.Fragments.*
import com.vikanshu.echo.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_all_songs.*
import kotlinx.android.synthetic.main.fragment_favourites.*

class MainActivity : AppCompatActivity(){

    object statified{
        lateinit var mediaPlayer: MediaPlayer
    }
    lateinit var preferences: SharedPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        preferences = SharedPrefs(this)
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        mediaPlayer = MediaPlayer()
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
        preferences.setFragment(true)
        all_songs.setBackgroundColor(resources.getColor(R.color.white))
        favourites.setBackgroundColor(resources.getColor(R.color.white))
        settings.setBackgroundColor(resources.getColor(R.color.white))
        about.setBackgroundColor(resources.getColor(R.color.gray))
        this.supportFragmentManager
                .beginTransaction()
                .replace(R.id.frag_holder_main, AboutFragment())
                .commit()
        drawer_layout.closeDrawers()
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
}
