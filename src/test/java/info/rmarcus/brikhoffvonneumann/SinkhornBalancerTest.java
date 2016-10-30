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

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import info.rmarcus.brikhoffvonneumann.exceptions.BVNNonBistochasticMatrixException;
import info.rmarcus.brikhoffvonneumann.exceptions.BVNNonSquareMatrixException;

public class SinkhornBalancerTest {

	@Test
	public void test1() throws BVNNonSquareMatrixException, BVNNonBistochasticMatrixException {
		double[][] matrix = new double[][] { {3, 2, 1}, {2, 1, 3}, {1, 2, 3} };
		SinkhornBalancer.balance(matrix);
		
		BVNUtils.checkMatrixInput(matrix);
		
		assertTrue(matrix[0][0] > matrix[0][1]);
		assertTrue(matrix[0][1] > matrix[0][2]);
		
		assertTrue(matrix[1][0] > matrix[1][1]);
		assertTrue(matrix[1][1] < matrix[1][2]);
		
		assertTrue(matrix[2][0] < matrix[2][1]);
		assertTrue(matrix[2][1] < matrix[2][2]);
	}

}
