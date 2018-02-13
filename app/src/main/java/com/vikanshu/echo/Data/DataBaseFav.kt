package com.vikanshu.echo.Data

import android.content.ContentValues
import android.content.Context
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DataBaseFav: SQLiteOpenHelper{

        var list = ArrayList<SongsData>()

    object static{var TABLE_NAME = "Favourites"
        var COLUMN_TITLE = "Title"
        var COLUMN_ARTIST = "Artist"
        var COLUMN_PATH = "Path"
        var COLUMN_DURATION = "Duration"
        var COLUMN_ID = "ID"
        var DB_VERSION = 1
        var SIZE = "SIZE"
        var DB_NAME = "EchoDB"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE Favourites(Title String, Artist String, Path String, Duration Integer, ID Integer, SIZE Integer);")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    constructor(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) :
            super(context, name, factory, version)

    constructor(context: Context?) :
            super(context, static.DB_NAME, null, static.DB_VERSION)

    fun store(title: String,artist: String,path: String,duration: Long,id: Int,size: Long){
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(static.COLUMN_TITLE,title)
        contentValues.put(static.COLUMN_ARTIST,artist)
        contentValues.put(static.COLUMN_PATH,path)
        contentValues.put(static.COLUMN_DURATION,duration)
        contentValues.put(static.COLUMN_ID,id)
        contentValues.put(static.SIZE,size)
        db.insert(static.TABLE_NAME,null,contentValues)
        db.close()
    }

    fun queryDBList(): ArrayList<SongsData>?{
        val db = this.readableDatabase
        val query_param = "SELECT * FROM Favourites"
        val csor = db.rawQuery(query_param,null)
        if (csor.moveToFirst()){
            do {
                val title = csor.getString(csor.getColumnIndexOrThrow(static.COLUMN_TITLE))
                val artist = csor.getString(csor.getColumnIndexOrThrow(static.COLUMN_ARTIST))
                val path = csor.getString(csor.getColumnIndexOrThrow(static.COLUMN_PATH))
                val duration = csor.getLong(csor.getColumnIndexOrThrow(static.COLUMN_DURATION))
                val id = csor.getInt(csor.getColumnIndexOrThrow(static.COLUMN_ID))
                val size = csor.getLong(csor.getColumnIndexOrThrow(static.SIZE))
                list.add(SongsData(title,artist,path,"unknown",id.toLong(),duration,size))
            }while (csor.moveToNext())
        }else{
            return null
        }
        return list
    }

    fun checkIfExists(id: Int): Boolean {
        var storeId = 2
        val db = this.readableDatabase
        val query_params = "SELECT * FROM Favourites WHERE ID = '$id'"
        val csor = db.rawQuery(query_params,null)
        if (csor.moveToFirst()){
            do {
                storeId = csor.getInt(csor.getColumnIndexOrThrow(static.COLUMN_ID))
            }while (csor.moveToNext())
        }else{
            return false
        }
        return storeId != 2
    }

    fun deleteFav(id: Int){
        val db = this.writableDatabase
        db.delete(static.TABLE_NAME,static.COLUMN_ID+" = "+id,null)
        db.close()
    }
}