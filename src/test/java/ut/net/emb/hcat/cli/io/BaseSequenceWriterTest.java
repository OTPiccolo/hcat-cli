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

	@SuppressWarnings({ "unused", "resource" })
	@Test(expected = IllegalArgumentException.class)
	public void nullConstructor() throws Exception {
		new BaseSequenceWriter(null);
	}

	@Test
	public void writeEmpty() throws Exception {
		final StringWriter writer = new StringWriter();
		final BaseSequenceWriter baseWriter = new BaseSequenceWriter(writer);
		baseWriter.write(Collections.emptyList());
		Assert.assertEquals(0, writer.getBuffer().length());
		baseWriter.write();
		Assert.assertEquals(0, writer.getBuffer().length());
		baseWriter.close();
	}

	@Test(expected = IllegalArgumentException.class)
	public void writeNullList() throws Exception {
		final StringWriter writer = new StringWriter();
		final BaseSequenceWriter baseWriter = new BaseSequenceWriter(writer);
		baseWriter.write((List<Sequence>) null);
		baseWriter.close();
	}

	@Test(expected = IllegalArgumentException.class)
	public void writeNullArray() throws Exception {
		final StringWriter writer = new StringWriter();
		final BaseSequenceWriter baseWriter = new BaseSequenceWriter(writer);
		baseWriter.write((Sequence[]) null);
		baseWriter.close();
	}

	@Test
	public void writeNamed() throws Exception {
		final StringWriter writer = new StringWriter();
		final BaseSequenceWriter baseWriter = new BaseSequenceWriter(writer);
		baseWriter.write(SEQUENCE_NAMED);
		baseWriter.close();
		final String output = writer.toString();
		Assert.assertEquals("Name\nDCBA\n".replace("\n", LINEBREAK), output);
	}

	@Test
	public void writeUnnamed() throws Exception {
		final StringWriter writer = new StringWriter();
		final BaseSequenceWriter baseWriter = new BaseSequenceWriter(writer);
		baseWriter.write(SEQUENCE);
		baseWriter.close();
		final String output = writer.toString();
		Assert.assertEquals("\nABCD\n".replace("\n", LINEBREAK), output);
	}

	@Test
	public void writeFull() throws Exception {
		final StringWriter writer = new StringWriter();
		final BaseSequenceWriter baseWriter = new BaseSequenceWriter(writer);
		baseWriter.setLineBreak(0);
		baseWriter.write(SEQUENCE);
		baseWriter.close();
		final String output = writer.toString();
		Assert.assertEquals("\nABCD\n".replace("\n", LINEBREAK), output);
	}

	@Test
	public void writeEven() throws Exception {
		final StringWriter writer = new StringWriter();
		final BaseSequenceWriter baseWriter = new BaseSequenceWriter(writer);
		baseWriter.setLineBreak(2);
		baseWriter.write(SEQUENCE);
		baseWriter.close();
		final String output = writer.toString();
		Assert.assertEquals("\nAB\nCD\n".replace("\n", LINEBREAK), output);
	}

	@Test
	public void writeUneven() throws Exception {
		final StringWriter writer = new StringWriter();
		final BaseSequenceWriter baseWriter = new BaseSequenceWriter(writer);
		baseWriter.setLineBreak(3);
		baseWriter.write(SEQUENCE);
		baseWriter.close();
		final String output = writer.toString();
		Assert.assertEquals("\nABC\nD\n".replace("\n", LINEBREAK), output);
	}

	@Test
	public void writeList() throws Exception {
		final StringWriter writer = new StringWriter();
		final BaseSequenceWriter baseWriter = new BaseSequenceWriter(writer);
		baseWriter.write(Arrays.asList(SEQUENCE, SEQUENCE_NAMED));
		baseWriter.close();
		final String output = writer.toString();
		Assert.assertEquals("\nABCD\nName\nDCBA\n".replace("\n", LINEBREAK), output);
	}

	@Test
	public void writeArray() throws Exception {
		final StringWriter writer = new StringWriter();
		final BaseSequenceWriter baseWriter = new BaseSequenceWriter(writer);
		baseWriter.write(new Sequence[] { SEQUENCE, SEQUENCE_NAMED });
		baseWriter.close();
		final String output = writer.toString();
		Assert.assertEquals("\nABCD\nName\nDCBA\n".replace("\n", LINEBREAK), output);
	}

	@Test(expected = IllegalArgumentException.class)
	public void lineBreakNegative() throws Exception {
		final StringWriter writer = new StringWriter();
		final BaseSequenceWriter baseWriter = new BaseSequenceWriter(writer);
		baseWriter.setLineBreak(-1);
		baseWriter.close();
	}

}
