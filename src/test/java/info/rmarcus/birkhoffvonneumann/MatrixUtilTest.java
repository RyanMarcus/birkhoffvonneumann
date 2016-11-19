package info.rmarcus.birkhoffvonneumann;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

import info.rmarcus.birkhoffvonneumann.BVNDecomposer;
import info.rmarcus.birkhoffvonneumann.MatrixUtils;

public class MatrixUtilTest {

	private static double twoByTwoPerm(double[][] d) {
		return d[0][0] * d[1][1] + d[0][1] * d[1][0];
	}

	private static double threeByThreePerm(double[][] inp) {
		double a = inp[0][0];
		double b = inp[0][1];
		double c = inp[0][2];
		double d = inp[1][0];
		double e = inp[1][1];
		double f = inp[1][2];
		double g = inp[2][0];
		double h = inp[2][1];
		double i = inp[2][2];

		return a*e*i + b*f*g + c*d*h + c*e*g 
				+ b*d*i + a*f*h;
	}

	@Test
	public void perm2x2Test() {
		double[][] d = new double[][] {
			{0.5, 0.5},
			{0.5, 0.5}
		};

		double p = MatrixUtils.permanent(d);

		assertEquals(twoByTwoPerm(d), p, BVNDecomposer.EPSILON);
	}

	@Test
	public void perm2x2x2Test() {
		double[][] d = new double[][] {
			{0.25, 0.75},
			{0.75, 0.25}
		};

		double p = MatrixUtils.permanent(d);

		assertEquals(twoByTwoPerm(d), p, BVNDecomposer.EPSILON);
	}

	@Test
	public void perm2x2x3Test() {
		double[][] d = new double[][] {
			{0.8, 0.2},
			{0.2, 0.8}
		};

		double p = MatrixUtils.permanent(d);

		assertEquals(twoByTwoPerm(d), p, BVNDecomposer.EPSILON);
	}

	@Test
	public void perm2x2x4Test() {
		Random r = new Random(50);

		for (int i = 0; i < 100; i++) {
			double[][] d = new double[][] {
				{r.nextDouble(), r.nextDouble()},
				{r.nextDouble(), r.nextDouble()}
			};

			double p = MatrixUtils.permanent(d);

			assertEquals(twoByTwoPerm(d), p, BVNDecomposer.EPSILON);
		}
	}

	@Test
	public void perm3x3Test() {
		double[][] d = new double[][] {
			{1./3., 1./3., 1./3.},
			{1./3., 1./3., 1./3.},
			{1./3., 1./3., 1./3.}
		};

		double p = MatrixUtils.permanent(d);

		assertEquals(threeByThreePerm(d), p, BVNDecomposer.EPSILON);
	}

	@Test
	public void perm3x3x2Test() {
		Random r = new Random(50);

		for (int i = 0; i < 100; i++) {
			double[][] d = new double[][] {
				{r.nextDouble(), r.nextDouble(), r.nextDouble()},
				{r.nextDouble(), r.nextDouble(), r.nextDouble()},
				{r.nextDouble(), r.nextDouble(), r.nextDouble()}
			};

			double p = MatrixUtils.permanent(d);

			assertEquals(threeByThreePerm(d), p, BVNDecomposer.EPSILON);
		}
	}

	
	@Test
	public void perm4x4Test() {
		double[][] d = new double[][] {
			{1, 1, 1, 1},
			{2, 1, 0, 0},
			{3, 0, 1, 0},
			{4, 0, 0, 1}
		};

		double p = MatrixUtils.permanent(d);

		assertEquals(10.0, p, BVNDecomposer.EPSILON);
	}

}
