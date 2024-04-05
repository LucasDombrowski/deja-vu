package fr.iutlens.mmi.dejaVu.utils

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.media.MediaPlayer.OnPreparedListener
import android.util.Log


class LoopMediaPlayer(context: Context, resId: Int) {
    private var mContext: Context? = null
    private var mResId = 0
    private var mCounter = 1
    private var mCurrentPlayer: MediaPlayer? = null
    private var mNextPlayer: MediaPlayer? = null
    private var volume : Float = 0.1f
    private fun createNextMediaPlayer() {
        mNextPlayer = MediaPlayer.create(mContext, mResId)
        mNextPlayer!!.setVolume(volume,volume)
        mCurrentPlayer!!.setNextMediaPlayer(mNextPlayer)
        mCurrentPlayer!!.setOnCompletionListener(onCompletionListener)
    }

    private val onCompletionListener =
        OnCompletionListener { mediaPlayer ->
            mediaPlayer.release()
            mCurrentPlayer = mNextPlayer
            createNextMediaPlayer()
            Log.d(TAG, String.format("Loop #%d", ++mCounter))
        }

    init {
        mContext = context
        mResId = resId
        mCurrentPlayer = MediaPlayer.create(mContext, mResId)
        mCurrentPlayer!!.setOnPreparedListener(OnPreparedListener { mCurrentPlayer!!.start() })
        createNextMediaPlayer()
    }

    @get:Throws(IllegalStateException::class)
    val isPlaying: Boolean
        // code-read additions:
        get() = mCurrentPlayer!!.isPlaying

    fun setVolume(volume: Float) {
        this.volume = volume
        mCurrentPlayer!!.setVolume(volume,volume)
    }

    @Throws(IllegalStateException::class)
    fun start() {
        mCurrentPlayer!!.start()
    }

    @Throws(IllegalStateException::class)
    fun stop() {
        mCurrentPlayer!!.stop()
    }

    @Throws(IllegalStateException::class)
    fun pause() {
        mCurrentPlayer!!.pause()
    }

    fun release() {
        mCurrentPlayer!!.release()
        mNextPlayer!!.release()
    }

    fun reset() {
        mCurrentPlayer!!.reset()
    }

    companion object {
        private val TAG = LoopMediaPlayer::class.java.simpleName
        fun create(context: Context, resId: Int): LoopMediaPlayer {
            return LoopMediaPlayer(context, resId)
        }
    }
}