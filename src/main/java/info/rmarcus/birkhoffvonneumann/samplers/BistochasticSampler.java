package info.rmarcus.birkhoffvonneumann.samplers;

public interface BistochasticSampler {
	
	public static BistochasticSampler dirichletSampler() {
		return new DirichletBistochasticSampler();
	}
	
	public double[][] sample(int n);
}
