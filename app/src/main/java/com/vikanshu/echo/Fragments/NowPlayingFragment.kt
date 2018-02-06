package com.vikanshu.echo.Fragments


import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.vikanshu.echo.Data.SongsData
import com.vikanshu.echo.R
import com.vikanshu.echo.Activities.MainActivity.statified.mediaPlayer
import com.vikanshu.echo.Data.SharedPrefs
import kotlinx.android.synthetic.main.fragment_now_playing.*
import java.util.concurrent.TimeUnit
import com.cleveroad.audiovisualization.DbmHandler
import android.support.v7.app.AppCompatActivity
import com.vikanshu.echo.Activities.MainActivity
import kotlinx.android.synthetic.main.fragment_all_songs.*
import kotlinx.android.synthetic.main.fragment_favourites.*


class NowPlayingFragment : Fragment() {

    lateinit var songs: ArrayList<SongsData>
    lateinit var preferences: SharedPrefs
    lateinit var visualizer: GLAudioVisualizationView
    lateinit var audioVisualizationView: AudioVisualization
    lateinit var seekBarNow: SeekBar
    var here = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                     savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar!!.hide()
        val itemView = inflater.inflate(R.layout.fragment_now_playing, container, false)
        visualizer = itemView.findViewById(R.id.visualizer_view)
        seekBarNow = itemView.findViewById(R.id.seekBar)
        songs = getSongsFromPhone()
        preferences = SharedPrefs(context as Context)
        here = arguments!!.getString("here","All Songs")
        return itemView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (songs.isNotEmpty()){
            seekBar.max = songs[preferences.getSongInfo()].duration.toInt()
            updateViews()
            playPauseNow.setOnClickListener {
                playPause()
            }
            playNextNow.setOnClickListener {
                playNext()
            }
            playPrevNow.setOnClickListener {
                playPrev()
            }
            mediaPlayer.setOnCompletionListener(object : MediaPlayer.OnCompletionListener{
                override fun onCompletion(mp: MediaPlayer?) {
                    playNext()
                }
            })
//            seekBarNow.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
//                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                    val min = TimeUnit.MILLISECONDS.toSeconds(seekBar?.progress?.toLong() as Long)
//                    val sec = TimeUnit.MILLISECONDS.toSeconds(seekBar.progress.toLong())
//                    startTime.text = String.format("%d:%d",(min/60),(sec%60))
//                }
//
//                override fun onStartTrackingTouch(seekBar: SeekBar?) {
//                }
//
//                override fun onStopTrackingTouch(seekBar: SeekBar?) {
//                    mediaPlayer.seekTo(seekBar?.progress as Int)
////                    seekProgress(1)
//                }
//            })
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
        audioVisualizationView.onPause()
        super.onPause()
    }
    override fun onResume() {
        audioVisualizationView.onResume()
        super.onResume()
    }
    fun seekProgress(i: Int){
        seekBarNow.progress = mediaPlayer.currentPosition
        if (mediaPlayer.isPlaying && i==1){
            Handler().postDelayed({
                seekProgress(1)
            },500)
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
        updateViews()
        playPauseNow.setImageResource(R.drawable.pause)
        return
    }
    fun updateViews(){
        val min = TimeUnit.MILLISECONDS.toSeconds(songs[preferences.getSongInfo()].duration)
        val sec = TimeUnit.MILLISECONDS.toSeconds(songs[preferences.getSongInfo()].duration)
        endTime.text = String.format("%d:%d",(min/60),(sec%60))
        startTime.text = "0:0"
        titleNow.text = songs[preferences.getSongInfo()].title
        artistNow.text = songs[preferences.getSongInfo()].artist
        if (mediaPlayer.isPlaying){
            playPauseNow.setImageResource(R.drawable.pause)
        }
        /*seekProgress(1)*/
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
//        seekProgress(1)
        return
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
