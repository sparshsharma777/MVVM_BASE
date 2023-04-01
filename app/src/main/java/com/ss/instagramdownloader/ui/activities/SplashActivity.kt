package com.ss.instagramdownloader.ui.activities

import android.graphics.Bitmap
import android.media.MediaDataSource
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.translation.TranslationManager
import android.widget.MediaController
import android.widget.ProgressBar
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.transition.TransitionManager
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import com.ss.instagramdownloader.R


class SplashActivity : AppCompatActivity() {

    private val url="http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    lateinit var videoView: VideoView
    lateinit var progressBar: ProgressBar
    lateinit var ivPause: AppCompatImageView
    lateinit var ivPlaceHolder: AppCompatImageView
    lateinit var exoPlayerView: StyledPlayerView
    lateinit var viewRoot: ViewGroup

    private var playWhenReady = true
    private var currentItem = 0
    private var playbackPosition = 0L
    private var presentationTimeUs = 0L

    private var player: ExoPlayer? = null
    private val playbackStateListener: Player.Listener = playbackStateListener()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
         initViews()
         // initVideoView()
       // initializePlayer()
    }

    private fun initViews() {
        videoView=findViewById(R.id.videoView)
        progressBar=findViewById(R.id.progress_bar)
        ivPause=findViewById(R.id.ivPause)
        exoPlayerView=findViewById(R.id.exoPlayerView)
        ivPlaceHolder=findViewById(R.id.ivVideoPlaceHolder)
        viewRoot=findViewById(R.id.rootVie)
//        exoPlayerView.controllerHideOnTouch=false
//        exoPlayerView.controllerAutoShow=true
//        exoPlayerView.controllerShowTimeoutMs=10000


        ivPause.setOnClickListener{
            ivPause.visibility=View.GONE
            ivPlaceHolder.visibility=View.INVISIBLE
            exoPlayerView.visibility = View.VISIBLE
            player?.play()


        }
        exoPlayerView.setControllerVisibilityListener(object : StyledPlayerView.ControllerVisibilityListener{
            override fun onVisibilityChanged(visibility: Int) {

            }

        })


    }

    private fun initializePlayer() {
        val trackSelector = DefaultTrackSelector(this).apply {
            setParameters(buildUponParameters().setMaxVideoSizeSd())
        }
        player = ExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .build()
            .also { exoPlayer ->
                exoPlayerView.player = exoPlayer
               // val mediaItem = MediaItem.fromUri(url)
                val mediaItem = MediaItem.Builder()
                    .setUri(url)
                    .setMimeType(MimeTypes.APPLICATION_MP4)
                    .build()
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.seekTo(currentItem,playbackPosition)
                exoPlayer.addListener(playbackStateListener)
                exoPlayer.setVideoFrameMetadataListener { presentationTimeUs, releaseTimeNs, format, mediaFormat ->
                    Log.d("exo",presentationTimeUs.toString())
                    this.presentationTimeUs=presentationTimeUs


                }
               // exoPlayer.setVideoFrameMetadataListener()
                exoPlayer.prepare()
            }
    }

    private fun initVideoView() {

        val mediacontrller=MediaController(this)
        videoView.setMediaController(mediacontrller)

       // mediacontrller.ge

        val uri= Uri.parse(url)
        videoView.setVideoURI(uri)
        progressBar.visibility=View.VISIBLE



        videoView.setOnPreparedListener(object :MediaPlayer.OnPreparedListener{
            override fun onPrepared(mp: MediaPlayer?) {
                progressBar.visibility=View.GONE
                videoView.start()

            }

        })
        videoView.setOnErrorListener(object :MediaPlayer.OnErrorListener{
            override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
                progressBar.visibility=View.VISIBLE
                ivPause.visibility=View.GONE

                // do something on error occur
                return false
            }
        })


      //  videoView.setOnClickListener{
//            if(videoView.isPlaying){
//                if(videoView.canPause()) {
//                    ivPause.visibility=View.VISIBLE
//                    videoView.pause()
//                }
//            }
//            else{
//                if(!videoView.isPlaying){
//                    ivPause.visibility=View.GONE
//                    videoView.resume()
//                }
//            }
//        }

    }


    public override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
//            if (exoPlayerView != null) {
//                exoPlayerView.onResume()
//            }
        }
    }

    public override fun onResume() {
        super.onResume()
       // hideSystemUi()
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer()
//            if (exoPlayerView != null) {
//                exoPlayerView.onResume()
//            }
        }
    }

    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, exoPlayerView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    public override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {

            if (exoPlayerView != null) {
                exoPlayerView.onPause()
            }
            releasePlayer()
        }
    }


    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {

            if (exoPlayerView != null) {
                exoPlayerView.onPause()
            }
            releasePlayer()
        }

    }


    private fun releasePlayer() {

        player?.let { exoPlayer ->
            playbackPosition = exoPlayer.currentPosition
            currentItem = exoPlayer.currentMediaItemIndex
            playWhenReady=false
            //playWhenReady = exoPlayer.playWhenReady
            exoPlayer.removeListener(playbackStateListener)
            exoPlayer.stop()
            exoPlayer.release()
        }
        player = null
        ivPause.visibility=View.VISIBLE
        exoPlayerView.visibility = View.GONE
        getVideoFrame()

    }

    fun getVideoFrame() {
        var bitmap: Bitmap? = null
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(url,HashMap<String,String>())
            bitmap = retriever.getFrameAtTime(presentationTimeUs)
        } catch (ex: RuntimeException) {
            ex.printStackTrace()
        } finally {
            try {
                retriever.release()
            } catch (ex: RuntimeException) {
                ex.printStackTrace()
            }
        }

        if(bitmap!=null) {
            ivPlaceHolder.setImageBitmap(bitmap)
            ivPlaceHolder.visibility = View.VISIBLE
            exoPlayerView.visibility = View.GONE


        }
        else {
             ivPlaceHolder.visibility = View.GONE
             exoPlayerView.visibility = View.GONE
        }




    }


    private fun playbackStateListener() = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            val stateString: String = when (playbackState) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
                ExoPlayer.STATE_READY -> {
                   //ivPlaceHolder.visibility=View.INVISIBLE
//                    exoPlayerView.visibility = View.VISIBLE
                    TransitionManager.beginDelayedTransition(viewRoot)
                  // ivPlaceHolder.visibility = View.INVISIBLE
                    exoPlayerView.visibility = View.VISIBLE



                    "ExoPlayer.STATE_READY     -"
                }

                ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
                else -> "UNKNOWN_STATE             -"
            }
            Log.d("exo", "changed state to $stateString")
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
        }

        override fun onPlayerErrorChanged(error: PlaybackException?) {
            super.onPlayerErrorChanged(error)
        }
    }

}


