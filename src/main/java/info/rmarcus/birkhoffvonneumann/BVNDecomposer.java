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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import info.rmarcus.NullUtils;
import info.rmarcus.birkhoffvonneumann.exceptions.BVNException;
import info.rmarcus.birkhoffvonneumann.exceptions.BVNNonSquareMatrixException;
import info.rmarcus.birkhoffvonneumann.exceptions.BVNRuntimeException;

/**
 * A class to produce Brikhoff von-Neumann decompositions of bistochastic matrices
 *
 */
public class BVNDecomposer {
	static final double EPSILON = 0.00001;
	private final DecompositionType type;
	private SamplingAlgorithm sampling;

	public BVNDecomposer() {
		type = DecompositionType.BVN;
		sampling = SamplingAlgorithm.DECOMPOSITION;
	}

	public BVNDecomposer(DecompositionType type) {
		this.type = type;
		sampling = SamplingAlgorithm.DECOMPOSITION;
	}



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
	public Iterator<@NonNull CoeffAndMatrix> decomposeBistocastic(double@Nullable[][] matrix) throws BVNException {
		if (matrix == null) {
			throw new BVNNonSquareMatrixException();
		}
		BVNUtils.checkMatrixInput(matrix);
		return new BVNIterator(matrix, type);
	}

	/**
	 * Randomly samples a permutation from the decomposition.
	 * 
	 * @param r a random number generator
	 * @param matrix the matrix to decompose
	 * @return a sample permutation from the matrix
	 * @throws BVNException if the matrix is not square, bistochastic, or r is not between 0 and 1
	 */
	public double[][] sample(Random r, double[][] matrix) throws BVNException {
		BVNUtils.checkMatrixInput(matrix);
		switch (sampling) {
		case DECOMPOSITION:
			return sampleFromDecomposition(r, matrix);
		case ENTROPY:
			return sampleFromEntropyMethod(r, matrix);
		case GIBBS:
			return sampleFromGibbsMethod(r, matrix);
		case METROPOLIS_HASTINGS:
			return sampleFromMetropolisHastingsMethod(r, matrix);
		default:
			throw new BVNException("No support for selected sampling method: " + sampling);

		}

	}

	private double[][] sampleFromMetropolisHastingsMethod(Random r, double[][] matrix) throws BVNException {
		return MetropolisHastings.generateSample(r, matrix);
	}

	private double[][] sampleFromGibbsMethod(Random r, double[][] matrix) throws BVNException {
		double[][] toR = new double[matrix.length][matrix.length];

		List<Integer> rowOrder = IntStream.range(0, matrix.length)
				.mapToObj(i -> i)
				.collect(Collectors.toCollection(() -> new ArrayList<Integer>()));
		
		Collections.shuffle(rowOrder);
		
		boolean[] removedCols = new boolean[matrix.length];
		
		for (Integer selectedRow : rowOrder) {
			// we now have the index of the row. Calculate
			// the remaining mass from the columns still available
			double remainingMass = 0.0;
			for (int col = 0; col < matrix.length; col++) {
				if (removedCols[col])
					continue;
				
				remainingMass += matrix[selectedRow][col];
			}
			
			// roll a random number between 0 and 1 and select accordingly
			double v = r.nextDouble();
			int bestColIdx = -1;
			for (int col = 0; col < matrix.length; col++) {
				if (removedCols[col])
					continue;
				
				v -= matrix[selectedRow][col] / remainingMass;
				if (v <= 0) {
					bestColIdx = col;
					break;
				}
			}
			
			if (bestColIdx == -1)
				throw new BVNException("Unable to sample a row up to mass " + v);
			
			removedCols[bestColIdx] = true;
						
			toR[selectedRow][bestColIdx] = 1.0;
		}
		
		return toR;
	}

	private double[][] sampleFromEntropyMethod(Random r, double[][] matrix) throws BVNException {
		double[][] toR = new double[matrix.length][matrix.length];

		Set<Integer> removedRows = new HashSet<>();
		Set<Integer> removedCols = new HashSet<>();
		while(removedRows.size() != matrix.length) {
			// find the entropy of each row
			double[] ent = new double[matrix.length];
			for (int row = 0; row < matrix.length; row++) {
				if (removedRows.contains(row))
					continue;
				
				for (int col = 0; col < matrix.length; col++) {
					if (removedCols.contains(col))
						continue;
					
					ent[row] += matrix[row][col] * Math.log(matrix[row][col]);
					
				}
			}
			
			// find the smallest entry in ent, excluding removed rows.
			int bestIdx = -1;
			for (int row = 0; row < matrix.length; row++) {
				if (removedRows.contains(row))
					continue;
				
				if (bestIdx == -1 || ent[bestIdx] > ent[row])
					bestIdx = row;
			}
			
			// we now have the index of the best row. Calculate
			// the remaining mass from the columns still available
			double remainingMass = 0.0;
			for (int col = 0; col < matrix.length; col++) {
				if (removedCols.contains(col))
					continue;
				
				remainingMass += matrix[bestIdx][col];
			}
			
			// roll a random number between 0 and 1 and select accordingly
			double v = r.nextDouble();
			int bestColIdx = -1;
			for (int col = 0; col < matrix.length; col++) {
				if (removedCols.contains(col))
					continue;
				
				v -= matrix[bestIdx][col] / remainingMass;
				if (v <= 0) {
					bestColIdx = col;
					break;
				}
			}
			
			if (bestColIdx == -1)
				throw new BVNException("Unable to sample a row up to mass " + v);
						
			removedRows.add(bestIdx);
			removedCols.add(bestColIdx);
			toR[bestIdx][bestColIdx] = 1.0;
		}
		
		return toR;
	}

	private double[][] sampleFromDecomposition(Random r, double[][] matrix) throws BVNException {
		Iterator<CoeffAndMatrix> i = sampleUntil(r.nextDouble(), matrix);
		@Nullable CoeffAndMatrix cam = null;
		while (i.hasNext())
			cam = i.next();

		if (cam == null) 
			throw new BVNException("Could not sample up to " + r + " density.");

		return cam.matrix;
	}

	private Iterator<CoeffAndMatrix> sampleUntil(double r, double[][] matrix) throws BVNException {
		if (r < 0.0 || r > 1.0)
			throw new BVNException("r must be between 0 and 1!");

		return new Iterator<CoeffAndMatrix>() {
			double left = r;
			final Iterator<CoeffAndMatrix> i = decomposeBistocastic(matrix);


			@Override
			public boolean hasNext() {
				return left > 0;
			}

			@Override
			public CoeffAndMatrix next() {
				CoeffAndMatrix toR = NullUtils.orThrow(i.next(), () -> new BVNRuntimeException("Got a null value from the decomposition iterator!"));
				left -= toR.coeff;
				return toR;
			}

		};


	}

	/**
	 * Gets the mean permutation from the weight matrix (the permutation with the largest
	 * coeff).
	 * 
	 * @param matrix the bistochastic weight matrix
	 * @return the permutation with the largest coeff
	 * @throws BVNException
	 */
	public double[][] meanPermutation(double[][] matrix) throws BVNException {
		return ((BVNIterator)decomposeBistocastic(matrix)).getMean();
	}

	/**
	 * Sets the sampling algorithm of this decomposer
	 * @param sa the sampling algorithm
	 */
	public void setSamplingAlgorithm(SamplingAlgorithm sa) {
		this.sampling = sa;
	}



}
