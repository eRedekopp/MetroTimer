
import javax.sound.sampled.*;

public class Audio {

    private volatile boolean metroPlaying;

    private Thread metroThread;

    private Thread alarmThread;

    private SourceDataLine dataLine;

    /**
     * The volume modifier, 0 <= vol <= 1
     */
    private double vol = 0.8d;

    private static final int BEEP_MS = 100;

    private static final int SAMPLE_RATE_HZ = 44100;

    public Audio() {
        try {
            dataLine = AudioSystem.getSourceDataLine(new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    SAMPLE_RATE_HZ,
                    16,
                    1,
                    2,
                    SAMPLE_RATE_HZ,
                    true
            ));
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    /**
     * Start playing a metronome on a new thread
     *
     * @param bpm The tempo of the metronome, in bpm
     * @param hiFreq The frequency of the accented sine wave "beep"
     * @param loFreq The frequency of the unaccented sine wave "beep"
     * @param accentInterval Play an accented tone every N beats, where N = accentInterval
     */
    public void start(final int bpm, final float hiFreq, final float loFreq, final int accentInterval) {

        if (metroPlaying) {
            System.err.println("start() called when audio was already playing");
            return;
        } else {
            metroPlaying = true;
        }

        final byte[] loBeep = getBeep(bpm, loFreq);
        final byte[] hiBeep = (accentInterval == 0) ? null : getBeep(bpm, hiFreq);

        this.metroThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("metronome started");
                long beeps = 0;
                dataLine.start();
                while (metroPlaying) {
                    System.out.println("Writing tone #" + beeps);
                    byte[] toPlay;
                    beeps += 1;
                    if (accentInterval == 0 || beeps % accentInterval != 0) {
                        System.out.println("lobeep");
                        toPlay = loBeep;
                    } else {
                        System.out.println("hibeep");
                        toPlay = hiBeep;
                    }
                    dataLine.write(toPlay, 0, toPlay.length);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        stop();
                    }

                }

                // metronome paused: flush the data line then fall off the end
                dataLine.stop();
                dataLine.flush();
            }
        });
        metroThread.start();
    }

    /**
     * Kill the metronome thread
     */
    public void stop() {
        System.out.println("metronome stopped");
        this.metroPlaying = false;
        this.metroThread = null;
    }

    /**
     * Play an alarm sound without disrupting the metronome
     */
    public void alarm() {
        // TODO
        this.alarmThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("!!!! Alarm !!!!");
            }
        });
        alarmThread.start();
    }

    /**
     * Get a byte buffer containing the PCM data for a single "beep" and the silence following it.
     *
     * @param bpm The tempo in BPM of the metronome for which this beep is being made
     * @param freq The pitch of the beep
     * @return A byte buffer containing the beep and however much silence is necessary to keep the given tempo
     */
    private byte[] getBeep(int bpm, float freq) {
        double secsBetweenBeats = 60f / bpm;
        int numSamples = (int) Math.floor(SAMPLE_RATE_HZ * secsBetweenBeats) * 2;
        byte[] samples = new byte[numSamples];

        // fill buffer
        for (int i = 0; i < BEEP_MS * (float) 44100 / 1000; i++) {
            float period = (float) SAMPLE_RATE_HZ / freq;
            double angle = 2 * i * Math.PI / (period);
            short a = (short) (Math.sin(angle) / (2 * Math.PI) * vol);
            samples[2 * i] = (byte) (a & 0xFF);     // write lower 8bits (________WWWWWWWW) out of 16
            samples[2 * i + 1] = (byte) (a >> 8);   // write upper 8bits (WWWWWWWW________) out of 16
        }

        return samples;
    }

    public boolean audioPlaying() {
        return this.metroPlaying;
    }

}
