import java.util.Timer;
import java.util.TimerTask;

/**
 * Controls for the timer
 */
public class TimerController {

    private Model model;

    private Audio audio;

    private Timer timer;

    /**
     * Is the timer currently running?
     */
    private volatile boolean timerRunning = false;

    /**
     * Start decreasing the timer by 1 every second from its current value,
     * then stop and set off an alarm when it hits 0
     */
    public void startTimer() {
        if (! model.timeUp() && timer == null) {
            timerRunning = true;
            timer = new Timer(true);
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    if (! timerRunning) {
                        stopTimer();
                    } else if (model.timeUp()) {
                        if (model.isTimerLooping()) {
                            model.restoreSavedTime();
                        } else {
                            stopTimer();
                        }
                        audio.alarm();
                    } else {
                        model.decrTimer();
                    }
                }
            };
            timer.scheduleAtFixedRate(task, 0, 1000);
        }
    }

    /**
     * Stop the timer thread
     */
    public void stopTimer() {
        this.timer.cancel();
        this.timer = null;
        this.timerRunning = false;
        this.model.notifySubscribers();
    }

    public boolean timerRunning() {
        return timerRunning;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public void setAudio(Audio audio) {
        this.audio = audio;
    }
}
