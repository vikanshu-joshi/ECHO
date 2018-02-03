package com.vikanshu.echo.Fragments


import android.os.Bundle
import android.provider.MediaStore
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import com.vikanshu.echo.Adapters.SongsListAdapter
import com.vikanshu.echo.Data.SongsData

import com.vikanshu.echo.R
import kotlinx.android.synthetic.main.bottom_bar.*
import kotlinx.android.synthetic.main.fragment_all_songs.*

class AllSongsFragment : Fragment() {

    lateinit var songs: ArrayList<SongsData>
    lateinit var invisibleLayout: ConstraintLayout
    lateinit var visibleLayout: ConstraintLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_all_songs, container, false)
        songs = getSongsFromPhone()
        invisibleLayout = view.findViewById(R.id.invisible)
        visibleLayout = view.findViewById(R.id.visible)
        if (songs.isEmpty()) {
            invisibleLayout.visibility = View.VISIBLE
            visibleLayout.visibility = View.INVISIBLE
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adapter = SongsListAdapter(context,songs)
        songsList.adapter = adapter
        songsList.setOnItemClickListener { parent, view, position, id ->
            songNameBottomBar.text = songs[position].title
            artistNameBottomBar.text = songs[position].artist
        }
    }

    fun getSongsFromPhone(): ArrayList<SongsData>{
        var arrayList = arrayListOf<SongsData>()
        var contentResolver = context?.contentResolver
        var songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
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
