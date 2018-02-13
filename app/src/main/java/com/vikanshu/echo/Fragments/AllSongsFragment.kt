package com.vikanshu.echo.Fragments


import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vikanshu.echo.Activities.MainActivity
import com.vikanshu.echo.Adapters.SongsListAdapter
import com.vikanshu.echo.Data.SharedPrefs
import com.vikanshu.echo.Data.SongsData
import com.vikanshu.echo.Activities.MainActivity.statified.mediaPlayer
import com.vikanshu.echo.Fragments.AllSongsFragment.staticated.mSensorManager
import com.vikanshu.echo.Fragments.AllSongsFragment.staticated.mSensorListener
import com.vikanshu.echo.R
import kotlinx.android.synthetic.main.bottom_bar.*
import kotlinx.android.synthetic.main.fragment_all_songs.*
import java.util.*
import android.widget.TextView
import java.util.concurrent.TimeUnit


class AllSongsFragment : Fragment() {

    lateinit var songs: ArrayList<SongsData>
    lateinit var invisibleLayout: ConstraintLayout
    lateinit var visibleLayout: ConstraintLayout
    lateinit var preferences: SharedPrefs

    object staticated{
        lateinit var mSensorManager: SensorManager
        lateinit var mSensorListener: SensorEventListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar!!.show()
        (activity as AppCompatActivity).supportActionBar!!.title = resources.getText(R.string.app_name)
        val view = inflater.inflate(R.layout.fragment_all_songs, container, false)
        preferences = SharedPrefs(context as Context)
        songs = getSongsFromPhone()
        invisibleLayout = view.findViewById(R.id.invisible)
        visibleLayout = view.findViewById(R.id.visible)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val arg = arguments?.getString("where")
        if (songs.isEmpty()) {
            invisibleLayout.visibility = View.VISIBLE
            visibleLayout.visibility = View.INVISIBLE
            visibleLayout.isClickable = false
        }
        else{
            if (arg.equals("Main")){
                mediaPlayer.setDataSource(songs[preferences.getSongInfo()].path)
                mediaPlayer.prepare()
            }
            updateViews()
            playPauseBottomBar.setOnClickListener {
                playPause()
                updateViews()
            }
            playNextBottomBar.setOnClickListener {
                next()
                updateViews()
            }
        }
        val adapter = SongsListAdapter(context,songs)
        songsList.adapter = adapter
        songsList.setOnItemClickListener { parent, view, position, id ->
            preferences.setSongInfo(position)
            playSong()
            updateViews()
        }
        songsList.setOnItemLongClickListener { parent, view, position, id ->
            val dialog = AlertDialog.Builder(context!!)
            val dialogView = layoutInflater.inflate(R.layout.alert_box,null)
            val alertTitle = dialogView.findViewById<TextView>(R.id.titleAlert)
            val alertArtist = dialogView.findViewById<TextView>(R.id.artistAlert)
            val alertAlbum = dialogView.findViewById<TextView>(R.id.albumAlert)
            val alertDuration = dialogView.findViewById<TextView>(R.id.durationAlert)
            val alertPath = dialogView.findViewById<TextView>(R.id.pathAlert)

            val min = TimeUnit.MILLISECONDS.toSeconds(songs[position].duration)
            val sec = TimeUnit.MILLISECONDS.toSeconds(songs[position].duration)
            val time = String.format("%d:%d",(min/60),(sec%60))

            alertTitle.text = "title :  " + songs[position].title
            alertPath.text = "location :  " + songs[position].path
            alertDuration.text = "duration :  " + time
            if (songs[position].artist == "<unknown>"){
                alertArtist.text = "artist :  unknown"
            }else{
                alertArtist.text = "artist :  " + songs[position].artist
            }
            if (songs[position].album == "<unknown>"){
                alertAlbum.text = "album :  unknown"
            }else{
                alertAlbum.text = "album :  " + songs[position].album
            }
            dialog.setView(dialogView)
            dialog.show()
            true
        }
        mediaPlayer.setOnCompletionListener(object : MediaPlayer.OnCompletionListener{
            override fun onCompletion(mp: MediaPlayer?) {
                next()
            }
        })
        includeBottomBar.setOnClickListener {
            val nowPlaying = NowPlayingFragment()
            val bundle = Bundle()
            preferences.setFragment(true)
            bundle.putString("here","All Songs")
            nowPlaying.arguments = bundle
            (context as MainActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frag_holder_main,nowPlaying)
                    .commit()
        }
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
    fun updateViews(){
        songNameBottomBar.text = songs[preferences.getSongInfo()].title
        if (songs[preferences.getSongInfo()].artist == "<unknown>")
            artistNameBottomBar.text = "unknown artist"
        else
            artistNameBottomBar.text = songs[preferences.getSongInfo()].artist
        if (mediaPlayer.isPlaying){
            playPauseBottomBar.setImageResource(R.drawable.pause_icon)
        }
        return
    }
    fun playSong(){
        mediaPlayer.pause()
        mediaPlayer.reset()
        val pos = preferences.getSongInfo()
        mediaPlayer.setDataSource(songs[pos].path)
        mediaPlayer.prepare()
        mediaPlayer.start()
        playPauseBottomBar.setImageResource(R.drawable.pause_icon)
        return
    }
    fun playPause(){
        if (mediaPlayer.isPlaying){
            mediaPlayer.pause()
            playPauseBottomBar.setImageResource(R.drawable.play_icon)
        }else{
            mediaPlayer.start()
            playPauseBottomBar.setImageResource(R.drawable.pause_icon)
        }
        return
    }
    fun next(){
        if (preferences.getShuffleSettings() && !preferences.getLoopSettings()){
            val rand = Random()
            val pos = rand.nextInt(songs.size - 0) + 0
            preferences.setSongInfo(pos)
            playSong()
        }else if(preferences.getLoopSettings()){
            if (mediaPlayer.isPlaying)
                mediaPlayer.seekTo(0)
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
        updateViews()
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
            while(songCursor.moveToNext()){
                val id = songCursor.getLong(songID)
                val duration =  songCursor.getLong(songDuration)
                val tittle =  songCursor.getString(songTittle)
                val artist =  songCursor.getString(songArtist)
                val path =  songCursor.getString(songPath)
                val album =  songCursor.getString(songAlbum)
                if (preferences.getExcludeSettings()) {
                    val time = (preferences.getExcludeTime() * 1000)
                    if (duration > time)
                        arrayList.add(SongsData(tittle, artist, path, album, id, duration))
                }
                else {
                    arrayList.add(SongsData(tittle, artist, path, album, id, duration))
                }
            }
        }
        songCursor?.close()
        return arrayList
    }
}