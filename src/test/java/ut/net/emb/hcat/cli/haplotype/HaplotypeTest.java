package ut.net.emb.hcat.cli.haplotype;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.emb.hcat.cli.Sequence;
import net.emb.hcat.cli.haplotype.Haplotype;
import net.emb.hcat.cli.io.FastaReader;

@SuppressWarnings("javadoc")
public class HaplotypeTest {

	private static final Sequence MASTER_SEQUENCE = new Sequence("ABCD", "Master");
	private static final Sequence MID_DIFF_SEQUENCE = new Sequence("AACD", "Mid");
	private static final Sequence SHORT_SEQUENCE = new Sequence("ABC", "Short");
	private static final Sequence LONG_SEQUENCE = new Sequence("ABCDE", "Long");

	private static final Sequence copy(final Sequence copy, final String newName) {
		return new Sequence(copy.getValue(), newName);
	}

	@Test
	public void testCreate() {
		final Sequence masterCopy = copy(MASTER_SEQUENCE, "Master2");
		final Sequence longCopy = copy(LONG_SEQUENCE, "Long2");
		final List<Sequence> sequences = Arrays.asList(MASTER_SEQUENCE, MID_DIFF_SEQUENCE, SHORT_SEQUENCE, LONG_SEQUENCE, masterCopy, MID_DIFF_SEQUENCE, longCopy);
		final List<Haplotype> haplotypes = Haplotype.createHaplotypes(sequences);
		Assert.assertNotNull(haplotypes);
		Assert.assertEquals(4, haplotypes.size());
		Assert.assertEquals(2, haplotypes.get(0).size());
		Assert.assertEquals(1, haplotypes.get(1).size());
		Assert.assertEquals(1, haplotypes.get(2).size());
		Assert.assertEquals(2, haplotypes.get(3).size());
		Assert.assertTrue(haplotypes.get(0).contains(MASTER_SEQUENCE));
		Assert.assertTrue(haplotypes.get(0).contains(masterCopy));
		Assert.assertTrue(haplotypes.get(1).contains(MID_DIFF_SEQUENCE));
		Assert.assertTrue(haplotypes.get(2).contains(SHORT_SEQUENCE));
		Assert.assertTrue(haplotypes.get(3).contains(LONG_SEQUENCE));
		Assert.assertTrue(haplotypes.get(3).contains(longCopy));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullAdd() {
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
		haplotype.add(MID_DIFF_SEQUENCE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddNull() {
		final Haplotype haplotype = new Haplotype(MASTER_SEQUENCE);
		haplotype.add(null);
	}

	@Test
	public void testData() throws Exception {
		List<Sequence> sequences;
		try (Reader reader = new InputStreamReader(getClass().getResourceAsStream("/fasta-testdata1.txt"), StandardCharsets.UTF_8)) {
			final FastaReader fasta = new FastaReader(reader);
			fasta.setEnforceSameLength(true);
			sequences = fasta.read();
		}

		final List<Haplotype> haplotypes = Haplotype.createHaplotypes(sequences);
		Assert.assertEquals(24, haplotypes.size());
	}

}