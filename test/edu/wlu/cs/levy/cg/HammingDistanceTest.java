package edu.wlu.cs.levy.cg;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HammingDistanceTest {

	@Test
	public void testHammingDistance2DDiagonal() {
		int start = 0;
		int end = 100;

		double[] startPoint = new double[] { start, start };
		double[] endPoint;

		HammingDistance h = new HammingDistance();
		for (int i = 0; i < end; i++) {
			endPoint = new double[] { i, i };
			assertEquals(2 * i, h.distance(startPoint, endPoint), 0.0);
		}
	}

	@Test
	public void testHammingDistance2DBuildEdge() {
		int start = 0;
		int end = 100;

		double[] startPoint = new double[] { start, start };
		double[] endPoint;

		HammingDistance h = new HammingDistance();
		for (int i = 0; i < end; i++) {
			endPoint = new double[] { i, end };
			assertEquals(end + i, h.distance(startPoint, endPoint), 0.0);
		}
	}

}
