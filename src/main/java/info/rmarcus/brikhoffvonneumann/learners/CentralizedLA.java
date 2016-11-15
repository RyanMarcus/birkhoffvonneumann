package info.rmarcus.brikhoffvonneumann.learners;

import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.function.ToDoubleFunction;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jdt.annotation.Nullable;

import info.rmarcus.NullUtils;
import info.rmarcus.brikhoffvonneumann.BVNDecomposer;
import info.rmarcus.brikhoffvonneumann.CoeffAndMatrix;
import info.rmarcus.brikhoffvonneumann.CoeffAndMatrix.Swap;
import info.rmarcus.brikhoffvonneumann.exceptions.BVNException;
import info.rmarcus.brikhoffvonneumann.exceptions.BVNRuntimeException;

public class CentralizedLA {

	
	@SuppressWarnings("null")
	private static final Logger l = Logger.getLogger(CentralizedLA.class.getName());

	private double[][] w;
	private double learningRate;
	private Random r = new Random(30);
	private double@Nullable[][] best = null;
	private double bestVal = Double.MAX_VALUE;

	public CentralizedLA(int numItems, double learningRate) {
		w = new double[numItems][numItems];
		this.learningRate = learningRate;

		// initialize our random guess where all permutations are equally likely
		for (int i = 0; i < w.length; i++)
			for (int j = 0; j < w[i].length; j++)
				w[i][j] = 1.0 / ((double)numItems); 
	}

	public void iterate(ToDoubleFunction<double[][]> lossFunc) {
		try {
			double[][] sample = BVNDecomposer.sample(r.nextDouble(), w);
			double reward = 1.0 - lossFunc.applyAsDouble(sample);
			Set<Swap> swaps = CoeffAndMatrix.asSwaps(sample);
			
			if (bestVal > reward)
				best = sample;
			
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



		CentralizedLA search = new CentralizedLA(toSort.length, 0.01);
		for (int i = 0; i < 1000; i++) {
			search.iterate(lossFunc);
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

