package com.vikanshu.echo

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.widget.Toast
import com.vikanshu.echo.Activities.MainActivity
import com.vikanshu.echo.Activities.MainActivity.statified.notificationManager
import com.vikanshu.echo.Fragments.AllSongsFragment.staticated.ppBottomAll
import com.vikanshu.echo.Fragments.FavouritesFragment.staticated.ppBottomFav
import com.vikanshu.echo.Fragments.NowPlayingFragment.staticated.ppNow

class MyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == Intent.ACTION_NEW_OUTGOING_CALL && MainActivity.statified.mediaPlayer.isPlaying) {
            notificationManager?.cancel(1978)
            MainActivity.statified.mediaPlayer.pause()
            ppNow?.setImageResource(R.drawable.play)
            ppBottomFav?.setImageResource(R.drawable.play_icon)
            ppBottomAll?.setImageResource(R.drawable.play_icon)
        }
        else{
            val tm: TelephonyManager = context.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
            when (tm.callState){
                TelephonyManager.CALL_STATE_RINGING -> {
                    if (MainActivity.statified.mediaPlayer.isPlaying){
                        notificationManager?.cancel(1978)
                        MainActivity.statified.mediaPlayer.pause()
                        ppNow?.setImageResource(R.drawable.play)
                        ppBottomFav?.setImageResource(R.drawable.play_icon)
                        ppBottomAll?.setImageResource(R.drawable.play_icon)
                    }
                }
            }
        }
        if (intent.action == Intent.ACTION_HEADSET_PLUG){
            if (MainActivity.statified.mediaPlayer.isPlaying){
                notificationManager?.cancel(1978)
                MainActivity.statified.mediaPlayer.pause()
                ppNow?.setImageResource(R.drawable.play)
                ppBottomFav?.setImageResource(R.drawable.play_icon)
                ppBottomAll?.setImageResource(R.drawable.play_icon)
            }
        }

    }
}
