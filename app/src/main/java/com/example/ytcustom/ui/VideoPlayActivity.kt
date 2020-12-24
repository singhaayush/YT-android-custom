package com.example.ytcustom.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ytcustom.R
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.loadOrCueVideo
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


class VideoPlayActivity : AppCompatActivity() {
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

        val customPlayerUi =
            youTubePlayerView.inflateCustomPlayerUi(R.layout.custom_player_ui)
        youTubePlayerView.enableAutomaticInitialization = false
        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {

                val customPlayerUiController = CustomPlayerUiController(
                    this@VideoPlayActivity,
                    customPlayerUi,
                    youTubePlayer,
                    youTubePlayerView
                )
                youTubePlayer.addListener(customPlayerUiController)
                youTubePlayerView.addFullScreenListener(customPlayerUiController)
                youTubePlayer.loadOrCueVideo(lifecycle, "V1Pl8CzNzCw", 0f)
            }
        })
    }
}