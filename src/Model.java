import java.util.ArrayList;

public class Model {

    private int bpm;

    private int accentInterval;

    private float hiFreq;

    private float loFreq;

    private int timerMin;

    private int timerSec;

    private double volume;

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
}
