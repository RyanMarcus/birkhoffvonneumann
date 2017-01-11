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

import static org.junit.Assert.*;

import java.util.function.ToDoubleFunction;

import org.junit.Test;

import info.rmarcus.birkhoffvonneumann.CoeffAndMatrix;
import info.rmarcus.birkhoffvonneumann.learners.generalized_loss.MetropolisHastingsPermutationSearch;

public class MetropolisHastingsLearnerTest {

	@Test
	public void test() {
		final int sortDim = 10;
		ToDoubleFunction<double[][]> lossFunc = (d -> {
			return CoeffAndMatrix.asSwaps(d)
					.stream()
					.mapToDouble(swap -> swap.getOriginalPosition() * swap.getNewPosition())
					.sum();
		});



		MetropolisHastingsPermutationSearch search = new MetropolisHastingsPermutationSearch(sortDim, lossFunc);
		for (int i = 0; i < 100000; i++) {
			search.iterate();
		}


		double finalLoss = lossFunc.applyAsDouble(search.getBest());

		// it should really find the optimal (120) but because of noise we will accept anything better than 123
		assertTrue(finalLoss < 123.0);

	}

}
