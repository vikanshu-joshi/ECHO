package com.vikanshu.echo.Activities

import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.vikanshu.echo.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        all_songs.setBackgroundColor(resources.getColor(R.color.gray))
        favourites.setBackgroundColor(resources.getColor(R.color.white))
        settings.setBackgroundColor(resources.getColor(R.color.white))
        about.setBackgroundColor(resources.getColor(R.color.white))
    }

    fun all_songs(v: View){
        all_songs.setBackgroundColor(resources.getColor(R.color.gray))
        favourites.setBackgroundColor(resources.getColor(R.color.white))
        settings.setBackgroundColor(resources.getColor(R.color.white))
        about.setBackgroundColor(resources.getColor(R.color.white))
        drawer_layout.closeDrawers()
    }
    fun favourites(v: View){
        all_songs.setBackgroundColor(resources.getColor(R.color.white))
        favourites.setBackgroundColor(resources.getColor(R.color.gray))
        settings.setBackgroundColor(resources.getColor(R.color.white))
        about.setBackgroundColor(resources.getColor(R.color.white))
        drawer_layout.closeDrawers()

    }
    fun settings(v: View){
        all_songs.setBackgroundColor(resources.getColor(R.color.white))
        favourites.setBackgroundColor(resources.getColor(R.color.white))
        settings.setBackgroundColor(resources.getColor(R.color.gray))
        about.setBackgroundColor(resources.getColor(R.color.white))
        drawer_layout.closeDrawers()

    }
    fun about(v: View){
        all_songs.setBackgroundColor(resources.getColor(R.color.white))
        favourites.setBackgroundColor(resources.getColor(R.color.white))
        settings.setBackgroundColor(resources.getColor(R.color.white))
        about.setBackgroundColor(resources.getColor(R.color.gray))
        drawer_layout.closeDrawers()

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
