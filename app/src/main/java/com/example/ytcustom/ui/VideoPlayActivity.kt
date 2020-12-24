package com.example.ytcustom.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.ytcustom.R
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.loadOrCueVideo
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.utils.FadeViewHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.log


class VideoPlayActivity : AppCompatActivity() {
    private val TAG = "VideoPlayActivity"
    lateinit var customPlayerUi: View
    lateinit var ytPlayer: YouTubePlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val youTubePlayerView =
            findViewById<YouTubePlayerView>(R.id.youtube_player_view)
//        val iFramePlayerOptions =
//            IFramePlayerOptions.Builder()
//                .controls(0)
//                .rel(1)
//                .ivLoadPolicy(3)
//                .ccLoadPolicy(1)
//                .build()
        lifecycle.addObserver(youTubePlayerView)
        customPlayerUi =
            youtube_player_view.inflateCustomPlayerUi(R.layout.custom_player_ui)
        youtube_player_view.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                ytPlayer = youTubePlayer
                val customPlayerUiController = CustomPlayerUiController(
                    this@VideoPlayActivity,
                    customPlayerUi,
                    youTubePlayer,
                    youtube_player_view
                )
                youTubePlayer.addListener(customPlayerUiController)
                youtube_player_view.addFullScreenListener(customPlayerUiController)
                youTubePlayer.loadOrCueVideo(lifecycle, "mtYBBInIIE8", 0f)
            }
        })
    }

    override fun onRestart() {
        super.onRestart()
        if (ytPlayer != null)
            ytPlayer.play()
    }


    override fun onBackPressed() {

        if (youtube_player_view.isFullScreen())
            youtube_player_view.exitFullScreen()
        else
            super.onBackPressed()
    }
}