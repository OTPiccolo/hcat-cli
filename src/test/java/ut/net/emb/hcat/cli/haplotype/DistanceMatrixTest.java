package ut.net.emb.hcat.cli.haplotype;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import net.emb.hcat.cli.haplotype.DistanceMatrix;
import net.emb.hcat.cli.haplotype.Haplotype;
import net.emb.hcat.cli.sequence.Sequence;

@SuppressWarnings("javadoc")
public class DistanceMatrixTest {

	private static final Haplotype MASTER_HAPLOTYPE = new Haplotype(new Sequence("ABCD"));
	private static final Haplotype START_DIFF_HAPLOTYPE = new Haplotype(new Sequence("BBCD"));
	private static final Haplotype MID_DIFF_HAPLOTYPE = new Haplotype(new Sequence("AACD"));
	private static final Haplotype END_DIFF_HAPLOTYPE = new Haplotype(new Sequence("ABCC"));
	private static final Haplotype MULTI_DIFF_HAPLOTYPE = new Haplotype(new Sequence("ACBD"));
	private static final Haplotype ALL_DIFF_HAPLOTYPE = new Haplotype(new Sequence("DCBA"));
	private static final Haplotype SHORT_HAPLOTYPE = new Haplotype(new Sequence("ABC"));
	private static final Haplotype LONG_HAPLOTYPE = new Haplotype(new Sequence("ABCDE"));

	@Test
	public void testEmpty() {
		final DistanceMatrix matrix = new DistanceMatrix(Collections.emptyList());
		Assert.assertTrue(matrix.getMatrix().isEmpty());
	}

	@Test
	public void testSingle() {
		final DistanceMatrix matrix = new DistanceMatrix(Collections.singletonList(MASTER_HAPLOTYPE));
		final Map<Haplotype, Map<Haplotype, Integer>> map = matrix.getMatrix();
		Assert.assertEquals(1, map.size());
		Assert.assertEquals(0, map.get(MASTER_HAPLOTYPE).size());
		Assert.assertEquals(0, matrix.getDistances(MASTER_HAPLOTYPE).size());
		Assert.assertEquals(0, matrix.getMinDistance());
		Assert.assertEquals(0, matrix.getMaxDistance());
	}

	@Test
	public void testDuo() {
		final DistanceMatrix matrix = new DistanceMatrix(Arrays.asList(MASTER_HAPLOTYPE, ALL_DIFF_HAPLOTYPE));
		final Map<Haplotype, Map<Haplotype, Integer>> map = matrix.getMatrix();
		Assert.assertEquals(2, map.size());
		Assert.assertEquals(1, map.get(MASTER_HAPLOTYPE).size());
		Assert.assertEquals(1, matrix.getDistances(MASTER_HAPLOTYPE).size());
		Assert.assertEquals(1, map.get(ALL_DIFF_HAPLOTYPE).size());
		Assert.assertEquals(1, matrix.getDistances(ALL_DIFF_HAPLOTYPE).size());
		Assert.assertEquals(4, matrix.getDistance(MASTER_HAPLOTYPE, ALL_DIFF_HAPLOTYPE).intValue());
	}

	@Test
	public void testMultiple() {
		final DistanceMatrix matrix = new DistanceMatrix(Arrays.asList(MASTER_HAPLOTYPE, START_DIFF_HAPLOTYPE, MID_DIFF_HAPLOTYPE, END_DIFF_HAPLOTYPE, MULTI_DIFF_HAPLOTYPE, ALL_DIFF_HAPLOTYPE));
		Assert.assertEquals(1, matrix.getDistance(MASTER_HAPLOTYPE, START_DIFF_HAPLOTYPE).intValue());
		Assert.assertEquals(1, matrix.getDistance(MASTER_HAPLOTYPE, MID_DIFF_HAPLOTYPE).intValue());
		Assert.assertEquals(1, matrix.getDistance(MASTER_HAPLOTYPE, END_DIFF_HAPLOTYPE).intValue());
		Assert.assertEquals(2, matrix.getDistance(MASTER_HAPLOTYPE, MULTI_DIFF_HAPLOTYPE).intValue());
		Assert.assertEquals(4, matrix.getDistance(MASTER_HAPLOTYPE, ALL_DIFF_HAPLOTYPE).intValue());
		Assert.assertEquals(2, matrix.getDistance(START_DIFF_HAPLOTYPE, MID_DIFF_HAPLOTYPE).intValue());
		Assert.assertEquals(2, matrix.getDistance(START_DIFF_HAPLOTYPE, END_DIFF_HAPLOTYPE).intValue());
		Assert.assertEquals(3, matrix.getDistance(START_DIFF_HAPLOTYPE, MULTI_DIFF_HAPLOTYPE).intValue());
		Assert.assertEquals(4, matrix.getDistance(START_DIFF_HAPLOTYPE, ALL_DIFF_HAPLOTYPE).intValue());
		Assert.assertEquals(2, matrix.getDistance(MID_DIFF_HAPLOTYPE, END_DIFF_HAPLOTYPE).intValue());
		Assert.assertEquals(2, matrix.getDistance(MID_DIFF_HAPLOTYPE, MULTI_DIFF_HAPLOTYPE).intValue());
		Assert.assertEquals(4, matrix.getDistance(MID_DIFF_HAPLOTYPE, ALL_DIFF_HAPLOTYPE).intValue());
		Assert.assertEquals(3, matrix.getDistance(END_DIFF_HAPLOTYPE, MULTI_DIFF_HAPLOTYPE).intValue());
		Assert.assertEquals(4, matrix.getDistance(END_DIFF_HAPLOTYPE, ALL_DIFF_HAPLOTYPE).intValue());
		Assert.assertEquals(2, matrix.getDistance(MULTI_DIFF_HAPLOTYPE, ALL_DIFF_HAPLOTYPE).intValue());
	}

	@Test
	public void testDiffLength() {
		final DistanceMatrix matrix = new DistanceMatrix(Arrays.asList(MASTER_HAPLOTYPE, SHORT_HAPLOTYPE, LONG_HAPLOTYPE));
		Assert.assertEquals(1, matrix.getDistance(MASTER_HAPLOTYPE, SHORT_HAPLOTYPE).intValue());
		Assert.assertEquals(1, matrix.getDistance(MASTER_HAPLOTYPE, LONG_HAPLOTYPE).intValue());
		Assert.assertEquals(2, matrix.getDistance(SHORT_HAPLOTYPE, LONG_HAPLOTYPE).intValue());
	}

	@Test
	public void testNotFound() {
		final DistanceMatrix matrix = new DistanceMatrix(Arrays.asList(MASTER_HAPLOTYPE, ALL_DIFF_HAPLOTYPE));
		Assert.assertNull(matrix.getMatrix().get(MULTI_DIFF_HAPLOTYPE));
		Assert.assertNull(matrix.getDistances(MULTI_DIFF_HAPLOTYPE));
		Assert.assertNull(matrix.getDistance(MASTER_HAPLOTYPE, MULTI_DIFF_HAPLOTYPE));
		Assert.assertNull(matrix.getDistance(MULTI_DIFF_HAPLOTYPE, MASTER_HAPLOTYPE));
	}

	@Test
	public void testSelfReference() {
		final DistanceMatrix matrix = new DistanceMatrix(Arrays.asList(MASTER_HAPLOTYPE, ALL_DIFF_HAPLOTYPE));
		Assert.assertNull(matrix.getMatrix().get(MASTER_HAPLOTYPE).get(MASTER_HAPLOTYPE));
		Assert.assertNull(matrix.getDistance(MASTER_HAPLOTYPE, MASTER_HAPLOTYPE));
	}

	@Test
	public void testDistance1() {
		final DistanceMatrix matrix = new DistanceMatrix(Arrays.asList(MASTER_HAPLOTYPE, MID_DIFF_HAPLOTYPE, MULTI_DIFF_HAPLOTYPE));
		Assert.assertEquals(1, matrix.getMinDistance());
		Assert.assertEquals(2, matrix.getMaxDistance());
	}

	@Test
	public void testDistance2() {
		final DistanceMatrix matrix = new DistanceMatrix(Arrays.asList(MASTER_HAPLOTYPE, MASTER_HAPLOTYPE, ALL_DIFF_HAPLOTYPE));
		Assert.assertEquals(0, matrix.getMinDistance());
		Assert.assertEquals(4, matrix.getMaxDistance());
	}

}
