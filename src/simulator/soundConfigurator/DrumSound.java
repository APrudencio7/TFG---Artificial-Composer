package simulator.soundConfigurator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import kuusisto.tinysound.Sound;
import simulator.dataEnumerators.typeOfDrum;

public class DrumSound {

	private String soundPath;
	private Sound drumSound;
	private int numReps;
	private ArrayList<Integer> startTimes;
	private int numPrincipalDivs;
	private typeOfDrum drumType;
	
	public DrumSound(String path, int numRepetitions, typeOfDrum type, Sound sound) {
		this.soundPath = path;
		this.numReps = numRepetitions;
		this.startTimes = new ArrayList<Integer>();
		this.drumType = type;
		this.drumSound = sound;
		this.numPrincipalDivs = 0;
	}

	/**
	 * 
	 * Set the times when the drum sound will be played.
	 * 
	 * @param durBeat Duration of a beat in miliseconds. 
	 * @param numBars Number of bars that compose the song.
	 */
	public void setDrumTimes(int durBeat, int numBars) {
		Random rand = new Random();
		int i = 0;
		
		if (this.drumType == typeOfDrum.HiHatsClosed || this.drumType == typeOfDrum.HiHatsOpen) {
			// We set the times where the drum will be played.
			for (i=0; i < numBars; i++) {
				for (int k = 0; k < this.numReps; k++)
					this.startTimes.add(Math.round((durBeat*i*4) + (selectRandomBarMultiplyer(rand)*durBeat*4)));
			}			
		}else {
			// We set the times where the drum will be played.
			for (i=0; i < numBars; i++)
				this.startTimes.add(Math.round((durBeat*i*4) + (selectRandomBarMultiplyer(rand)*durBeat*4)));
		}		
	}
	
	private float selectRandomBarMultiplyer(Random rand) {
		List<Float> sepValuesMain = Arrays.asList(0f, 0.125f, 0.25f, 0.375f, 0.5f, 0.625f, 0.75f, 0.875f);
		
		List<Float> sepValuesReps = Arrays.asList(0f, 0.0625f, 0.125f, 0.1875f, 0.25f, 0.3125f, 0.375f, 0.4375f, 0.5f,
											  0.5625f, 0.625f, 0.6875f, 0.75f, 0.8125f, 0.875f, 0.9375f);
		float selectedValue;
		
		if (this.drumType == typeOfDrum.Kicks || this.drumType == typeOfDrum.Snares || this.drumType == typeOfDrum.Claps || this.drumType == typeOfDrum.Cymbals)
			selectedValue = sepValuesMain.get(rand.nextInt(sepValuesMain.size()));
		else
			selectedValue = sepValuesReps.get(rand.nextInt(sepValuesReps.size()));
		
		if(selectedValue == 0f || selectedValue == 0.25f || selectedValue == 0.5f || selectedValue == 0.75f) {
			this.numPrincipalDivs += 1;
		}
		
		return selectedValue;
	}

	public String getSoundPath() {
		return soundPath;
	}

	public ArrayList<Integer> getStartTimes() {
		return startTimes;
	}

	public int getNumReps() {
		return numReps;
	}
	
	public typeOfDrum getDrumType() {
		return drumType;
	}
	
	public Sound getDrumSound() {
		return drumSound;
	}
	
	public int getNumPrincipalDivs() {
		return this.numPrincipalDivs;
	}
}
