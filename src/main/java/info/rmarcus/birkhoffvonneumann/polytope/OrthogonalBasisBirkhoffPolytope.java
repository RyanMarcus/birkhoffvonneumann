package info.rmarcus.birkhoffvonneumann.polytope;

import java.util.Arrays;
import java.util.Random;

import org.eclipse.jdt.annotation.NonNull;

import info.rmarcus.birkhoffvonneumann.MatrixUtils;
import info.rmarcus.birkhoffvonneumann.exceptions.BVNException;

/**
 * Represents the birkhoff polytope using orthogonal Fourier basis 
 * 
 *
 */
public class OrthogonalBasisBirkhoffPolytope implements BirkhoffPolytope {

	private int numItems;
	private int basisDimension;
	private int numEntries;

	public OrthogonalBasisBirkhoffPolytope(int n) {
		this.numItems = n;
		this.basisDimension = (int) Math.pow(this.numItems - 1, 2);
		this.numEntries = n * n;
	}

	private double[] getBasis(int idx) {
		double[] toR = new double[numEntries];
		int n = (idx / 2) + 1;
		for (int i = 0; i < toR.length; i++) {
			double quant = 2.0 * Math.PI * n * ((double)i) / (double)numEntries;
			toR[i] = (idx % 2 == 0 ? Math.cos(quant) : Math.sin(quant));
		}
		
		return MatrixUtils.normalize(toR);
	}

	@Override
	public void setCurrentPoint(double[][] d) throws BVNException {
		// TODO Auto-generated method stub

	}

	@Override
	public double[][] getCurrentPoint() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[] getRandomDirection(Random r) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void movePoint(double[] direction, double inc) {
		// TODO Auto-generated method stub

	}
	
	public static void main(String[] args) {
		OrthogonalBasisBirkhoffPolytope obbp = new OrthogonalBasisBirkhoffPolytope(3);
		for (int i = 0; i < obbp.basisDimension; i++) {
			System.out.println(Arrays.toString(obbp.getBasis(i)));
		}
		
	}


}
