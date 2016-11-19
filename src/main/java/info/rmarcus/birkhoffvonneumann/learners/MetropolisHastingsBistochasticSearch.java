package info.rmarcus.birkhoffvonneumann.learners;

import java.util.Random;
import java.util.function.ToDoubleFunction;

import info.rmarcus.birkhoffvonneumann.BirkhoffPolytope;
import info.rmarcus.birkhoffvonneumann.exceptions.BVNException;
import info.rmarcus.birkhoffvonneumann.exceptions.BVNRuntimeException;

public class MetropolisHastingsBistochasticSearch {
	
	private ToDoubleFunction<double[][]> loss;
	
	private double bestLoss = Double.POSITIVE_INFINITY;
	private double currentLoss;
	private double[][] bestPerm;
	
	private BirkhoffPolytope bp;
	
	private Random r = new Random(32);
	
	public MetropolisHastingsBistochasticSearch(int n, ToDoubleFunction<double[][]> loss) {
		this.loss = loss;
		bestPerm = new double[n][n];
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				bestPerm[i][j] = 1.0 / (double) n;
		
		currentLoss = 1.0 / loss.applyAsDouble(bestPerm);
		bp = new BirkhoffPolytope(n);
	}
	
	public double[][] getBestPerm() {
		return bestPerm;
	}
	
	public void iterate() {
		//MatrixUtils.printMatrix(bp.getCurrentPoint());
		
		double[] dir = bp.getRandomDirection(r);
		double[][] current = bp.getCurrentPoint();
		bp.movePoint(dir, r.nextDouble());
		double[][] proposed = bp.getCurrentPoint();
		
		double proposedLoss = 1.0 / loss.applyAsDouble(proposed);
		
		if (proposedLoss < bestLoss) {
			bestLoss = proposedLoss;
			bestPerm = proposed;
		}
		
		double ratio = proposedLoss / currentLoss;
				
		if (ratio >= 1.0) {
			// accept
			return;
		}
			
		// reject with probability = ratio
		if (r.nextDouble() > ratio) {
			// accept
			return;
		}

		// reject
		try {
			bp.setCurrentPoint(current);
		} catch (BVNException e) {
			throw new BVNRuntimeException("Matrix that was bistochastic is no longer!" + e);
		}
	}
	
	
}
