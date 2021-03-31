package ut.net.emb.hcat.cli.io;

import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;

import net.emb.hcat.cli.io.sequence.FastaWriter;
import net.emb.hcat.cli.sequence.Sequence;

@SuppressWarnings("javadoc")
public class FastaWriterTest {

	private static final String LINEBREAK = System.getProperty("line.separator");

	private static final Sequence SEQUENCE = new Sequence("ABCD");
	private static final Sequence SEQUENCE_NAMED = new Sequence("DCBA", "Name");

	@SuppressWarnings({ "unused", "resource" })
	@Test(expected = IllegalArgumentException.class)
	public void nullConstructor() throws Exception {
		new FastaWriter(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void writeNull() throws Exception {
		final StringWriter writer = new StringWriter();
		final FastaWriter fastaWriter = new FastaWriter(writer);
		fastaWriter.write((Sequence) null);
		fastaWriter.close();
	}

	@Test
	public void writeNamed() throws Exception {
		final StringWriter writer = new StringWriter();
		final FastaWriter fastaWriter = new FastaWriter(writer);
		fastaWriter.write(SEQUENCE_NAMED);
		fastaWriter.close();
		final String output = writer.toString();
		Assert.assertEquals(">Name\nDCBA\n".replace("\n", LINEBREAK), output);
	}

	@Test
	public void writeUnnamed() throws Exception {
		final StringWriter writer = new StringWriter();
		final FastaWriter fastaWriter = new FastaWriter(writer);
		fastaWriter.write(SEQUENCE);
		fastaWriter.close();
		final String output = writer.toString();
		Assert.assertEquals(">\nABCD\n".replace("\n", LINEBREAK), output);
	}

	@Test
	public void writeMultiple() throws Exception {
		final StringWriter writer = new StringWriter();
		final FastaWriter fastaWriter = new FastaWriter(writer);
		fastaWriter.write(SEQUENCE, SEQUENCE_NAMED);
		fastaWriter.close();
		final String output = writer.toString();
		Assert.assertEquals(">\nABCD\n>Name\nDCBA\n".replace("\n", LINEBREAK), output);
	}

}
