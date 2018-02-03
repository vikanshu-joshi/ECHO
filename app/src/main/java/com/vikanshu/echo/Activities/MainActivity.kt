package com.vikanshu.echo.Activities

import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.widget.BaseAdapter
import com.vikanshu.echo.Adapters.NavViewAdapter
import com.vikanshu.echo.Data.NavViewData
import com.vikanshu.echo.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(){

    lateinit var list: ArrayList<NavViewData>
    lateinit var listAdapter: BaseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        list.add(NavViewData("Allsongs",R.drawable.navigation_allsongs))
        list.add(NavViewData("Favourites",R.drawable.navigation_favorites))
        list.add(NavViewData("Settings",R.drawable.navigation_settings))
        list.add(NavViewData("About Us",R.drawable.navigation_aboutus))
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        listAdapter = NavViewAdapter(this,list)
        nav_list_view.adapter = listAdapter
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
