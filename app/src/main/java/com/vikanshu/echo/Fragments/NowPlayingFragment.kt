package com.vikanshu.echo.Fragments


import android.content.Context
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vikanshu.echo.Data.SongsData
import com.vikanshu.echo.R
import com.vikanshu.echo.Activities.MainActivity.statified.mediaPlayer
import com.vikanshu.echo.Data.SharedPrefs
import kotlinx.android.synthetic.main.fragment_now_playing.*


class NowPlayingFragment : Fragment() {

    lateinit var songs: ArrayList<SongsData>
    lateinit var preferences: SharedPrefs

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                     savedInstanceState: Bundle?): View? {
        val itemView = inflater.inflate(R.layout.fragment_now_playing, container, false)
        songs = getSongsFromPhone()
        preferences = SharedPrefs(context as Context)
        return itemView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (songs.isNotEmpty()){
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
        }
    }
    fun playSong(){
        mediaPlayer.pause()
        mediaPlayer.reset()
        val pos = preferences.getSongInfo()
        mediaPlayer.setDataSource(songs[pos].path)
        mediaPlayer.prepare()
        mediaPlayer.start()
        updateViews()
        playPauseNow.setImageResource(R.drawable.pause_icon)
        return
    }
    fun updateViews(){
        titleNow.text = songs[preferences.getSongInfo()].title
        artistNow.text = songs[preferences.getSongInfo()].artist
        if (mediaPlayer.isPlaying){
            playPauseNow.setImageResource(R.drawable.pause_icon)
        }
        return
    }
    fun playPause(){
        if (mediaPlayer.isPlaying){
            mediaPlayer.pause()
            playPauseNow.setImageResource(R.drawable.play_icon)
        }else{
            mediaPlayer.start()
            playPauseNow.setImageResource(R.drawable.pause_icon)
        }
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
