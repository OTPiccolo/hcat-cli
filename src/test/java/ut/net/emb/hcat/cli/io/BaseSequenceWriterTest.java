package ut.net.emb.hcat.cli.io;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.emb.hcat.cli.io.BaseSequenceWriter;
import net.emb.hcat.cli.sequence.Sequence;

@SuppressWarnings("javadoc")
public class BaseSequenceWriterTest {

	private static final String LINEBREAK = System.getProperty("line.separator");

	private static final Sequence SEQUENCE = new Sequence("ABCD");
	private static final Sequence SEQUENCE_NAMED = new Sequence("DCBA", "Name");

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void nullConstructor() throws Exception {
		new BaseSequenceWriter(null);
	}

	@Test
	public void writeEmpty() throws Exception {
		final StringWriter writer = new StringWriter();
		final BaseSequenceWriter fastaWriter = new BaseSequenceWriter(writer);
		fastaWriter.write(Collections.emptyList());
		Assert.assertEquals(0, writer.getBuffer().length());
		fastaWriter.write();
		Assert.assertEquals(0, writer.getBuffer().length());
	}

	@Test(expected = IllegalArgumentException.class)
	public void writeNullList() throws Exception {
		final StringWriter writer = new StringWriter();
		final BaseSequenceWriter fastaWriter = new BaseSequenceWriter(writer);
		fastaWriter.write((List<Sequence>) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void writeNullArray() throws Exception {
		final StringWriter writer = new StringWriter();
		final BaseSequenceWriter fastaWriter = new BaseSequenceWriter(writer);
		fastaWriter.write((Sequence[]) null);
	}

	@Test
	public void writeNamed() throws Exception {
		final StringWriter writer = new StringWriter();
		final BaseSequenceWriter fastaWriter = new BaseSequenceWriter(writer);
		fastaWriter.write(SEQUENCE_NAMED);
		final String output = writer.toString();
		Assert.assertEquals("Name\nDCBA\n".replace("\n", LINEBREAK), output);
	}

	@Test
	public void writeUnnamed() throws Exception {
		final StringWriter writer = new StringWriter();
		final BaseSequenceWriter fastaWriter = new BaseSequenceWriter(writer);
		fastaWriter.write(SEQUENCE);
		final String output = writer.toString();
		Assert.assertEquals("\nABCD\n".replace("\n", LINEBREAK), output);
	}

	@Test
	public void writeFull() throws Exception {
		final StringWriter writer = new StringWriter();
		final BaseSequenceWriter fastaWriter = new BaseSequenceWriter(writer);
		fastaWriter.setLineBreak(0);
		fastaWriter.write(SEQUENCE);
		final String output = writer.toString();
		Assert.assertEquals("\nABCD\n".replace("\n", LINEBREAK), output);
	}

	@Test
	public void writeEven() throws Exception {
		final StringWriter writer = new StringWriter();
		final BaseSequenceWriter fastaWriter = new BaseSequenceWriter(writer);
		fastaWriter.setLineBreak(2);
		fastaWriter.write(SEQUENCE);
		final String output = writer.toString();
		Assert.assertEquals("\nAB\nCD\n".replace("\n", LINEBREAK), output);
	}

	@Test
	public void writeUneven() throws Exception {
		final StringWriter writer = new StringWriter();
		final BaseSequenceWriter fastaWriter = new BaseSequenceWriter(writer);
		fastaWriter.setLineBreak(3);
		fastaWriter.write(SEQUENCE);
		final String output = writer.toString();
		Assert.assertEquals("\nABC\nD\n".replace("\n", LINEBREAK), output);
	}

	@Test
	public void writeList() throws Exception {
		final StringWriter writer = new StringWriter();
		final BaseSequenceWriter fastaWriter = new BaseSequenceWriter(writer);
		fastaWriter.write(Arrays.asList(SEQUENCE, SEQUENCE_NAMED));
		final String output = writer.toString();
		Assert.assertEquals("\nABCD\nName\nDCBA\n".replace("\n", LINEBREAK), output);
	}

	@Test
	public void writeArray() throws Exception {
		final StringWriter writer = new StringWriter();
		final BaseSequenceWriter fastaWriter = new BaseSequenceWriter(writer);
		fastaWriter.write(new Sequence[] { SEQUENCE, SEQUENCE_NAMED });
		final String output = writer.toString();
		Assert.assertEquals("\nABCD\nName\nDCBA\n".replace("\n", LINEBREAK), output);
	}

	@Test(expected = IllegalArgumentException.class)
	public void lineBreakNegative() throws Exception {
		final StringWriter writer = new StringWriter();
		final BaseSequenceWriter fastaWriter = new BaseSequenceWriter(writer);
		fastaWriter.setLineBreak(-1);
	}

}
