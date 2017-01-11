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
 
package info.rmarcus.birkhoffvonneumann;

import java.util.Arrays;
import java.util.stream.IntStream;

import info.rmarcus.birkhoffvonneumann.exceptions.BVNNonBistochasticMatrixException;
import info.rmarcus.birkhoffvonneumann.exceptions.BVNNonSquareMatrixException;

public class BVNUtils {

	//@SuppressWarnings("null")
	//private static final Logger l = Logger.getLogger(PermELearn.class.getName());

	private BVNUtils() {

	}

	static void checkSquare(double[][] matrix) throws BVNNonSquareMatrixException {
		// check to make sure the matrix is square
		int matrixHeight = matrix.length;
		if (Arrays.stream(matrix).anyMatch(row -> row.length != matrixHeight))
			throw new BVNNonSquareMatrixException();
	}

	public static void checkMatrixInput(double[][] matrix) throws BVNNonSquareMatrixException, BVNNonBistochasticMatrixException {

		checkSquare(matrix);

		if (!isBistochastic(matrix))
			throw new BVNNonBistochasticMatrixException();
	

	}

	static boolean isNonNeg(double[][] matrix) {
		// check for non-neg
		return !(Arrays.stream(matrix)
				.flatMapToDouble(d -> Arrays.stream(d))
				.anyMatch(p -> p < 0));
	}

	static boolean isBistochastic(double[][] matrix) {
		// check to make sure the matrix is bistochastic.
		// first, check the row sums.
		if (Arrays.stream(matrix)
				.map(row -> Arrays.stream(row).sum())
				.anyMatch(d -> Math.abs(1.0 - d) > BVNDecomposer.EPSILON))
			return false;

		// next, check the column sums
		if (IntStream.range(0, matrix.length).mapToDouble(i -> {
			double collector = 0.0;
			for (int j = 0; j < matrix[i].length; j++)
				collector += matrix[i][j];
			return collector;
		}).anyMatch(d -> Math.abs(1 - d) > BVNDecomposer.EPSILON))
			return false;;

		// check the individual values
		if (!isNonNeg(matrix))
			return false;
		
		return true;
	}


}
