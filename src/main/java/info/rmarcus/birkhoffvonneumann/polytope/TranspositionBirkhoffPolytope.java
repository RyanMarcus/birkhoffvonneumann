package info.rmarcus.birkhoffvonneumann.polytope;

import java.util.Random;

import info.rmarcus.birkhoffvonneumann.MatrixUtils;
import info.rmarcus.birkhoffvonneumann.exceptions.BVNException;
import info.rmarcus.birkhoffvonneumann.exceptions.BVNRuntimeException;

public class TranspositionBirkhoffPolytope implements BirkhoffPolytope {

	private int n;
	private double[][] currPoint;

	public TranspositionBirkhoffPolytope(int n) {
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
		// select a random coordinate in (n-1)^2 space
		int row = r.nextInt(n-1);
		int col = r.nextInt(n-1);

		// 50/50, use 1 as the first entry or -1 as the first entry
		if (r.nextBoolean()) {
			// figure out the max distance
			double maxGive = Math.min(1.0 - currPoint[row][col], 1.0 - currPoint[row+1][col+1]);
			double maxTake = Math.min(currPoint[row][col+1], currPoint[row+1][col]);
			double max = Math.min(maxTake, maxGive);

			// build the direction
			double[][] dir = new double[n][n];
			dir[row][col] = max;
			dir[row][col + 1] = -max;
			dir[row+1][col] = -max;
			dir[row+1][col+1] = max;

			return MatrixUtils.flatten(dir);
		} else {
			// figure out the max distance
			double maxGive = Math.min(1.0 - currPoint[row][col+1], 1.0 - currPoint[row+1][col]);
			double maxTake = Math.min(currPoint[row][col], currPoint[row+1][col+1]);
			double max = Math.min(maxTake, maxGive);

			// build the direction
			double[][] dir = new double[n][n];
			dir[row][col] = -max;
			dir[row][col + 1] = max;
			dir[row+1][col] = max;
			dir[row+1][col+1] = -max;

			return MatrixUtils.flatten(dir);
		}
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
