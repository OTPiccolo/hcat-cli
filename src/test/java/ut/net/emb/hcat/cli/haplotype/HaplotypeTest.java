package ut.net.emb.hcat.cli.haplotype;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.emb.hcat.cli.haplotype.Haplotype;
import net.emb.hcat.cli.io.sequence.FastaReader;
import net.emb.hcat.cli.sequence.Sequence;

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
	public void testWrap() {
		final Sequence masterCopy = copy(MASTER_SEQUENCE, "Master2");
		final Sequence longCopy = copy(LONG_SEQUENCE, "Long2");
		final List<Sequence> sequences = Arrays.asList(MASTER_SEQUENCE, MID_DIFF_SEQUENCE, SHORT_SEQUENCE, LONG_SEQUENCE, masterCopy, MID_DIFF_SEQUENCE, longCopy);
		final List<Haplotype> haplotypes = Haplotype.wrap(sequences);
		Assert.assertNotNull(haplotypes);
		Assert.assertEquals(4, haplotypes.size());
		Assert.assertEquals(2, haplotypes.get(0).size());
		Assert.assertEquals(2, haplotypes.get(1).size());
		Assert.assertEquals(1, haplotypes.get(2).size());
		Assert.assertEquals(2, haplotypes.get(3).size());
		Assert.assertSame(MASTER_SEQUENCE, haplotypes.get(0).get(0));
		Assert.assertSame(masterCopy, haplotypes.get(0).get(1));
		Assert.assertSame(MID_DIFF_SEQUENCE, haplotypes.get(1).get(0));
		Assert.assertSame(MID_DIFF_SEQUENCE, haplotypes.get(1).get(1));
		Assert.assertSame(SHORT_SEQUENCE, haplotypes.get(2).get(0));
		Assert.assertSame(LONG_SEQUENCE, haplotypes.get(3).get(0));
		Assert.assertSame(longCopy, haplotypes.get(3).get(1));
	}

	@Test
	public void testWrapNames() {
		final List<Sequence> sequences = new ArrayList<>(1100);
		for (int i = 0; i < 1100; i++) {
			sequences.add(new Sequence(Integer.toString(i)));
		}
		final List<Haplotype> haplotypes = Haplotype.wrap(sequences);

		Assert.assertNotNull(haplotypes);
		Assert.assertEquals(1100, haplotypes.size());
		Assert.assertEquals("Hap0001", haplotypes.get(0).getName());
		Assert.assertEquals("Hap0009", haplotypes.get(8).getName());
		Assert.assertEquals("Hap0010", haplotypes.get(9).getName());
		Assert.assertEquals("Hap0099", haplotypes.get(98).getName());
		Assert.assertEquals("Hap0100", haplotypes.get(99).getName());
		Assert.assertEquals("Hap0999", haplotypes.get(998).getName());
		Assert.assertEquals("Hap1000", haplotypes.get(999).getName());
		Assert.assertEquals("Hap1100", haplotypes.get(1099).getName());
	}

	@Test
	public void testUnwrap() {
		final Sequence masterCopy = copy(MASTER_SEQUENCE, "Master2");
		final Sequence longCopy = copy(LONG_SEQUENCE, "Long2");
		final List<Sequence> orgSequences = Arrays.asList(MASTER_SEQUENCE, MID_DIFF_SEQUENCE, SHORT_SEQUENCE, LONG_SEQUENCE, masterCopy, MID_DIFF_SEQUENCE, longCopy);
		final List<Haplotype> haplotypes = Haplotype.wrap(orgSequences);
		final List<Sequence> sequences = Haplotype.unwrap(haplotypes);
		System.out.println(sequences);
		Assert.assertNotNull(sequences);
		Assert.assertEquals(7, sequences.size());
		Assert.assertSame(MASTER_SEQUENCE, sequences.get(0));
		Assert.assertSame(masterCopy, sequences.get(1));
		Assert.assertSame(MID_DIFF_SEQUENCE, sequences.get(2));
		Assert.assertSame(MID_DIFF_SEQUENCE, sequences.get(3));
		Assert.assertSame(SHORT_SEQUENCE, sequences.get(4));
		Assert.assertSame(LONG_SEQUENCE, sequences.get(5));
		Assert.assertSame(longCopy, sequences.get(6));
	}

	@Test
	public void testFind() {
		final Sequence masterCopy = copy(MASTER_SEQUENCE, "Master2");
		final List<Haplotype> haplotypes = Haplotype.wrap(Arrays.asList(MID_DIFF_SEQUENCE, MASTER_SEQUENCE));
		haplotypes.add(0, new Haplotype("Empty"));
		final Haplotype foundHaplotype = Haplotype.find(masterCopy, haplotypes);
		Assert.assertNotNull(foundHaplotype);
		Assert.assertEquals(MASTER_SEQUENCE, foundHaplotype.getFirstSequence());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullAdd() {
		final Haplotype haplotype = new Haplotype();
		haplotype.add(null);
	}

	@Test(expected = IllegalArgumentException.class)
	@SuppressWarnings("unused")
	public void testNullSequenceConstructor() {
		new Haplotype((Sequence) null);
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
	public void testFirstSequence() {
		final Haplotype haplotype = new Haplotype(MASTER_SEQUENCE);
		haplotype.add(copy(MASTER_SEQUENCE, "Master2"));
		final Sequence sequence = haplotype.getFirstSequence();
		Assert.assertSame(MASTER_SEQUENCE, sequence);
	}

	@Test
	public void testFirstSequenceEmpty() {
		final Haplotype haplotype = new Haplotype();
		final Sequence sequence = haplotype.getFirstSequence();
		Assert.assertNull(sequence);
	}

	@Test
	public void testAsSequence() {
		final Haplotype haplotype = new Haplotype("Haplotype");
		haplotype.add(MASTER_SEQUENCE);
		final Sequence sequence = haplotype.asSequence();
		Assert.assertNotNull(sequence);
		Assert.assertEquals("Haplotype", sequence.getName());
		Assert.assertEquals(MASTER_SEQUENCE.getValue(), sequence.getValue());
	}

	@Test
	public void testAsSequenceEmpty() {
		final Haplotype haplotype = new Haplotype("Haplotype");
		final Sequence sequence = haplotype.asSequence();
		Assert.assertNull(sequence);
	}

	@Test
	public void testData() throws Exception {
		List<Sequence> sequences;
		try (FastaReader fasta = new FastaReader(new InputStreamReader(getClass().getResourceAsStream("/fasta-testdata1.txt"), StandardCharsets.UTF_8))) {
			fasta.setEnforceSameLength(true);
			sequences = fasta.read();
		}

		final List<Haplotype> haplotypes = Haplotype.wrap(sequences);
		Assert.assertEquals(24, haplotypes.size());
	}

}
