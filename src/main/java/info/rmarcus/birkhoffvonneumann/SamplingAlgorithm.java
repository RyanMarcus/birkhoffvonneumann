package info.rmarcus.birkhoffvonneumann;

public enum SamplingAlgorithm {	
	DECOMPOSITION, ENTROPY, GIBBS, METROPOLIS_HASTINGS, UNIFORM;
	
	static int getBurnIn() {
		return 500;
	}
}
