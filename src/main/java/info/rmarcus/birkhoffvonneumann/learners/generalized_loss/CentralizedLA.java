package info.rmarcus.birkhoffvonneumann.learners.generalized_loss;

import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.function.ToDoubleFunction;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jdt.annotation.Nullable;

import info.rmarcus.NullUtils;
import info.rmarcus.birkhoffvonneumann.BVNDecomposer;
import info.rmarcus.birkhoffvonneumann.CoeffAndMatrix;
import info.rmarcus.birkhoffvonneumann.SamplingAlgorithm;
import info.rmarcus.birkhoffvonneumann.CoeffAndMatrix.Swap;
import info.rmarcus.birkhoffvonneumann.exceptions.BVNException;
import info.rmarcus.birkhoffvonneumann.exceptions.BVNRuntimeException;

public class CentralizedLA implements PermutationLearner {
	
	private static final Logger l = Logger.getLogger(CentralizedLA.class.getName());

	private double[][] w;
	private double learningRate;
	private Random r = new Random(30);
	private double@Nullable[][] best = null;
	private double bestVal = Double.MIN_VALUE;
	private BVNDecomposer bvn;
	private ToDoubleFunction<double[][]> loss;

	public CentralizedLA(int numItems, double learningRate, SamplingAlgorithm algo, ToDoubleFunction<double[][]> loss) {
		w = new double[numItems][numItems];
		this.learningRate = learningRate;
		this.bvn = new BVNDecomposer();
		this.bvn.setSamplingAlgorithm(algo);
		this.loss = loss;

		// initialize our random guess where all permutations are equally likely
		for (int i = 0; i < w.length; i++)
			for (int j = 0; j < w[i].length; j++)
				w[i][j] = 1.0 / ((double)numItems); 
	}

	public void iterate() {
		try {
			double[][] sample = bvn.sample(r, w);
			double reward = 1.0 - loss.applyAsDouble(sample);
			Set<Swap> swaps = CoeffAndMatrix.asSwaps(sample);
			
			if (bestVal < reward) {
				best = sample;
				bestVal = reward;
			}
			
			// new value for selected = old value + alpha * (1 - reward) * (1 - old value)
			// new value for other = old value - alpha * (1 - reward) * old value
			for (Swap s : swaps) {
				int row = s.getOriginalPosition();
				int selected = s.getNewPosition();
				
				for (int i = 0; i < w[row].length; i++) {
					// apply the approp. update rule
					if (i == selected) {
						w[row][i] = w[row][i] + learningRate * (1 - reward) * (1 - w[row][i]);
					} else {
						w[row][i] = w[row][i] - learningRate * (1 - reward) * w[row][i];
					}
				}
			}
			
		} catch (BVNException e) {
			l.log(Level.WARNING, "sampling failed in iterate()", e);
			System.out.println(Arrays.deepToString(w));
			return;
		}
	}

	public double[][] getBest() {
		return NullUtils.orThrow(best, () -> new BVNRuntimeException("Cannot get best without performing at least one iteration!"));
	}
	
	public static void main(String[] args) throws BVNException {
	//	final double[] toSort = new double[] {5, 1, 8, 3, 9};
		final double[] toSort = new double[] {10, 9, 8, 7, 6, 5, 4, 3, 2, 1};

		ToDoubleFunction<double[][]> lossFunc = (d -> {
			Set<Swap> swaps = CoeffAndMatrix.asSwaps(d);
			double[] sortedAs = new double[toSort.length];

			for (Swap s : swaps) {
				sortedAs[s.getNewPosition()] = toSort[s.getOriginalPosition()];
			}

			double collector = 0.0;
			for (int i = 0; i < sortedAs.length; i++) {
				collector += (i+1) * sortedAs[i];
			}
			return collector / 385.0;
		});



		CentralizedLA search = new CentralizedLA(toSort.length, 0.1, SamplingAlgorithm.ENTROPY, lossFunc);
		for (int i = 0; i < 10000; i++) {
			search.iterate();
		}

		for (double[] row : search.w) {
			for (double itm : row) {
				System.out.printf("%.2f\t", itm);
			}
			System.out.println();
		}
		System.out.println();
		System.out.println();
		System.out.println();

		System.out.println(CoeffAndMatrix.asSwaps(search.getBest()));
		System.out.println(lossFunc.applyAsDouble(search.getBest()));
	}
}

