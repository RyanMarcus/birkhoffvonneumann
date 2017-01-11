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
 
package info.rmarcus.birkhoffvonneumann.polytope;

import java.util.Random;

import info.rmarcus.birkhoffvonneumann.MatrixUtils;
import info.rmarcus.birkhoffvonneumann.exceptions.BVNException;
import info.rmarcus.birkhoffvonneumann.exceptions.BVNRuntimeException;
import info.rmarcus.birkhoffvonneumann.samplers.BistochasticSampler;

public class PointLinearBirkhoffPolytope implements BirkhoffPolytope {

	private int n;
	private BistochasticSampler s;
	private double[][] currPoint;
	
	public PointLinearBirkhoffPolytope(int n, BistochasticSampler s) {
		this.n = n;
		this.s = s;
		currPoint = MatrixUtils.identity(n);
	}
	
	@Override
	public void setCurrentPoint(double[][] d) throws BVNException {
		this.currPoint = d;
	}

	@Override
	public double[][] getCurrentPoint() {
		return currPoint;
	}

	@Override
	public double[] getRandomDirection(Random r) {
		double[][] bistoc = s.sample(n);
		MatrixUtils.subtract(bistoc, bistoc, currPoint);
		return MatrixUtils.flatten(bistoc);
	}

	@Override
	public void movePoint(double[] direction, double inc) {
		if (inc < 0 || inc >= 1) {
			throw new BVNRuntimeException("Increment inc must be 0 <= inc < 1");
		}
		
		double[][] tmp = new double[n][n];
		MatrixUtils.multiply(tmp, direction, inc);
		MatrixUtils.add(currPoint, currPoint, tmp);
		
		// this check is too slow...
//		try {
//			BVNUtils.checkMatrixInput(currPoint);
//		} catch (BVNNonSquareMatrixException | BVNNonBistochasticMatrixException e) {
//			throw new BVNRuntimeException("New matrix was not a bistochastic!");
//		}
	}
	
	@Override
	public String toString() {
		return "point-linear";
	}

}
