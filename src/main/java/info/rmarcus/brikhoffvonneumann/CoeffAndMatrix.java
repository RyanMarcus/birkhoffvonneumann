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

import java.util.HashSet;
import java.util.Set;

public class CoeffAndMatrix {
	public final double coeff;
	public final double[][] matrix;
	
	public CoeffAndMatrix(double coeff, double[][] matrix) {
		this.coeff = coeff;
		this.matrix = matrix;
	}
	
	public Set<Swap> asSwaps() {
		Set<Swap> toR = new HashSet<>();
		
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				if (matrix[i][j] > 0) {
					toR.add(new Swap(i, j));
					break;
				}
			}
		}
		
		return toR;
	}
	
	public class Swap {
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
		public boolean equals(Object o) {
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
