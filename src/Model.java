import java.util.ArrayList;

/**
 * Where all the data gets stored
 */
public class Model {

    /**
     * The current tempo setting in BPM
     */
    private int bpm;

    /**
     * Accent every N beats, where this = N
     */
    private int accentInterval;

    /**
     * The frequency (pitch) of the accented tone
     */
    private float hiFreq;

    /**
     * The frequency (pitch) of the unaccented tone
     */
    private float loFreq;

    /**
     * The number of minutes on the timer
     */
    private int timerMin;

    /**
     * The number of seconds on the timer
     */
    private int timerSec;

    /**
     * The current volume setting
     */
    private double volume;

    /**
     * The last value of minutes for the timer entered by the user
     */
    private int lastSavedTimerMin = 0;

    /**
     * The last value of seconds for the timer entered by the user
     */
    private int lastSavedTimerSec = 0;

    /**
     * Should the timer loop?
     * Always true right now, but putting this here so it's easy to add a checkbox for it later
     */
    private boolean timerLooping = true;

    /**
     * All ModelListeners listening to this model
     */
    private ArrayList<ModelListener> subscribers;

    public Model() {
        subscribers = new ArrayList<>();
        // set defaults
        bpm = 120;
        accentInterval = 0;
        hiFreq = 1760;
        loFreq = 880;
        timerMin = 0;
        timerSec = 0;
    }

    public void notifySubscribers() {
        for (ModelListener l : subscribers) l.modelChanged();
    }

    /////////////////////////////// Accessor / Mutator ////////////////////////////////////////

    public void addSubscriber(ModelListener listener) {
        subscribers.add(listener);
    }

    public int getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
        this.notifySubscribers();
    }

    public int getAccentInterval() {
        return accentInterval;
    }

    public void setAccentInterval(int accentInterval) {
        this.accentInterval = accentInterval;
        this.notifySubscribers();
    }

    public float getHiFreq() {
        return hiFreq;
    }

    public void setHiFreq(float hiFreq) {
        this.hiFreq = hiFreq;
        this.notifySubscribers();
    }

    public float getLoFreq() {
        return loFreq;
    }

    public void setLoFreq(float loFreq) {
        this.loFreq = loFreq;
        this.notifySubscribers();
    }

    public int getTimerMin() {
        return timerMin;
    }

    public void setTimerMin(int timerMin) {
        this.timerMin = timerMin;
        this.notifySubscribers();
    }

    public int getTimerSec() {
        return timerSec;
    }

    public void setTimerSec(int timerSec) {
        this.timerSec = timerSec;
        this.notifySubscribers();
    }

    /**
     * Decrement the timer by 1 second, rolling over the minute if necessary
     */
    public void decrTimer() {
        this.timerSec--;
        if (timerSec < 0) {
            timerSec = 59;
            timerMin--;
        }
        this.notifySubscribers();
    }

    public boolean timeUp() {
        return timerMin == 0 && timerSec == 0;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double vol) {
        this.volume = vol;
        this.notifySubscribers();
    }

    public void setSavedTime(int min, int sec) {
        this.lastSavedTimerMin = min;
        this.lastSavedTimerSec = sec;
    }

    public void restoreSavedTime() {
        this.timerMin = this.lastSavedTimerMin;
        this.timerSec = this.lastSavedTimerSec;
        this.notifySubscribers();
    }

    public boolean isTimerLooping() {
        return timerLooping;
    }

    public void setTimerLooping(boolean timerLooping) {
        this.timerLooping = timerLooping;
    }
}
