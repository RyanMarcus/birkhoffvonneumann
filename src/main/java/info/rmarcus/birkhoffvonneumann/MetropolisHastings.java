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

import java.util.Random;

import info.rmarcus.birkhoffvonneumann.exceptions.BVNException;

class MetropolisHastings {

	public static double[][] generateSample(Random r, double[][] matrix) throws BVNException {
		// first, pull out any old permutation
		double[][] toR = MatrixUtils.randomPermutation(r, matrix.length);
				
		double currQ = getPropDensity(toR, matrix);
		for (int i = 0; i < SamplingAlgorithm.getBurnIn(); i++) {
			double[][] proposed = MatrixUtils.clone(toR);
			getNeighbor(r, proposed);
			
			double nextQ = getPropDensity(proposed, matrix);
			double alpha = nextQ / currQ;
						
			if (alpha > 1.0 || r.nextDouble() > alpha) {
				toR = proposed;
				currQ = nextQ;
			}
		}
		
		return toR;
		
	}
	
	private static double getPropDensity(double[][] perm, double[][] matrix) {
		double collector = 1.0;
		
		for (int i = 0; i < perm.length; i++) {
			for (int j = 0; j < perm[i].length; j++) {
				if (perm[i][j] >= 1.0)
					collector *= matrix[i][j];
			}
		}
		
		return collector;
	}
	
	private static void getNeighbor(Random r, double[][] toModify) {
		// pick a random transposition uniformally and apply it to the matrix.
		// we just need two row indexes that are not identical.
		int[] transposition = r.ints(0, toModify.length).distinct().limit(2).toArray();
		double[] tmp = toModify[transposition[0]];
		toModify[transposition[0]] = toModify[transposition[1]];
		toModify[transposition[1]] = tmp;
	}
	
	public static void main(String[] args) throws BVNException {
		generateSample(new Random(), MatrixUtils.uniformBistoc(5));
	}

}
