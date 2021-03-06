package com.vikanshu.echo.Fragments


import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.vikanshu.echo.Activities.MainActivity.statified.mediaPlayer
import com.vikanshu.echo.Fragments.SettingsFragment.staticated.mSensorManager
import com.vikanshu.echo.Fragments.SettingsFragment.staticated.mSensorListener
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.TextView
import com.vikanshu.echo.Activities.MainActivity
import com.vikanshu.echo.Data.SharedPrefs
import com.vikanshu.echo.Data.SongsData
import com.vikanshu.echo.R
import kotlinx.android.synthetic.main.fragment_settings.*
import java.util.*

class SettingsFragment : Fragment() {

    lateinit var preferences: SharedPrefs
    lateinit var shakeCheck: CheckBox
    lateinit var excludeCheck: CheckBox
    lateinit var excludeSeek: SeekBar
    lateinit var excludeText: TextView
    lateinit var checkThread: CheckBox

    lateinit var songs: ArrayList<SongsData>
    object staticated{
        lateinit var mSensorManager: SensorManager
        lateinit var mSensorListener: SensorEventListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar!!.show()
        (activity as AppCompatActivity).supportActionBar!!.title = resources.getText(R.string.settings)
        val itemView = inflater.inflate(R.layout.fragment_settings, container, false)
        shakeCheck = itemView.findViewById(R.id.shakeDevice)
        excludeCheck = itemView.findViewById(R.id.exclude)
        excludeSeek = itemView.findViewById(R.id.seekBarExclude)
        excludeText = itemView.findViewById(R.id.excludeText)
        checkThread = itemView.findViewById(R.id.threadCheck)
        preferences = SharedPrefs(context!!)
        songs = getSongsFromPhone()
        if (preferences.readSetting())
            shakeCheck.isChecked = true
        if (preferences.getExcludeSettings())
            excludeCheck.isChecked = true
        if (preferences.getThread())
            checkThread.isChecked = true
        excludeSeek.progress = preferences.getExcludeTime()
        excludeText.text = "Exclude files less than " + excludeSeek.progress.toString() + " sec"
        return itemView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        shakeCheck.setOnCheckedChangeListener { buttonView, isChecked ->
                preferences.settings(isChecked)
        }
        exclude.setOnCheckedChangeListener { buttonView, isChecked ->
            preferences.setExcludeSettings(isChecked)
        }
        checkThread.setOnCheckedChangeListener { buttonView, isChecked ->
            preferences.setThread(isChecked)
        }
        excludeSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                preferences.setExcludeTime(progress)
                excludeText.text = "Exclude files less than " + progress.toString() + " sec"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAcceleration = 0.0F
        mAccelerationCurrent = SensorManager.GRAVITY_EARTH
        mAccelerationLast = SensorManager.GRAVITY_EARTH
        shake()
    }
    override fun onPause() {
        mSensorManager.unregisterListener(mSensorListener)
        super.onPause()
    }
    override fun onResume() {
        mSensorManager.registerListener(mSensorListener,mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL)
        super.onResume()
    }
    var mAcceleration: Float = 0F
    var mAccelerationCurrent: Float = 0F
    var mAccelerationLast: Float = 0F
    fun shake(){
        mSensorListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            }
            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                mAccelerationLast = mAccelerationCurrent
                mAccelerationCurrent = Math.sqrt(((x*x+y*y+z*z).toDouble())).toFloat()
                val delta = mAccelerationCurrent - mAccelerationLast
                mAcceleration = mAcceleration*0.9F + delta

                if (mAcceleration > 20.0 && preferences.readSetting()){
                    next()
                }
            }
        }
    }
    fun next(){
        if (preferences.getShuffleSettings() && !preferences.getLoopSettings()){
            val rand = Random()
            val pos = rand.nextInt(songs.size - 0) + 0
            preferences.setSongInfo(pos)
            playSong()
        }else if(preferences.getLoopSettings()){
            if (mediaPlayer!!.isPlaying)
                mediaPlayer!!.seekTo(0)
            else{
                playSong()
            }
        }else{
            playNext()
        }
    }
    fun playNext(){
        if (preferences.getSongInfo() == (songs.size - 1)){
            preferences.setSongInfo(0)
            playSong()
        }else{
            val pos = (preferences.getSongInfo()+1)
            preferences.setSongInfo(pos)
            playSong()
        }
        return
    }
    fun playSong(){
        mediaPlayer!!.pause()
        mediaPlayer!!.reset()
        val pos = preferences.getSongInfo()
        MainActivity.statified.mediaPlayer!!.setDataSource(songs[pos].path)
        MainActivity.statified.mediaPlayer!!.prepare()
        MainActivity.statified.mediaPlayer!!.start()
        return
    }
    fun getSongsFromPhone(): ArrayList<SongsData>{
        val arrayList = arrayListOf<SongsData>()
        val contentResolver = context?.contentResolver
        val songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val songCursor = contentResolver?.query(songUri, null, null, null, null)
        if (songCursor != null && songCursor.moveToFirst()){
            val songID = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songDuration = songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
            val songTittle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songPath = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val songAlbum = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
            val songSize = songCursor.getColumnIndex(MediaStore.Audio.Media.SIZE)
            while(songCursor.moveToNext()){
                val id = songCursor.getLong(songID)
                val duration =  songCursor.getLong(songDuration)
                val tittle =  songCursor.getString(songTittle)
                val artist =  songCursor.getString(songArtist)
                val path =  songCursor.getString(songPath)
                val album =  songCursor.getString(songAlbum)
                val size = songCursor.getLong(songSize)
                if (preferences.getExcludeSettings()) {
                    val time = (preferences.getExcludeTime() * 1000)
                    if (duration > time)
                        arrayList.add(SongsData(tittle, artist, path, album, id, duration,size))
                }
                else {
                    arrayList.add(SongsData(tittle, artist, path, album, id, duration,size))
                }
            }
        }
        songCursor?.close()
        return arrayList
    }
}
