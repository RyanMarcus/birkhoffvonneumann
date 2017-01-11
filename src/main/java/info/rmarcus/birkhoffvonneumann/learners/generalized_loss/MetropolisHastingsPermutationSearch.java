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
 
package info.rmarcus.birkhoffvonneumann.learners.generalized_loss;

import java.util.Random;
import java.util.function.ToDoubleFunction;

import info.rmarcus.birkhoffvonneumann.BVNDecomposer;
import info.rmarcus.birkhoffvonneumann.CoeffAndMatrix;
import info.rmarcus.birkhoffvonneumann.MatrixUtils;
import info.rmarcus.birkhoffvonneumann.SamplingAlgorithm;
import info.rmarcus.birkhoffvonneumann.exceptions.BVNException;

public class MetropolisHastingsPermutationSearch implements PermutationLearner {
	private static final int SAMPLES_PER_MATRIX = 20;

	private ToDoubleFunction<double[][]> loss;
	private double bestLoss = Double.POSITIVE_INFINITY;
	private double[][] bestPerm;
	private Random r = new Random(42);

	private MetropolisHastingsBistochasticSearch mhbs;

	public MetropolisHastingsPermutationSearch(int n, ToDoubleFunction<double[][]> loss) {
		this.loss = loss;
		bestPerm = MatrixUtils.identity(n);
		mhbs = new MetropolisHastingsBistochasticSearch(n, this::loss);
	}

	private double loss(double[][] bistoc) {
		BVNDecomposer bvn = new BVNDecomposer();
		bvn.setSamplingAlgorithm(SamplingAlgorithm.GIBBS);
		double collector = 0.0;

		try {
			for (int i = 0; i < SAMPLES_PER_MATRIX; i++) {
				double[][] perm = bvn.sample(r, bistoc);
				double realizedLoss = this.loss.applyAsDouble(perm);

				if (realizedLoss < bestLoss) {
					bestLoss = realizedLoss;
					bestPerm = perm;
					System.out.println("New best: " + bestLoss);
				}
				collector += realizedLoss;
			}

			collector /= (double) SAMPLES_PER_MATRIX;
			return collector;
		} catch (BVNException e) {
			e.printStackTrace();
			return Double.POSITIVE_INFINITY;
		}

	}

	public void iterate() {
		mhbs.iterate();
	}

	public double[][] getBest() {
		return bestPerm;
	}

	public static void main(String[] args) {
		final int sortDim = 40;
		ToDoubleFunction<double[][]> lossFunc = (d -> {
			return CoeffAndMatrix.asSwaps(d)
					.stream()
					.mapToDouble(swap -> swap.getOriginalPosition() * swap.getNewPosition())
					.sum();
		});



		long t = System.currentTimeMillis();
		MetropolisHastingsPermutationSearch search = new MetropolisHastingsPermutationSearch(sortDim, lossFunc);
		for (int i = 0; i < 300000; i++) {
			search.iterate();
		}


		System.out.println(CoeffAndMatrix.asSwaps(search.getBest()));
		System.out.println(lossFunc.applyAsDouble(search.getBest()));

		System.out.println("Search time: " + (System.currentTimeMillis() - t));
	}
}
