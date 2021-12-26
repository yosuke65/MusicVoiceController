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





class SmartPlayerActivity : AppCompatActivity() {

    private lateinit var parentRelativeLayout: RelativeLayout
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

    private val myMediaPlayer: MediaPlayer? = null
    private val position = 0
    private val mySongs: ArrayList<File>? = null
    private val mSongName: String? = null

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
        parentRelativeLayout = findViewById(R.id.parentRelativelayout);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());


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
                    keeper = matchesFound[0]
                    Toast.makeText(this@SmartPlayerActivity, "Result $keeper", Toast.LENGTH_LONG).show()
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
            }

        })

        parentRelativeLayout.setOnTouchListener { view, motionEvent ->
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
            if(mode == "ON"){
                mode = "OFF"
                voiceEnabledBtn.text = "VOICE ENABLED MODE - $mode"
                lowerRelativeLayout.visibility = View.VISIBLE
            }else {
                mode = "ON"
                voiceEnabledBtn.text = "VOICE ENABLED MODE - $mode"
                lowerRelativeLayout.visibility = View.GONE
            }
        }
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
}