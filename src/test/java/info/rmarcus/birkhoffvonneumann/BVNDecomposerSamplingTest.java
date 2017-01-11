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
