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
package info.rmarcus.birkhoffvonneumann.learners;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import info.rmarcus.birkhoffvonneumann.SinkhornBalancer;
import info.rmarcus.birkhoffvonneumann.learners.PermELearn;

public class PermELearnTest {

	@Test
	public void learnsSortedOrderTest() {
		double[] toSort = new double[] {5, 1, 8, 3, 9};
		double[][] lossMatrix;

		lossMatrix = new double[][] {
			{2, 1, 0, 1, 2},
			{0, 1, 2, 3, 4},
			{3, 2, 1, 0, 1},
			{1, 0, 1, 2, 3},
			{4, 3, 2, 1, 0}
		};

		SinkhornBalancer.normalize(lossMatrix, 1);

		
		PermELearn pel = new PermELearn(toSort.length, 0.05);
		for (int i = 0; i < 5; i++) {
			pel.updateWeights(lossMatrix);
		}

		double[][] mean = pel.getMeanPermutation();

		assertArrayEquals(new double[] {0,0,1,0,0}, mean[0], 0.01);
		assertArrayEquals(new double[] {1,0,0,0,0}, mean[1], 0.01);
		assertArrayEquals(new double[] {0,0,0,1,0}, mean[2], 0.01);
		assertArrayEquals(new double[] {0,1,0,0,0}, mean[3], 0.01);
		assertArrayEquals(new double[] {0,0,0,0,1}, mean[4], 0.01);

		assertEquals(0, PermELearn.calculateLoss(mean, lossMatrix), 0.01);
		
	}
	
	@Test
	public void learnsSortedOrderAltLossTest() {
		double[] toSort = new double[] {5, 1, 8, 3, 9};
		double[][] lossMatrix = new double[5][5];
		
		for (int i = 0; i < lossMatrix.length; i++) {
			for (int j = 0; j < lossMatrix[i].length; j++) {
				lossMatrix[i][j] = toSort[i] * (1.0/(j+1));
			}
		}
		
		SinkhornBalancer.normalize(lossMatrix, 1);
		
		PermELearn pel = new PermELearn(toSort.length, 0.05);
		for (int i = 0; i < 5; i++) {
			pel.updateWeights(lossMatrix);
		}
		

		double[][] mean = pel.getMeanPermutation();

		assertArrayEquals(new double[] {0,0,1,0,0}, mean[0], 0.01);
		assertArrayEquals(new double[] {1,0,0,0,0}, mean[1], 0.01);
		assertArrayEquals(new double[] {0,0,0,1,0}, mean[2], 0.01);
		assertArrayEquals(new double[] {0,1,0,0,0}, mean[3], 0.01);
		assertArrayEquals(new double[] {0,0,0,0,1}, mean[4], 0.01);
	}

}
