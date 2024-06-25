import android.annotation.SuppressLint
import android.os.Handler

/**
 * Timer class that provides functionality to start, stop, and reset a timer
 * and notifies listeners about time updates.
 *
 * @property listener Listener to receive timer update callbacks.
 */
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

    /**
     * Starts the timer.
     */
    fun startTimer() {
        handler.postDelayed(runnable, 1000) // Start the timer
    }

    /**
     * Stops the timer.
     */
    fun stopTimer() {
        handler.removeCallbacks(runnable) // Stop the timer
    }

    /**
     * Resets the timer to zero and stops it.
     */
    fun resetTimer() {
        stopTimer()
        seconds = 0
        listener?.onTimeUpdate(secondsToTime(seconds))
    }

    /**
     * Converts total seconds to a formatted time string (mm:ss).
     *
     * @param seconds Total seconds to convert.
     * @return Formatted time string in mm:ss format.
     */
    @SuppressLint("DefaultLocale")
    private fun secondsToTime(seconds: Int): String {
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60

        return String.format("%02d:%02d", minutes, secs)
    }

    /**
     * Interface for receiving timer update callbacks.
     */
    interface TimerUpdateListener {
        fun onTimeUpdate(time: String?)
    }
}