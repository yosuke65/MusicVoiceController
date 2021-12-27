package com.example.aimusicplayer

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.MotionEvent
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import java.util.*
import java.io.File

import java.util.ArrayList

import android.media.MediaPlayer
import android.view.View

import android.widget.Button

import android.widget.ImageView

import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout


class SmartPlayerActivity : AppCompatActivity() {

    private lateinit var parentConstraintLayout: ConstraintLayout
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechRecognizerIntent: Intent
    private var keeper = ""


    private lateinit var pausePlayBtn: ImageView
    private lateinit var nextBtn:ImageView
    private lateinit var previousBtn:ImageView
    private lateinit var songNameTxt: TextView

    private lateinit var artwork: ImageView
    private lateinit var lowerRelativeLayout: RelativeLayout
    private lateinit var voiceEnabledBtn: Button
    private var mode = "ON"

    private var myMediaPlayer: MediaPlayer? = null
    private var position = 0
    private var mySongs: ArrayList<File>? = null
    private var mSongName: String? = null

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smart_player)

        checkVoiceCommandPermission()

        pausePlayBtn = findViewById(R.id.playPauseBtn)
        previousBtn = findViewById(R.id.previousBtn)
        nextBtn = findViewById(R.id.nextBtn)
        artwork = findViewById(R.id.artwork)
        lowerRelativeLayout = findViewById(R.id.lower)
        voiceEnabledBtn = findViewById(R.id.voiceEnabledBtn)
        songNameTxt = findViewById(R.id.songTitle)
        parentConstraintLayout = findViewById(R.id.parentConstraintlayout);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        validateReceiveValuesAndStartPlaying()
        artwork.setBackgroundResource(R.drawable.logo)

        speechRecognizer.setRecognitionListener(object : RecognitionListener{
            override fun onReadyForSpeech(params: Bundle?) {
            }

            override fun onBeginningOfSpeech() {
            }

            override fun onRmsChanged(rmsdB: Float) {
            }


            override fun onBufferReceived(buffer: ByteArray?) {
            }

            override fun onEndOfSpeech() {
            }

            override fun onError(error: Int) {
            }

            override fun onResults(results: Bundle?) {
                val matchesFound = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if(!matchesFound.isNullOrEmpty()){
                    if(mode == "ON"){
                        keeper = matchesFound[0]
                        if(keeper.equals("pause the song", true) || keeper.equals("pause song", true)){
                            playPauseSong()
                            Toast.makeText(this@SmartPlayerActivity, keeper, Toast.LENGTH_LONG).show()
                        } else if(keeper.equals("play the song", true) || keeper.equals("play song", true)) {
                            playPauseSong()
                            Toast.makeText(this@SmartPlayerActivity, keeper, Toast.LENGTH_LONG).show()
                        } else if(keeper.equals("play the next song", true) || keeper.equals("play next song", true)) {
                            playNextSong()
                            Toast.makeText(this@SmartPlayerActivity, keeper, Toast.LENGTH_LONG).show()
                        } else if(keeper.equals("play the previous song", true) || keeper.equals("play precious song", true)) {
                             playPreviousSong()
                            Toast.makeText(this@SmartPlayerActivity, keeper, Toast.LENGTH_LONG).show()
                        }
                        Toast.makeText(this@SmartPlayerActivity, "Result $keeper", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
            }

        })

        parentConstraintLayout.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    speechRecognizer.startListening(speechRecognizerIntent)
                    keeper = ""
                }
                MotionEvent.ACTION_UP -> speechRecognizer.stopListening()
            }
            false
        }

        voiceEnabledBtn.setOnClickListener{
            if(myMediaPlayer?.isPlaying == true) {
                pausePlayBtn.setImageResource(R.drawable.ic_pause)
            }else {
                pausePlayBtn.setImageResource(R.drawable.ic_pause)
            }

            if(mode == "ON"){
                mode = "OFF"
                voiceEnabledBtn.text = "VOICE ENABLED MODE - $mode"
                lowerRelativeLayout.visibility = View.VISIBLE
            }else {
                mode = "ON"
                voiceEnabledBtn.text = "VOICE ENABLED MODE - $mode"
                lowerRelativeLayout.visibility = View.INVISIBLE
            }
        }

        pausePlayBtn.setOnClickListener{
            playPauseSong()
        }
        previousBtn.setOnClickListener {
            if(myMediaPlayer?.currentPosition?:0 > 0) {
                playPreviousSong()
            }
        }
        nextBtn.setOnClickListener {
            if(myMediaPlayer?.currentPosition?:0 > 0) {
                playNextSong()
            }
        }
    }

    private fun validateReceiveValuesAndStartPlaying() {

        if(myMediaPlayer != null) {
            myMediaPlayer?.stop()
            myMediaPlayer?.release()
        }

        val bundle = intent.extras
        mySongs = bundle?.getSerializable("song") as ArrayList<File>
        mSongName = mySongs?.getOrNull(position)?.name
        val songName = intent.getStringExtra("name")

        songNameTxt.text = songName
        songNameTxt.isSelected = true
        position = bundle.getInt("position", 0)
        val uri = Uri.parse(mySongs?.get(position).toString())
        myMediaPlayer = MediaPlayer.create(this, uri)
        myMediaPlayer?.start()

    }

    fun checkVoiceCommandPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
                startActivity(intent)
                finish()
            }
        }
    }

    private fun playPauseSong(){
        artwork.setBackgroundResource(R.drawable.four)
        if(myMediaPlayer?.isPlaying == true) {
            pausePlayBtn.setImageResource(R.drawable.ic_pause)
            myMediaPlayer?.pause()
        } else {
            pausePlayBtn.setImageResource(R.drawable.ic_play)
            myMediaPlayer?.start()
            artwork.setBackgroundResource(R.drawable.five)
        }
    }

    private fun playNextSong() {
        myMediaPlayer?.pause()
        myMediaPlayer?.stop()
        myMediaPlayer?.release()

        if(!mySongs.isNullOrEmpty()){
            val size:Int = mySongs?.size?:0
            position = ((position+1)%size)
            val uri = Uri.parse(mySongs?.get(position).toString())
            myMediaPlayer = MediaPlayer.create(this, uri)
            mSongName = mySongs?.get(position).toString()
            songNameTxt.text = mSongName
            myMediaPlayer?.start()
            artwork.setImageResource(R.drawable.three)

            if(myMediaPlayer?.isPlaying == true) {
                pausePlayBtn.setImageResource(R.drawable.ic_play)
            } else {
                pausePlayBtn.setImageResource(R.drawable.ic_pause)
            }
        }
    }

    private fun playPreviousSong() {
        myMediaPlayer?.pause()
        myMediaPlayer?.stop()
        myMediaPlayer?.release()

        if(!mySongs.isNullOrEmpty()){
            val size:Int = mySongs?.size?:0
            position = if(position -1 < 0) mySongs?.size?.minus(1)?:0
            else (position -1)
            val uri = Uri.parse(mySongs?.get(position).toString())
            myMediaPlayer = MediaPlayer.create(this, uri)
            mSongName = mySongs?.get(position).toString()
            songNameTxt.text = mSongName
            myMediaPlayer?.start()
            artwork.setImageResource(R.drawable.two)

            if(myMediaPlayer?.isPlaying == true) {
                pausePlayBtn.setImageResource(R.drawable.ic_play)
            } else {
                pausePlayBtn.setImageResource(R.drawable.ic_pause)
            }
        }
    }

}