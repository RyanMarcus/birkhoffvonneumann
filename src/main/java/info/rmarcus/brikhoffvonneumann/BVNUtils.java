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

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import info.rmarcus.brikhoffvonneumann.exceptions.BVNException;
import info.rmarcus.brikhoffvonneumann.exceptions.BVNNonBistochasticMatrixException;
import info.rmarcus.brikhoffvonneumann.exceptions.BVNNonSquareMatrixException;
import info.rmarcus.brikhoffvonneumann.learners.PermELearn;

class BVNUtils {
	private static final Logger l = Logger.getLogger(PermELearn.class.getName());

	private BVNUtils() {
		
	}
	
	static void checkSquare(double[][] matrix) throws BVNNonSquareMatrixException {
		// check to make sure the matrix is square
		int matrixHeight = matrix.length;
		if (Arrays.stream(matrix).anyMatch(row -> row.length != matrixHeight))
			throw new BVNNonSquareMatrixException();
	}
	
	static void checkMatrixInput(double[][] matrix) throws BVNNonSquareMatrixException, BVNNonBistochasticMatrixException {
		
		checkSquare(matrix);
		
		// check to make sure the matrix is bistochastic.
		// first, check the row sums.
		if (Arrays.stream(matrix)
				.map(row -> Arrays.stream(row).sum())
				.anyMatch(d -> Math.abs(1.0 - d) > BVNDecomposer.EPSILON))
			throw new BVNNonBistochasticMatrixException();
		
		// next, check the column sums
		if (IntStream.range(0, matrix.length).mapToDouble(i -> {
			double collector = 0.0;
			for (int j = 0; j < matrix[i].length; j++)
				collector += matrix[i][j];
			return collector;
		}).anyMatch(d -> Math.abs(1 - d) > BVNDecomposer.EPSILON))
			throw new BVNNonBistochasticMatrixException();
	}
	
	static boolean isBistochastic(double[][] matrix) {
		try {
			checkMatrixInput(matrix);
			return true;
		} catch (BVNException e) {
			l.log(Level.FINEST, "matrix was not bistochastic", e);
			return false;
		}
	}
	
	static void printMatrix(double[][] matrix) {
		for (double[] d : matrix) {
			System.out.println(Arrays.toString(d));
		}
		System.out.println("---");
	}
}
