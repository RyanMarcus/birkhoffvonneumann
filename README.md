# Birkhoff-von Neumann Decomposition

![Codeship status](https://codeship.com/projects/6d90aaa0-792d-0134-382a-3a1a91268848/status?branch=master)

This package provides an implementation of Brikhoff's heuristic for generating Birkhoff-von Neumann decompositions of [bistochastic matricies](https://en.wikipedia.org/wiki/Doubly_stochastic_matrix).

Maven:
```xml
<dependency>
    <groupId>info.rmarcus</groupId>
	<artifactId>brikhoffvonneumann</artifactId>
	<version>0.0.1</version>
</dependency>
```

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
