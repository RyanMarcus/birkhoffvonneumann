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
package info.rmarcus.brikhoffvonneumann.learners;

import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import info.rmarcus.brikhoffvonneumann.BVNDecomposer;
import info.rmarcus.brikhoffvonneumann.SinkhornBalancer;
import info.rmarcus.brikhoffvonneumann.exceptions.BVNException;
import info.rmarcus.brikhoffvonneumann.exceptions.BVNNonSquareMatrixException;
import info.rmarcus.brikhoffvonneumann.exceptions.BVNRuntimeException;

public class PermELearn {

	private static final Logger l = Logger.getLogger(PermELearn.class.getName());

	private double[][] w;
	private double learningRate;
	private Random r = new Random(30);

	public PermELearn(int numItems, double learningRate) {
		w = new double[numItems][numItems];
		this.learningRate = learningRate;

		// initialize our random guess where all permutations are equally likely
		for (int i = 0; i < w.length; i++)
			for (int j = 0; j < w[i].length; j++)
				w[i][j] = 1.0 / ((double)numItems); 
	}

	public double[][] iterateAndUpdateWeights(double[][] lossMatrix) {
		checkLossMatrix(lossMatrix);
		try {
			// create a sample from our current weight matrix
			double[][] sample = BVNDecomposer.sample(r.nextDouble(), w);
			updateWeights(lossMatrix);

			return sample;
		} catch (BVNException e) {
			l.log(Level.WARNING, "Error in iterateAndUpdateWeights", e);
			throw new BVNRuntimeException("error while sampling matrix: " + e.getMessage());
		}

	}
	
	public double[][] getPreferredPositions() {
		double[][] toR = new double[w.length][w[0].length];
		
		for (int row = 0; row < w.length; row++) {
			int bestIdx = 0;
			for (int col = 1; col < w[row].length; col++) {
				bestIdx = w[row][col] > w[row][bestIdx] ? col : bestIdx;
			}
			
			toR[row][bestIdx] = 1.0;
		}
		
		return toR;
		
	}
	
	public double[][] getMeanPermutation() {
		try {
			return BVNDecomposer.meanPermutation(w);
		} catch (BVNException e) {
			l.log(Level.WARNING, "Error in getMeanPermutation", e);
			throw new BVNRuntimeException("error while getting mean permutation: " + e.getMessage());
		}
	}

	public void updateWeights(double[][] lossMatrix) {
		checkLossMatrix(lossMatrix);
		for (int i = 0; i < w.length; i++)
			for (int j = 0; j < w.length; j++)
				w[i][j] = Math.exp(-1.0 * learningRate * lossMatrix[i][j]);
		
		try {
			SinkhornBalancer.balance(w);
		} catch (BVNNonSquareMatrixException e) {
			l.log(Level.WARNING, "Error in updateWeights", e);
			throw new BVNRuntimeException("error while updating weight matrix: " + e.getMessage());
		}
	}
	
	public double[][] getWeights() {
		return w;
	}

	private void checkLossMatrix(double[][] lossMatrix) {
		if (lossMatrix.length != w.length)
			throw new BVNRuntimeException("Loss matrix must be an NxN matrix where N is the number of items being permuted");

		for (int i = 0; i < w.length; i++)
			if (lossMatrix[i].length != w[i].length)
				throw new BVNRuntimeException("Loss matrix must be an NxN matrix where N is the number of items being permuted");
	}
	
	public static double calculateLoss(double[][] weights, double[][] lossMatrix) {		
		double col = 0.0;
		for (int i = 0; i < weights.length; i++)
			for (int j = 0; j < weights[i].length; j++)
				col += weights[i][j] * lossMatrix[i][j];
		
		return col;
	}

	public static void main(String[] args) {
		double[] toSort = new double[] {5, 1, 8, 3, 9};
		double[][] lossMatrix = new double[5][5];
		
		for (int i = 0; i < lossMatrix.length; i++) {
			for (int j = 0; j < lossMatrix[i].length; j++) {
				lossMatrix[i][j] = toSort[i] * (1.0 / (j+1));
			}
		}
		
		
		SinkhornBalancer.normalize(lossMatrix, 1);

		System.out.println(Arrays.deepToString(lossMatrix));
		
		PermELearn pel = new PermELearn(toSort.length, 0.5);
		for (int i = 0; i < 100; i++) {
			pel.iterateAndUpdateWeights(lossMatrix);
		}
		
		System.out.println(Arrays.deepToString(pel.getMeanPermutation()));
	}


}
