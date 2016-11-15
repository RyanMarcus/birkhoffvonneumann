package info.rmarcus.brikhoffvonneumann;

public class MatrixUtils {
	public static void multiply(double[][] dest, double[][] a, double[][] b) {
		for (int i = 0; i < dest.length; i++) {
			for (int j = 0; j < dest[i].length; j++) {
				dest[i][j] = a[i][j] * b[i][j];
			}
		}
	}
	
	public static void multiply(double[][] dest, double[][] a, double b) {
		for (int i = 0; i < dest.length; i++) {
			for (int j = 0; j < dest[i].length; j++) {
				dest[i][j] = a[i][j] * b;
			}
		}
	}
	
	
	public static void add(double[][] dest, double[][] a, double[][] b) {
		for (int i = 0; i < dest.length; i++) {
			for (int j = 0; j < dest[i].length; j++) {
				dest[i][j] = a[i][j] + b[i][j];
			}
		}
	}
}
