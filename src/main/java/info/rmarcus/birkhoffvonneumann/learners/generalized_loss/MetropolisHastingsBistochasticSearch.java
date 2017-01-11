// < begin copyright > 
// Copyright Ryan Marcus 2017
// 
// This file is part of birkhoffvonneumann.
// 
// birkhoffvonneumann is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// birkhoffvonneumann is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with birkhoffvonneumann.  If not, see <http://www.gnu.org/licenses/>.
// 
// < end copyright > 
 
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
