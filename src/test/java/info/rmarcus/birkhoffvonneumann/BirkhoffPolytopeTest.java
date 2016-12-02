package info.rmarcus.birkhoffvonneumann;

import static org.junit.Assert.assertTrue;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import info.rmarcus.birkhoffvonneumann.polytope.BirkhoffPolytope;
import info.rmarcus.birkhoffvonneumann.polytope.UnderconstrainedBirkhoffPolytope;

@RunWith(Parameterized.class)
public class BirkhoffPolytopeTest {

	@SuppressWarnings("null")
	@Parameters(name = "dim: {0}")
	public static Iterable<Object> data() {
		return IntStream.range(1, 11).mapToObj(i -> 40 * Integer.valueOf(i)).collect(Collectors.toList());
	}
	
	@Parameter
	public Integer dim = 0;
	
	@Test
	public void randomMovementTest() {
		BirkhoffPolytope bp = new UnderconstrainedBirkhoffPolytope(dim);
		Random r = new Random(42);
		
		for (int i = 0; i < 100; i++) {
			bp.movePoint(bp.getRandomDirection(r), r.nextDouble());
			assertTrue(BVNUtils.isBistochastic(bp.getCurrentPoint()));
		}
	}

}
