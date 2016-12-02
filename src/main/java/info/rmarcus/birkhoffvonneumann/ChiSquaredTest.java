package info.rmarcus.birkhoffvonneumann;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.commons.math3.stat.inference.ChiSquareTest;

import info.rmarcus.birkhoffvonneumann.CoeffAndMatrix.Swap;
import info.rmarcus.birkhoffvonneumann.exceptions.BVNException;

public class ChiSquaredTest {
	
	public static void testMethod(SamplingAlgorithm samp, int n, int samples) throws BVNException {
		double[][] bistoc = MatrixUtils.uniformBistoc(n);
		
		long[] classes = new long[factorial(bistoc.length)];
		
		BVNDecomposer bvn = new BVNDecomposer();
		bvn.setSamplingAlgorithm(samp);
		Random r = new Random(42);
		
		for (int i = 0; i < samples; i++) {
			int[] p = CoeffAndMatrix.asFlatPerm(bvn.sample(r, bistoc));
			classes[inv(p)]++;
		}
		
		double[] expected = new double[classes.length];
		for (int i = 0; i < expected.length; i++)
			expected[i] = (double)classes.length / (double) samples;
		
		ChiSquareTest chiT = new ChiSquareTest();
		double testStat = chiT.chiSquare(expected, classes);
				
		ChiSquaredDistribution chi = new ChiSquaredDistribution(classes.length - 1);
		System.out.println(Arrays.toString(classes));
		System.out.println(chi.cumulativeProbability(testStat) + "\t" + testStat);
		
	}
	
	public static void testMethodEachPos(SamplingAlgorithm samp, int n, int samples) throws BVNException {
		double[][] bistoc = MatrixUtils.uniformBistoc(n);
		
		long[] classes = new long[bistoc.length * bistoc.length];
		
		BVNDecomposer bvn = new BVNDecomposer();
		bvn.setSamplingAlgorithm(samp);
		Random r = new Random(42);
		
		for (int i = 0; i < samples; i++) {
			Collection<Swap> p = CoeffAndMatrix.asSwaps(bvn.sample(r, bistoc));
			for (Swap s : p) {
				classes[s.getOriginalPosition() * bistoc.length + s.getNewPosition()]++;
			}
		}
		
		double[] expected = new double[classes.length];
		for (int i = 0; i < expected.length; i++)
			expected[i] = (double)classes.length / (double) samples;
		
		ChiSquareTest chiT = new ChiSquareTest();
		double testStat = chiT.chiSquareTest(expected, classes);
				
		System.out.println(testStat + " " + Arrays.toString(classes));
		
	}
	
	private static int factorial(int n) {
		int toR = n;
		
		while (n --> 1)
			toR *= n;
		
		return toR;
	}
	
	public static void main(String[] args) throws BVNException {
		testMethodEachPos(SamplingAlgorithm.ENTROPY, 10, 2000);
		testMethodEachPos(SamplingAlgorithm.GIBBS, 10, 2000);
		testMethodEachPos(SamplingAlgorithm.UNIFORM, 10, 2000);
		testMethodEachPos(SamplingAlgorithm.METROPOLIS_HASTINGS, 10, 2000);
	}
	
	// http://stackoverflow.com/questions/1506078/fast-permutation-number-permutation-mapping-algorithms
	public static int[] perm(int n, int k){
	    int i, ind, m=k;
	    int[] permuted = new int[n];
	    int[] elems = new int[n];

	    for(i=0;i<n;i++) elems[i]=i;

	    for(i=0;i<n;i++)
	    {
	            ind=m%(n-i);
	            m=m/(n-i);
	            permuted[i]=elems[ind];
	            elems[ind]=elems[n-i-1];
	    }

	    return permuted;
	}

	public static int inv(int[] perm) {
	    int i, k=0, m=1;
	    int n=perm.length;
	    int[] pos = new int[n];
	    int[] elems = new int[n];

	    for(i=0;i<n;i++) {pos[i]=i; elems[i]=i;}

	    for(i=0;i<n-1;i++)
	    {
	            k+=m*pos[perm[i]];
	            m=m*(n-i);
	            pos[elems[n-i-1]]=pos[perm[i]];
	            elems[pos[perm[i]]]=elems[n-i-1];
	    }

	    return k;
	}
}
