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
 
package info.rmarcus.birkhoffvonneumann.learners;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.function.ToDoubleFunction;

import org.junit.Test;

import info.rmarcus.birkhoffvonneumann.CoeffAndMatrix;
import info.rmarcus.birkhoffvonneumann.learners.generalized_loss.MHJointPermutationLearner;

public class MHJointPermutationLearnerTest {

	@Test
	public void test() {
		final int[] sortDims = new int[] {10, 12, 8};
		ToDoubleFunction<List<double[][]>> lossFunc = (perms -> {
			return perms.stream()
					.mapToDouble(d -> CoeffAndMatrix.asSwaps(d).stream()
							.mapToDouble(swap -> swap.getOriginalPosition() * swap.getNewPosition())
							.sum())
					
					.reduce((a, b) -> a * b).getAsDouble();
		});



		MHJointPermutationLearner search = new MHJointPermutationLearner(sortDims, lossFunc);
		for (int i = 0; i < 100000; i++) {
			search.iterate();
		}


		double finalLoss = lossFunc.applyAsDouble(search.getBest());

		for (double[][] perm : search.getBest()) {
			System.out.println(CoeffAndMatrix.asSwaps(perm));
		}
		
		// it won't find the optimal (probably, of course), but it should get close.
		assertTrue(finalLoss < 2258000.0);

	}

}
