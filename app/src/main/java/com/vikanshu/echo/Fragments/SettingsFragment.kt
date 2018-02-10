package com.vikanshu.echo.Fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import com.vikanshu.echo.Data.SharedPrefs
import com.vikanshu.echo.R
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment() {

    lateinit var preferences: SharedPrefs
    lateinit var shakeCheck: CheckBox

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar!!.show()
        val itemView = inflater.inflate(R.layout.fragment_settings, container, false)
        shakeCheck = itemView.findViewById(R.id.shakeDevice)
        preferences = SharedPrefs(context!!)
        if (preferences.readSetting())
            shakeCheck.isChecked = true
        return itemView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        shakeCheck.setOnCheckedChangeListener { buttonView, isChecked ->
                preferences.settings(isChecked)
        }
    }
}
