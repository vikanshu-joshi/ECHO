package com.vikanshu.echo.Fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.vikanshu.echo.R
/**
 * A simple [Fragment] subclass.
 *
 */
class SettingsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar!!.show()
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }
}
