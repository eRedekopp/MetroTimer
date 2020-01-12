/**
 * Actions to be called when something changes or gets clicked
 */
public class Controller {

    private Model model;

    private TimerController timerController;

    private Audio audio;

    /**
     * For the timer "start" button
     */
    public void handleTimerClick() {
        if (!timerController.timerRunning()) {
            timerController.startTimer();
        } else {
            timerController.stopTimer();
        }
        model.notifySubscribers();
    }

    /**
     * for the metronome "start" button
     */
    public void handleMetroClick() {
        if (!audio.audioPlaying()) {
            audio.start(model.getBpm(), model.getHiFreq(), model.getLoFreq(), model.getAccentInterval());
        } else {
            audio.stop();
        }
        model.notifySubscribers();
    }

    /**
     * For the timer's minute textbox
     *
     * @param newText The new text entered by the user
     * @return True if it was successfully converted to an integer and stored in the Model, else false
     */
    public boolean handleMinTextChanged(String newText) {
        int newMin;
        try {
            newMin = Integer.parseInt(newText);
            if (newMin < 0 || newMin > 99) {
                return false;
            }
            model.setTimerMin(newMin);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * For the timer's second textbox
     *
     * @param newText The new text entered by the user
     * @return True if it was successfully converted to an integer and stored in the Model, else false
     */
    public boolean handleSecTextChanged(String newText) {
        int newSec;
        try {
            newSec = Integer.parseInt(newText);
            if (newSec < 0 || newSec > 59) {
                return false;
            }
            model.setTimerSec(newSec);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * For the BPM textbox
     *
     * @param newText The new text entered by the user
     * @return True if it was successfully converted to an integer and stored in the Model, else false
     */
    public boolean handleBpmTextChanged(String newText) {
        int newBpm;
        try {
            newBpm = Integer.parseInt(newText);
            if (newBpm < 10 || newBpm > 300)
                return false;
            model.setBpm(newBpm);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * For the interval textbox
     *
     * @param newText The new text entered by the user
     * @return True if it was successfully converted to an integer and stored in the Model, else false
     */
    public boolean handleIntervalTextChanged(String newText) {
        int newInterval;
        try {
            newInterval = Integer.parseInt(newText);
            if (newInterval < 0)
                return false;
            model.setAccentInterval(newInterval);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void handleVolSliderChange(double newVol) {
        model.setVolume(newVol);
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public void setTimerController(TimerController timerController) {
        this.timerController = timerController;
    }

    public void setAudio(Audio audio) {
        this.audio = audio;
    }
}
