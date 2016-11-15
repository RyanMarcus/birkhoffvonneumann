package info.rmarcus.brikhoffvonneumann;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.DoubleUnaryOperator;

import org.eclipse.jdt.annotation.NonNull;

import info.rmarcus.NullUtils;
import info.rmarcus.brikhoffvonneumann.exceptions.BVNRuntimeException;

public class MatrixUtils {
	public static void multiply(double[][] dest, double[][] a, double[][] b) {
		for (int i = 0; i < dest.length; i++) {
			for (int j = 0; j < dest[i].length; j++) {
				dest[i][j] = a[i][j] * b[i][j];
			}
		}
	}

	public static void multiply(double[][] dest, double[][] a, double b) {
		for (int i = 0; i < dest.length; i++) {
			for (int j = 0; j < dest[i].length; j++) {
				dest[i][j] = a[i][j] * b;
			}
		}
	}


	public static void add(double[][] dest, double[][] a, double[][] b) {
		for (int i = 0; i < dest.length; i++) {
			for (int j = 0; j < dest[i].length; j++) {
				dest[i][j] = a[i][j] + b[i][j];
			}
		}
	}

	public static void apply(double[][] dest, double[][] a, DoubleUnaryOperator f) {
		for (int i = 0; i < dest.length; i++) {
			for (int j = 0; j < dest[i].length; j++) {
				dest[i][j] = f.applyAsDouble(a[i][j]);
			}
		}
	}

	public static double permanent(double[][] input) {
		int n = input.length;

		double collector = 0.0;
		Iterator<BitSet> i = bitStringsOfSize(n);
		while (i.hasNext()) {
			BitSet nxt = NullUtils.orThrow(i.next(), () -> new BVNRuntimeException("Iterator returned null!"));
			if (nxt.cardinality() == 0)
				continue;
			
			int mult = (int)Math.pow(-1, nxt.cardinality());
			double accum = 1.0;

			for (int row = 0; row < n; row++) {
				double x = 0;
				for (int col = 0; col < n; col++) {
					if (!nxt.get(col))
						continue;
					
					x += input[row][col];
				}

				accum *= x;
			}
			
			collector += mult * accum;
		}
		
		int mult = (int)Math.pow(-1, n);
		return mult * collector;

	}

	private static Iterator<BitSet> bitStringsOfSize(int n) {
		return new Iterator<BitSet>() {
			private BigInteger b = new BigInteger("0");
			@Override
			public boolean hasNext() {
				return b.compareTo((new BigInteger("2")).pow(n)) < 0;
			}

			@Override
			public @NonNull BitSet next() {
				byte[] bytes = b.toByteArray();
				BitSet toR = BitSet.valueOf(bytes);
				BigInteger nxt = b.add(new BigInteger("1"));
				if (nxt == null || toR == null) {
					throw new NoSuchElementException("Unable to increment big int");
				}
				b = nxt;
				return toR;
			}

		};
	}

	public static void main(String[] args) {
		System.out.println(permanent(new double[][] {
				{0.5, 0.5},
				{0.5, 0.5}
		}));	
	}
}
