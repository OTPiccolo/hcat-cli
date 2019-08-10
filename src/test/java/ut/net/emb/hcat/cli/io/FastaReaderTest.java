package ut.net.emb.hcat.cli.io;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.emb.hcat.cli.io.FastaReader;
import net.emb.hcat.cli.sequence.Sequence;

@SuppressWarnings("javadoc")
public class FastaReaderTest {

	private static final char ID_CHAR = '>';
	private static final char COMMENT_CHAR = ';';

	private static final String STANDARD_ID = "Standard";
	private static final String STANDARD_VALUE = "ABCD";

	private static final String COMMENT_ID = "Comment";
	private static final String COMMENT_COMMENT = "This is a comment.";
	private static final String COMMENT_VALUE = "EFGH";

	private static final String LONG_ID = "Long";
	private static final String LONG_VALUE = "ABCDEFGH";

	private static final String getStandard() {
		final StringBuilder builder = new StringBuilder();
		builder.append(ID_CHAR);
		builder.append(STANDARD_ID);
		builder.append('\n');
		builder.append(STANDARD_VALUE);
		builder.append('\n');
		return builder.toString();
	}

	private static final String getComment() {
		final StringBuilder builder = new StringBuilder();
		builder.append(ID_CHAR);
		builder.append(COMMENT_ID);
		builder.append('\n');
		builder.append(COMMENT_CHAR);
		builder.append(COMMENT_COMMENT);
		builder.append('\n');
		builder.append(COMMENT_VALUE);
		builder.append('\n');
		return builder.toString();
	}

	private static final String getLong() {
		final StringBuilder builder = new StringBuilder();
		builder.append(ID_CHAR);
		builder.append(LONG_ID);
		builder.append('\n');
		builder.append(LONG_VALUE);
		builder.append('\n');
		return builder.toString();
	}

	@Test
	public void readEmpty() throws Exception {
		final FastaReader fasta = new FastaReader(new StringReader(""));
		final List<Sequence> sequences = fasta.read();
		fasta.close();
		Assert.assertNotNull(sequences);
		Assert.assertEquals(0, sequences.size());
	}

	@Test
	public void readStandard() throws Exception {
		final FastaReader fasta = new FastaReader(new StringReader(getStandard()));
		final List<Sequence> sequences = fasta.read();
		fasta.close();
		Assert.assertNotNull(sequences);
		Assert.assertEquals(1, sequences.size());
		Assert.assertEquals(STANDARD_ID, sequences.get(0).getName());
		Assert.assertEquals(STANDARD_VALUE, sequences.get(0).getValue());
		Assert.assertEquals(STANDARD_VALUE.length(), sequences.get(0).getLength());
	}

	@Test
	public void readComment() throws Exception {
		final FastaReader fasta = new FastaReader(new StringReader(getComment()));
		final List<Sequence> sequences = fasta.read();
		fasta.close();
		Assert.assertNotNull(sequences);
		Assert.assertEquals(1, sequences.size());
		Assert.assertEquals(COMMENT_ID, sequences.get(0).getName());
		Assert.assertEquals(COMMENT_VALUE, sequences.get(0).getValue());
		Assert.assertEquals(COMMENT_VALUE.length(), sequences.get(0).getLength());
	}

	@Test
	public void readLong() throws Exception {
		final FastaReader fasta = new FastaReader(new StringReader(getLong()));
		final List<Sequence> sequences = fasta.read();
		fasta.close();
		Assert.assertNotNull(sequences);
		Assert.assertEquals(1, sequences.size());
		Assert.assertEquals(LONG_ID, sequences.get(0).getName());
		Assert.assertEquals(LONG_VALUE, sequences.get(0).getValue());
		Assert.assertEquals(LONG_VALUE.length(), sequences.get(0).getLength());
	}

	@Test
	public void readAll() throws Exception {
		final FastaReader fasta = new FastaReader(new StringReader(getStandard() + getComment() + getLong()));
		final List<Sequence> sequences = fasta.read();
		fasta.close();
		Assert.assertNotNull(sequences);
		Assert.assertEquals(3, sequences.size());
		Assert.assertEquals(STANDARD_ID, sequences.get(0).getName());
		Assert.assertEquals(STANDARD_VALUE, sequences.get(0).getValue());
		Assert.assertEquals(STANDARD_VALUE.length(), sequences.get(0).getLength());
		Assert.assertEquals(COMMENT_ID, sequences.get(1).getName());
		Assert.assertEquals(COMMENT_VALUE, sequences.get(1).getValue());
		Assert.assertEquals(COMMENT_VALUE.length(), sequences.get(1).getLength());
		Assert.assertEquals(LONG_ID, sequences.get(2).getName());
		Assert.assertEquals(LONG_VALUE, sequences.get(2).getValue());
		Assert.assertEquals(LONG_VALUE.length(), sequences.get(2).getLength());
	}

	@Test
	public void enforceSameLengthValid() throws Exception {
		final FastaReader fasta = new FastaReader(new StringReader(getStandard() + getComment()));
		fasta.setEnforceSameLength(true);
		final List<Sequence> sequences = fasta.read();
		fasta.close();
		Assert.assertEquals(2, sequences.size());
	}

	@Test(expected = IOException.class)
	public void enforceSameLengthInvalid() throws Exception {
		final FastaReader fasta = new FastaReader(new StringReader(getStandard() + getLong()));
		fasta.setEnforceSameLength(true);
		fasta.read();
		fasta.close();
	}

	@Test
	public void readTestData1() throws Exception {
		List<Sequence> sequences;
		try (Reader reader = new InputStreamReader(getClass().getResourceAsStream("/fasta-testdata1.txt"), StandardCharsets.UTF_8)) {
			final FastaReader fasta = new FastaReader(reader);
			fasta.setEnforceSameLength(true);
			sequences = fasta.read();
			fasta.close();
		}

		Assert.assertNotNull(sequences);
		Assert.assertEquals(35, sequences.size());
	}

	@Test
	public void readTestData2() throws Exception {
		List<Sequence> sequences;
		try (Reader reader = new InputStreamReader(getClass().getResourceAsStream("/fasta-testdata2.fas"), StandardCharsets.UTF_8)) {
			final FastaReader fasta = new FastaReader(reader);
			fasta.setEnforceSameLength(true);
			sequences = fasta.read();
			fasta.close();
		}

		Assert.assertNotNull(sequences);
		Assert.assertEquals(34, sequences.size());
	}

}
