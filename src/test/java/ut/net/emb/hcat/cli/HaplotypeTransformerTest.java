package ut.net.emb.hcat.cli;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Test;

import net.emb.hcat.cli.Difference;
import net.emb.hcat.cli.Sequence;
import net.emb.hcat.cli.haplotype.Haplotype;
import net.emb.hcat.cli.haplotype.HaplotypeTransformer;

@SuppressWarnings("javadoc")
public class HaplotypeTransformerTest {

	private static final Sequence MASTER_SEQUENCE = new Sequence("ABCD", "Master");
	private static final Sequence DIFF_SEQUENCE = new Sequence("BBCD", "Different");

	private static final Sequence copy(final Sequence copy, final String newName) {
		return new Sequence(copy.getValue(), newName);
	}

	@Test(expected = IllegalArgumentException.class)
	public void compNull() throws Exception {
		new HaplotypeTransformer().compareToMaster((Sequence) null);
	}

	@Test
	public void compNothing() throws Exception {
		final Map<Haplotype, Difference> map = new HaplotypeTransformer().compareToMaster(MASTER_SEQUENCE);
		Assert.assertNotNull(map);
		Assert.assertEquals(0, map.size());
	}

	@Test
	public void compItself() throws Exception {
		final List<Haplotype> compare = Haplotype.createHaplotypes(Arrays.asList(MASTER_SEQUENCE));
		final Map<Haplotype, Difference> map = new HaplotypeTransformer(compare).compareToMaster(MASTER_SEQUENCE);
		Assert.assertNotNull(map);
		Assert.assertEquals(1, map.size());
		final Entry<Haplotype, Difference> entry = map.entrySet().iterator().next();
		final Haplotype haplotype = entry.getKey();
		Assert.assertSame(compare.get(0), haplotype);
		final Difference diff = entry.getValue();
		Assert.assertNotNull(diff);
		Assert.assertEquals(new Difference(MASTER_SEQUENCE, MASTER_SEQUENCE), diff);
	}

	@Test
	public void compDiff() throws Exception {
		final List<Haplotype> compare = Haplotype.createHaplotypes(Arrays.asList(DIFF_SEQUENCE));
		final Map<Haplotype, Difference> map = new HaplotypeTransformer(compare).compareToMaster(MASTER_SEQUENCE);
		Assert.assertNotNull(map);
		Assert.assertEquals(1, map.size());
		final Entry<Haplotype, Difference> entry = map.entrySet().iterator().next();
		final Haplotype haplotype = entry.getKey();
		Assert.assertEquals(compare.get(0), haplotype);
		final Difference diff = entry.getValue();
		Assert.assertNotNull(diff);
		Assert.assertEquals(new Difference(MASTER_SEQUENCE, DIFF_SEQUENCE), diff);
	}

	@Test
	public void compMore() throws Exception {
		final Sequence endSequence = new Sequence("ABCC", "EndDiff");
		final List<Sequence> compare = Arrays.asList(MASTER_SEQUENCE, DIFF_SEQUENCE, copy(MASTER_SEQUENCE, "Master2"), copy(DIFF_SEQUENCE, "Different2"), endSequence);
		final List<Haplotype> haplotypes = Haplotype.createHaplotypes(compare);
		final Map<Haplotype, Difference> map = new HaplotypeTransformer(haplotypes).compareToMaster(MASTER_SEQUENCE);
		Assert.assertNotNull(map);
		Assert.assertEquals(3, map.size());
		final Difference sameDiff = map.get(haplotypes.get(0));
		Assert.assertNotNull(sameDiff);
		Assert.assertEquals(new Difference(MASTER_SEQUENCE, MASTER_SEQUENCE), sameDiff);
		final Difference diffDiff = map.get(haplotypes.get(1));
		Assert.assertNotNull(diffDiff);
		Assert.assertEquals(new Difference(MASTER_SEQUENCE, DIFF_SEQUENCE), diffDiff);
		final Difference endDiff = map.get(haplotypes.get(2));
		Assert.assertNotNull(endDiff);
		Assert.assertEquals(new Difference(MASTER_SEQUENCE, endSequence), endDiff);
	}

	@Test
	public void idNull() throws Exception {
		final Map<Haplotype, Difference> map = new HaplotypeTransformer(Collections.singletonList(new Haplotype(MASTER_SEQUENCE))).compareToMaster((String) null);
		Assert.assertNull(map);
	}

	@Test
	public void idNotFound() throws Exception {
		final Map<Haplotype, Difference> map = new HaplotypeTransformer(Collections.singletonList(new Haplotype(MASTER_SEQUENCE))).compareToMaster("NotFound");
		Assert.assertNull(map);
	}

	@Test
	public void idFound() throws Exception {
		final Map<Haplotype, Difference> map = new HaplotypeTransformer(Collections.singletonList(new Haplotype(MASTER_SEQUENCE))).compareToMaster("Master");
		Assert.assertNotNull(map);
		Assert.assertEquals(1, map.size());
	}

}
