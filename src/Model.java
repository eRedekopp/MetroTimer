import java.util.ArrayList;

public class Model {

    private int bpm;

    private int accentInterval;

    private float hiFreq;

    private float loFreq;

    private int timerMin;

    private int timerSec;

    private ArrayList<ModelListener> subscribers;

    public Model() {
        subscribers = new ArrayList();
    }

    private void notifySubscribers() {
        for (ModelListener l : subscribers) l.modelChanged();
    }

}
