package com.vikanshu.echo.Activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import com.vikanshu.echo.R

class SplashActivity : AppCompatActivity() {

    var permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.PROCESS_OUTGOING_CALLS)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        if (!hasAllPermissions(this,*permissions)){
            ActivityCompat.requestPermissions(this,permissions,1024)
        }else{
            move()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode){
            1024 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED
                        && grantResults[4] == PackageManager.PERMISSION_GRANTED){
                    move()
                }else{
                    Toast.makeText(this,"Please Grant all the Permissions to Continue",Toast.LENGTH_LONG).show()
                    this.finish()
                }
            }else -> {
                Toast.makeText(this,"Something wen't Wrong",Toast.LENGTH_LONG).show()
            this.finish()
            }
        }
    }

    private fun hasAllPermissions(ctx: Context,vararg permissions: String): Boolean {
        for (i in permissions){
            val res = ctx.checkCallingOrSelfPermission(i)
            if (res == PackageManager.PERMISSION_DENIED)
                return false
        }
        return true
    }
    fun move(){
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            this.finish()
        },1000)
    }
}