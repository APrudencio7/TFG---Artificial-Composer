package simulator.soundRecorder;

import javax.sound.sampled.*;
import java.io.*;
 
/**
 * A sample program is to demonstrate how to record sound in Java
 * 
 * Author: www.codejava.net
 * Link: http://www.codejava.net/coding/capture-and-record-sound-into-wav-file-with-java-sound-api
 * 
 */
public class SoundRecorder { 
    private File wavFile; // path of the wav file
    private AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE; // format of audio file
    private TargetDataLine line; // the line from which audio data is captured
 
    public SoundRecorder(String path) {
    	this.wavFile =  new File(path);
    }
    
    
    /**
     * Defines an audio format
     */
    AudioFormat getAudioFormat() {
        float sampleRate = 192000;
        int sampleSizeInBits = 16;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
                                             channels, signed, bigEndian);
        return format;
    }
 
    /**
     * Captures the sound and record into a WAV file
     */
    void start() {
        try {
            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
 
            // checks if system supports the data line
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                System.exit(0);
            }
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();   // start capturing
 
            System.out.println("Start capturing...");
 
            AudioInputStream ais = new AudioInputStream(line);
 
            System.out.println("Start recording...");
 
            // start recording
            AudioSystem.write(ais, fileType, wavFile);
 
        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
 
    /**
     * Closes the target data line to finish capturing and recording
     */
    public void finish() {
        line.stop();
        line.close();
        System.out.println("Finished");
    }
 
    /**
     * Entry to run the program
     */
    public void recording(SoundRecorder recorder, int RECORD_TIME) {
        // creates a new thread that starts recording
        Thread runner = new Thread(new Runnable() {
            public void run() {
                recorder.start();
            }
        });
 
        runner.start();
    }
}
