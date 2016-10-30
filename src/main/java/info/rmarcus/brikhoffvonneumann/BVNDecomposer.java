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
package info.rmarcus.brikhoffvonneumann;

import java.util.Iterator;

import info.rmarcus.brikhoffvonneumann.exceptions.BVNException;
import info.rmarcus.brikhoffvonneumann.exceptions.BVNNonSquareMatrixException;

/**
 * A class to produce Brikhoff von-Neumann decompositions of bistochastic matrices
 *
 */
public class BVNDecomposer {
	static final double EPSILON = 0.0001;

	private BVNDecomposer() {}
	
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
	public static Iterator<CoeffAndMatrix> decomposeBistocastic(double[][] matrix) throws BVNException {
		if (matrix == null) {
			throw new BVNNonSquareMatrixException();
		}
		BVNUtils.checkMatrixInput(matrix);
		return new BVNIterator(matrix);
	}
	
	/**
	 * Randomly samples a permutation from the decomposition. The parameter r should be
	 * a random value between 0 and 1.
	 * 
	 * @param r a random value between 0 and 1.
	 * @param matrix the matrix to decompose
	 * @return a sample permutation from the matrix
	 * @throws BVNException if the matrix is not square, bistochastic, or r is not between 0 and 1
	 */
	public static double[][] sample(double r, double[][] matrix) throws BVNException {
		if (r < 0.0 || r > 1.0)
			throw new BVNException("r must be between 0 and 1!");
		
		double rLeft = r;
		Iterator<CoeffAndMatrix> i = decomposeBistocastic(matrix);
		
		CoeffAndMatrix candidate;
		do {
			candidate = i.next();
			
			if (!i.hasNext())
				return candidate.matrix;
			
			rLeft -= candidate.coeff;
		} while (rLeft > 0);
		
		return candidate.matrix;
	}
	
	/**
	 * Gets the mean permutation from the weight matrix (the permutation with the largest
	 * coeff).
	 * 
	 * @param matrix the bistochastic weight matrix
	 * @return the permutation with the largest coeff
	 * @throws BVNException
	 */
	public static double[][] meanPermutation(double[][] matrix) throws BVNException {
		Iterator<CoeffAndMatrix> i = decomposeBistocastic(matrix);
		return StreamUtils.asStream(i)
				.max((a, b) -> (int)Math.signum(a.coeff - b.coeff))
				.map(c -> c.matrix)
				.orElse(null);
	}
	
	
}
