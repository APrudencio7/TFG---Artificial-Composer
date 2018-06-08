package simulator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import kuusisto.tinysound.TinySound;
import simulator.dataEnumerators.*;
import simulator.soundConfigurator.DrumSound;

public class SimulationConfigurator {
	
	private float bpm;
	private int numberOfSounds;
	private int numberOfBars;
	private ArrayList<DrumSound> sounds;
	private SortedMap<Integer, List<DrumSound>> soundsOrdered;
	private SimulationEvaluation evaluation;
	
	/**
	 * Constructor of the simulation configurator.
	 * 
	 * @param beatsPerMinute Number of beats per minute in the current simulation.
	 */
	public SimulationConfigurator(float beatsPerMinute, int numSounds, int numBars) {
		this.bpm = beatsPerMinute;
		this.numberOfSounds = numSounds;
		this.numberOfBars = numBars;
		this.sounds = new ArrayList<DrumSound>();
		this.soundsOrdered = new TreeMap<Integer, List<DrumSound>>();
		this.evaluation = new SimulationEvaluation();
	}
	
	/**
	 * Alternative constructor of the simulation configurator.
	 * 
	 */
	public SimulationConfigurator(SimulationConfigurator simulation) {
		this.bpm = simulation.getBeatsPerMinute();
		this.numberOfSounds = simulation.getNumberOfSounds();
		this.numberOfBars = simulation.getNumberOfBars();
		this.sounds = simulation.getSounds();
		this.soundsOrdered = simulation.getSoundsOrdered();
		this.evaluation = new SimulationEvaluation(simulation.getEvaluation());
	}

	/**
	 * Randomly selects N sounds without repetition from a library. 
	 * It selects always at least 2 Drums sounds.
	 * 
	 * @param libraryPath Path to the sound library.
	 * @param numSounds Number of sounds to select.
	 * @return A set composed of N sounds.
	 * @throws IOException 
	 */
	public void soundRandomizer(BufferedWriter metricsPath, String[] args, Boolean optimize) throws IOException {
		DrumSound auxDrum;
		String sound = "/Drums";
		Random rand = new Random();
		int numReps = 0;
		String auxPath = "";
		typeOfDrum auxType;
		
		// Metrics variables.
		if (args.length == 9) {
			// Genetics/Results mode
			evaluation.setnKicks(Integer.parseInt(args[2]));
			evaluation.setnSnares(Integer.parseInt(args[3]));
			evaluation.setnClaps(Integer.parseInt(args[4]));
			evaluation.setnHHC(Integer.parseInt(args[5]));
			evaluation.setnHHO(Integer.parseInt(args[6]));
			evaluation.setnCymbals(Integer.parseInt(args[7]));
			evaluation.setnPercs(Integer.parseInt(args[8]));
			
			this.numberOfSounds = evaluation.getNumSounds();
			
			while(this.sounds.size() < this.numberOfSounds) {

				// We choose randomly the sounds and then we create them and
				// add them to our class atribute sounds.
				
				for(int i = 0; i < evaluation.getnKicks(); i++) {
					sound = "/Drums/Kicks";
					
					File dir = new File("Sounds" + sound);
					File[] files = dir.listFiles();
					File file = files[rand.nextInt(files.length)];
					
					auxPath = file.toString().replace("Sounds\\", "");
					numReps = 1;

					auxDrum = new DrumSound(auxPath, numReps, typeOfDrum.Kicks, TinySound.loadSound(auxPath));
					auxDrum.setDrumTimes(this.getDurationBeat(), this.numberOfBars);
					this.sounds.add(auxDrum);
				}
				
				for(int i = 0; i < evaluation.getnSnares(); i++) {
					sound = "/Drums/Snares";
					
					File dir = new File("Sounds" + sound);
					File[] files = dir.listFiles();
					File file = files[rand.nextInt(files.length)];
					
					auxPath = file.toString().replace("Sounds\\", "");
					numReps = 1;

					auxDrum = new DrumSound(auxPath, numReps, typeOfDrum.Snares, TinySound.loadSound(auxPath));
					auxDrum.setDrumTimes(this.getDurationBeat(), this.numberOfBars);
					this.sounds.add(auxDrum);
				}
				
				for(int i = 0; i < evaluation.getnClaps(); i++) {
					sound = "/Drums/Claps";
					
					File dir = new File("Sounds" + sound);
					File[] files = dir.listFiles();
					File file = files[rand.nextInt(files.length)];
					
					auxPath = file.toString().replace("Sounds\\", "");
					numReps = 1;

					auxDrum = new DrumSound(auxPath, numReps, typeOfDrum.Claps, TinySound.loadSound(auxPath));
					auxDrum.setDrumTimes(this.getDurationBeat(), this.numberOfBars);
					this.sounds.add(auxDrum);
				}
				
				for(int i = 0; i < evaluation.getnHHC(); i++) {
					sound = "/Drums/HiHatsClosed";
					
					File dir = new File("Sounds" + sound);
					File[] files = dir.listFiles();
					File file = files[rand.nextInt(files.length)];
					
					auxPath = file.toString().replace("Sounds\\", "");
					numReps = rand.nextInt(4);

					auxDrum = new DrumSound(auxPath, numReps, typeOfDrum.HiHatsClosed, TinySound.loadSound(auxPath));
					auxDrum.setDrumTimes(this.getDurationBeat(), this.numberOfBars);
					this.sounds.add(auxDrum);
				}
				
				for(int i = 0; i < evaluation.getnHHO(); i++) {
					sound = "/Drums/HiHatsOpen";
					
					File dir = new File("Sounds" + sound);
					File[] files = dir.listFiles();
					File file = files[rand.nextInt(files.length)];
					
					auxPath = file.toString().replace("Sounds\\", "");
					numReps = rand.nextInt(2);

					auxDrum = new DrumSound(auxPath, numReps, typeOfDrum.HiHatsOpen, TinySound.loadSound(auxPath));
					auxDrum.setDrumTimes(this.getDurationBeat(), this.numberOfBars);
					this.sounds.add(auxDrum);
				}
				
				for(int i = 0; i < evaluation.getnCymbals(); i++) {
					sound = "/Drums/Cymbals";
					
					File dir = new File("Sounds" + sound);
					File[] files = dir.listFiles();
					File file = files[rand.nextInt(files.length)];
					
					auxPath = file.toString().replace("Sounds\\", "");
					numReps = 1;

					auxDrum = new DrumSound(auxPath, numReps, typeOfDrum.Cymbals, TinySound.loadSound(auxPath));
					auxDrum.setDrumTimes(this.getDurationBeat(), this.numberOfBars);
					this.sounds.add(auxDrum);
				}
				
				for(int i = 0; i < evaluation.getnPercs(); i++) {
					sound = "/Drums/Percussion";
					
					File dir = new File("Sounds" + sound);
					File[] files = dir.listFiles();
					File file = files[rand.nextInt(files.length)];
					
					auxPath = file.toString().replace("Sounds\\", "");
					numReps = 1;

					auxDrum = new DrumSound(auxPath, numReps, typeOfDrum.Percussion, TinySound.loadSound(auxPath));
					auxDrum.setDrumTimes(this.getDurationBeat(), this.numberOfBars);
					this.sounds.add(auxDrum);
				}
			}
			
		}else {
			// Normal mode
			while(this.sounds.size() < this.numberOfSounds) {
				sound = "/Drums/" + typeOfDrum.getRandomDrum(rand).name();
							
				File dir = new File("Sounds" + sound);
				File[] files = dir.listFiles();
				File file = files[rand.nextInt(files.length)];
							
				auxPath = file.toString().replace("Sounds\\", "");
				
				if (auxPath.contains("HiHatsClosed")){
					numReps = rand.nextInt(4);
					auxType = typeOfDrum.HiHatsClosed;
					evaluation.setnHHC(evaluation.getnHHC() + 1);
				}
				else if (auxPath.contains("Kicks")) {
					numReps = 1;
					auxType = typeOfDrum.Kicks;
					evaluation.setnKicks(evaluation.getnKicks() + 1);
				}
				else if (auxPath.contains("Snares")) {
					numReps = 1;
					auxType = typeOfDrum.Snares;
					evaluation.setnSnares(evaluation.getnSnares() + 1);
				}
				else if (auxPath.contains("Claps")) {
					numReps = 1;
					auxType = typeOfDrum.Claps;
					evaluation.setnClaps(evaluation.getnClaps() + 1);
				}
				else if (auxPath.contains("HiHatsOpen")) {
					numReps = rand.nextInt(2);
					auxType = typeOfDrum.HiHatsOpen;
					evaluation.setnHHO(evaluation.getnHHO() + 1);
				}
				else if (auxPath.contains("Percussion")) {
					numReps = 1;
					auxType = typeOfDrum.Percussion;
					evaluation.setnPercs(evaluation.getnPercs() + 1);
				}
				else {
					numReps = 1;
					auxType = typeOfDrum.Cymbals;
					evaluation.setnCymbals(evaluation.getnCymbals() + 1);
				}
				
				auxDrum = new DrumSound(auxPath, numReps, auxType, TinySound.loadSound(auxPath));
				auxDrum.setDrumTimes(this.getDurationBeat(), this.numberOfBars);
				this.sounds.add(auxDrum);
			}
		}
		
		// We order sounds by their start time for having an ordered reproduction of them.
		this.orderTimes(optimize);
		
		// Now with the times setted and ordered, we can make our automated evaluation and put it into the file.
		// With the evaluation made by the software and an own evaluation from the user we can finish 
		// the evaluation of every beat.
		evaluation.automatedEvaluation(this);
		
		// Write the major properties of the current simulation and their result.
		String line = 
				this.bpm + " " + this.numberOfSounds + " " + evaluation.getnKicks() + " " + 
				evaluation.getnSnares() + " " + evaluation.getnClaps() + " " + evaluation.getnHHC() + " " +
				evaluation.getnHHO() + " " + evaluation.getnCymbals() + " " + evaluation.getnPercs() + " " + evaluation.getEnergy() + "\n";
		
		metricsPath.write(line);
		return;
	}
	
	/**
	 * 
	 * Creates a Map that orders the times where the sounds will be played
	 * and displays which sounds will be played at that time.
	 * 
	 * Key is the time when the sound will be played.
	 * Values are the sounds that will be played at the time specified.
	 * 
	 * @return
	 */
	public void orderTimes(Boolean optimize) {
		List<DrumSound> auxList;
		int numRepsAux = 0;

		if (optimize) {
			this.soundsOrdered = new TreeMap<Integer, List<DrumSound>>();
		}
		
		for (int i = 0; i < this.numberOfBars; i++) {
			for(DrumSound drum: this.sounds) {					
				if (drum.getDrumType() == typeOfDrum.HiHatsClosed || drum.getDrumType() == typeOfDrum.HiHatsOpen) {
					numRepsAux = drum.getNumReps();

					for (int k = 0; k < numRepsAux; k++) {
						//If the key exist, we add a value.
						if (this.soundsOrdered.containsKey(drum.getStartTimes().get((numRepsAux*i) + k ))) {
							this.soundsOrdered.get(drum.getStartTimes().get((numRepsAux*i)+k)).add(drum);
						}
						//If not, we create a new key, and the values with it.
						else {
							auxList = new ArrayList<DrumSound>();
							auxList.add(drum);
							this.soundsOrdered.put(drum.getStartTimes().get((numRepsAux*i)+k), auxList);
						}
					}
				}else {
					//If the key exist, we add a value.
					if (this.soundsOrdered.containsKey(drum.getStartTimes().get(i))) {
						this.soundsOrdered.get(drum.getStartTimes().get(i)).add(drum);
					}
					//If not, we create a new key, and the value with it.
					else {
						auxList = new ArrayList<DrumSound>();
						auxList.add(drum);
						this.soundsOrdered.put(drum.getStartTimes().get(i), auxList);
					}
				}
			}
		}
	}
		
	public void playSimulation() {
		int oldTime = 0;
		
		System.out.println("Simulation settings: ");
		System.out.println("    -Beats per minute: " + this.bpm + ".");
		System.out.println("    -Number of sounds: " + this.numberOfSounds + ".");
		System.out.println("    -Length of the beat: " + ((this.getDurationBeat()*4*this.numberOfBars)/1000) + " seconds aprox.");
		System.out.println("    -Energy: " + evaluation.getEnergy());
		
		System.out.println("\nAnd the beat goes on!");
		
		for (Entry<Integer, List<DrumSound>> entry : this.soundsOrdered.entrySet()) {

			try {
				Thread.sleep(entry.getKey() - oldTime);
				oldTime = entry.getKey();
			} catch (InterruptedException e) {}
				
			//Play the random beat!!
			for (DrumSound drum: entry.getValue()) {
				drum.getDrumSound().play();
			}			
		}
		
		
		
		//We wait one more second for the delay/reverb on sounds.
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {}
		
		//System.out.println("\nBeat finished!\n");
	}
	
	// Getters and setters methods.
	
	public float getBeatsPerMinute() {
		return this.bpm;
	}
	
	public int getNumberOfSounds() {
		return this.numberOfSounds;
	}
	
	public int getNumberOfBars() {
		return this.numberOfBars;
	}
	
	public void setSounds(ArrayList<DrumSound> arr) {
		this.sounds = arr;
	}
	
	public int getDurationBeat() {
		return Math.round(((1/bpm)*60)*1000);
	}
	
	public ArrayList<DrumSound> getSounds (){
		return this.sounds;
	}
	
	public SortedMap<Integer, List<DrumSound>> getSoundsOrdered (){
		return this.soundsOrdered;
	}

	public void setBeatsPerMinute(float beatsPerMin) {
		this.bpm = beatsPerMin;
	}

	public void setNumberOfSounds(int numSounds) {
		this.numberOfSounds = numSounds;
	}
	
	public SimulationEvaluation getEvaluation() {
		return this.evaluation;
	}
}
