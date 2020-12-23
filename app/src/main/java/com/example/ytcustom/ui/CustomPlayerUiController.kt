package com.example.ytcustom.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.ytcustom.R
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerState
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.utils.FadeViewHelper
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.views.YouTubePlayerSeekBar
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.views.YouTubePlayerSeekBarListener


internal class CustomPlayerUiController(
    private val context: Context,
    private val playerUi: View,
    private val youTubePlayer: YouTubePlayer,
    private val youTubePlayerView: YouTubePlayerView
) : AbstractYouTubePlayerListener(), YouTubePlayerFullScreenListener {

    private lateinit var playPauseButton: ImageView

    // panel is used to intercept clicks on the WebView, I don't want the user to be able to click the WebView directly.
    private var panel: View? = null
    private var videoCurrentTimeTextView: TextView? = null
    private var videoDurationTextView: TextView? = null
    private val playerTracker: YouTubePlayerTracker = YouTubePlayerTracker()
    private var fullscreen = false
    private lateinit var fullScreenIcon: ImageView
    private lateinit var youTubePlayerSeekBar: YouTubePlayerSeekBar

    private fun initViews(playerUi: View) {
        panel = playerUi.findViewById(R.id.panel)
        val fadeViewHelper = FadeViewHelper(playerUi.findViewById(R.id.controls_container))
        fadeViewHelper.animationDuration = FadeViewHelper.DEFAULT_ANIMATION_DURATION
        fadeViewHelper.fadeOutDelay = FadeViewHelper.DEFAULT_FADE_OUT_DELAY
        fullScreenIcon = playerUi.findViewById(R.id.enter_exit_fullscreen_button)
        youTubePlayer.addListener(fadeViewHelper)
        youTubePlayerSeekBar =
            playerUi.findViewById<YouTubePlayerSeekBar>(R.id.youtube_player_seekbar)
        youTubePlayer.addListener(youTubePlayerSeekBar)
        youTubePlayerSeekBar.youtubePlayerSeekBarListener = object : YouTubePlayerSeekBarListener {
            override fun seekTo(time: Float) {
                youTubePlayer.seekTo(time)
            }
        }

        playPauseButton =
            playerUi.findViewById<ImageView>(R.id.play_pause_button)
        val enterExitFullscreenButton =
            playerUi.findViewById<ImageView>(R.id.enter_exit_fullscreen_button)

        playPauseButton.setOnClickListener { view: View? ->
            if (playerTracker.state == PlayerState.PLAYING) {
                playPauseButton.background = ContextCompat.getDrawable(
                    context,
                    R.drawable.exo_icon_play
                )
                youTubePlayer.pause()
            } else if (playerTracker.state == PlayerState.PAUSED) {
                youTubePlayer.play()
                playPauseButton.background = ContextCompat.getDrawable(
                    context,
                    R.drawable.exo_icon_pause
                )
            }
        }
        enterExitFullscreenButton.setOnClickListener { view: View? ->
            if (fullscreen) youTubePlayerView.exitFullScreen() else youTubePlayerView.enterFullScreen()
            fullscreen = !fullscreen
        }

        val forwardButton = playerUi.findViewById<ImageView>(R.id.exo_ffwd)
        forwardButton.setOnClickListener {
            if (playerTracker.state == PlayerState.PLAYING) {
                youTubePlayer.seekTo(playerTracker.currentSecond + 10f)
            }
        }

        val backwardButton = playerUi.findViewById<ImageView>(R.id.exo_rew);
        backwardButton.setOnClickListener {
            if (playerTracker.state == PlayerState.PLAYING) {
                youTubePlayer.seekTo(playerTracker.currentSecond - 10f)
            }
        }

        panel?.setOnClickListener { fadeViewHelper.toggleVisibility() }

        val backBtn = playerUi.findViewById<ImageView>(R.id.iv_back);
        backBtn.setOnClickListener {
            if (youTubePlayerView.isFullScreen()) youTubePlayerView.exitFullScreen()
            else (context as VideoPlayActivity).onBackPressed()
        }
    }

    override fun onReady(youTubePlayer: YouTubePlayer) {
        // progressbar!!.visibility = View.GONE

    }

    override fun onStateChange(
        youTubePlayer: YouTubePlayer,
        state: PlayerState
    ) {
        Log.d("TAG21", "onStateChange: $state")
        if (state == PlayerState.PLAYING || state == PlayerState.PAUSED || state == PlayerState.VIDEO_CUED) panel!!.setBackgroundColor(
            ContextCompat.getColor(context, android.R.color.transparent)
        ) else if (state == PlayerState.BUFFERING) panel!!.setBackgroundColor(
            ContextCompat.getColor(
                context,
                android.R.color.transparent
            )
        )
        when (state) {
            PlayerState.PLAYING -> playPauseButton.background = ContextCompat.getDrawable(
                context,
                R.drawable.exo_icon_pause
            )
            PlayerState.PAUSED -> playPauseButton.background = ContextCompat.getDrawable(
                context,
                R.drawable.exo_icon_play
            )
        }


    }

    @SuppressLint("SetTextI18n")
    override fun onCurrentSecond(
        youTubePlayer: YouTubePlayer,
        second: Float
    ) {
        if (playerTracker.videoDuration - second <= 1f) {
            (context as VideoPlayActivity).onBackPressed()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onVideoDuration(
        youTubePlayer: YouTubePlayer,
        duration: Float
    ) {
        // videoDurationTextView!!.text = duration.toString() + ""
    }

    override fun onYouTubePlayerEnterFullScreen() {
        fullScreenIcon.background =
            ContextCompat.getDrawable(context, R.drawable.ic_baseline_fullscreen_exit_24)
        playerUi.findViewById<TextView>(R.id.tv_fullscreen_state).text = "Exit Fullscreen"
        val viewParams = playerUi.layoutParams
        viewParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        viewParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        playerUi.layoutParams = viewParams
        var window = (context as VideoPlayActivity).window
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        (context).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

    }

    override fun onYouTubePlayerExitFullScreen() {
        fullScreenIcon.background =
            ContextCompat.getDrawable(context, R.drawable.ic_baseline_fullscreen_48)
        playerUi.findViewById<TextView>(R.id.tv_fullscreen_state).text = "Fullscreen"

        val viewParams = playerUi.layoutParams
        viewParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        viewParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        playerUi.layoutParams = viewParams

        val playerViewParam = youTubePlayerView.layoutParams
        playerViewParam.height = ViewGroup.LayoutParams.MATCH_PARENT
        playerViewParam.width = ViewGroup.LayoutParams.MATCH_PARENT
        youTubePlayerView.layoutParams = playerViewParam
        var window = (context as VideoPlayActivity).window
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN
        )
        (context).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    init {
        youTubePlayer.addListener(playerTracker)
        initViews(playerUi)
    }
}