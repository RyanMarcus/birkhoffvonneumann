// { begin copyright } 
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
// { end copyright } 
package edu.brandeis.brikhoffvonneumann;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.IntStream;

import edu.brandeis.brikhoffvonneumann.exceptions.BVNException;
import edu.brandeis.brikhoffvonneumann.exceptions.BVNNonBistochasticMatrixException;
import edu.brandeis.brikhoffvonneumann.exceptions.BVNNonSquareMatrixException;

/**
 * A class to produce Brikhoff von-Neumann decompositions of bistochastic matrices
 * @author "Ryan Marcus <rcmarcus@brandeis.edu>"
 *
 */
public class BVNDecomposer {
	static final double EPSILON = 0.0001;

	/**
	 * Produces a decomposition. The input matrix must be square and bistochastic.
	 * 
	 * The sum of all the coefficients returned by the iterator will be one, and each coeff
	 * returned by the iterator will be between zero and one.
	 * 
	 * Each matrix returned by the iterator will be a permutation matrix (all entries either zero
	 * or one).
	 * 
	 * For a series of coefficients a, b, c... and matricies A, B, C, we know that:
	 * 
	 * aA + bB + cC ... = the original input matrix.
	 * 
	 * In other words, we return permutations that are a convex combination of the input matrix.
	 * 
	 * 
	 * @param matrix the input matrix
	 * @return an iterator over the permutations that compose the input matrix.
	 * @throws BVNException if the matrix is not square or if the matrix is not bistochastic
	 */
	public static Iterator<CoeffAndMatrix> decomposeBiStocastic(double[][] matrix) throws BVNException {
		checkMatrixInput(matrix);
		return new BVNIterator(matrix);
	}
	
	
	private static void checkMatrixInput(double[][] matrix) throws BVNNonSquareMatrixException, BVNNonBistochasticMatrixException {
		// check to make sure the matrix is square
		if (matrix == null) {
			throw new BVNNonSquareMatrixException();
		}
		
		int matrixHeight = matrix.length;
		if (Arrays.stream(matrix).anyMatch(row -> row.length != matrixHeight))
			throw new BVNNonSquareMatrixException();
		
		
		// check to make sure the matrix is bistochastic.
		// first, check the row sums.
		if (Arrays.stream(matrix)
				.map(row -> Arrays.stream(row).sum())
				.anyMatch(d -> Math.abs(1.0 - d) > EPSILON))
			throw new BVNNonBistochasticMatrixException();
		
		// next, check the column sums
		if (IntStream.range(0, matrix.length).mapToDouble(i -> {
			double collector = 0.0;
			for (int j = 0; j < matrix[i].length; j++)
				collector += matrix[i][j];
			return collector;
		}).anyMatch(d -> Math.abs(1 - d) > EPSILON))
			throw new BVNNonBistochasticMatrixException();
	}
	
}
