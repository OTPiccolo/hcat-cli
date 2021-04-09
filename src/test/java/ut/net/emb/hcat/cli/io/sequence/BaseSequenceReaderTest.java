package ut.net.emb.hcat.cli.io.sequence;

import java.io.StringReader;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.emb.hcat.cli.ErrorCodeException;
import net.emb.hcat.cli.ErrorCodeException.EErrorCode;
import net.emb.hcat.cli.io.sequence.BaseSequenceReader;
import net.emb.hcat.cli.sequence.Sequence;

@SuppressWarnings("javadoc")
public class BaseSequenceReaderTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private static final String STANDARD_VALUE = "ABCD";
	private static final String STANDARD_2_VALUE = "DCBA";
	private static final String SHORT_VALUE = "ABC";
	private static final String LONG_VALUE = "ABCDE";

	private static final String getStandard() {
		final StringBuilder builder = new StringBuilder();
		builder.append(STANDARD_VALUE);
		builder.append('\n');
		return builder.toString();
	}

	private static final String getStandard2() {
		final StringBuilder builder = new StringBuilder();
		builder.append(STANDARD_2_VALUE);
		builder.append('\n');
		return builder.toString();
	}

	private static final String getShort() {
		final StringBuilder builder = new StringBuilder();
		builder.append(SHORT_VALUE);
		builder.append('\n');
		return builder.toString();
	}

	private static final String getLong() {
		final StringBuilder builder = new StringBuilder();
		builder.append(LONG_VALUE);
		builder.append('\n');
		return builder.toString();
	}

	@Test
	public void readEmpty() throws Exception {
		final BaseSequenceReader baseReader = new BaseSequenceReader(new StringReader(""));
		final List<Sequence> sequences = baseReader.read();
		baseReader.close();
		Assert.assertNotNull(sequences);
		Assert.assertEquals(0, sequences.size());
	}

	@Test
	public void readStandard() throws Exception {
		final BaseSequenceReader baseReader = new BaseSequenceReader(new StringReader(getStandard()));
		final List<Sequence> sequences = baseReader.read();
		baseReader.close();
		Assert.assertNotNull(sequences);
		Assert.assertEquals(1, sequences.size());
		Assert.assertEquals("1", sequences.get(0).getName());
		Assert.assertEquals(STANDARD_VALUE, sequences.get(0).getValue());
		Assert.assertEquals(STANDARD_VALUE.length(), sequences.get(0).getLength());
	}

	@Test
	public void readAll() throws Exception {
		final BaseSequenceReader baseReader = new BaseSequenceReader(new StringReader(getStandard() + getStandard2() + getShort() + getLong()));
		final List<Sequence> sequences = baseReader.read();
		baseReader.close();
		Assert.assertNotNull(sequences);
		Assert.assertEquals(4, sequences.size());
		final Sequence standardSeq = sequences.get(0);
		Assert.assertEquals("1", standardSeq.getName());
		Assert.assertEquals(STANDARD_VALUE, standardSeq.getValue());
		Assert.assertEquals(STANDARD_VALUE.length(), standardSeq.getLength());
		final Sequence standard2Seq = sequences.get(1);
		Assert.assertEquals("2", standard2Seq.getName());
		Assert.assertEquals(STANDARD_2_VALUE, standard2Seq.getValue());
		Assert.assertEquals(STANDARD_2_VALUE.length(), standard2Seq.getLength());
		final Sequence shortSeq = sequences.get(2);
		Assert.assertEquals("3", shortSeq.getName());
		Assert.assertEquals(SHORT_VALUE, shortSeq.getValue());
		Assert.assertEquals(SHORT_VALUE.length(), shortSeq.getLength());
		final Sequence longSeq = sequences.get(3);
		Assert.assertEquals("4", longSeq.getName());
		Assert.assertEquals(LONG_VALUE, longSeq.getValue());
		Assert.assertEquals(LONG_VALUE.length(), longSeq.getLength());
	}

	@Test
	public void enforceSameLengthValid() throws Exception {
		final BaseSequenceReader baseReader = new BaseSequenceReader(new StringReader(getStandard() + getStandard2()));
		baseReader.setEnforceSameLength(true);
		final List<Sequence> sequences = baseReader.read();
		baseReader.close();
		Assert.assertEquals(2, sequences.size());
	}

	@Test
	public void enforceSameLengthInvalidShort() throws Exception {
		thrown.expect(ErrorCodeException.class);
		thrown.expect(Matchers.hasProperty("errorCode", Matchers.is(EErrorCode.SEQUENCE_WRONG_LENGTH)));
		thrown.expect(Matchers.hasProperty("values", Matchers.arrayContaining(new Sequence(SHORT_VALUE), 1, 4)));

		final BaseSequenceReader baseReader = new BaseSequenceReader(new StringReader(getStandard() + getShort()));
		baseReader.setEnforceSameLength(true);
		baseReader.read();
		baseReader.close();
	}

	@Test
	public void enforceSameLengthInvalidLong() throws Exception {
		thrown.expect(ErrorCodeException.class);
		thrown.expect(Matchers.hasProperty("errorCode", Matchers.is(EErrorCode.SEQUENCE_WRONG_LENGTH)));
		thrown.expect(Matchers.hasProperty("values", Matchers.arrayContaining(new Sequence(LONG_VALUE), 1, 4)));

		final BaseSequenceReader baseReader = new BaseSequenceReader(new StringReader(getStandard() + getLong()));
		baseReader.setEnforceSameLength(true);
		baseReader.read();
		baseReader.close();
	}

}
