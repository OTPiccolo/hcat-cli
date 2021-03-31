package ut.net.emb.hcat.cli.io;

import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;

import net.emb.hcat.cli.io.sequence.CsvWriter;
import net.emb.hcat.cli.sequence.Sequence;

@SuppressWarnings("javadoc")
public class CsvWriterTest {

	private static final String LINEBREAK = System.getProperty("line.separator");

	@Test
	public void writeEmpty() throws Exception {
		final StringWriter writer = new StringWriter();
		final CsvWriter csvWriter = new CsvWriter(writer);
		csvWriter.write();
		csvWriter.close();

		final String output = writer.toString();
		Assert.assertEquals("", output);
	}

	@Test
	public void writeEmptyWithDelimiter() throws Exception {
		final StringWriter writer = new StringWriter();
		final CsvWriter csvWriter = new CsvWriter(writer);
		csvWriter.setExcelHeader(true);
		csvWriter.write();
		csvWriter.close();

		final String output = writer.toString();
		Assert.assertEquals("sep=,\n".replace("\n", LINEBREAK), output);
	}

	@Test
	public void writeSequence() throws Exception {
		final StringWriter writer = new StringWriter();
		final CsvWriter csvWriter = new CsvWriter(writer);
		csvWriter.write(new Sequence("ACGT", "Test"));
		csvWriter.close();

		final String output = writer.toString();
		Assert.assertEquals("Test,A,C,G,T\n".replace("\n", LINEBREAK), output);
	}

	@Test
	public void writeSequenceWithSemicolon() throws Exception {
		final StringWriter writer = new StringWriter();
		final CsvWriter csvWriter = new CsvWriter(writer, ';');
		csvWriter.write(new Sequence("ACGT", "Test"));
		csvWriter.close();

		final String output = writer.toString();
		Assert.assertEquals("Test;A;C;G;T\n".replace("\n", LINEBREAK), output);
	}

	@Test
	public void writeSequenceWithTab() throws Exception {
		final StringWriter writer = new StringWriter();
		final CsvWriter csvWriter = new CsvWriter(writer, '\t');
		csvWriter.write(new Sequence("ACGT", "Test"));
		csvWriter.close();

		final String output = writer.toString();
		Assert.assertEquals("Test\tA\tC\tG\tT\n".replace("\n", LINEBREAK), output);
	}

	@Test
	public void writeSequenceWithDelimiter() throws Exception {
		final StringWriter writer = new StringWriter();
		final CsvWriter csvWriter = new CsvWriter(writer);
		csvWriter.setExcelHeader(true);
		csvWriter.write(new Sequence("ACGT", "Test"));
		csvWriter.close();

		final String output = writer.toString();
		Assert.assertEquals("sep=,\nTest,A,C,G,T\n".replace("\n", LINEBREAK), output);
	}

	@Test
	public void writeSequenceWithoutName() throws Exception {
		final StringWriter writer = new StringWriter();
		final CsvWriter csvWriter = new CsvWriter(writer);
		csvWriter.write(new Sequence("ACGT"));
		csvWriter.close();

		final String output = writer.toString();
		Assert.assertEquals(",A,C,G,T\n".replace("\n", LINEBREAK), output);
	}

	@Test
	public void writeMultipleSequences() throws Exception {
		final StringWriter writer = new StringWriter();
		final CsvWriter csvWriter = new CsvWriter(writer);
		csvWriter.write(new Sequence("ACGT", "Test1"), new Sequence("TGCA", "Test2"));
		csvWriter.close();

		final String output = writer.toString();
		Assert.assertEquals("Test1,A,C,G,T\nTest2,T,G,C,A\n".replace("\n", LINEBREAK), output);
	}

}
