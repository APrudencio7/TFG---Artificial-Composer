package simulator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import simulator.dataEnumerators.typeOfDrum;
import simulator.soundConfigurator.DrumSound;

public class SimulationEvaluation {

	private double energy;
	private int nKicks;
	private int nSnares;
	private int nClaps;
	private int nHHC;
	private int nHHO;
	private int nCymbals;
	private int nPercs;
	
	public SimulationEvaluation () {
		this.energy = 0.0;
		this.nKicks = 0;
		this.nSnares = 0;
		this.nClaps = 0;
		this.nHHC = 0;
		this.nHHO = 0;
		this.nCymbals = 0;
		this.nPercs = 0;
	}
	
	public SimulationEvaluation (SimulationEvaluation eval) {
		this.energy = eval.getEnergy();
		this.nKicks = eval.getnKicks();
		this.nSnares = eval.getnSnares();
		this.nClaps = eval.getnClaps();
		this.nHHC = eval.getnHHC();
		this.nHHO = eval.getnHHO();
		this.nCymbals = eval.getnCymbals();
		this.nPercs = eval.getnPercs();
	}
	
	public void automatedEvaluation(SimulationConfigurator sim) {
		double finalResult = 0.0;
		double detailedResult = 0.0;
		double divisionsResult = 0.0;
		double generalResult = 0.0;

		
		// Start of the automated evaluation that will give us a 
		// mark between 0 and 10 based on the basic elements of
		// our beat.
		generalResult = this.generalEvaluation();		
		detailedResult = this.detailedEvaluation(sim);
		divisionsResult = this.divisionsEvaluation(sim);
		
		// Redondeamos la nota final a 3 decimales
		finalResult = (generalResult + divisionsResult + detailedResult);
		
		/*System.out.println("	- General evaluation: " + generalResult);
		System.out.println("	- Detailed evaluation: " + detailedResult);
		System.out.println("	- Divisions evaluation: " + divisionsResult + "\n");*/
		
		BigDecimal bd = new BigDecimal(finalResult);
	    this.energy = (bd.setScale(2, RoundingMode.HALF_UP)).doubleValue();
	    
	    // System.out.println("Evaluation result: " + bd.doubleValue() + "\n");
	}
	
	private double generalEvaluation() {
		double gRes = 0.0;
		
		if (nKicks > 0)
			gRes += 1;
		else
			gRes -= 0.125;
		
		if (nSnares > 0 || nClaps > 0)
			gRes += 1;
		else
			gRes -= 0.125;
		
		if (nHHC > 0)
			gRes += 1;
		else
			gRes -= 0.125;
		
		if (nHHO > 0)
			gRes += 1;
		else
			gRes -= 0.125;
		
		if (nKicks <= 2 && nSnares <= 2 && nClaps <= 2 && nHHC <= 2 && nHHO <= 2 && nCymbals <= 2 && nPercs <=2)
			gRes += 1;
		else
			gRes -= 0.5;
		
		if (gRes < 0.0)
			gRes = 0.0;
		
		return (gRes*2.5)/5.0;
	}

	private double divisionsEvaluation(SimulationConfigurator sim) {
		double divTimes = 0.0;
		double result = 0.0;
		ArrayList<DrumSound> auxKicks = new ArrayList<DrumSound>();
		ArrayList<DrumSound> auxSnares = new ArrayList<DrumSound>();
		ArrayList<DrumSound> auxClaps = new ArrayList<DrumSound>();
		
		for (DrumSound drum: sim.getSounds()) {
			if (drum.getDrumType() == typeOfDrum.Kicks)
				auxKicks.add(drum);
			
			else if(drum.getDrumType() == typeOfDrum.Snares)
				auxSnares.add(drum);
			
			else if(drum.getDrumType() == typeOfDrum.Claps)
				auxClaps.add(drum);
						
		}
		
		if (auxKicks.size() == 0 && auxSnares.size() == 0 && auxClaps.size() == 0)
			result = 2.5;
		else {
			if (auxKicks.size() > 0) {
				if (auxSnares.size() > 0) {
					// Check principal divisions with the snares

					for (DrumSound aux: auxKicks) {
						divTimes += aux.getNumPrincipalDivs();
					}
					
					for (DrumSound aux: auxSnares) {
						divTimes += aux.getNumPrincipalDivs();
					}
				}else if (auxClaps.size() > 0) {
					// Check principal divisions with the claps
					
					for (DrumSound aux: auxKicks) {
						divTimes += aux.getNumPrincipalDivs();
					}
					
					for (DrumSound aux: auxClaps) {
						divTimes += aux.getNumPrincipalDivs();
					}
				}
			}else {
				if (auxSnares.size() > 0) {
					// Check principal divisions with the snares
					
					for (DrumSound aux: auxSnares) {
						divTimes += aux.getNumPrincipalDivs();
					}
				}else if (auxClaps.size() > 0) {
					// Check principal divisions with the claps
					
					for (DrumSound aux: auxClaps) {
						divTimes += aux.getNumPrincipalDivs();
					}
				}
			}
			
			// The number of main Divisions will always be the number of bars*4.
			// As this is a very difficult property to accomplish, we multiply 
			// by 12,5 instead of 10.
			result = (divTimes*12.5)/(sim.getNumberOfBars()*4.0);
			
			if (result > 10.0) {
				result = 10.0;
			}
		}

		//System.out.println("Nota divisiones (Sobre 1.5): " + (result/10.0)*1.5);
		
		return (result/10.0)*1.5;
	}
	
	private double detailedEvaluation(SimulationConfigurator sim) {
		// Variable to store the result of the detailed evaluation
		double auxResult = 0.0;
		
		// Variable for overload in an exact time.
		double repetitions = 0.0;
		
		// Variables for overload evaluation.
		int barDuration = sim.getDurationBeat()*4;
		int iteratorBars = 1;
		int numSoundsPerBar = 0;
		double overload = 0.0;
		
		// Variables for silence evaluation.
		int maxSilenceTime = Math.round(sim.getDurationBeat()*2f);
		int actualTime = 0;
		int lastTime = 0;
		double silence = 0.0;
		
		for (Entry<Integer, List<DrumSound>> entry : sim.getSoundsOrdered().entrySet()) {
			
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
		
		// Problema con el tamanyo a veces
		if (sim.getSoundsOrdered().size() == 0) {
			return 0.0;
		}
		
		auxResult += ((1 - (repetitions/ (double) sim.getSoundsOrdered().size())) * 10.0);
		auxResult += (1 - (silence/ (double) sim.getNumberOfBars())) * 10.0;
		auxResult += (1 - (overload/ (double) sim.getNumberOfBars())) * 10.0;
		
		//System.out.println("Nota detallada (Sobre 6): " + ((auxResult/30.0)*6.0) + "\n	-> Penalizaciones: Repeticiones: " + ((int) repetitions/4) + ", Silencios: " + ((int) silence/4) + ", Sobrecarga: " + ((int) overload/4));
		
		return (auxResult/30.0)*6.0;
	}
	
	public void cleanSounds(SimulationConfigurator sim) {
		this.nKicks = 0;
		this.nSnares = 0;
		this.nClaps = 0;
		this.nHHC = 0;
		this.nHHO = 0;
		this.nCymbals = 0;
		this.nPercs = 0;
		
		sim.setSounds(new ArrayList<DrumSound>());
	}
	
	// Getters and Setters methods
	
	public double getEnergy() {
		return energy;
	}

	public void setEnergy(double energy) {
		this.energy = energy;
	}

	public int getnKicks() {
		return nKicks;
	}

	public void setnKicks(int nKicks) {
		this.nKicks = nKicks;
	}

	public int getnSnares() {
		return nSnares;
	}

	public void setnSnares(int nSnares) {
		this.nSnares = nSnares;
	}

	public int getnClaps() {
		return nClaps;
	}

	public void setnClaps(int nClaps) {
		this.nClaps = nClaps;
	}

	public int getnHHC() {
		return nHHC;
	}

	public void setnHHC(int nHHC) {
		this.nHHC = nHHC;
	}

	public int getnHHO() {
		return nHHO;
	}

	public void setnHHO(int nHHO) {
		this.nHHO = nHHO;
	}

	public int getnCymbals() {
		return nCymbals;
	}

	public void setnCymbals(int nCymbals) {
		this.nCymbals = nCymbals;
	}

	public int getnPercs() {
		return nPercs;
	}

	public void setnPercs(int nPercs) {
		this.nPercs = nPercs;
	}

	public int getNumSounds() {
		return nKicks + nSnares + nClaps + nHHC + nHHO + nCymbals + nPercs;
	}
	
}
