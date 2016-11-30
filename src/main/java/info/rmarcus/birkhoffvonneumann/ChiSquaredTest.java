package info.rmarcus.birkhoffvonneumann;

import java.util.Arrays;
import java.util.Random;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

import info.rmarcus.birkhoffvonneumann.exceptions.BVNException;

public class ChiSquaredTest {
	
	public static void testMethod(SamplingAlgorithm samp, int n, int samples) throws BVNException {
		double[][] bistoc = MatrixUtils.uniformBistoc(n);
		
		double[] classes = new double[factorial(bistoc.length)];
		
		BVNDecomposer bvn = new BVNDecomposer();
		bvn.setSamplingAlgorithm(samp);
		Random r = new Random(42);
		
		for (int i = 0; i < samples; i++) {
			int[] p = CoeffAndMatrix.asFlatPerm(bvn.sample(r, bistoc));
			classes[inv(p)]++;
		}
		
		// compute the test statitic, which is the sum of:
		// (obs - expected)^2 / expected
		double testStat = 0.0;
		for (int i = 0; i < classes.length; i++) {
			double expected = ((double)samples / (double)classes.length);
			testStat += Math.pow(classes[i] - expected, 2) / expected;
		}
				
		ChiSquaredDistribution chi = new ChiSquaredDistribution(classes.length - 1);
		System.out.println(Arrays.toString(classes));
		System.out.println(chi.cumulativeProbability(testStat) + "\t" + testStat);


		
		
		
	}
	
	private static int factorial(int n) {
		int toR = n;
		
		while (n --> 1)
			toR *= n;
		
		return toR;
	}
	
	public static void main(String[] args) throws BVNException {
		testMethod(SamplingAlgorithm.ENTROPY, 5, 2000000);
		testMethod(SamplingAlgorithm.GIBBS, 5, 2000000);
		testMethod(SamplingAlgorithm.UNIFORM, 5, 2000000);
		//testMethod(SamplingAlgorithm.METROPOLIS_HASTINGS, 5, 20000);

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
