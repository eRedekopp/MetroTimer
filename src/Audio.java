
import javax.sound.sampled.*;

/**
 * Controls for audio output
 */
public class Audio {

    /**
     * Is the metronome currently supposed to be playing?
     */
    private volatile boolean metroPlaying = false;

    /**
     * The thread writing metronome data to dataLine
     */
    private Thread metroThread;

    /**
     * The thread writing alarm data to a private SourceDataLine
     */
    private Thread alarmThread;

    /**
     * The format to be used for audio output
     */
    private AudioFormat audioFormat;

    /**
     * The data line to which metronome data is written
     */
    private SourceDataLine dataLine;

    /**
     * The model from which we get volume data
     */
    private Model model;

    /**
     * How long does a single metronome beep last?
     */
    private static final int BEEP_MS = 100;

    /**
     * The output sample rate in Hz
     */
    private static final int SAMPLE_RATE_HZ = 44100;

    public Audio() {
        try {
            audioFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    SAMPLE_RATE_HZ,
                    16,
                    1,
                    2,
                    SAMPLE_RATE_HZ,
                    true
            );
            dataLine = AudioSystem.getSourceDataLine(audioFormat);
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
                long beeps = 0;
                if (! dataLine.isOpen())
                    try {
                        dataLine.open(audioFormat, 4096);
                    } catch (LineUnavailableException e) {
                        e.printStackTrace();
                    }
                dataLine.start();
                while (metroPlaying) {
                    byte[] toPlay;
                    beeps += 1;
                    // get the volume-adjusted byte buffer
                    if (accentInterval == 0 || beeps % accentInterval != 0) {
                        toPlay = adjustVolume(loBeep, model.getVolume());
                    } else {
                        toPlay = adjustVolume(hiBeep, model.getVolume());
                    }
                    // write the samples
                    dataLine.write(toPlay, 0, toPlay.length);
                }

                // metronome paused: flush the data line then fall off the end
                dataLine.stop();
                dataLine.flush();
            }
        });
        metroThread.setDaemon(true);
        metroThread.start();
    }

    /**
     * Kill the metronome thread
     */
    public void stop() {
        this.metroPlaying = false;
        this.metroThread = null;
    }

    /**
     * Play an alarm sound without disrupting the metronome
     */
    public void alarm() {
        this.alarmThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // the alarm is pretty much just a really fast metronome for a short time
                byte[] toPlay = adjustVolume(getBeep(500, 3600), model.getVolume());

                // run on its own line so it doesn't interfere with the metronome
                SourceDataLine alarmDataLine;
                try {
                    alarmDataLine = AudioSystem.getSourceDataLine(audioFormat);
                    alarmDataLine.open(audioFormat, 4096);
                } catch (LineUnavailableException e) {
                    e.printStackTrace();
                    return;
                }
                alarmDataLine.start();
                // write 7 beeps
                for (int i = 0; i < 7; i++) {
                    alarmDataLine.write(toPlay, 0, toPlay.length);
                }
            }
        });
        alarmThread.setDaemon(true);
        alarmThread.start();
    }

    /**
     * Multiply all shorts in the buffer by the given volume modifier
     *
     * @param originalTone The tone from which to generate the volume-adjusted buffer (this buffer is not changed).
     *                     Must be 16-bit signed big-endian PCM data
     * @param volume The volume multiplier from 0 to 1 inclusive
     * @return A new byte array containing
     */
    private static byte[] adjustVolume(byte[] originalTone, double volume) {
        byte[] out = new byte[originalTone.length];
        for (int i = 0; i < originalTone.length; i += 2) {
            short adjusted = (short)(((originalTone[i] & 0xFF) << 8) | (originalTone[i+1] & 0xFF));
            adjusted *= volume;
            out[i] = (byte) (adjusted >> 8);
            out[i+1] = (byte) (adjusted & 0xFF);
        }
        return out;
    }

    /**
     * Get a byte buffer containing the PCM data for a single "beep" (at max volume) and the silence following it.
     * Adjust the volume of this beep by scaling each by the volume modifier
     *
     * @param bpm The tempo in BPM of the metronome for which this beep is being made
     * @param freq The pitch of the beep
     * @return A byte buffer containing the beep and however much silence is necessary to keep the given tempo
     */
    private static byte[] getBeep(int bpm, float freq) {
        double secsBetweenBeats = 60d / bpm;
        int numSamples = (int) Math.floor(SAMPLE_RATE_HZ * secsBetweenBeats) * 2;
        byte[] samples = new byte[numSamples];

        // fill buffer
        for (int i = 0; i < BEEP_MS * (float) 44100 / 1000; i++) {
            float period = (float) SAMPLE_RATE_HZ / freq;
            double angle = 2 * i * Math.PI / (period);
            short a = (short) (Math.sin(angle) / (2 * Math.PI) * Short.MAX_VALUE);
            // write to buffer as bytes
            samples[2 * i] = (byte) (a >> 8);
            samples[2 * i + 1] = (byte) (a & 0xFF);
        }
        return samples;
    }

    /**
     * @return Is audio playing right now?
     */
    public boolean audioPlaying() {
        return this.metroPlaying;
    }

    public void setModel(Model model) {
        this.model = model;
    }

}
