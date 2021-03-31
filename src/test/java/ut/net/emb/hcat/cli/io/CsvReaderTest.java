package ut.net.emb.hcat.cli.io;

import java.io.StringReader;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.emb.hcat.cli.io.sequence.CsvReader;
import net.emb.hcat.cli.sequence.Sequence;

@SuppressWarnings("javadoc")
public class CsvReaderTest {

	@Test
	public void readEmpty() throws Exception {
		final CsvReader reader = new CsvReader(new StringReader(""));
		final List<Sequence> sequences = reader.read();
		reader.close();

		Assert.assertNotNull(sequences);
		Assert.assertEquals(0, sequences.size());
	}

	@Test
	public void readEmptyWithDelimiter() throws Exception {
		final CsvReader reader = new CsvReader(new StringReader("sep=;"));
		final List<Sequence> sequences = reader.read();
		reader.close();

		Assert.assertNotNull(sequences);
		Assert.assertEquals(0, sequences.size());
		Assert.assertEquals(';', reader.getDelimiter());
	}

	@Test
	public void readSequence() throws Exception {
		final CsvReader reader = new CsvReader(new StringReader("A,C,G,T"));
		final List<Sequence> sequences = reader.read();
		reader.close();

		Assert.assertNotNull(sequences);
		Assert.assertEquals(1, sequences.size());
		final Sequence seq = sequences.get(0);
		Assert.assertEquals("ACGT", seq.getValue());
		Assert.assertEquals("1", seq.getName());
	}

	@Test
	public void readSequenceWithSemicolon() throws Exception {
		final CsvReader reader = new CsvReader(new StringReader("A;C;G;T"));
		reader.setDelimiter(';');
		final List<Sequence> sequences = reader.read();
		reader.close();

		Assert.assertNotNull(sequences);
		Assert.assertEquals(1, sequences.size());
		final Sequence seq = sequences.get(0);
		Assert.assertEquals("ACGT", seq.getValue());
		Assert.assertEquals("1", seq.getName());
	}

	@Test
	public void readSequenceWithTab() throws Exception {
		final CsvReader reader = new CsvReader(new StringReader("A\tC\tG\tT"));
		reader.setDelimiter('\t');
		final List<Sequence> sequences = reader.read();
		reader.close();

		Assert.assertNotNull(sequences);
		Assert.assertEquals(1, sequences.size());
		final Sequence seq = sequences.get(0);
		Assert.assertEquals("ACGT", seq.getValue());
		Assert.assertEquals("1", seq.getName());
	}

	@Test
	public void readSequenceWithDelimiter() throws Exception {
		final CsvReader reader = new CsvReader(new StringReader("sep=,\nA,C,G,T"));
		reader.setDelimiter(';');
		final List<Sequence> sequences = reader.read();
		reader.close();

		Assert.assertNotNull(sequences);
		Assert.assertEquals(1, sequences.size());
		final Sequence seq = sequences.get(0);
		Assert.assertEquals("ACGT", seq.getValue());
		Assert.assertEquals("1", seq.getName());
	}

	@Test
	public void readSequenceWithName() throws Exception {
		final CsvReader reader = new CsvReader(new StringReader("Test,A,C,G,T"));
		reader.setNameIncluded(true);
		final List<Sequence> sequences = reader.read();
		reader.close();

		Assert.assertNotNull(sequences);
		Assert.assertEquals(1, sequences.size());
		final Sequence seq = sequences.get(0);
		Assert.assertEquals("ACGT", seq.getValue());
		Assert.assertEquals("Test", seq.getName());
	}

	@Test
	public void readMultipleSequences() throws Exception {
		final CsvReader reader = new CsvReader(new StringReader("A,C,G,T\nT,G,C,A"));
		final List<Sequence> sequences = reader.read();
		reader.close();

		Assert.assertNotNull(sequences);
		Assert.assertEquals(2, sequences.size());
		final Sequence seq1 = sequences.get(0);
		Assert.assertEquals("ACGT", seq1.getValue());
		Assert.assertEquals("1", seq1.getName());
		final Sequence seq2 = sequences.get(1);
		Assert.assertEquals("TGCA", seq2.getValue());
		Assert.assertEquals("2", seq2.getName());
	}

}
