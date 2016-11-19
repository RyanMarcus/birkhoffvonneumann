// < begin copyright > 
// Copyright Ryan Marcus 2016
// 
// This file is part of brikhoffvonneumann.
// 
// brikhoffvonneumann is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// brikhoffvonneumann is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with brikhoffvonneumann.  If not, see <http://www.gnu.org/licenses/>.
// 
// < end copyright > 
// < begin copyright > 
// Copyright Ryan Marcus 2016
// 
// This file is part of brikhoffvonneumann.
// 
// brikhoffvonneumann is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// brikhoffvonneumann is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with brikhoffvonneumann.  If not, see <http://www.gnu.org/licenses/>.
// 
// < end copyright > 

package info.rmarcus.birkhoffvonneumann;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.Test;

import info.rmarcus.NullUtils;
import info.rmarcus.birkhoffvonneumann.BVNDecomposer;
import info.rmarcus.birkhoffvonneumann.CoeffAndMatrix;
import info.rmarcus.birkhoffvonneumann.DecompositionType;
import info.rmarcus.birkhoffvonneumann.SinkhornBalancer;
import info.rmarcus.birkhoffvonneumann.CoeffAndMatrix.Swap;
import info.rmarcus.birkhoffvonneumann.exceptions.BVNException;
import info.rmarcus.birkhoffvonneumann.exceptions.BVNNonBistochasticMatrixException;
import info.rmarcus.birkhoffvonneumann.exceptions.BVNNonSquareMatrixException;
import info.rmarcus.birkhoffvonneumann.exceptions.BVNRuntimeException;

public class BVNDecomposerTest {

	public static Iterator<CoeffAndMatrix> decompose(double[][] m) throws BVNException {
		BVNDecomposer d = new BVNDecomposer(DecompositionType.BVN);
		return d.decomposeBistocastic(m);
	}
	
	public static Iterator<CoeffAndMatrix> decompose(double[][] m, DecompositionType dt) throws BVNException {
		BVNDecomposer d = new BVNDecomposer(dt);
		return d.decomposeBistocastic(m);
	}
	
	public static double[][] mean(double[][] m) throws BVNException {
		BVNDecomposer d = new BVNDecomposer(DecompositionType.BVN);
		return d.meanPermutation(m);
	}
	
	@Test(expected=BVNNonSquareMatrixException.class)
	public void shouldThrowOnNonSquareTest() throws BVNException {
		decompose(new double[][] {{0, 0, 0}});
	}

	@Test(expected=BVNNonSquareMatrixException.class)
	public void shouldThrowOnNonSquare2Test() throws BVNException {
		decompose(new double[][] {{0, 0, 0}, {0, 0, 0, 0}, {0,0,0} });
	}

	@Test(expected=BVNNonBistochasticMatrixException.class)
	public void shouldThrowOnNonBiTest() throws BVNException {
		decompose(new double[][] {{0, 0, 0}, {0, 0, 0}, {0,0,0} });
	}

	@Test(expected=BVNNonBistochasticMatrixException.class)
	public void shouldThrowOnNonBi2Test() throws BVNException {
		decompose(new double[][] {{0, 1, 0}, {1, 0, 0}, {0,0,0.8} });
	}
	
	@Test(expected=BVNNonBistochasticMatrixException.class)
	public void shouldThrowOnNonBi3Test() throws BVNException {
		decompose(new double[][] {{-1, 1, 1}, {1, 0, 0}, {1,0,0} });
	}

	@Test
	public void decompExpiresAfterOneTest() throws BVNException {
		Iterator<CoeffAndMatrix> i = decompose(new double[][] {{1, 0, 0}, {0, 1, 0}, {0,0,1} });

		assertTrue(i.hasNext());
		CoeffAndMatrix cam = NullUtils.orThrow(i.next(), () -> new BVNRuntimeException("assertion error!"));
		assertEquals(cam.coeff, 1.0, BVNDecomposer.EPSILON);

		double[] r1 = new double[] {1.0, 0.0, 0.0};
		double[] r2 = new double[] {0.0, 1.0, 0.0};
		double[] r3 = new double[] {0.0, 0.0, 1.0};

		assertArrayEquals(cam.matrix[0], r1, BVNDecomposer.EPSILON);
		assertArrayEquals(cam.matrix[1], r2, BVNDecomposer.EPSILON);
		assertArrayEquals(cam.matrix[2], r3, BVNDecomposer.EPSILON);

		assertFalse(i.hasNext());

		try {
			i.next();
			fail("should have thrown exception!");
		} catch (NoSuchElementException e) {
			// good!
		}
	}

	@Test
	public void decompExpiresAfterTwoTest() throws BVNException {
		Iterator<CoeffAndMatrix> i = decompose(new double[][] {
			{0.5, 0, 0.5},
			{0,   1, 0  },
			{0.5, 0, 0.5} 
			});

		assertTrue(i.hasNext());
		CoeffAndMatrix cam = NullUtils.orThrow(i.next(), () -> new BVNRuntimeException("assertion error!"));
		assertEquals(0.5, cam.coeff, BVNDecomposer.EPSILON);

		cam = NullUtils.orThrow(i.next(), () -> new BVNRuntimeException("assertion error!"));
		assertEquals(0.5, cam.coeff, BVNDecomposer.EPSILON);


		assertFalse(i.hasNext());
	}

	@Test
	public void coeffSumTest() throws BVNException {
		Iterator<CoeffAndMatrix> i = decompose(new double[][] {{1./3., 1./3., 1./3.}, {1./3., 1./3., 1./3.}, {1./3.,1./3.,1./3.} });
		double coeffSum = 0.0;

		while (i.hasNext()) {
			CoeffAndMatrix cam = NullUtils.orThrow(i.next(), () -> new BVNRuntimeException("assertion error!"));
			coeffSum += cam.coeff;
		}

		assertEquals(coeffSum, 1.0, BVNDecomposer.EPSILON);
	}

	@Test
	public void coeffSum2Test() throws BVNException {
		Iterator<CoeffAndMatrix> i = decompose(new double[][] {{1./4., 1./4., 1./4., 1./4.}, 
			{1./4., 1./4., 1./4., 1./4.}, 
			{1./4., 1./4., 1./4., 1./4.},
			{1./4., 1./4., 1./4., 1./4.}});

		double coeffSum = 0.0;

		while (i.hasNext()) {
			CoeffAndMatrix cam = NullUtils.orThrow(i.next(), () -> new BVNRuntimeException("assertion error!"));
			coeffSum += cam.coeff;
		}

		assertEquals(coeffSum, 1.0, BVNDecomposer.EPSILON);
	}

	@Test
	public void noDuplicatePermsTest() throws BVNException {
		double[][] m = new double[][] {
			{0.12386116124039533, 0.19789512012353908, 0.3564696062871973, 0.19789512012353905, 0.12386116124039531}, 
			{0.5243301492950436, 0.24001405039545726, 0.12386710429142027, 0.06876517675214192, 0.04303963958335231},
			{0.06876291027350388, 0.1098636913488036, 0.19789809256498114, 0.38346196138005817, 0.24000613960770473},
			{0.24000613960770475, 0.38346196138005806, 0.19789809256498112, 0.1098636913488036, 0.06876291027350388},
			{0.0430396395833523, 0.06876517675214192, 0.12386710429142027, 0.24001405039545726, 0.5243301492950437}
		};

		SinkhornBalancer.balance(m);
		Iterator<CoeffAndMatrix> i = decompose(m);
		Set<Set<Swap>> perms = new HashSet<>();

		while (i.hasNext()) {
			CoeffAndMatrix cm = NullUtils.orThrow(i.next(), () -> new BVNRuntimeException("assertion error!"));
			Set<Swap> asSwaps = cm.asSwaps();
			assertFalse(perms.contains(asSwaps));
			perms.add(asSwaps);
		}

	}
	
	@Test
	public void noDuplicatePermsGreedyTest() throws BVNException {
		double[][] m = new double[][] {
			{0.12386116124039533, 0.19789512012353908, 0.3564696062871973, 0.19789512012353905, 0.12386116124039531}, 
			{0.5243301492950436, 0.24001405039545726, 0.12386710429142027, 0.06876517675214192, 0.04303963958335231},
			{0.06876291027350388, 0.1098636913488036, 0.19789809256498114, 0.38346196138005817, 0.24000613960770473},
			{0.24000613960770475, 0.38346196138005806, 0.19789809256498112, 0.1098636913488036, 0.06876291027350388},
			{0.0430396395833523, 0.06876517675214192, 0.12386710429142027, 0.24001405039545726, 0.5243301492950437}
		};

		SinkhornBalancer.balance(m);
		Iterator<CoeffAndMatrix> i = decompose(m, DecompositionType.GREEDY);
		Set<Set<Swap>> perms = new HashSet<>();

		while (i.hasNext()) {
			CoeffAndMatrix cm = NullUtils.orThrow(i.next(), () -> new BVNRuntimeException("assertion error!"));
			Set<Swap> asSwaps = cm.asSwaps();
			assertFalse(perms.contains(asSwaps));
			perms.add(asSwaps);
		}

	}

	@Test
	public void allPermsValidTest() throws BVNException {
		double[][] m = new double[][] {
			{0.19792615205786793, 0.19999575588922414, 0.20407645656300846, 0.19999575588922414, 0.1979261520578679},
			{0.21218201028385858, 0.2039442351693745, 0.1979560753458031, 0.19399775745040237, 0.191990222338926},
			{0.19397807341132017, 0.19600639437728223, 0.20000569637269275, 0.20605585711371685, 0.2039235419080274},
			{0.20392354190802736, 0.2060558571137168, 0.2000056963726927, 0.1960063943772822, 0.1939780734113201},
			{0.19199022233892601, 0.19399775745040237, 0.1979560753458031, 0.2039442351693745, 0.21218201028385858}
		};
		
		SinkhornBalancer.balance(m);
		Iterator<CoeffAndMatrix> i = decompose(m);

		while (i.hasNext()) {
			CoeffAndMatrix cm = NullUtils.orThrow(i.next(), () -> new BVNRuntimeException("assertion error!"));
			// ensure each element has exactly one entry that is a 1
			
			for (double[] row : cm.matrix) {
				long numNonZero = Arrays.stream(row)
						.filter(d -> d > 0.01)
						.count();
				assertEquals(1, numNonZero);
			}
		}

	}
	
	@Test
	public void getMeanTest() throws BVNException {
		double[][] m = new double[][] {
			{0.19792615205786793, 0.19999575588922414, 0.20407645656300846, 0.19999575588922414, 0.1979261520578679},
			{0.21218201028385858, 0.2039442351693745, 0.1979560753458031, 0.19399775745040237, 0.191990222338926},
			{0.19397807341132017, 0.19600639437728223, 0.20000569637269275, 0.20605585711371685, 0.2039235419080274},
			{0.20392354190802736, 0.2060558571137168, 0.2000056963726927, 0.1960063943772822, 0.1939780734113201},
			{0.19199022233892601, 0.19399775745040237, 0.1979560753458031, 0.2039442351693745, 0.21218201028385858}
		};
		
		SinkhornBalancer.balance(m);
		double[][] mean = mean(m);
		
		assertArrayEquals(new double[] {0,0,1,0,0}, mean[0], 0.01);
		assertArrayEquals(new double[] {1,0,0,0,0}, mean[1], 0.01);
		assertArrayEquals(new double[] {0,0,0,1,0}, mean[2], 0.01);
		assertArrayEquals(new double[] {0,1,0,0,0}, mean[3], 0.01);
		assertArrayEquals(new double[] {0,0,0,0,1}, mean[4], 0.01);

	}
	
	@Test
	public void getMean2Test() throws BVNException {
		double[][] m = new double[][] {
			{1,0,0,0,0},
			{0,0,0,0,1},
			{0,1,0,0,0},
			{0,0,1,0,0},
			{0,0,0,1,0}
		};
		
		double[][] mean = mean(m);
		
		assertArrayEquals(new double[] {1,0,0,0,0}, mean[0], 0.01);
		assertArrayEquals(new double[] {0,0,0,0,1}, mean[1], 0.01);
		assertArrayEquals(new double[] {0,1,0,0,0}, mean[2], 0.01);
		assertArrayEquals(new double[] {0,0,1,0,0}, mean[3], 0.01);
		assertArrayEquals(new double[] {0,0,0,1,0}, mean[4], 0.01);

	}



}
