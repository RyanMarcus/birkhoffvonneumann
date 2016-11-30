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
package info.rmarcus.birkhoffvonneumann;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;

public class CoeffAndMatrix {
	public final double coeff;
	public final double[][] matrix;
	
	public CoeffAndMatrix(double coeff, double[][] matrix) {
		this.coeff = coeff;
		this.matrix = matrix;
	}
	
	public Set<Swap> asSwaps() {
		return CoeffAndMatrix.asSwaps(matrix);
	}
	
	public static int[] asFlatPerm(double[][] permutation) {
		int[] toR = new int[permutation.length];
		CoeffAndMatrix.asSwaps(permutation).stream()
			.forEach(s -> toR[s.origPos] = s.newPos);
		return toR;
	}
	
	public static double[][] fromFlatPerm(int[] perm) {
		double[][] toR = new double[perm.length][perm.length];
		for (int i = 0; i < perm.length; i++) {
			toR[i][perm[i]] = 1.0;
		}
		return toR;
	}
	
	public static Set<Swap> asSwaps(double[][] permutation) {
		Set<Swap> toR = new HashSet<>();
		
		for (int i = 0; i < permutation.length; i++) {
			for (int j = 0; j < permutation[i].length; j++) {
				if (permutation[i][j] > 0) {
					toR.add(new Swap(i, j));
					break;
				}
			}
		}
		
		return toR;
	}
	
	public static class Swap {
		private final int origPos;
		private final int newPos;
		private Swap(int o, int n) {
			this.origPos = o;
			this.newPos = n;
		}
		
		public int getOriginalPosition() {
			return origPos;
		}
		
		public int getNewPosition() {
			return newPos;
		}
		
		@Override
		public int hashCode() {
			return Integer.hashCode(origPos);
		}
		
		@Override
		public boolean equals(@Nullable Object o) {
			if (!(o instanceof Swap))
				return false;
			
			return this.origPos == ((Swap)o).getOriginalPosition() 
					&& this.newPos == ((Swap)o).getNewPosition();
		}
		
		@Override
		public String toString() {
			return "(" + getOriginalPosition() + " -> " + getNewPosition() + ")";
		}
	}
}
