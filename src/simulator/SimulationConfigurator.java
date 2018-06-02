package simulator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
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
	private SortedMap<Integer, Set<DrumSound>> soundsOrdered;
	
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
		this.soundsOrdered = new TreeMap<Integer, Set<DrumSound>>();
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
	public void soundRandomizer(BufferedWriter metricsPath) throws IOException {
		DrumSound auxDrum;
		String sound = "/Drums";
		Random rand = new Random();
		int numReps = 0;
		String auxPath = "";
		typeOfDrum auxType;
		
		// Metrics variables.
		int numKicks = 0;
		int numSnares = 0;
		int numClaps = 0;
		int numHHC = 0;
		int numHHO = 0;
		int numCymbals = 0;
		int numPercs = 0;
		double autoEval = 0;
		
		while(this.sounds.size() < this.numberOfSounds) {
			sound = "/Drums/" + typeOfDrum.getRandomDrum(rand).name();
						
			File dir = new File("Sounds" + sound);
			File[] files = dir.listFiles();
			File file = files[rand.nextInt(files.length)];
						
			auxPath = file.toString().replace("Sounds\\", "");
			
			if (auxPath.contains("HiHatsClosed")){
				numReps = rand.nextInt(4);
				auxType = typeOfDrum.HiHatsClosed;
				numHHC += 1;
			}
			else if (auxPath.contains("Kicks")) {
				numReps = 1;
				auxType = typeOfDrum.Kicks;
				numKicks += 1;
			}
			else if (auxPath.contains("Snares")) {
				numReps = 1;
				auxType = typeOfDrum.Snares;
				numSnares += 1;
			}
			else if (auxPath.contains("Claps")) {
				numReps = 1;
				auxType = typeOfDrum.Claps;
				numClaps += 1;
			}
			else if (auxPath.contains("HiHatsOpen")) {
				numReps = rand.nextInt(2);
				auxType = typeOfDrum.HiHatsOpen;
				numHHO += 1;
			}else if (auxPath.contains("Percussion")) {
				numReps = 1;
				auxType = typeOfDrum.Percussion;
				numPercs += 1;
			}
			else {
				numReps = rand.nextInt(2);
				auxType = typeOfDrum.Cymbals;
				numCymbals += 1;
			}
			
			auxDrum = new DrumSound(auxPath, numReps, auxType, TinySound.loadSound(auxPath));
			auxDrum.setDrumTimes(this.getDurationBeat(), this.numberOfBars);
			this.sounds.add(auxDrum);
		}
		
		// We order sounds by their start time for having an ordered reproduction of them.
		this.orderTimes();
		
		// Now with the times setted and ordered, we can make our automated evaluation and put it into the file.
		// With the evaluation made by the software and an own evaluation from the user we can finish 
		// the evaluation of every beat.
		autoEval = this.automatedEvaluation(numKicks, numSnares, numClaps, numHHC, numHHO, numCymbals, numPercs);
		
		// Write the major properties of the current simulation and their result.
		String line = this.bpm + " " + this.numberOfSounds + " " + numKicks + " " + numSnares + " " + numClaps + " " + numHHC + " " + numHHO + " " + numCymbals + " " + numPercs + " " + autoEval + "\n";
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
	 * @param numBars Number of bars of the song (Length of the song).
	 * @return
	 */
	public void orderTimes() {
		Set<DrumSound> auxSet;
		int numRepsAux = 0;

		for (int i = 0; i < this.numberOfBars; i++) {
			for(DrumSound drum: this.sounds) {					
				if (drum.getDrumType() == typeOfDrum.HiHatsClosed || drum.getDrumType() == typeOfDrum.HiHatsOpen || drum.getDrumType() == typeOfDrum.Cymbals) {
					numRepsAux = drum.getNumReps();

					for (int k = 0; k < numRepsAux; k++) {
						//If the key exist, we add a value.
						if (this.soundsOrdered.containsKey(drum.getStartTimes().get((numRepsAux*i) + k ))) {
							this.soundsOrdered.get(drum.getStartTimes().get((numRepsAux*i)+k)).add(drum);
						}
						//If not, we create a new key, and the values with it.
						else {
							auxSet = new HashSet<DrumSound>();
							auxSet.add(drum);
							this.soundsOrdered.put(drum.getStartTimes().get((numRepsAux*i)+k), auxSet);
						}
					}
				}else {
					//If the key exist, we add a value.
					if (this.soundsOrdered.containsKey(drum.getStartTimes().get(i))) {
						this.soundsOrdered.get(drum.getStartTimes().get(i)).add(drum);
					}
					//If not, we create a new key, and the value with it.
					else {
						auxSet = new HashSet<DrumSound>();
						auxSet.add(drum);
						this.soundsOrdered.put(drum.getStartTimes().get(i), auxSet);			
					}
				}
			}
		}
	}

	public double automatedEvaluation(int nKicks, int nSnares, int nClaps, int nHHC, int nHHO, int nCymbals, int nPercs) {
		double finalResult = 0.0;
		double detailedResult = 0.0;
		double divisionsResult = 0.0;
		double generalResult = 0.0;
		ArrayList<DrumSound> auxKicks = new ArrayList<DrumSound>();
		ArrayList<DrumSound> auxSnares = new ArrayList<DrumSound>();
		ArrayList<DrumSound> auxClaps = new ArrayList<DrumSound>();
		ArrayList<Integer> allTimes = new ArrayList<Integer>();
		
		// Start of the automated evaluation that will give us a 
		// mark between 0 and 10 based on the basic elements of
		// our beat.
		
		if (nKicks > 0)
			generalResult += 1;
		else
			generalResult -= 0.125;
		
		if (nSnares > 0 || nClaps > 0)
			generalResult += 1;
		else
			generalResult -= 0.125;
		
		if (nHHC > 0)
			generalResult += 1;
		else
			generalResult -= 0.125;
		
		if (nHHO > 0)
			generalResult += 1;
		else
			generalResult -= 0.125;
		
		if (nKicks <= 2 && nSnares <= 2 && nClaps <= 2 && nHHC <= 2 && nHHO <= 2 && nCymbals <= 2 && nPercs <=2)
			generalResult += 1;
		else
			generalResult -= 0.5;
		
		if (generalResult < 0.0)
			generalResult = 0.0;
		
		
		generalResult *= 2.5;
		generalResult /= 5.0;
		
		System.out.println("Nota general (Sobre 2.5): " + generalResult);
		
		for (DrumSound drum: this.sounds) {
			if (drum.getDrumType() == typeOfDrum.Kicks)
				auxKicks.add(drum);
			
			else if(drum.getDrumType() == typeOfDrum.Snares)
				auxSnares.add(drum);
			
			else if(drum.getDrumType() == typeOfDrum.Claps)
				auxClaps.add(drum);
			
			for (int time: drum.getStartTimes())
				allTimes.add(time);
						
		}
		
		// TODO: PROBAR EVALUACION GLOBAL VS EVALUACION POR 4 BEATS.
		
		// VERSION 31 MAYO: SOBRE 30 SIN RESTAR POR 4 VECES ERROR EN DETALLADA (4-16-5)
		// VERSION 1 JUNIO: SOBRE 10 CON RESTA POR 4 VECES ERROR EN DETALLADA (2.5-6-1.5)
		
		detailedResult = this.detailedEvaluation(allTimes);
		divisionsResult = this.getMainDivisions(auxKicks, auxSnares, auxClaps);
		
		// Redondeamos la nota final a 3 decimales
		finalResult = (generalResult + divisionsResult + detailedResult);
		
		BigDecimal bd = new BigDecimal(finalResult);
	    bd = bd.setScale(2, RoundingMode.HALF_UP);
	    
	    System.out.println("\nResultado evaluacion: " + bd.doubleValue() + "\n");
		
		return bd.doubleValue();
	}
	
	private double getMainDivisions(ArrayList<DrumSound> kicks, ArrayList<DrumSound> snares, ArrayList<DrumSound> claps) {
		double divTimes = 0.0;
		double result = 0.0;
		
		if (kicks.size() == 0 && snares.size() == 0 && claps.size() == 0)
			result = 2.5;
		else {
			if (kicks.size() > 0) {
				if (snares.size() > 0) {
					// Check principal divisions with the snares

					for (DrumSound aux: kicks) {
						divTimes += aux.getNumPrincipalDivs();
					}
					
					for (DrumSound aux: snares) {
						divTimes += aux.getNumPrincipalDivs();
					}
				}else if (claps.size() > 0) {
					// Check principal divisions with the claps
					
					for (DrumSound aux: kicks) {
						divTimes += aux.getNumPrincipalDivs();
					}
					
					for (DrumSound aux: claps) {
						divTimes += aux.getNumPrincipalDivs();
					}
				}
			}else {
				if (snares.size() > 0) {
					// Check principal divisions with the snares
					
					for (DrumSound aux: snares) {
						divTimes += aux.getNumPrincipalDivs();
					}
				}else if (claps.size() > 0) {
					// Check principal divisions with the claps
					
					for (DrumSound aux: claps) {
						divTimes += aux.getNumPrincipalDivs();
					}
				}
			}
			
			// The number of main Divisions will always be the number of bars*4.
			// As this is a very difficult property to accomplish, we multiply 
			// by 12,5 instead of 10.
			result = (divTimes*12.5)/(this.numberOfBars*4.0);
			
			if (result > 10.0) {
				result = 10.0;
			}
		}

		System.out.println("Nota divisiones (Sobre 1.5): " + (result/10.0)*1.5);
		
		return (result/10.0)*1.5;
	}
	
	private double detailedEvaluation(ArrayList<Integer> allTimes) {
		// Variable to store the result of the detailed evaluation
		double auxResult = 0.0;
		
		// Variable for overload in an exact time.
		double repetitions = 0.0;
		
		// Variables for overload evaluation.
		int barDuration = this.getDurationBeat()*4;
		int iteratorBars = 1;
		int numSoundsPerBar = 0;
		double overload = 0.0;
		
		// Variables for silence evaluation.
		int maxSilenceTime = Math.round(this.getDurationBeat()*2.5f);
		int actualTime = 0;
		int lastTime = 0;
		double silence = 0.0;
		
		for (Entry<Integer, Set<DrumSound>> entry : this.soundsOrdered.entrySet()) {
			
			if (entry.getValue().size() > 3) {
				repetitions += 1.0;
			}
			
			numSoundsPerBar += 1;
			actualTime = entry.getKey();
			
			if ((actualTime - lastTime) >= maxSilenceTime) {
				silence += 1.0;
			}
			
			lastTime = entry.getKey();
			
			if (entry.getKey() >= barDuration) {
				barDuration += barDuration/iteratorBars;
				iteratorBars += 1;
								
				if (numSoundsPerBar >= 7)
					overload += 1.0;
				
				numSoundsPerBar = 0;
			}	
		}
		
		// We penalize one more time for every 4 fails.
		repetitions += ((int) repetitions/4);
		silence += ((int) silence/4);
		overload += ((int) overload/4);
		
		auxResult += ((1 - (repetitions/ (double) this.soundsOrdered.size())) * 10.0);
		auxResult += (1 - (silence/ (double) this.numberOfBars)) * 10.0;
		auxResult += (1 - (overload/ (double) this.numberOfBars)) * 10.0;
		
		System.out.println("Nota detallada (Sobre 6): " + ((auxResult/30.0)*6.0) + "\n	-> Penalizaciones: Repeticiones: " + ((int) repetitions/4) + ", Silencios: " + ((int) silence/4) + ", Sobrecarga: " + ((int) overload/4));
		
		return (auxResult/30.0)*6.0;
	}
		
	public void playSimulation() {
		int oldTime = 0;
		
		System.out.println("Simulation settings: ");
		System.out.println("    -Beats per minute: " + this.bpm + ".");
		System.out.println("    -Number of sounds: " + this.numberOfSounds + ".");
		System.out.println("    -Length of the beat: " + ((this.getDurationBeat()*4*this.numberOfBars)/1000) + " seconds aprox.");
		
		System.out.println("\nAnd the beat goes on!");
		
		for (Entry<Integer, Set<DrumSound>> entry : this.soundsOrdered.entrySet()) {
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
		
		System.out.println("\nBeat finished!\n");
	}
	
	/**
	 * Obtain the duration of a beat in the current simulation.
	 * It depends entirely on the bpm.
	 * 
	 * @return The time of a beat in the current simulation.
	 */
	public int getDurationBeat() {
		return Math.round(((1/bpm)*60)*1000);
	}
	
	/**
	 * Obtains an array with the path of all sounds in the current simulation.
	 * 
	 * @return The array of the paths to the sounds for the current simulation.
	 */
	public ArrayList<DrumSound> getSimulationSounds (){
		return this.sounds;
	}
}
