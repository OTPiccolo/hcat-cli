package ut.net.emb.hcat.cli.sequence;

import org.junit.Assert;
import org.junit.Test;

import net.emb.hcat.cli.sequence.Difference;
import net.emb.hcat.cli.sequence.Sequence;

@SuppressWarnings("javadoc")
public class DifferenceTest {

	private static final Sequence MASTER_SEQUENCE = new Sequence("ABCD");
	private static final Sequence START_DIFF_SEQUENCE = new Sequence("BBCD");
	private static final Sequence MID_DIFF_SEQUENCE = new Sequence("AACD");
	private static final Sequence END_DIFF_SEQUENCE = new Sequence("ABCC");
	private static final Sequence MULTI_DIFF_SEQUENCE = new Sequence("ACBD");
	private static final Sequence ALL_DIFF_SEQUENCE = new Sequence("DCBA");
	private static final Sequence SHORT_SEQUENCE = new Sequence("ABC");
	private static final Sequence LONG_SEQUENCE = new Sequence("ABCDE");

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void nullMaster() throws Exception {
		new Difference(null, ALL_DIFF_SEQUENCE);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void nullSlave() throws Exception {
		new Difference(MASTER_SEQUENCE, null);
	}

	@Test
	public void itself() throws Exception {
		final Difference haplotype = new Difference(MASTER_SEQUENCE, MASTER_SEQUENCE);
		Assert.assertEquals(0, haplotype.getDifferencePosition().size());
		Assert.assertEquals("....", haplotype.getDifference());
	}

	@Test
	public void start() throws Exception {
		final Difference haplotype = new Difference(MASTER_SEQUENCE, START_DIFF_SEQUENCE);
		Assert.assertEquals(1, haplotype.getDifferencePosition().size());
		Assert.assertEquals(0, haplotype.getDifferencePosition().first().intValue());
		Assert.assertEquals("B...", haplotype.getDifference());
	}

	@Test
	public void mid() throws Exception {
		final Difference haplotype = new Difference(MASTER_SEQUENCE, MID_DIFF_SEQUENCE);
		Assert.assertEquals(1, haplotype.getDifferencePosition().size());
		Assert.assertEquals(1, haplotype.getDifferencePosition().first().intValue());
		Assert.assertEquals(".A..", haplotype.getDifference());
	}

	@Test
	public void end() throws Exception {
		final Difference haplotype = new Difference(MASTER_SEQUENCE, END_DIFF_SEQUENCE);
		Assert.assertEquals(1, haplotype.getDifferencePosition().size());
		Assert.assertEquals(3, haplotype.getDifferencePosition().first().intValue());
		Assert.assertEquals("...C", haplotype.getDifference());
	}

	@Test
	public void multi() throws Exception {
		final Difference haplotype = new Difference(MASTER_SEQUENCE, MULTI_DIFF_SEQUENCE);
		Assert.assertEquals(2, haplotype.getDifferencePosition().size());
		Assert.assertEquals(1, haplotype.getDifferencePosition().first().intValue());
		Assert.assertEquals(2, haplotype.getDifferencePosition().last().intValue());
		Assert.assertEquals(".CB.", haplotype.getDifference());
	}

	@Test
	public void all() throws Exception {
		final Difference haplotype = new Difference(MASTER_SEQUENCE, ALL_DIFF_SEQUENCE);
		Assert.assertEquals(4, haplotype.getDifferencePosition().size());
		Assert.assertArrayEquals(new Integer[] { 0, 1, 2, 3 }, haplotype.getDifferencePosition().toArray(new Integer[] {}));
		Assert.assertEquals("DCBA", haplotype.getDifference());
	}

	@Test
	public void shortSeq() throws Exception {
		final Difference haplotype = new Difference(MASTER_SEQUENCE, SHORT_SEQUENCE);
		Assert.assertEquals(1, haplotype.getDifferencePosition().size());
		Assert.assertEquals(3, haplotype.getDifferencePosition().first().intValue());
		Assert.assertEquals("... ", haplotype.getDifference());
	}

	@Test
	public void longSeq() throws Exception {
		final Difference haplotype = new Difference(MASTER_SEQUENCE, LONG_SEQUENCE);
		Assert.assertEquals(1, haplotype.getDifferencePosition().size());
		Assert.assertEquals(4, haplotype.getDifferencePosition().first().intValue());
		Assert.assertEquals("....E", haplotype.getDifference());
	}

}
