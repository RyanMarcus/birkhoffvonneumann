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
import java.util.stream.IntStream;

import info.rmarcus.brikhoffvonneumann.exceptions.BVNNonSquareMatrixException;

public class SinkhornBalancer {
	private SinkhornBalancer() {
		
	}
	
	public static void balance(double[][] matrix) throws BVNNonSquareMatrixException {
		BVNUtils.checkSquare(matrix);
		
		while (!BVNUtils.isBistochastic(matrix)) {
				balanceRows(matrix);
				balanceCols(matrix);
		}
		
	}
	
	public static void normalize(double[][] matrix, double maxVal) {
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				min = Math.min(matrix[i][j], min);
				max = Math.max(matrix[i][j], max);
			}
		}
		
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				matrix[i][j] -= min;
				matrix[i][j] /= (max - min);
				matrix[i][j] *= maxVal;
			}
		}
	}
	
	private static void balanceRows(double[][] matrix) {
		double[] rowSums = Arrays.stream(matrix)
				.mapToDouble(row -> Arrays.stream(row).sum())
				.toArray();
		
		for (int row = 0; row < matrix.length; row++) {
			for (int col = 0; col < matrix[row].length; col++) {
				matrix[row][col] /= rowSums[row];
			}
		}
	}
	
	private static void balanceCols(double[][] matrix) {
		double[] colSums = IntStream.range(0, matrix[0].length)
		.mapToDouble(col -> {
			double accum = 0.0;
			for (int row = 0; row < matrix.length; row++) {
				accum += matrix[row][col];
			}
			return accum;
		}).toArray();
		
		for (int row = 0; row < matrix.length; row++) {
			for (int col = 0; col < matrix[row].length; col++) {
				matrix[row][col] /= colSums[col];
			}
		}
	}
}
