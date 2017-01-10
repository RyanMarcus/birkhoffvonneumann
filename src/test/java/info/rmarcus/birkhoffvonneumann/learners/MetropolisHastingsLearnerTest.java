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
