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
