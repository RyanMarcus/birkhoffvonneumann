package info.rmarcus.birkhoffvonneumann.polytope;

import java.util.Random;

import info.rmarcus.birkhoffvonneumann.exceptions.BVNException;

public interface BirkhoffPolytope {

	public void setCurrentPoint(double[][] d) throws BVNException;

	public double[][] getCurrentPoint();

	public double[] getRandomDirection(Random r);

	public void movePoint(double[] direction, double inc);
	

}