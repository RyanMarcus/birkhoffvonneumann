package info.rmarcus.birkhoffvonneumann.learners;

import java.util.Random;
import java.util.function.ToDoubleFunction;

import info.rmarcus.birkhoffvonneumann.BVNDecomposer;
import info.rmarcus.birkhoffvonneumann.CoeffAndMatrix;
import info.rmarcus.birkhoffvonneumann.MatrixUtils;
import info.rmarcus.birkhoffvonneumann.SamplingAlgorithm;
import info.rmarcus.birkhoffvonneumann.exceptions.BVNException;

public class MetropolisHastingsPermutationSearch {
	private static final int SAMPLES_PER_MATRIX = 10;
	
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
		try {
			double collector = 0.0;
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
		final int sortDim = 20;
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


		System.out.println(CoeffAndMatrix.asSwaps(search.getBest()));
		System.out.println(lossFunc.applyAsDouble(search.getBest()));
	}
}
