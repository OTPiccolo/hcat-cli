package ut.net.emb.hcat.cli.io.sequence;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.emb.hcat.cli.ErrorCodeException;
import net.emb.hcat.cli.ErrorCodeException.EErrorCode;
import net.emb.hcat.cli.io.sequence.PhylipReader;
import net.emb.hcat.cli.sequence.Sequence;

@SuppressWarnings("javadoc")
public class PhylipReaderTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void emptyStream() throws Exception {
		thrown.expect(ErrorCodeException.class);
		thrown.expect(Matchers.hasProperty("errorCode", Matchers.is(EErrorCode.INVALID_HEADER)));
		thrown.expect(Matchers.hasProperty("values", Matchers.arrayContaining((Object) null)));

		final PhylipReader reader = new PhylipReader(new StringReader(""));
		reader.read();
		reader.close();
	}

	@Test
	public void illegalHeader1() throws Exception {
		thrown.expect(ErrorCodeException.class);
		thrown.expect(Matchers.hasProperty("errorCode", Matchers.is(EErrorCode.INVALID_HEADER)));
		thrown.expect(Matchers.hasProperty("values", Matchers.arrayContaining("a1    1")));

		final PhylipReader reader = new PhylipReader(new StringReader("a1    1"));
		reader.read();
		reader.close();
	}

	@Test
	public void illegalHeader2() throws Exception {
		thrown.expect(ErrorCodeException.class);
		thrown.expect(Matchers.hasProperty("errorCode", Matchers.is(EErrorCode.INVALID_HEADER)));
		thrown.expect(Matchers.hasProperty("values", Matchers.arrayContaining("1    a1")));

		final PhylipReader reader = new PhylipReader(new StringReader("1    a1"));
		reader.read();
		reader.close();
	}

	@Test
	public void illegalHeader3() throws Exception {
		thrown.expect(ErrorCodeException.class);
		thrown.expect(Matchers.hasProperty("errorCode", Matchers.is(EErrorCode.INVALID_HEADER)));
		thrown.expect(Matchers.hasProperty("values", Matchers.arrayContaining("0     1")));

		final PhylipReader reader = new PhylipReader(new StringReader("0     1"));
		reader.read();
		reader.close();
	}

	@Test
	public void illegalHeader4() throws Exception {
		thrown.expect(ErrorCodeException.class);
		thrown.expect(Matchers.hasProperty("errorCode", Matchers.is(EErrorCode.INVALID_HEADER)));
		thrown.expect(Matchers.hasProperty("values", Matchers.arrayContaining("0   1")));

		final PhylipReader reader = new PhylipReader(new StringReader("0   1"));
		reader.read();
		reader.close();
	}

	@Test
	public void zeroSeqHeader() throws Exception {
		final PhylipReader reader = new PhylipReader(new StringReader("0    1"));
		final List<Sequence> sequences = reader.read();
		reader.close();
		Assert.assertNotNull(sequences);
		Assert.assertEquals(0, sequences.size());
	}

	@Test
	public void singleSeq() throws Exception {
		final PhylipReader reader = new PhylipReader(new StringReader("1    4\nSingle\nABCD"));
		final List<Sequence> sequences = reader.read();
		reader.close();
		Assert.assertNotNull(sequences);
		Assert.assertEquals(1, sequences.size());
		Assert.assertEquals(new Sequence("ABCD", "Single"), sequences.get(0));
	}

	@Test
	public void multipleSeq() throws Exception {
		final PhylipReader reader = new PhylipReader(new StringReader("2    4\nSeq1\nABCD\nSeq2\nBCDE"));
		final List<Sequence> sequences = reader.read();
		reader.close();
		Assert.assertNotNull(sequences);
		Assert.assertEquals(2, sequences.size());
		Assert.assertEquals(new Sequence("ABCD", "Seq1"), sequences.get(0));
		Assert.assertEquals(new Sequence("BCDE", "Seq2"), sequences.get(1));
	}

	@Test
	public void multipleLines() throws Exception {
		final PhylipReader reader = new PhylipReader(new StringReader("3    4\nSeq1\nA\nBCD\nSeq2\nBC\nDE\nSeq3\nCDE\nF"));
		final List<Sequence> sequences = reader.read();
		reader.close();
		Assert.assertNotNull(sequences);
		Assert.assertEquals(3, sequences.size());
		Assert.assertEquals(new Sequence("ABCD", "Seq1"), sequences.get(0));
		Assert.assertEquals(new Sequence("BCDE", "Seq2"), sequences.get(1));
		Assert.assertEquals(new Sequence("CDEF", "Seq3"), sequences.get(2));
	}

	@Test
	public void wrongSeqCount() throws Exception {
		thrown.expect(ErrorCodeException.class);
		thrown.expect(Matchers.hasProperty("errorCode", Matchers.is(EErrorCode.SEQUENCES_WRONG_AMOUNT)));
		thrown.expect(Matchers.hasProperty("values", Matchers.arrayContaining(2, 1)));

		final PhylipReader reader = new PhylipReader(new StringReader("2    4\nSingle\nABCD"));
		reader.read();
		reader.close();
	}

	@Test
	public void wrongSeqLength1() throws Exception {
		thrown.expect(ErrorCodeException.class);
		thrown.expect(Matchers.hasProperty("errorCode", Matchers.is(EErrorCode.SEQUENCE_WRONG_LENGTH)));
		thrown.expect(Matchers.hasProperty("values", Matchers.arrayContaining(new Sequence("ABCD", "Single"), 2, 3)));

		final PhylipReader reader = new PhylipReader(new StringReader("1    3\nSingle\nABCD"));
		reader.read();
		reader.close();
	}

	@Test
	public void wrongSeqLength2() throws Exception {
		thrown.expect(ErrorCodeException.class);
		thrown.expect(Matchers.hasProperty("errorCode", Matchers.is(EErrorCode.SEQUENCE_WRONG_LENGTH)));
		thrown.expect(Matchers.hasProperty("values", Matchers.arrayContaining(new Sequence("ABCD", "Single"), 2, 5)));

		final PhylipReader reader = new PhylipReader(new StringReader("1    5\nSingle\nABCD"));
		reader.read();
		reader.close();
	}

	// Phylip Reader does not have maximum name length restriction. (In contrast
	// to Phylip TCS Readers)
	@Test
	public void ignoreMaxLengthName() throws Exception {
		final PhylipReader reader = new PhylipReader(new StringReader("1    4\nUnlimited Maximum Length Name\nABCD"));
		final List<Sequence> sequences = reader.read();
		reader.close();
		Assert.assertNotNull(sequences);
		Assert.assertEquals(1, sequences.size());
		final Sequence seq = sequences.get(0);
		Assert.assertEquals(new Sequence("ABCD", "Unlimited Maximum Length Name"), seq);
	}

	@Test
	public void missingLine() throws Exception {
		thrown.expect(ErrorCodeException.class);
		thrown.expect(Matchers.hasProperty("errorCode", Matchers.is(EErrorCode.MISSING_VALUE)));
		thrown.expect(Matchers.hasProperty("values", Matchers.emptyArray()));

		final PhylipReader reader = new PhylipReader(new StringReader("2    4\nThere\nABCD\nNotThere"));
		reader.read();
		reader.close();
	}

	@Test
	public void readTestData() throws Exception {
		List<Sequence> sequences;
		try (Reader reader = new InputStreamReader(getClass().getResourceAsStream("/phylip-tcs-testdata.phy"), StandardCharsets.UTF_8)) {
			final PhylipReader phylipReader = new PhylipReader(reader);
			sequences = phylipReader.read();
			phylipReader.close();
		}

		Assert.assertNotNull(sequences);
		Assert.assertEquals(208, sequences.size());
	}

}
