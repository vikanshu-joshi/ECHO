package com.vikanshu.echo.Fragments


import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vikanshu.echo.Activities.MainActivity.statified.mediaPlayer
import com.vikanshu.echo.Activities.MainActivity
import com.vikanshu.echo.Adapters.FavouritesAdapter
import com.vikanshu.echo.Data.DataBaseFav
import com.vikanshu.echo.Data.SharedPrefs
import com.vikanshu.echo.Data.SongsData
import com.vikanshu.echo.R
import kotlinx.android.synthetic.main.bottom_bar.*
import kotlinx.android.synthetic.main.fragment_favourites.*
import java.util.*


class FavouritesFragment : Fragment() {

    lateinit var songs: ArrayList<SongsData>
    var songsFav: ArrayList<SongsData> ?= null
    lateinit var preferences: SharedPrefs
    lateinit var favContent: DataBaseFav

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar!!.show()
        preferences = SharedPrefs(context as Context)
        return inflater.inflate(R.layout.fragment_favourites, container, false)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        favContent = DataBaseFav(context)
        songs = getSongsFromPhone()
        songsFav = favContent.queryDBList()
        if (songs.isEmpty()){
            invisibleFav.visibility = View.VISIBLE
            visibleFav.visibility = View.VISIBLE
            visibleFav.isClickable = false
        }else{
            if (songsFav != null) {
                favList.adapter = FavouritesAdapter(context, songsFav!!)
//                favList.setOnItemClickListener { parent, view, position, id ->
//                    mediaPlayer.pause()
//                    mediaPlayer.reset()
//                    mediaPlayer.setDataSource(songsFav!![position].path)
//                    mediaPlayer.prepare()
//                    mediaPlayer.start()
//                }
            }else{
                invisibleFav.visibility = View.VISIBLE
                invisibleFav.setOnClickListener{}
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
            bottomInclude.setOnClickListener {
                val nowPlaying = NowPlayingFragment()
                val bundle = Bundle()
                bundle.putString("here","Fav")
                nowPlaying.arguments = bundle
                (context as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frag_holder_main,nowPlaying)
                        .commit()
            }
            mediaPlayer.setOnCompletionListener(object : MediaPlayer.OnCompletionListener{
                override fun onCompletion(mp: MediaPlayer?) {
                    next()
                }
            })
        }
    }
    fun updateViews(){
        songNameBottomBar.text = songs[preferences.getSongInfo()].title
        if (songs[preferences.getSongInfo()].artist == "<unknown>")
            artistNameBottomBar.text = "unknown artist"
        else
            artistNameBottomBar.text = songs[preferences.getSongInfo()].artist
        if (MainActivity.statified.mediaPlayer.isPlaying){
            playPauseBottomBar.setImageResource(R.drawable.pause_icon)
        }
        return
    }
    fun playSong(){
        MainActivity.statified.mediaPlayer.pause()
        MainActivity.statified.mediaPlayer.reset()
        val pos = preferences.getSongInfo()
        MainActivity.statified.mediaPlayer.setDataSource(songs[pos].path)
        MainActivity.statified.mediaPlayer.prepare()
        MainActivity.statified.mediaPlayer.start()
        playPauseBottomBar.setImageResource(R.drawable.pause_icon)
        return
    }
    fun playPause(){
        if (MainActivity.statified.mediaPlayer.isPlaying){
            MainActivity.statified.mediaPlayer.pause()
            playPauseBottomBar.setImageResource(R.drawable.play_icon)
        }else{
            MainActivity.statified.mediaPlayer.start()
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
                arrayList.add(SongsData(tittle,artist,path,album,id,duration))
            }
        }
        songCursor?.close()
        return arrayList
    }
}
