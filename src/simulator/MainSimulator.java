package simulator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import kuusisto.tinysound.TinySound;
import simulator.SimulationConfigurator;

public class MainSimulator {
	public static void main(String[] args) throws IOException {

		/*
		 * Program arguments:
		 * 
		 * First: BPM (Beats per minute) (80 - 180 are good choices, but hey, do whatever you want)
		 * Second: Number of sounds that will be played on the song. (Always more than 2 for movement!)
		 * Third: Number of bars of the song. This applies to the length of the song (Multiples of 8 are a good choice, not zero).
		 * Fourth: Automatic/Manual mode. 1 will be automatic, this means that parameters won't aplly, and 0 means that parameters will aplly. 
		 * 
		 */
		
		Random rand = new Random();
		String nameRecording;
		
		BufferedWriter writer = new BufferedWriter(new FileWriter("Metrics\\metricsTest.txt"));
		
		
		// TinySound initialization.
				TinySound.init();
		
		if (Integer.parseInt(args[3]) == 1) {
			//Automatic mode (Work with no parameters, nIterations times)
			
			nameRecording = "RecordingsTest\\";
			
			float bpm = 100.0f;
			int numSounds;
			int numBars = 8;
			int nIterations = 300;
			
			for(int i = 0; i < nIterations; i++) {
				if (i % 15 == 0 && i > 0) {
					bpm += 5;
				}
				
				numSounds = rand.nextInt((20 - 3) + 1) + 3;
				
				// We create our simulation.
				SimulationConfigurator simulation = new SimulationConfigurator(bpm, numSounds, numBars);
				
				// We randomize our sounds and their times.
				try {
					simulation.soundRandomizer(writer);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				// We order sounds by their start time for having an ordered reproduction of them.
				simulation.orderTimes();
				
				/* If we want to record output audio:
				 		Enable Stereo Mix and plug something into any auxiliar entry on the
						motherboard, then set as default on sound properties the audio card
						on playback and stereo mix on recording. 
				
						If Stereo mix doesnt show audio, rise the volume on levels tab.*/
				
				
				nameRecording += "beat_" + (i+1) + "_" + Math.round(bpm) + "_" + numSounds + ".wav";
				
				SoundRecorder recorder = new SoundRecorder(nameRecording);
				recorder.recording(recorder, numBars*4*simulation.getDurationBeat());
				
				// We play our beat!
				simulation.playSimulation();
				
				recorder.finish();
				
				nameRecording = "RecordingsTest\\";
				
			}
			
		}else if (Integer.parseInt(args[3]) == 0) {
			//Manual mode (Work with parameters, only once)
			
			nameRecording = "SingleRecordings\\";
			
			final float bpm = Float.parseFloat(args[0]);
			final int numSounds = Integer.parseInt(args[1]);
			final int numBars = Integer.parseInt(args[2]);
			
			// We create our simulation.
			SimulationConfigurator simulation = new SimulationConfigurator(bpm, numSounds, numBars);
			
			// We randomize our sounds and their times.
			try {
				simulation.soundRandomizer(writer);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			/* If we want to record output audio:
			 		Enable Stereo Mix and plug something into any auxiliar entry on the
					motherboard, then set as default on sound properties the audio card
					on playback and stereo mix on recording. 
			
					If Stereo mix doesnt show audio, rise the volume on levels tab.*/
			
			
			nameRecording += "beatOnce_" + "_" + Math.round(bpm) + "_" + numSounds + ".wav";
			
			SoundRecorder recorder = new SoundRecorder(nameRecording);
			recorder.recording(recorder, numBars*4*simulation.getDurationBeat());
			
			// We play our beat!
			simulation.playSimulation();
			
			recorder.finish();
		}else {
			
			//Bad parameters
			System.out.println("\nBad parameters, introduce in this order: BPM, Number of Sounds, Number of Bars, 0 for Manual mode or 1 for automatic.\n");
		}
		
		writer.close();

		// TinySound closing.
		TinySound.shutdown();		
	}
}