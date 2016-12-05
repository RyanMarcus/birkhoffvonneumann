package info.rmarcus.birkhoffvonneumann;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import info.rmarcus.birkhoffvonneumann.exceptions.BVNException;

@RunWith(Parameterized.class)
public class BVNDecomposerSamplingTest {

	@Parameters(name = "method: {0}")
	public static Object[] data() {
		return SamplingAlgorithm.values();
	}

	@Parameter
	public SamplingAlgorithm algo = SamplingAlgorithm.DECOMPOSITION;

	private Random r = new Random(30);

	@Test
	public void methodIsValid() throws BVNException {
		BVNDecomposer d = new BVNDecomposer();
		d.setSamplingAlgorithm(algo);
		double[][] perm = d.sample(r, new double[][] {
			{ 1./3., 1./3., 1./3. },
			{ 1./3., 1./3., 1./3. },
			{ 1./3., 1./3., 1./3. }
		});

		assertTrue(MatrixUtils.isPermutation(perm));

	}

	@Test
	public void methodIsValidRandom() throws BVNException {
		BVNDecomposer d = new BVNDecomposer();
		d.setSamplingAlgorithm(algo);

		for (int i = 0; i < 10; i++) {
			double[][] random = MatrixUtils.randomMatrix(r, 30);
			SinkhornBalancer.balance(random);
			double[][] perm = d.sample(r, random);

			assertTrue(MatrixUtils.isPermutation(perm));
		}

	}


}
