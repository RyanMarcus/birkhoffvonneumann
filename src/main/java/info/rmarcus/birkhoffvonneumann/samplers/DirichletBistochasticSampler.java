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
 
package info.rmarcus.birkhoffvonneumann.samplers;

import org.apache.commons.math3.distribution.GammaDistribution;

import info.rmarcus.birkhoffvonneumann.BVNUtils;
import info.rmarcus.birkhoffvonneumann.SinkhornBalancer;
import info.rmarcus.birkhoffvonneumann.exceptions.BVNException;
import info.rmarcus.birkhoffvonneumann.exceptions.BVNRuntimeException;

class DirichletBistochasticSampler implements BistochasticSampler {

	private GammaDistribution gamma;
	
	DirichletBistochasticSampler() {
		gamma = new GammaDistribution(1.0, 1.0);
	}
	
	@Override
	public double[][] sample(int n) {
		double[][] toR = new double[n][n];
		
		for (int row = 0; row < n; row++) {
			for (int col = 0; col < n; col++) {
				toR[row][col] = gamma.sample();
			}
		}
		
		// no need to normalize each row because that's the first thing the Sinkhorn
		// balancer will do
		try {
			SinkhornBalancer.balance(toR);
			BVNUtils.checkMatrixInput(toR);
		} catch (BVNException e) {
			throw new BVNRuntimeException("Gamma distribution produced negative values or non-square error");
		}
		
		return toR;
	}

}
