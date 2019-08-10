package ut.net.emb.hcat.cli.io;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

import net.emb.hcat.cli.io.PhylipTcsWriter;
import net.emb.hcat.cli.sequence.Sequence;

@SuppressWarnings("javadoc")
public class PhylipWriterTest {

	private static final String LINEBREAK = System.getProperty("line.separator");

	private static final Sequence SEQUENCE_NAMED = new Sequence("ABCD", "Name");
	private static final Sequence SEQUENCE_UNNAMED = new Sequence("DCBA");
	private static final Sequence SEQUENCE_TOO_SHORT = new Sequence("ABC");
	private static final Sequence SEQUENCE_TOO_LONG = new Sequence("ABCDE");

	@Test
	public void writeEmpty() throws Exception {
		final StringWriter writer = new StringWriter();
		final PhylipTcsWriter phylipTcsWriter = new PhylipTcsWriter(writer);
		phylipTcsWriter.write(Collections.emptyList());
		Assert.assertEquals("0    0\n".replace("\n", LINEBREAK), writer.toString());
	}

	@Test
	public void writeNamed() throws Exception {
		final StringWriter writer = new StringWriter();
		final PhylipTcsWriter phylipTcsWriter = new PhylipTcsWriter(writer);
		phylipTcsWriter.write(SEQUENCE_NAMED);
		final String output = writer.toString();
		Assert.assertEquals("1    4\nName\nABCD\n".replace("\n", LINEBREAK), output);
	}

	@Test
	public void writeUnnamed() throws Exception {
		final StringWriter writer = new StringWriter();
		final PhylipTcsWriter phylipTcsWriter = new PhylipTcsWriter(writer);
		phylipTcsWriter.write(SEQUENCE_UNNAMED);
		final String output = writer.toString();
		Assert.assertEquals("1    4\n\nDCBA\n".replace("\n", LINEBREAK), output);
	}

	@Test
	public void writeMultiple() throws Exception {
		final StringWriter writer = new StringWriter();
		final PhylipTcsWriter phylipTcsWriter = new PhylipTcsWriter(writer);
		phylipTcsWriter.write(Arrays.asList(SEQUENCE_NAMED, SEQUENCE_UNNAMED));
		final String output = writer.toString();
		Assert.assertEquals("2    4\nName\nABCD\n\nDCBA\n".replace("\n", LINEBREAK), output);
	}

	@Test(expected = IOException.class)
	public void writeSeqTooShort() throws Exception {
		final StringWriter writer = new StringWriter();
		final PhylipTcsWriter phylipTcsWriter = new PhylipTcsWriter(writer);
		phylipTcsWriter.write(Arrays.asList(SEQUENCE_NAMED, SEQUENCE_TOO_SHORT));
	}

	@Test(expected = IOException.class)
	public void writeSeqTooLong() throws Exception {
		final StringWriter writer = new StringWriter();
		final PhylipTcsWriter phylipTcsWriter = new PhylipTcsWriter(writer);
		phylipTcsWriter.write(Arrays.asList(SEQUENCE_NAMED, SEQUENCE_TOO_LONG));
	}

}
