package info.rmarcus.birkhoffvonneumann;

import java.util.Random;

import info.rmarcus.birkhoffvonneumann.exceptions.BVNException;

class MetropolisHastings {

	public static double[][] generateSample(Random r, double[][] matrix) throws BVNException {
		// first, pull out any old permutation
		BVNDecomposer bvn = new BVNDecomposer();
		bvn.setSamplingAlgorithm(SamplingAlgorithm.ENTROPY);
		double[][] toR = bvn.sample(r, matrix);
		
		double currQ = getPropDensity(toR, matrix);
		for (int i = 0; i < SamplingAlgorithm.getBurnIn(); i++) {
			double[][] proposed = MatrixUtils.clone(toR);
			getNeighbor(r, proposed);
			
			double nextQ = getPropDensity(proposed, matrix);
			double alpha = nextQ / currQ;
			
			if (alpha > 1.0 || r.nextDouble() > alpha) {
				toR = proposed;
				currQ = nextQ;
			}
		}
		
		return toR;
		
	}
	
	private static double getPropDensity(double[][] perm, double[][] matrix) {
		double collector = 1.0;
		
		for (int i = 0; i < perm.length; i++) {
			for (int j = 0; j < perm[i].length; j++) {
				if (perm[i][j] >= 1.0)
					collector *= matrix[i][j];
			}
		}
		
		return collector;
	}
	
	private static void getNeighbor(Random r, double[][] toModify) {
		// pick a random transposition uniformally and apply it to the matrix.
		// we just need two row indexes that are not identical.
		int[] transposition = r.ints(0, toModify.length).distinct().limit(2).toArray();
		double[] tmp = toModify[transposition[0]];
		toModify[transposition[0]] = toModify[transposition[1]];
		toModify[transposition[1]] = tmp;
	}

}
