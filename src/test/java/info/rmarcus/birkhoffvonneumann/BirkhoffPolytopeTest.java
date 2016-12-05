package info.rmarcus.birkhoffvonneumann;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.eclipse.jdt.annotation.Nullable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import info.rmarcus.birkhoffvonneumann.polytope.BirkhoffPolytope;
import info.rmarcus.birkhoffvonneumann.polytope.PointLinearBirkhoffPolytope;
import info.rmarcus.birkhoffvonneumann.polytope.VertexCurveBirkhoffPolytope;
import info.rmarcus.birkhoffvonneumann.samplers.BistochasticSampler;

@RunWith(Parameterized.class)
public class BirkhoffPolytopeTest {

	@Parameters(name = "dim: {0} type: {1}")
	public static Collection<Object[]> data() {
		List<Object[]> toR = new LinkedList<>();
		for (int i = 0; i < 10; i++) {
			int dim = i * 40;
			toR.add(new Object[] { dim, new VertexCurveBirkhoffPolytope(dim)});
			toR.add(new Object[] { dim, new PointLinearBirkhoffPolytope(dim, BistochasticSampler.dirichletSampler())});
		}
		
		return toR;
	}
	
	@Parameter(0)
	public Integer dim = 0;
	
	@Parameter(1)
	public @Nullable BirkhoffPolytope toTest;
	
	@Test
	public void randomMovementTest() {
		final BirkhoffPolytope bp = toTest;
		if (bp == null) {
			fail("Birkhoff polytope was null!");
			return;
		}
		
		Random r = new Random(42);
		
		for (int i = 0; i < 100; i++) {
			bp.movePoint(bp.getRandomDirection(r), r.nextDouble());
			assertTrue(BVNUtils.isBistochastic(bp.getCurrentPoint()));
		}
	}

}
