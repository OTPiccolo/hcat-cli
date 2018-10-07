package ut.net.emb.hcat.cli;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.emb.hcat.cli.Sequence;
import net.emb.hcat.cli.haplotype.Haplotype;
import net.emb.hcat.cli.io.FastaReader;

@SuppressWarnings("javadoc")
public class HaplotypeTest {

	private static final Sequence MASTER_SEQUENCE = new Sequence("ABCD", "Master");
	private static final Sequence START_DIFF_SEQUENCE = new Sequence("BBCD", "Start");
	private static final Sequence MID_DIFF_SEQUENCE = new Sequence("AACD", "Mid");
	private static final Sequence END_DIFF_SEQUENCE = new Sequence("ABCC", "End");
	private static final Sequence MULTI_DIFF_SEQUENCE = new Sequence("ACBD", "Multi");
	private static final Sequence ALL_DIFF_SEQUENCE = new Sequence("DCBA", "All");
	private static final Sequence SHORT_SEQUENCE = new Sequence("ABC", "Short");
	private static final Sequence LONG_SEQUENCE = new Sequence("ABCDE", "Long");

	private static final Sequence copy(final Sequence copy, final String newName) {
		return new Sequence(copy.getValue(), newName);
	}

	// TODO Add tests for static method of Haplotype.

	@Test(expected = IllegalArgumentException.class)
	public void testNull() {
		final Haplotype haplotype = new Haplotype();
		haplotype.add(null);
	}

	@Test(expected = IllegalArgumentException.class)
	@SuppressWarnings("unused")
	public void testNullConstructor() {
		new Haplotype(null);
	}

	@Test
	public void testBelongsEmpty() {
		final Haplotype haplotype = new Haplotype();
		Assert.assertTrue(haplotype.belongsToHaplotype(MASTER_SEQUENCE));
	}

	@Test
	public void testBelongs() {
		final Haplotype haplotype = new Haplotype(MASTER_SEQUENCE);
		Assert.assertTrue(haplotype.belongsToHaplotype(MASTER_SEQUENCE));
	}

	@Test
	public void testBelongsNotShort() {
		final Haplotype haplotype = new Haplotype(MASTER_SEQUENCE);
		Assert.assertFalse(haplotype.belongsToHaplotype(SHORT_SEQUENCE));
	}

	@Test
	public void testBelongsNotLong() {
		final Haplotype haplotype = new Haplotype(MASTER_SEQUENCE);
		Assert.assertFalse(haplotype.belongsToHaplotype(LONG_SEQUENCE));
	}

	@Test
	public void testBelongsNotDiff() {
		final Haplotype haplotype = new Haplotype(MASTER_SEQUENCE);
		Assert.assertFalse(haplotype.belongsToHaplotype(MID_DIFF_SEQUENCE));
	}

	@Test
	public void testBelongsNull() {
		final Haplotype haplotype = new Haplotype(MASTER_SEQUENCE);
		Assert.assertFalse(haplotype.belongsToHaplotype(null));
	}

	@Test
	public void testAdd() {
		final Haplotype haplotype = new Haplotype(MASTER_SEQUENCE);
		haplotype.add(copy(MASTER_SEQUENCE, "master2"));
		Assert.assertTrue(haplotype.size() == 2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddNot() {
		final Haplotype haplotype = new Haplotype(MASTER_SEQUENCE);
		haplotype.add(ALL_DIFF_SEQUENCE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddNull() {
		final Haplotype haplotype = new Haplotype(MASTER_SEQUENCE);
		haplotype.add(null);
	}

	@Test
	public void testData() throws Exception {
		List<Sequence> sequences;
		try (Reader reader = new InputStreamReader(getClass().getResourceAsStream("/testdata.txt"), StandardCharsets.UTF_8)) {
			final FastaReader fasta = new FastaReader(reader);
			fasta.setEnforceSameLength(true);
			sequences = fasta.read();
		}

		final List<Haplotype> haplotypes = Haplotype.createHaplotypes(sequences);
		Assert.assertEquals(24, haplotypes.size());
	}

}
