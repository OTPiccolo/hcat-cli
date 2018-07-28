package ut.net.emb.hcat.cli;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Test;

import net.emb.hcat.cli.Haplotype;
import net.emb.hcat.cli.Sequence;
import net.emb.hcat.cli.Splicer;
import net.emb.hcat.cli.io.FastaReader;

@SuppressWarnings("javadoc")
public class SplicerTest {

	private static final Sequence MASTER_SEQUENCE = new Sequence("ABCD", "Master");
	private static final Sequence DIFF_SEQUENCE = new Sequence("BBCD", "Different");

	private static final Sequence copy(final Sequence copy, final String newName) {
		return new Sequence(copy.getValue(), newName);
	}

	@Test(expected = IllegalArgumentException.class)
	public void compNull() throws Exception {
		new Splicer().compareToMaster((Sequence) null);
	}

	@Test
	public void compNothing() throws Exception {
		final Map<Haplotype, List<Sequence>> map = new Splicer().compareToMaster(MASTER_SEQUENCE);
		Assert.assertNotNull(map);
		Assert.assertEquals(0, map.size());
	}

	@Test
	public void compItself() throws Exception {
		final List<Sequence> compare = Arrays.asList(MASTER_SEQUENCE);
		final Map<Haplotype, List<Sequence>> map = new Splicer(compare).compareToMaster(MASTER_SEQUENCE);
		Assert.assertNotNull(map);
		Assert.assertEquals(1, map.size());
		final Entry<Haplotype, List<Sequence>> entry = map.entrySet().iterator().next();
		final Haplotype haplotype = entry.getKey();
		Assert.assertEquals(new Haplotype(MASTER_SEQUENCE, MASTER_SEQUENCE), haplotype);
		final List<Sequence> sequences = entry.getValue();
		Assert.assertNotNull(sequences);
		Assert.assertEquals(1, sequences.size());
		Assert.assertSame(MASTER_SEQUENCE, sequences.get(0));
	}

	@Test
	public void compDiff() throws Exception {
		final List<Sequence> compare = Arrays.asList(DIFF_SEQUENCE);
		final Map<Haplotype, List<Sequence>> map = new Splicer(compare).compareToMaster(MASTER_SEQUENCE);
		Assert.assertNotNull(map);
		Assert.assertEquals(1, map.size());
		final Entry<Haplotype, List<Sequence>> entry = map.entrySet().iterator().next();
		final Haplotype haplotype = entry.getKey();
		Assert.assertEquals(new Haplotype(MASTER_SEQUENCE, DIFF_SEQUENCE), haplotype);
		final List<Sequence> sequences = entry.getValue();
		Assert.assertNotNull(sequences);
		Assert.assertEquals(1, sequences.size());
		Assert.assertSame(DIFF_SEQUENCE, sequences.get(0));
	}

	@Test
	public void compMore() throws Exception {
		final Sequence endSequence = new Sequence("ABCC", "EndDiff");
		final List<Sequence> compare = Arrays.asList(MASTER_SEQUENCE, DIFF_SEQUENCE, copy(MASTER_SEQUENCE, "Master2"), copy(DIFF_SEQUENCE, "Different2"), endSequence);
		final Map<Haplotype, List<Sequence>> map = new Splicer(compare).compareToMaster(MASTER_SEQUENCE);
		Assert.assertNotNull(map);
		Assert.assertEquals(3, map.size());
		final List<Sequence> sameSequences = map.get(new Haplotype(MASTER_SEQUENCE, MASTER_SEQUENCE));
		Assert.assertNotNull(sameSequences);
		Assert.assertEquals(2, sameSequences.size());
		Assert.assertEquals("Master", sameSequences.get(0).getName());
		Assert.assertEquals("Master2", sameSequences.get(1).getName());
		final List<Sequence> diffSequences = map.get(new Haplotype(MASTER_SEQUENCE, DIFF_SEQUENCE));
		Assert.assertNotNull(diffSequences);
		Assert.assertEquals(2, diffSequences.size());
		Assert.assertEquals("Different", diffSequences.get(0).getName());
		Assert.assertEquals("Different2", diffSequences.get(1).getName());
		final List<Sequence> endSequences = map.get(new Haplotype(MASTER_SEQUENCE, endSequence));
		Assert.assertNotNull(endSequences);
		Assert.assertEquals(1, endSequences.size());
		Assert.assertEquals("EndDiff", endSequences.get(0).getName());
	}

	@Test
	public void idNull() throws Exception {
		final Map<Haplotype, List<Sequence>> map = new Splicer(Collections.singletonList(MASTER_SEQUENCE)).compareToMaster((String) null);
		Assert.assertNull(map);
	}

	@Test
	public void idNotFound() throws Exception {
		final Map<Haplotype, List<Sequence>> map = new Splicer(Collections.singletonList(MASTER_SEQUENCE)).compareToMaster("NotFound");
		Assert.assertNull(map);
	}

	@Test
	public void idFound() throws Exception {
		final Map<Haplotype, List<Sequence>> map = new Splicer(Collections.singletonList(MASTER_SEQUENCE)).compareToMaster("Master");
		Assert.assertNotNull(map);
		Assert.assertEquals(1, map.size());
	}

	@Test
	public void findEmpty() throws Exception {
		final List<Sequence> sequences = new Splicer().findMostMatchSequences();
		Assert.assertNotNull(sequences);
		Assert.assertEquals(0, sequences.size());
	}

	@Test
	public void findEqualNumber() throws Exception {
		final List<Sequence> compare = Arrays.asList(MASTER_SEQUENCE, DIFF_SEQUENCE);
		final List<Sequence> sequences = new Splicer(compare).findMostMatchSequences();
		Assert.assertNotNull(sequences);
		Assert.assertEquals(1, sequences.size());
	}

	@Test
	public void find() throws Exception {
		final List<Sequence> compare = Arrays.asList(MASTER_SEQUENCE, DIFF_SEQUENCE, copy(MASTER_SEQUENCE, "Master2"), copy(DIFF_SEQUENCE, "Different2"), copy(DIFF_SEQUENCE, "Different3"));
		final List<Sequence> sequences = new Splicer(compare).findMostMatchSequences();
		Assert.assertNotNull(sequences);
		Assert.assertEquals(3, sequences.size());
		for (final Sequence sequence : sequences) {
			Assert.assertTrue("Wrong sequence found: " + sequence, sequence.getName().startsWith("Different"));
		}
	}

	@Test
	public void testData() throws Exception {
		List<Sequence> sequences;
		try (Reader reader = new InputStreamReader(getClass().getResourceAsStream("/testdata.txt"), StandardCharsets.UTF_8)) {
			final FastaReader fasta = new FastaReader(reader);
			fasta.setEnforceSameLength(true);
			sequences = fasta.read();
		}

		final Map<Haplotype, List<Sequence>> map = new Splicer(sequences).compareToMaster("01");
		Assert.assertNotNull("Master ID not found.", map);
		Assert.assertEquals(24, map.size());
	}

}
