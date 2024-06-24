import android.annotation.SuppressLint
import android.os.Handler

class Timer (private val listener: TimerUpdateListener?) {
    private val handler = Handler()
    private val runnable: Runnable
    private var seconds = 0


    init {
        runnable = object : Runnable {
            override fun run() {
                seconds++
                listener?.onTimeUpdate(secondsToTime(seconds))
                handler.postDelayed(this, 1000) // Schedule the next update in 1 second
            }
        }
    }

    fun startTimer() {
        handler.postDelayed(runnable, 1000) // Start the timer
    }

    fun stopTimer() {
        handler.removeCallbacks(runnable) // Stop the timer
    }

    fun resetTimer() {
        stopTimer()
        seconds = 0
        listener?.onTimeUpdate(secondsToTime(seconds))
    }

    @SuppressLint("DefaultLocale")
    private fun secondsToTime(seconds: Int): String {
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60

        return String.format("%02d:%02d", minutes, secs)
    }

    interface TimerUpdateListener {
        fun onTimeUpdate(time: String?)
    }
}