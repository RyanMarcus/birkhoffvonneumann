// < begin copyright > 
// Copyright Ryan Marcus 2016
// 
// This file is part of brikhoffvonneumann.
// 
// brikhoffvonneumann is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// brikhoffvonneumann is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with brikhoffvonneumann.  If not, see <http://www.gnu.org/licenses/>.
// 
// < end copyright > 
// < begin copyright > 
// Copyright Ryan Marcus 2016
// 
// This file is part of brikhoffvonneumann.
// 
// brikhoffvonneumann is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// brikhoffvonneumann is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with brikhoffvonneumann.  If not, see <http://www.gnu.org/licenses/>.
// 
// < end copyright > 
package info.rmarcus.brikhoffvonneumann;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.HopcroftKarpBipartiteMatching;
import org.jgrapht.alg.KuhnMunkresMinimalWeightBipartitePerfectMatching;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.SimpleWeightedGraph;


public class BVNIterator implements Iterator<CoeffAndMatrix> {
	private final double[][] matrix;

	BVNIterator(double[][] matrix) {
		this.matrix = new double[matrix.length][matrix[0].length];
		for (int i = 0; i < matrix.length; i++)
			this.matrix[i] = Arrays.copyOf(matrix[i], matrix[i].length);
	}

	/**
	 * Returns the mean of the distribution represented by the weight matrix. the mean
	 * returned by this method is computed by finding a maximal weight matching across the initial
	 * weight matrix, and thus the mean returned here may not appear anywhere during iteration.
	 * 
	 * This method does not affect the status of the iterator, but it is affected by calls to
	 * next(). Specifically, getMean() returns the mean of the *remaining* permutations, i.e.
	 * the most likely permutation drawn from the weight matrix minus the permutations already
	 * drawn.
	 * 
	 * @return the mean permutation
	 */
	public double[][] getMean() {
		WeightedGraph<LabeledInt, DefaultWeightedEdge> g =
				new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

		// we will create a bipartite graph where the partitions are two sets
		// of nodes, 1,2,3 for each row and column

		// add 2x vertex for each value
		Set<LabeledInt> p1 = new HashSet<>();
		Set<LabeledInt> p2 = new HashSet<>();
		for (int row = 0; row < matrix.length; row++) {
			LabeledInt l1 = new LabeledInt(row, true);
			LabeledInt l2 = new LabeledInt(row, false);

			p1.add(l1); 
			p2.add(l2);
			g.addVertex(l1);
			g.addVertex(l2);
		}

		// there is an edge between vertex A and vertex B iff 
		// matrix[A][B] is non-zero. The weight of the edge is the
		// value of the cell in the matrix.
		for (int row = 0; row < matrix.length; row++) {
			for (int col = 0; col < matrix[row].length; col++) {
				// if the entry is zero, ignore it.
				if (Math.abs(matrix[row][col] - 0) <= BVNDecomposer.EPSILON)
					continue;

				DefaultWeightedEdge de = g.addEdge(new LabeledInt(row, true), new LabeledInt(col, false));
				// 1.0/weight because the algorithm searches for a minimal matching,
				// but we want a maximal matching and we know all the weights will be between 0 and 1
				g.setEdgeWeight(de, 1.0/matrix[row][col]); 
			}
		}

		Set<DefaultWeightedEdge> matching = 
				(new KuhnMunkresMinimalWeightBipartitePerfectMatching<LabeledInt, DefaultWeightedEdge>(g, new ArrayList<>(p1), new ArrayList<>(p2)))
				.getMatching();
		double[][] toR = new double[matrix.length][matrix.length];

		for (DefaultWeightedEdge de : matching) {
			int row = g.getEdgeSource(de).i;
			int col = g.getEdgeTarget(de).i;
			toR[row][col] = 1;
		}

		return toR;
	}

	@Override
	public boolean hasNext() {
		return findSmallestNonZero(matrix).isPresent();
	}

	@Override
	public CoeffAndMatrix next() {
		// we could do this with orElseThrow, but SonarLint doesn't recognize that
		Optional<Index> smallestNonZeroOpt = findSmallestNonZero(matrix);

		if (!smallestNonZeroOpt.isPresent())
			throw new NoSuchElementException();

		Index smallestNonZero = smallestNonZeroOpt.get();

		double coeff = matrix[smallestNonZero.row][smallestNonZero.col];
		double[][] perm = getNextPerm(smallestNonZero);
		System.out.println(smallestNonZero + ": " + Arrays.deepToString(matrix));

		// subtract coeff * perm from this.matrix
		for (int row = 0; row < matrix.length; row++) {
			for (int col = 0; col < matrix[row].length; col++) {
				matrix[row][col] -= coeff * perm[row][col];

				if (matrix[row][col] < BVNDecomposer.EPSILON)
					matrix[row][col] = 0;
			}
		}

		// ensure that we forced the value we selected to become zero
		matrix[smallestNonZero.row][smallestNonZero.col] = 0;

		return new CoeffAndMatrix(coeff, perm);
	}

	private double[][] getNextPerm(Index edgeToForce) {
		UndirectedGraph<LabeledInt, DefaultEdge> g =
				new SimpleGraph<>(DefaultEdge.class);

		// we will create a bipartite graph where the partitions are two sets
		// of nodes, 1,2,3 for each row and column

		// add 2x vertex for each value
		Set<LabeledInt> p1 = new HashSet<>();
		Set<LabeledInt> p2 = new HashSet<>();
		for (int row = 0; row < matrix.length; row++) {
			LabeledInt l1 = new LabeledInt(row, true);
			LabeledInt l2 = new LabeledInt(row, false);

			p1.add(l1); 
			p2.add(l2);
			g.addVertex(l1);
			g.addVertex(l2);
		}

		// there is an edge between vertex A and vertex B iff 
		// matrix[A][B] is non-zero. However, we want to force
		// the edge that corrosponds to the lowest weight in the matrix
		// to be selected in the matching.
		for (int row = 0; row < matrix.length; row++) {
			for (int col = 0; col < matrix[row].length; col++) {
				// if the entry is zero, ignore it.
				// only include the forced edge from the edgeToForce.row row.
				if ((Math.abs(matrix[row][col] - 0) <= BVNDecomposer.EPSILON)
						|| (row == edgeToForce.row && col != edgeToForce.col))
					continue;

				g.addEdge(new LabeledInt(row, true), new LabeledInt(col, false));

			}
		}

		Set<DefaultEdge> matching = (new HopcroftKarpBipartiteMatching<LabeledInt, DefaultEdge>(g, p1, p2)).getMatching();
		double[][] toR = new double[matrix.length][matrix.length];

		for (DefaultEdge de : matching) {
			int row = g.getEdgeSource(de).i;
			int col = g.getEdgeTarget(de).i;
			toR[row][col] = 1;
		}

		return toR;
	}

	private Optional<Index> findSmallestNonZero(double[][] matrix) {
		Map<Index, Double> nonZeros = new HashMap<>();
		for (int row = 0; row < matrix.length; row++) {
			for (int col = 0; col < matrix[row].length; col++) {
				if (Math.abs(matrix[row][col] - 0) > BVNDecomposer.EPSILON)
					nonZeros.put(new Index(row, col), matrix[row][col]);
			}
		}

		return nonZeros.keySet().stream()
				.min((a, b) -> (int)Math.signum(nonZeros.get(a) - nonZeros.get(b)));
	}

	private class LabeledInt {
		final int i;
		final boolean label;

		public LabeledInt(int i, boolean label) {
			this.i = i;
			this.label = label;
		}

		@Override
		public int hashCode() {
			return Integer.hashCode(i);
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof LabeledInt))
				return false;

			LabeledInt oi = (LabeledInt) o;
			return oi.i == i && oi.label == label;
		}

		@Override
		public String toString() {
			return "(" + i + ", " + label + ")";
		}
	}

	private class Index {
		public final int row;
		public final int col;
		public Index(int row, int col) {
			this.row = row;
			this.col = col;
		}

		@Override
		public String toString() {
			return "(" + row + "," + col + ")";
		}
	}
}
