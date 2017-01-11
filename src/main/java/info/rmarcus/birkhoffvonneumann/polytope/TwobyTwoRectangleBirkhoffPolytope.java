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

public class TwobyTwoRectangleBirkhoffPolytope implements BirkhoffPolytope {

	private int n;
	private double[][] currPoint;

	public TwobyTwoRectangleBirkhoffPolytope(int n) {
		this.n = n;
		currPoint = MatrixUtils.uniformBistoc(n);
	}

	@Override
	public void setCurrentPoint(double[][] d) throws BVNException {
		currPoint = d;
	}

	@Override
	public double[][] getCurrentPoint() {
		return currPoint;
	}

	@Override
	public double[] getRandomDirection(Random r) {
		int[] rows = r.ints(0, n).distinct().limit(2).toArray();
		int[] cols = r.ints(0, n).distinct().limit(2).toArray();

		
		double[][] dir = new double[n][n];
		
		double maxGive;
		double maxTake;
		double max;
		
		if (r.nextBoolean()) {
			maxGive = Math.min(1.0 - currPoint[rows[0]][cols[0]], 1.0 - currPoint[rows[1]][cols[1]]);
			maxTake = Math.min(currPoint[rows[0]][cols[1]], currPoint[rows[1]][cols[0]]);
			max = Math.max(maxGive, maxTake);
			
			dir[rows[0]][cols[0]] = max;
			dir[rows[0]][cols[1]] = -max;
			dir[rows[1]][cols[0]] = -max;
			dir[rows[1]][cols[1]] = max;
		} else {
			maxGive = Math.min(1.0 - currPoint[rows[1]][cols[0]], 1.0 - currPoint[rows[0]][cols[1]]);
			maxTake = Math.min(currPoint[rows[0]][cols[0]], currPoint[rows[1]][cols[1]]);
			max = Math.max(maxGive, maxTake);
			
			dir[rows[0]][cols[0]] = -max;
			dir[rows[0]][cols[1]] = max;
			dir[rows[1]][cols[0]] = max;
			dir[rows[1]][cols[1]] = -max;
		}
		
		return MatrixUtils.flatten(dir);
	
	}

	@Override
	public void movePoint(double[] direction, double inc) {
		if (inc < 0 || inc >= 1) {
			throw new BVNRuntimeException("Increment inc must be 0 <= inc < 1");
		}

		double[][] tmp = new double[n][n];
		MatrixUtils.multiply(tmp, direction, inc);
		MatrixUtils.add(currPoint, currPoint, tmp);

	}

}
