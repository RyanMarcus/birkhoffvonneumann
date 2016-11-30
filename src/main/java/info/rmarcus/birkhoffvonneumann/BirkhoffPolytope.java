package info.rmarcus.birkhoffvonneumann;

import java.util.Arrays;
import java.util.Random;

import info.rmarcus.NullUtils;
import info.rmarcus.birkhoffvonneumann.exceptions.BVNException;
import info.rmarcus.birkhoffvonneumann.exceptions.BVNRuntimeException;

public class BirkhoffPolytope {
	private int n;
	private double[][] point;

	public BirkhoffPolytope(int n) {
		this.n = n;

		point = new double[n][n];

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				point[i][j] = 1.0 / (double)n;
			}
		}
	}

	public int getN() {
		return n;
	}

	public void setCurrentPoint(double[][] d) throws BVNException {
		// this check was too expensive: BVNUtils.checkMatrixInput(d);

		if (d.length != n)
			throw new BVNException("Dimension of matrix for this polytope must be " + n + " but was " + d.length);

		this.point = d;
	}

	public double[][] getCurrentPoint() {
		return point;
	}

	public double[] getRandomDirection(Random r) {
		int[] p1 = MatrixUtils.randomPermutaitonSparse(r, n);
		int[] p2 = MatrixUtils.randomPermutaitonSparse(r, n);
	
		// to find the maximal coeffs for each permutation,
		// which is the minimal entry in the matrix.
		
		double alpha = Double.POSITIVE_INFINITY;
		double beta = Double.POSITIVE_INFINITY;
		for (int i = 0; i < n; i++) {
			alpha = Math.min(alpha, point[i][p1[i]]);
			beta = Math.min(beta, point[i][p2[i]]);
		}
		
		double u = Math.min(1.0 - alpha, beta);
		
		double[][] toR = new double[n][n];
		for (int i = 0; i < n; i++) {
			toR[i][p1[i]] += u;
			toR[i][p2[i]] -= u;
		}
		
//		MatrixUtils.multiply(p1, p1, u);
//		MatrixUtils.multiply(p2, p2, -1.0 * u);
//		MatrixUtils.add(p1, p1, p2);
		return NullUtils.orThrow(
				Arrays.stream(toR).flatMapToDouble(d -> Arrays.stream(d)).toArray(),
				() -> new BVNRuntimeException("could not flatten direction matrix"));
		
	}

	public double getDistanceFromPointToEdge(double[] direction) throws BVNException {

		if ((int)Math.sqrt(direction.length) != n)
			throw new BVNException("Direction of matrix for this polytope must be " + n + "^2 but was " + direction.length);

		double min = 0.0;
		double max = 1000.0;

		while (max - min > BVNDecomposer.EPSILON) {
			double midpoint = (max + min) / 2.0;
			double[][] tmp = new double[n][n];
			MatrixUtils.multiply(tmp, direction, midpoint);
			MatrixUtils.add(tmp, tmp, point);

			if (BVNUtils.isBistochastic(tmp)) {
				min = midpoint;
			} else {
				max = midpoint;
			}
		}

		return min;
	}

	public void movePoint(double[] direction, double inc) {
		if (inc < 0 || inc >= 1) {
			throw new BVNRuntimeException("Increment inc must be 0 <= inc < 1");
		}
		
		double[][] tmp = new double[n][n];
		MatrixUtils.multiply(tmp, direction, inc);
		MatrixUtils.add(point, point, tmp);
	}

	public static void main(String[] args) throws BVNException {
		Random r = new Random();

		BirkhoffPolytope bp = new BirkhoffPolytope(5);
		double[] direction = bp.getRandomDirection(r);

		System.out.println(Arrays.deepToString(bp.getCurrentPoint()));
		bp.movePoint(direction, 0.5);
		System.out.println(Arrays.deepToString(bp.getCurrentPoint()));

	}
}
