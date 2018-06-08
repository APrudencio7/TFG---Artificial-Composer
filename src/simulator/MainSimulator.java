package simulator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import kuusisto.tinysound.TinySound;
import simulator.SimulationConfigurator;
import simulator.soundRecorder.SoundRecorder;

public class MainSimulator {
	
    // Calculate the acceptance probability
    public static double acceptanceProbability(double energy, double newEnergy, double temperature) {
        // If the new solution is better, accept it
        if (newEnergy < energy) {
            return 1.0;
        }
        // If the new solution is worse, calculate an acceptance probability
        
        double aux = Math.exp((energy - newEnergy) / temperature);
        
        return aux;
    }
	
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
		
		BufferedWriter writer = new BufferedWriter(new FileWriter("Metrics\\metricsTeret.txt"));
		
		
		// TinySound initialization.
		TinySound.init();
		
		if (args.length == 2) {
			//Automatic mode (Work with no parameters, nIterations times)
			
			nameRecording = "Recordingsqwet\\";
			
			float bpm = 100.0f;
			int numSounds;
			int numBars = 8;
			int nIterations = Integer.parseInt(args[0]);
			int changeBPM = Integer.parseInt(args[1]);
			
			for(int i = 0; i < nIterations; i++) {
				if (i % changeBPM == 0 && i > 0) {
					bpm += 5;
				}
				
				numSounds = rand.nextInt((20 - 3) + 1) + 3;
				
				// We create our simulation.
				SimulationConfigurator simulation = new SimulationConfigurator(bpm, numSounds, numBars);
				
				// We randomize our sounds and their times.
				try {
					simulation.soundRandomizer(writer, args, false);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				// We order sounds by their start time for having an ordered reproduction of them.
				//simulation.orderTimes();
				
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
				
				nameRecording = "Recordingsqwet\\";
				
			}
			
		}else if (args.length == 3) {
			//Manual mode (Work with parameters, only once)
			
			nameRecording = "SingleRecordings\\";
			
			final float bpm = Float.parseFloat(args[0]);
			final int numSounds = Integer.parseInt(args[1]);
			final int numBars = Integer.parseInt(args[2]);
			
			// We create our simulation.
			SimulationConfigurator simulation = new SimulationConfigurator(bpm, numSounds, numBars);
			
			// We randomize our sounds and their times.
			try {
				simulation.soundRandomizer(writer, args, false);
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
		}else if (args.length == 0) {
			/* Optimization and results mode
			 *
			 * This mode includes the number of each elements which means
			 * that this wont be randomised then.
			 */
			
			List<Float> selectBPM = Arrays.asList(90f, 95f, 100f, 105f, 110f, 115f, 120f, 125f, 130f,
												  135f, 140f, 145f, 150f, 155f, 160f, 165f, 170f, 175f,
												  180f, 185f, 190f, 195f, 200f);
			
			// Initial parameters for SA
			final float bpm = selectBPM.get(rand.nextInt(selectBPM.size()));
			final int numSounds = rand.nextInt(((20-3)+1)-3);
			final int numBars = 8;
			
			// SA Variables
			double temp = 10000;
			double coolingRate = 0.003;
			String[] auxArr = {"AG"};
			
			// Initialize initial solution
			SimulationConfigurator currentSolution = new SimulationConfigurator(bpm, numSounds, numBars);
		
			// We randomize our sounds and their times.
			try {
				currentSolution.soundRandomizer(writer, auxArr, false);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// Set as current best
			SimulationConfigurator best = new SimulationConfigurator(currentSolution);
			
			int auxIter = 0;
			// Loop until system has cooled
			while (temp > 1) {
	            // Create new neighbour simulation
				SimulationConfigurator newSolution = new SimulationConfigurator(currentSolution);

				// We change a random sound for a new random sound
				// and then we evaluate again our simulation
				
				if (auxIter % 5 == 0) {
					/* METHOD 3 - CHANGING NUM SOUNDS */
 					newSolution.setNumberOfSounds(rand.nextInt((20 - 3) + 1) + 3);
					newSolution.getEvaluation().cleanSounds(newSolution);
					
					newSolution.soundRandomizer(writer, auxArr, true);
					/* --------- END METHOD 3 --------- */
				}else {
					/* METHOD 1 - CHANGING SOUNDS */
					int auxIndex = rand.nextInt(newSolution.getSounds().size());
					newSolution.getSounds().remove(auxIndex);
					
					newSolution.soundRandomizer(writer, auxArr, true);
					/* --------- END METHOD 1 --------- */
				}
			
	            // Get energy of solutions
	            double currentEnergy = (10 - currentSolution.getEvaluation().getEnergy());
	            double neighbourEnergy = (10 - newSolution.getEvaluation().getEnergy());
	            
	            // Decide if we should accept the neighbour
	            if (acceptanceProbability(currentEnergy, neighbourEnergy, temp) > Math.random()) {
	                currentSolution = new SimulationConfigurator(newSolution);
	            }

	            // Keep track of the best solution found
	            if (currentSolution.getEvaluation().getEnergy() > best.getEvaluation().getEnergy()) {
	                best = new SimulationConfigurator(currentSolution);
	            }
	            
	            System.out.println("Iteration " + auxIter + " best: " + best.getEvaluation().getEnergy());
	            System.out.println("	- Neighbourg result: " + (10 - neighbourEnergy) + "\n");
	            
	            
	            // Cool system
	            temp *= 1 - coolingRate;
	            auxIter += 1;
	        }
			
			
			// Play best simulation
			best.playSimulation();
			
		}else {
			
			//Bad parameters
			System.out.println("\nBad parameters, introduce in this order: BPM, Number of Sounds, Number of Bars, 0 for Manual mode or 1 for automatic.\n");
		}
		
		writer.close();

		// TinySound closing.
		TinySound.shutdown();		
	}
}