package com.vikanshu.echo.Fragments


import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.vikanshu.echo.Data.SongsData
import com.vikanshu.echo.R
import com.vikanshu.echo.Activities.MainActivity.statified.mediaPlayer
import com.vikanshu.echo.Fragments.NowPlayingFragment.staticated.mSensorManager
import com.vikanshu.echo.Fragments.NowPlayingFragment.staticated.mSensorListener
import com.vikanshu.echo.Data.SharedPrefs
import kotlinx.android.synthetic.main.fragment_now_playing.*
import java.util.concurrent.TimeUnit
import com.cleveroad.audiovisualization.DbmHandler
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast
import com.vikanshu.echo.Activities.MainActivity
import com.vikanshu.echo.Data.DataBaseFav
import java.util.*


class NowPlayingFragment : Fragment() {

    object staticated{
        lateinit var mSensorManager: SensorManager
        lateinit var mSensorListener: SensorEventListener
    }
    lateinit var seekBarNow: SeekBar
    lateinit var startTimeText: TextView
    lateinit var songs: ArrayList<SongsData>
    lateinit var preferences: SharedPrefs
    lateinit var visualizer: GLAudioVisualizationView
    lateinit var audioVisualizationView: AudioVisualization
    lateinit var favContent: DataBaseFav
    var here = ""
    var updateSeekBar = object: Runnable{
        override fun run() {
            val progress = mediaPlayer.currentPosition
            val min = TimeUnit.MILLISECONDS.toSeconds(progress.toLong())
            val sec = TimeUnit.MILLISECONDS.toSeconds(progress.toLong())
            startTimeText.text = String.format("%d:%d",(min/60),(sec%60))
            seekBarNow.progress = progress
            Handler().postDelayed(this,500)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                     savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar!!.hide()
        val itemView = inflater.inflate(R.layout.fragment_now_playing, container, false)
        startTimeText = itemView.findViewById(R.id.startTime)
        visualizer = itemView.findViewById(R.id.visualizer_view)
        seekBarNow = itemView.findViewById(R.id.seekBar)
        songs = getSongsFromPhone()
        preferences = SharedPrefs(context as Context)
        here = arguments!!.getString("here","All Songs")
        return itemView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAcceleration = 0.0F
        mAccelerationCurrent = SensorManager.GRAVITY_EARTH
        mAccelerationLast = SensorManager.GRAVITY_EARTH
        shake()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        favContent = DataBaseFav(context)
        if (songs.isNotEmpty()){
            seekBar.max = songs[preferences.getSongInfo()].duration.toInt()
            updateViews()
            playPauseNow.setOnClickListener {
                playPause()
            }
            playNextNow.setOnClickListener {
                next()
            }
            playPrevNow.setOnClickListener {
                playPrev()
            }
            mediaPlayer.setOnCompletionListener(object : MediaPlayer.OnCompletionListener{
                override fun onCompletion(mp: MediaPlayer?) {
                    next()
                }
            })
            val vizualizerHandler = DbmHandler.Factory.newVisualizerHandler(context as Context, 0)
            audioVisualizationView.linkTo(vizualizerHandler)
            nowPlaying.setOnClickListener {
                if (here == "All Songs"){
                    (context as MainActivity).supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.frag_holder_main,AllSongsFragment())
                            .commit()
                }else if (here == "Fav"){
                    (context as MainActivity).supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.frag_holder_main,FavouritesFragment())
                            .commit()
                }
            }
            dropDownBtn.setOnClickListener {
                if (here == "All Songs"){
                    (context as MainActivity).supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.frag_holder_main,AllSongsFragment())
                            .commit()
                }else if (here == "Fav"){
                    (context as MainActivity).supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.frag_holder_main,FavouritesFragment())
                            .commit()
                }
            }
            shuffle.setOnClickListener {
                if (preferences.getShuffleSettings()){
                    Toast.makeText(context,"Shuffle Off",Toast.LENGTH_SHORT).show()
                    preferences.setShuffleSettings(false)
                    shuffle.setImageResource(R.drawable.shuffle)
                }else{
                    Toast.makeText(context,"Shuffle On",Toast.LENGTH_SHORT).show()
                    preferences.setShuffleSettings(true)
                    shuffle.setImageResource(R.drawable.shuffle_on)
                }
            }
            loop.setOnClickListener {
                if (preferences.getLoopSettings()){
                    Toast.makeText(context,"Loop Off",Toast.LENGTH_SHORT).show()
                    preferences.setLoopSettings(false)
                    loop.setImageResource(R.drawable.loop)
                }else{
                    Toast.makeText(context,"Loop On",Toast.LENGTH_SHORT).show()
                    preferences.setLoopSettings(true)
                    loop.setImageResource(R.drawable.loop_on)
                }
            }
            favBtn.setOnClickListener {
                if (favContent.checkIfExists(songs[preferences.getSongInfo()].id.toInt())) {
                    favBtn.setImageResource(R.drawable.favorite_off)
                    favContent.deleteFav(songs[preferences.getSongInfo()].id.toInt())
                    Toast.makeText(context,"Removed from Favourites",Toast.LENGTH_SHORT).show()
                }
                else {
                    favBtn.setImageResource(R.drawable.favorite_on)
                    favContent.store(songs[preferences.getSongInfo()].title,songs[preferences.getSongInfo()].artist,
                            songs[preferences.getSongInfo()].path,songs[preferences.getSongInfo()].duration,
                            songs[preferences.getSongInfo()].id.toInt())
                    Toast.makeText(context,"Added to Favourites",Toast.LENGTH_SHORT).show()
                }
            }
            seekBarNow.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    mediaPlayer.seekTo(seekBar?.progress!!)
                }

            })
        }
    }

    var mAcceleration: Float = 0F
    var mAccelerationCurrent: Float = 0F
    var mAccelerationLast: Float = 0F
    fun shake(){
        mSensorListener = object : SensorEventListener{
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        audioVisualizationView = visualizer
    }
    override fun onDestroyView() {
        audioVisualizationView.release()
        super.onDestroyView()
    }
    override fun onPause() {
        mSensorManager.unregisterListener(mSensorListener)
        audioVisualizationView.onPause()
        super.onPause()
    }
    override fun onResume() {
        audioVisualizationView.onResume()
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL)
        super.onResume()
    }
    fun playSong(){
        mediaPlayer.pause()
        mediaPlayer.reset()
        val pos = preferences.getSongInfo()
        mediaPlayer.setDataSource(songs[pos].path)
        mediaPlayer.prepare()
        mediaPlayer.start()
        updateViews()
        playPauseNow.setImageResource(R.drawable.pause)
        return
    }
    fun updateViews(){

        seekBarNow.progress = 0

        if (favContent.checkIfExists(songs[preferences.getSongInfo()].id.toInt()))
            favBtn.setImageResource(R.drawable.favorite_on)
        else
            favBtn.setImageResource(R.drawable.favorite_off)

        val min = TimeUnit.MILLISECONDS.toSeconds(songs[preferences.getSongInfo()].duration)
        val sec = TimeUnit.MILLISECONDS.toSeconds(songs[preferences.getSongInfo()].duration)
        endTime.text = String.format("%d:%d",(min/60),(sec%60))
        startTimeText.text = "0:0"

        Handler().postDelayed(updateSeekBar,500)

        titleNow.text = songs[preferences.getSongInfo()].title

        if (songs[preferences.getSongInfo()].artist == "<unknown>")
            artistNow.text = "unknown artist"
        else
            artistNow.text = songs[preferences.getSongInfo()].artist

        if (mediaPlayer.isPlaying)
            playPauseNow.setImageResource(R.drawable.pause)

        if (preferences.getShuffleSettings())
            shuffle.setImageResource(R.drawable.shuffle_on)

        if (preferences.getLoopSettings())
            loop.setImageResource(R.drawable.loop_on)

        return
    }
    fun playPause(){
        if (mediaPlayer.isPlaying){
            mediaPlayer.pause()
            playPauseNow.setImageResource(R.drawable.play)
        }else{
            mediaPlayer.start()
            playPauseNow.setImageResource(R.drawable.pause)
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
        return
    }
    fun playPrev(){
        if (preferences.getSongInfo() == 0){
            preferences.setSongInfo((songs.size - 1))
            playSong()
        }else{
            val pos = (preferences.getSongInfo()-1)
            preferences.setSongInfo(pos)
            playSong()
        }
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
                arrayList.add(SongsData(tittle,artist,path,album,id,duration))
            }
        }
        songCursor?.close()
        return arrayList
    }
}
