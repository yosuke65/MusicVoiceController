package com.example.aimusicplayer

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import com.karumi.dexter.PermissionToken

import com.karumi.dexter.listener.PermissionDeniedResponse

import com.karumi.dexter.listener.PermissionGrantedResponse

import com.karumi.dexter.listener.single.PermissionListener

import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.PermissionRequest
import java.io.File


class MainActivity : AppCompatActivity() {
    private lateinit var mSongList: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnReadSongs:Button = findViewById(R.id.readSong)
        btnReadSongs.setOnClickListener{
            appExternalStoragePermission()

        }
        mSongList = findViewById(R.id.songList)
    }

    fun appExternalStoragePermission() {
        Dexter.withContext(this)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    displayAudioSongsName()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {}

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?,
                ) {}
            }).check()
    }

    fun readOnlyAudioSongs(file: File):ArrayList<File>{
        val arrayList = ArrayList<File>()
        val allFiles = file.listFiles()
        if(!allFiles.isNullOrEmpty()){
            allFiles.forEach { file ->
                if(file.isDirectory && !file.isHidden){
                    val dir = readOnlyAudioSongs(file)
                    if(!dir.isNullOrEmpty()){
                        arrayList.addAll(dir)
                    }

                } else {
                    if(file.name.endsWith(".mp3") || file.name.endsWith(".aac") || file.name.endsWith(".wav") || file.name.endsWith(".wma")) {
                        arrayList.add(file)
                    }
                }
            }
        }
        return arrayList
    }

    fun displayAudioSongsName() {

        val items = arrayListOf<String>()
        val audioSongs:ArrayList<File>? = readOnlyAudioSongs(Environment.getExternalStorageDirectory())
        if(!audioSongs.isNullOrEmpty()) {
            for(songCounter in 0 until  audioSongs.size) {
                items.add(audioSongs[songCounter].name)
            }
        }
        val arrayAdapter  = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items)
        mSongList.adapter = arrayAdapter

        mSongList.setOnItemClickListener { adapterView, view, position, l ->
            val songName = mSongList.getItemAtPosition(position).toString()
            val intent = Intent(this, SmartPlayerActivity::class.java)
            intent.putExtra("song", audioSongs)
            intent.putExtra("name", songName)
            intent.putExtra("position", position)
            startActivity(intent)
        }
    }
}