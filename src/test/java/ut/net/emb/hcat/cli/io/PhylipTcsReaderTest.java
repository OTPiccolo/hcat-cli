package ut.net.emb.hcat.cli.io;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.emb.hcat.cli.io.PhylipTcsReader;
import net.emb.hcat.cli.sequence.Sequence;

@SuppressWarnings("javadoc")
public class PhylipTcsReaderTest {

	@Test(expected = IOException.class)
	public void emptyStream() throws Exception {
		final PhylipTcsReader reader = new PhylipTcsReader(new StringReader(""));
		reader.read();
		reader.close();
	}

	@Test(expected = IOException.class)
	public void illegalHeader1() throws Exception {
		final PhylipTcsReader reader = new PhylipTcsReader(new StringReader("a1    1"));
		reader.read();
		reader.close();
	}

	@Test(expected = IOException.class)
	public void illegalHeader2() throws Exception {
		final PhylipTcsReader reader = new PhylipTcsReader(new StringReader("1    a1"));
		reader.read();
		reader.close();
	}

	@Test(expected = IOException.class)
	public void illegalHeader3() throws Exception {
		final PhylipTcsReader reader = new PhylipTcsReader(new StringReader("0     1"));
		reader.read();
		reader.close();
	}

	@Test(expected = IOException.class)
	public void illegalHeader4() throws Exception {
		final PhylipTcsReader reader = new PhylipTcsReader(new StringReader("0   1"));
		reader.read();
		reader.close();
	}

	@Test
	public void zeroSeqHeader() throws Exception {
		final PhylipTcsReader reader = new PhylipTcsReader(new StringReader("0    1"));
		final List<Sequence> sequences = reader.read();
		reader.close();
		Assert.assertNotNull(sequences);
		Assert.assertEquals(0, sequences.size());
	}

	@Test
	public void singleSeq() throws Exception {
		final PhylipTcsReader reader = new PhylipTcsReader(new StringReader("1    4\nSingle\nABCD"));
		final List<Sequence> sequences = reader.read();
		reader.close();
		Assert.assertNotNull(sequences);
		Assert.assertEquals(1, sequences.size());
	}

	@Test
	public void multipleSeq() throws Exception {
		final PhylipTcsReader reader = new PhylipTcsReader(new StringReader("2    4\nSeq1\nABCD\nSeq2\nBCDE"));
		final List<Sequence> sequences = reader.read();
		reader.close();
		Assert.assertNotNull(sequences);
		Assert.assertEquals(2, sequences.size());
	}

	@Test(expected = IOException.class)
	public void wrongSeqCount() throws Exception {
		final PhylipTcsReader reader = new PhylipTcsReader(new StringReader("2    1\nSingle\nABCD"));
		reader.read();
		reader.close();
	}

	@Test(expected = IOException.class)
	public void wrongSeqLength1() throws Exception {
		final PhylipTcsReader reader = new PhylipTcsReader(new StringReader("1    3\nSingle\nABCD"));
		reader.read();
		reader.close();
	}

	@Test(expected = IOException.class)
	public void wrongSeqLength2() throws Exception {
		final PhylipTcsReader reader = new PhylipTcsReader(new StringReader("1    5\nSingle\nABCD"));
		reader.read();
		reader.close();
	}

	@Test
	public void maxLengthName() throws Exception {
		final PhylipTcsReader reader = new PhylipTcsReader(new StringReader("1    4\nMax   Len\nABCD"));
		final List<Sequence> sequences = reader.read();
		reader.close();
		Assert.assertNotNull(sequences);
		Assert.assertEquals(1, sequences.size());
	}

	@Test(expected = IOException.class)
	public void overMaxLengthName() throws Exception {
		final PhylipTcsReader reader = new PhylipTcsReader(new StringReader("1    4\nOver   Max\nABCD"));
		reader.read();
		reader.close();
	}

	@Test(expected = IOException.class)
	public void missingLine() throws Exception {
		final PhylipTcsReader reader = new PhylipTcsReader(new StringReader("2    4\nThere\nABCD\nNotThere"));
		reader.read();
		reader.close();
	}

	@Test
	public void readTestData() throws Exception {
		List<Sequence> sequences;
		try (Reader reader = new InputStreamReader(getClass().getResourceAsStream("/phylip-tcs-testdata.phy"), StandardCharsets.UTF_8)) {
			final PhylipTcsReader phylipReader = new PhylipTcsReader(reader);
			sequences = phylipReader.read();
			phylipReader.close();
		}

		Assert.assertNotNull(sequences);
		Assert.assertEquals(208, sequences.size());
	}

}
