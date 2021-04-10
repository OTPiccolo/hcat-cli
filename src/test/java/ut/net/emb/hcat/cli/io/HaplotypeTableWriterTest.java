package ut.net.emb.hcat.cli.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import net.emb.hcat.cli.haplotype.Haplotype;
import net.emb.hcat.cli.haplotype.HaplotypeTransformer;
import net.emb.hcat.cli.io.HaplotypeTableWriter;
import net.emb.hcat.cli.sequence.Difference;
import net.emb.hcat.cli.sequence.Sequence;

@SuppressWarnings("javadoc")
public class HaplotypeTableWriterTest {

	private static final Sequence MASTER_SEQUENCE = new Sequence("ABCDE", "Master");
	private static final Sequence MID_DIFF_SEQUENCE = new Sequence("ABBDE", "Mid");
	private static final Sequence MID2_DIFF_SEQUENCE = new Sequence("ABBDE", "Mid2");
	private static final Sequence FRONT_BACK_DIFF_SEQUENCE = new Sequence("BBCDD", "FrontAndBack");

	@Test
	public void testWriter() throws Exception {
		final List<Haplotype> haplotypes = Haplotype.wrap(Arrays.asList(FRONT_BACK_DIFF_SEQUENCE, MID_DIFF_SEQUENCE, MID2_DIFF_SEQUENCE, MASTER_SEQUENCE));
		final HaplotypeTransformer transformer = new HaplotypeTransformer(haplotypes);
		final Map<Haplotype, Difference> transformed = transformer.compareToMaster(MASTER_SEQUENCE);

		final ByteArrayOutputStream boas = new ByteArrayOutputStream(100);
		final HaplotypeTableWriter writer = new HaplotypeTableWriter(new OutputStreamWriter(boas));
		writer.write(MASTER_SEQUENCE, transformed);

		final StringBuilder builder = new StringBuilder(100);
		builder.append("Positions   \t1\t3\t5").append(System.lineSeparator());
		builder.append("Master      \tA\tC\tE").append(System.lineSeparator());
		builder.append("FrontAndBack\tB\t.\tD").append(System.lineSeparator());
		builder.append("Mid; Mid2   \t.\tB\t.").append(System.lineSeparator());

		Assert.assertEquals(builder.toString(), boas.toString());
	}

	@Test(expected = IOException.class)
	public void testMasterHaplotypeNotFound() throws Exception {
		final Map<Haplotype, Difference> map = new HashMap<>();
		map.put(new Haplotype(FRONT_BACK_DIFF_SEQUENCE), new Difference(MASTER_SEQUENCE, FRONT_BACK_DIFF_SEQUENCE));
		final HaplotypeTableWriter writer = new HaplotypeTableWriter(new OutputStreamWriter(System.out));
		writer.write(MASTER_SEQUENCE, map);
	}

}
