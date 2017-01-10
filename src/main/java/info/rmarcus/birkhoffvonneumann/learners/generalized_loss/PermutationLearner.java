package info.rmarcus.birkhoffvonneumann.learners.generalized_loss;

public interface PermutationLearner {
	public double[][] getBest();
	public void iterate();
}
