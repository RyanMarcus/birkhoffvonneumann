package info.rmarcus.birkhoffvonneumann;

public enum SamplingAlgorithm {	
	DECOMPOSITION, ENTROPY, GIBBS, METROPOLIS_HASTINGS;
	
	static int getBurnIn() {
		return 200;
	}
}
