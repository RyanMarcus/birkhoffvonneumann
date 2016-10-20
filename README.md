# Brikhoff-von Neumann Decomposition

This package provides an implementation of Brikhoff's heuristic for generating Brikhoff-von Neumann decompositions of [bistochastic matricies](https://en.wikipedia.org/wiki/Doubly_stochastic_matrix).

The algorithm decomposes a square bistochastic matrix (a matrix whose column and row sums are all equal to one) into a convex combination of permutation matrices. Each entry `i,j` in the bistochastic matrix can be interpreted as the probability that a permutation swapping elements `i` and `j` will appear in the (weighted) decomposition.




The results are a convex combination of permutation matrices:
```java
Iterator<CoeffAndMatrix> i = BVNDecomposer.decomposeBiStocastic(new double[][] {{1./4., 1./4., 1./4., 1./4.}, 
		{1./4., 1./4., 1./4., 1./4.}, 
		{1./4., 1./4., 1./4., 1./4.},
		{1./4., 1./4., 1./4., 1./4.}});
		
double coeffSum = 0.0;

while (i.hasNext()) {
	CoeffAndMatrix cam = i.next();
	coeffSum += cam.coeff;
}

assertEquals(coeffSum, 1.0, BVNDecomposer.EPSILON);
```
