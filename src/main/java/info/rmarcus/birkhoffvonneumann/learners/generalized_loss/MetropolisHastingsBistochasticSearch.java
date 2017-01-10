package info.rmarcus.birkhoffvonneumann.learners.generalized_loss;

import java.util.Random;
import java.util.function.ToDoubleFunction;

import info.rmarcus.birkhoffvonneumann.exceptions.BVNException;
import info.rmarcus.birkhoffvonneumann.exceptions.BVNRuntimeException;
import info.rmarcus.birkhoffvonneumann.polytope.BirkhoffPolytope;
import info.rmarcus.birkhoffvonneumann.polytope.VertexCurveBirkhoffPolytope;

public class MetropolisHastingsBistochasticSearch {
	
	private ToDoubleFunction<double[][]> loss;
	
	private double currentMass;
		
	private BirkhoffPolytope bp;
	
	private Random r = new Random(32);
	
	public MetropolisHastingsBistochasticSearch(int n, ToDoubleFunction<double[][]> loss) {
		this.loss = loss;
		bp = new VertexCurveBirkhoffPolytope(n);
		currentMass = 1.0 / loss.applyAsDouble(bp.getCurrentPoint());
	}
	
	
	public void iterate() {
		//MatrixUtils.printMatrix(bp.getCurrentPoint());
		
		double[] dir = bp.getRandomDirection(r);
		double[][] current = bp.getCurrentPoint();
		double moveBy = r.nextDouble();//1.0 - distanceDistrib.sample();
		bp.movePoint(dir, moveBy);
		double[][] proposed = bp.getCurrentPoint();
		
		double pmass = 1.0 / loss.applyAsDouble(proposed);
		
		
		double ratio = pmass / currentMass;
				
		if (ratio >= 1.0) {
			// accept
			currentMass = pmass;
			return;
		}
			
		// reject with probability = ratio
		if (r.nextDouble() > ratio) {
			// accept
			currentMass = pmass;
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
