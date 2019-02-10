package ut.net.emb.hcat.cli.io;

import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;

import net.emb.hcat.cli.Sequence;
import net.emb.hcat.cli.io.FastaWriter;

@SuppressWarnings("javadoc")
public class FastaWriterTest {

	private static final String LINEBREAK = System.getProperty("line.separator");

	private static final Sequence SEQUENCE = new Sequence("ABCD");
	private static final Sequence SEQUENCE_NAMED = new Sequence("DCBA", "Name");

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void nullConstructor() throws Exception {
		new FastaWriter(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void writeNull() throws Exception {
		final StringWriter writer = new StringWriter();
		final FastaWriter fastaWriter = new FastaWriter(writer);
		fastaWriter.write((Sequence) null);
	}

	@Test
	public void writeNamed() throws Exception {
		final StringWriter writer = new StringWriter();
		final FastaWriter fastaWriter = new FastaWriter(writer);
		fastaWriter.write(SEQUENCE_NAMED);
		final String output = writer.toString();
		Assert.assertEquals(">Name\nDCBA\n".replace("\n", LINEBREAK), output);
	}

	@Test
	public void writeUnnamed() throws Exception {
		final StringWriter writer = new StringWriter();
		final FastaWriter fastaWriter = new FastaWriter(writer);
		fastaWriter.write(SEQUENCE);
		final String output = writer.toString();
		Assert.assertEquals(">\nABCD\n".replace("\n", LINEBREAK), output);
	}

}