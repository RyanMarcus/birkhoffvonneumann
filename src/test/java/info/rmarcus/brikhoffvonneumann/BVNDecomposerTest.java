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
 
package info.rmarcus.brikhoffvonneumann;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Test;

import info.rmarcus.brikhoffvonneumann.BVNDecomposer;
import info.rmarcus.brikhoffvonneumann.CoeffAndMatrix;
import info.rmarcus.brikhoffvonneumann.exceptions.BVNException;
import info.rmarcus.brikhoffvonneumann.exceptions.BVNNonBistochasticMatrixException;
import info.rmarcus.brikhoffvonneumann.exceptions.BVNNonSquareMatrixException;

public class BVNDecomposerTest {

	@Test(expected=BVNNonSquareMatrixException.class)
	public void shouldThrowOnNonSquare() throws BVNException {
		BVNDecomposer.decomposeBiStocastic(new double[][] {{0, 0, 0}});
	}

	@Test(expected=BVNNonSquareMatrixException.class)
	public void shouldThrowOnNonSquare2() throws BVNException {
		BVNDecomposer.decomposeBiStocastic(new double[][] {{0, 0, 0}, {0, 0, 0, 0}, {0,0,0} });
	}

	@Test(expected=BVNNonBistochasticMatrixException.class)
	public void shouldThrowOnNonBi() throws BVNException {
		BVNDecomposer.decomposeBiStocastic(new double[][] {{0, 0, 0}, {0, 0, 0}, {0,0,0} });
	}

	@Test(expected=BVNNonBistochasticMatrixException.class)
	public void shouldThrowOnNonBi2() throws BVNException {
		BVNDecomposer.decomposeBiStocastic(new double[][] {{0, 1, 0}, {1, 0, 0}, {0,0,0.8} });
	}

	@Test
	public void decompExpiresAfterOne() throws BVNException {
		Iterator<CoeffAndMatrix> i = BVNDecomposer.decomposeBiStocastic(new double[][] {{1, 0, 0}, {0, 1, 0}, {0,0,1} });

		assertTrue(i.hasNext());
		CoeffAndMatrix cam = i.next();
		assertEquals(cam.coeff, 1.0, BVNDecomposer.EPSILON);

		double[] r1 = new double[] {1.0, 0.0, 0.0};
		double[] r2 = new double[] {0.0, 1.0, 0.0};
		double[] r3 = new double[] {0.0, 0.0, 1.0};

		assertArrayEquals(cam.matrix[0], r1, BVNDecomposer.EPSILON);
		assertArrayEquals(cam.matrix[1], r2, BVNDecomposer.EPSILON);
		assertArrayEquals(cam.matrix[2], r3, BVNDecomposer.EPSILON);



		assertFalse(i.hasNext());
	}

	@Test
	public void decompExpiresAfterTwo() throws BVNException {
		Iterator<CoeffAndMatrix> i = BVNDecomposer.decomposeBiStocastic(new double[][] {{0.5, 0, 0.5}, {0, 1, 0}, {0.5,0,0.5} });

		assertTrue(i.hasNext());
		CoeffAndMatrix cam = i.next();
		assertEquals(cam.coeff, 0.5, BVNDecomposer.EPSILON);


		cam = i.next();
		assertEquals(cam.coeff, 0.5, BVNDecomposer.EPSILON);

		assertFalse(i.hasNext());
	}

	@Test
	public void coeffSum() throws BVNException {
		Iterator<CoeffAndMatrix> i = BVNDecomposer.decomposeBiStocastic(new double[][] {{1./3., 1./3., 1./3.}, {1./3., 1./3., 1./3.}, {1./3.,1./3.,1./3.} });
		double coeffSum = 0.0;

		while (i.hasNext()) {
			CoeffAndMatrix cam = i.next();
			coeffSum += cam.coeff;
		}

		assertEquals(coeffSum, 1.0, BVNDecomposer.EPSILON);
	}

	@Test
	public void coeffSum2() throws BVNException {
		Iterator<CoeffAndMatrix> i = BVNDecomposer.decomposeBiStocastic(new double[][] {{1./4., 1./4., 1./4., 1./4.}, 
			{1./4., 1./4., 1./4., 1./4.}, 
			{1./4., 1./4., 1./4., 1./4.},
			{1./4., 1./4., 1./4., 1./4.}});
		
		double coeffSum = 0.0;

		while (i.hasNext()) {
			CoeffAndMatrix cam = i.next();
			coeffSum += cam.coeff;
		}

		assertEquals(coeffSum, 1.0, BVNDecomposer.EPSILON);
	}



}
