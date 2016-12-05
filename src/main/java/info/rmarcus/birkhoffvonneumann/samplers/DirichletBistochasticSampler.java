package info.rmarcus.birkhoffvonneumann.samplers;

import org.apache.commons.math3.distribution.GammaDistribution;

import info.rmarcus.birkhoffvonneumann.BVNUtils;
import info.rmarcus.birkhoffvonneumann.SinkhornBalancer;
import info.rmarcus.birkhoffvonneumann.exceptions.BVNException;
import info.rmarcus.birkhoffvonneumann.exceptions.BVNRuntimeException;

class DirichletBistochasticSampler implements BistochasticSampler {

	private GammaDistribution gamma;
	
	DirichletBistochasticSampler() {
		gamma = new GammaDistribution(1.0, 1.0);
	}
	
	@Override
	public double[][] sample(int n) {
		double[][] toR = new double[n][n];
		
		for (int row = 0; row < n; row++) {
			for (int col = 0; col < n; col++) {
				toR[row][col] = gamma.sample();
			}
		}
		
		// no need to normalize each row because that's the first thing the Sinkhorn
		// balancer will do
		try {
			SinkhornBalancer.balance(toR);
			BVNUtils.checkMatrixInput(toR);
		} catch (BVNException e) {
			throw new BVNRuntimeException("Gamma distribution produced negative values or non-square error");
		}
		
		return toR;
	}

}
