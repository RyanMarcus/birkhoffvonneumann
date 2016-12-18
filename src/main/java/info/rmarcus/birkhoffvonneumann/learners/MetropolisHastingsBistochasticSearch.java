package info.rmarcus.birkhoffvonneumann.learners;

import java.util.Random;
import java.util.function.ToDoubleFunction;

import org.apache.commons.math3.distribution.BetaDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

import info.rmarcus.birkhoffvonneumann.MatrixUtils;
import info.rmarcus.birkhoffvonneumann.exceptions.BVNException;
import info.rmarcus.birkhoffvonneumann.exceptions.BVNRuntimeException;
import info.rmarcus.birkhoffvonneumann.polytope.BirkhoffPolytope;
import info.rmarcus.birkhoffvonneumann.polytope.VertexCurveBirkhoffPolytope;

public class MetropolisHastingsBistochasticSearch {
	
	private ToDoubleFunction<double[][]> loss;
	
	private double bestLoss = Double.POSITIVE_INFINITY;
	private double currentLoss;
	private double[][] bestPerm;
	
	private RealDistribution distanceDistrib;
	
	private BirkhoffPolytope bp;
	
	private Random r = new Random(32);
	
	public MetropolisHastingsBistochasticSearch(int n, ToDoubleFunction<double[][]> loss) {
		this.loss = loss;
		bestPerm = MatrixUtils.uniformBistoc(n);
		
		distanceDistrib = new BetaDistribution(1.0, 3.0);
		
		currentLoss = 1.0 / loss.applyAsDouble(bestPerm);
		//bp = new RectangleBirkhoffPolytope(n);
		//bp = new PointLinearBirkhoffPolytope(n, BistochasticSampler.dirichletSampler());
		//bp = new TranspositionBirkhoffPolytope(n);
		bp = new VertexCurveBirkhoffPolytope(n);
	}
	
	public double[][] getBestPerm() {
		return bestPerm;
	}
	
	public void iterate() {
		//MatrixUtils.printMatrix(bp.getCurrentPoint());
		
		double[] dir = bp.getRandomDirection(r);
		double[][] current = bp.getCurrentPoint();
		double moveBy = r.nextDouble();//1.0 - distanceDistrib.sample();
		bp.movePoint(dir, moveBy);
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
