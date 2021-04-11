package ut.net.emb.hcat.cli.io;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.emb.hcat.cli.haplotype.DistanceMatrix;
import net.emb.hcat.cli.haplotype.Haplotype;
import net.emb.hcat.cli.io.DistanceMatrixWriter;
import net.emb.hcat.cli.sequence.Sequence;

@SuppressWarnings("javadoc")
public class DistanceMatrixWriterTest {

	private static final Sequence MASTER_SEQUENCE = new Sequence("ABCDE", "Master");
	private static final Sequence MID_DIFF_SEQUENCE = new Sequence("ABBDE", "Mid");
	private static final Sequence FRONT_BACK_DIFF_SEQUENCE = new Sequence("BBCDD", "FrontAndBack");

	@Test
	public void testWriter() throws Exception {
		final List<Haplotype> haplotypes = Haplotype.wrap(Arrays.asList(MASTER_SEQUENCE, MID_DIFF_SEQUENCE, FRONT_BACK_DIFF_SEQUENCE));
		final DistanceMatrix matrix = new DistanceMatrix(haplotypes);

		final ByteArrayOutputStream boas = new ByteArrayOutputStream(100);
		final DistanceMatrixWriter writer = new DistanceMatrixWriter(new OutputStreamWriter(boas));
		writer.write(matrix);

		final StringBuilder builder = new StringBuilder(200);
		builder.append("HT-ID\tHap1\tHap2\tHap3").append(System.lineSeparator());
		builder.append("Hap1 \t-   \t1   \t2   ").append(System.lineSeparator());
		builder.append("Hap2 \t1   \t-   \t3   ").append(System.lineSeparator());
		builder.append("Hap3 \t2   \t3   \t-   ").append(System.lineSeparator());

		Assert.assertEquals(builder.toString(), boas.toString());
	}

	@Test
	public void testWriterBigDiff() throws Exception {
		final Haplotype hap1 = new Haplotype("Alphabetic");
		hap1.add(new Sequence("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
		final Haplotype hap2 = new Haplotype("BackwardAlphabetic");
		hap2.add(new Sequence("ZYXWVUTSRQPONMLKJIHGFEDCBA"));
		final DistanceMatrix matrix = new DistanceMatrix(Arrays.asList(hap1, hap2));

		final ByteArrayOutputStream boas = new ByteArrayOutputStream(200);
		final DistanceMatrixWriter writer = new DistanceMatrixWriter(new OutputStreamWriter(boas));
		writer.write(matrix);

		final StringBuilder builder = new StringBuilder(200);
		builder.append("HT-ID             \tAlphabetic\tBackwardAlphabetic").append(System.lineSeparator());
		builder.append("Alphabetic        \t-         \t26                ").append(System.lineSeparator());
		builder.append("BackwardAlphabetic\t26        \t-                 ").append(System.lineSeparator());

		Assert.assertEquals(builder.toString(), boas.toString());
	}

	@Test
	public void testWriterNames() throws Exception {
		final Haplotype hap1 = new Haplotype(new Sequence("ABCD"));
		final Haplotype hap2 = new Haplotype(new Sequence("ABBD"));
		hap2.setName("Hap");
		final Haplotype hap3 = new Haplotype(new Sequence("ACCD"));
		hap3.setName("VeryLongName");
		final DistanceMatrix matrix = new DistanceMatrix(Arrays.asList(hap1, hap2, hap3));

		final ByteArrayOutputStream boas = new ByteArrayOutputStream(200);
		final DistanceMatrixWriter writer = new DistanceMatrixWriter(new OutputStreamWriter(boas));
		writer.write(matrix);

		final StringBuilder builder = new StringBuilder(200);
		builder.append("HT-ID       \tnull\tHap \tVeryLongName").append(System.lineSeparator());
		builder.append("null        \t-   \t1   \t1           ").append(System.lineSeparator());
		builder.append("Hap         \t1   \t-   \t2           ").append(System.lineSeparator());
		builder.append("VeryLongName\t1   \t2   \t-           ").append(System.lineSeparator());

		Assert.assertEquals(builder.toString(), boas.toString());
	}

}
