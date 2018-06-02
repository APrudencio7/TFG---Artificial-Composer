package simulator.dataEnumerators;

import java.util.Random;

public enum typeOfDrum {
	
	Kicks,
	Snares,
	Claps,
	HiHatsClosed,
	HiHatsOpen,
	Percussion,
	Cymbals;
	
	public static typeOfDrum getRandomDrum(Random random) {
		return values()[random.nextInt(values().length)];
	}

}
