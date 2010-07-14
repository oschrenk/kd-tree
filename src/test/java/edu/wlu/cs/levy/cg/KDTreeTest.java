package edu.wlu.cs.levy.cg;

/*
 written by MSL for SpeedDate
 */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.List;

import org.junit.Test;

public class KDTreeTest {
	static java.util.Random rand = new java.util.Random();

	static double[] makeSample(int dims) {
		double[] rv = new double[dims];
		for (int j = 0; j < dims; ++j) {
			rv[j] = rand.nextDouble();
		}
		return rv;
	}

	static double distSquared(double[] p0, double[] p1) {
		double rv = 0;
		for (int i = 0; i < p0.length; i++) {
			double diff = p0[i] - p1[i];
			rv += (diff * diff);
		}
		return rv;
	}

	@Test
	public void testNearestNeighbor() throws KDException {
		int dims = 3;
		int samples = 300;
		KDTree<Integer> kt = new KDTree<Integer>(dims);
		double[] targ = makeSample(dims);

		int min_index = 0;
		double min_value = Double.MAX_VALUE;
		for (int i = 0; i < samples; ++i) {
			double[] keys = makeSample(dims);
			kt.insert(keys, new Integer(i));

			/*
			 * for the purposes of test, we want the nearest EVEN-NUMBERED point
			 */
			if ((i % 2) == 0) {
				double dist = distSquared(targ, keys);
				if (dist < min_value) {
					min_value = dist;
					min_index = i;
				}
			}
		}

		List<Integer> nbrs = kt.nearest(targ, 1, new Checker<Integer>() {
			public boolean usable(Integer v) {
				return (v.intValue() % 2) == 0;
			}
		});

		assertEquals(1, nbrs.size());
		if (nbrs.size() == 1) {
			assertEquals(min_index, nbrs.get(0).intValue());
		}
	}

	@Test
	public void testRange() throws KDException {
		int dims = 2;
		KDTree<Object> kt = new KDTree<Object>(dims);
		double[] p0 = { 0.5, 0.5 };
		double[] p1 = { 0.65, 0.5 };
		double[] p2 = { 0.75, 0.5 };

		kt.insert(p0, new Object());
		kt.insert(p1, new Object());
		kt.insert(p2, new Object());

		double[] lower = { 0.25, 0.3 };
		double[] upper = { 0.7, 0.6 };

		List<Object> rv = kt.range(lower, upper);
		assertEquals(2, rv.size());

		kt.delete(p1);
		rv = kt.range(lower, upper);
		assertEquals(1, rv.size());
	}

	@Test
	public void testSearch() throws KDException {
		int dims = 3;
		int samples = 300;
		KDTree<Object> kt = new KDTree<Object>(dims);
		double[] targ = makeSample(dims);
		Object treasure = new Object();
		kt.insert(targ, treasure);

		for (int i = 0; i < samples; ++i) {
			double[] keys = makeSample(dims);
			kt.insert(keys, new Integer(i));
		}

		Object found = kt.search(targ);
		assertSame(treasure, found);

		kt.delete(targ);
		found = kt.search(targ);
		assertNull(found);

	}

	@Test
	public void testDelete() throws KDException {
		int dims = 3;
		KDTree<Object> kt = new KDTree<Object>(dims);
		double[] targ = makeSample(dims);
		kt.insert(targ, new Object());
		kt.delete(targ);
		try {
			kt.delete(targ);
			assertFalse(true);
		} catch (edu.wlu.cs.levy.cg.KeyMissingException e) {
			// supposed to be here
		}
		kt.delete(targ, true);
		assertEquals(0, kt.size());
	}

	@Test
	public void testEditing() throws KDException {
		int dims = 3;
		KDTree<Object> kt = new KDTree<Object>(dims);
		double[] targ = makeSample(dims);

		Object p1 = "p1";
		Object p2 = "p2";
		kt.insert(targ, p1);
		try {
			kt.insert(targ, p2);
			assertFalse(true);
		} catch (edu.wlu.cs.levy.cg.KeyDuplicateException e) {
			// supposed to be here
		}

		kt.edit(targ, new Editor.OptionalInserter<Object>(p2));
		Object found = kt.search(targ);
		assertSame(p1, found);
		kt.edit(targ, new Editor.Replacer<Object>(p2));
		found = kt.search(targ);
		assertSame(p2, found);
	}
}
