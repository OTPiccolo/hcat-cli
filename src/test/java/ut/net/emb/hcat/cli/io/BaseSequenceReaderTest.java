package ut.net.emb.hcat.cli.io;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.emb.hcat.cli.io.BaseSequenceReader;
import net.emb.hcat.cli.sequence.Sequence;

@SuppressWarnings("javadoc")
public class BaseSequenceReaderTest {

	private static final String STANDARD_ID = "Standard";
	private static final String STANDARD_VALUE = "ABCD";
	private static final String STANDARD_2_ID = "Standard2";
	private static final String STANDARD_2_VALUE = "DCBA";
	private static final String SHORT_ID = "Short";
	private static final String SHORT_VALUE = "ABC";
	private static final String LONG_ID = "Long";
	private static final String LONG_VALUE = "ABCDE";

	private static final String getStandard() {
		final StringBuilder builder = new StringBuilder();
		builder.append(STANDARD_ID);
		builder.append('\n');
		builder.append(STANDARD_VALUE);
		builder.append('\n');
		return builder.toString();
	}

	private static final String getStandard2() {
		final StringBuilder builder = new StringBuilder();
		builder.append(STANDARD_2_ID);
		builder.append('\n');
		builder.append(STANDARD_2_VALUE);
		builder.append('\n');
		return builder.toString();
	}

	private static final String getShort() {
		final StringBuilder builder = new StringBuilder();
		builder.append(SHORT_ID);
		builder.append('\n');
		builder.append(SHORT_VALUE);
		builder.append('\n');
		return builder.toString();
	}

	private static final String getLong() {
		final StringBuilder builder = new StringBuilder();
		builder.append(LONG_ID);
		builder.append('\n');
		builder.append(LONG_VALUE);
		builder.append('\n');
		return builder.toString();
	}

	@Test
	public void readEmpty() throws Exception {
		final BaseSequenceReader baseReader = new BaseSequenceReader(new StringReader(""));
		final List<Sequence> sequences = baseReader.read();
		Assert.assertNotNull(sequences);
		Assert.assertEquals(0, sequences.size());
	}

	@Test
	public void readStandard() throws Exception {
		final BaseSequenceReader baseReader = new BaseSequenceReader(new StringReader(getStandard()));
		final List<Sequence> sequences = baseReader.read();
		Assert.assertNotNull(sequences);
		Assert.assertEquals(1, sequences.size());
		Assert.assertEquals(STANDARD_ID, sequences.get(0).getName());
		Assert.assertEquals(STANDARD_VALUE, sequences.get(0).getValue());
		Assert.assertEquals(STANDARD_VALUE.length(), sequences.get(0).getLength());
	}

	@Test
	public void readAll() throws Exception {
		final BaseSequenceReader baseReader = new BaseSequenceReader(new StringReader(getStandard() + getStandard2() + getShort() + getLong()));
		final List<Sequence> sequences = baseReader.read();
		Assert.assertNotNull(sequences);
		Assert.assertEquals(4, sequences.size());
		final Sequence standardSeq = sequences.get(0);
		Assert.assertEquals(STANDARD_ID, standardSeq.getName());
		Assert.assertEquals(STANDARD_VALUE, standardSeq.getValue());
		Assert.assertEquals(STANDARD_VALUE.length(), standardSeq.getLength());
		final Sequence standard2Seq = sequences.get(1);
		Assert.assertEquals(STANDARD_2_ID, standard2Seq.getName());
		Assert.assertEquals(STANDARD_2_VALUE, standard2Seq.getValue());
		Assert.assertEquals(STANDARD_2_VALUE.length(), standard2Seq.getLength());
		final Sequence shortSeq = sequences.get(2);
		Assert.assertEquals(SHORT_ID, shortSeq.getName());
		Assert.assertEquals(SHORT_VALUE, shortSeq.getValue());
		Assert.assertEquals(SHORT_VALUE.length(), shortSeq.getLength());
		final Sequence longSeq = sequences.get(3);
		Assert.assertEquals(LONG_ID, longSeq.getName());
		Assert.assertEquals(LONG_VALUE, longSeq.getValue());
		Assert.assertEquals(LONG_VALUE.length(), longSeq.getLength());
	}

	@Test
	public void enforceSameLengthValid() throws Exception {
		final BaseSequenceReader baseReader = new BaseSequenceReader(new StringReader(getStandard() + getStandard2()));
		baseReader.setEnforceSameLength(true);
		final List<Sequence> sequences = baseReader.read();
		Assert.assertEquals(2, sequences.size());
	}

	@Test(expected = IOException.class)
	public void enforceSameLengthInvalidShort() throws Exception {
		final BaseSequenceReader baseReader = new BaseSequenceReader(new StringReader(getStandard() + getShort()));
		baseReader.setEnforceSameLength(true);
		baseReader.read();
	}

	@Test(expected = IOException.class)
	public void enforceSameLengthInvalidLong() throws Exception {
		final BaseSequenceReader baseReader = new BaseSequenceReader(new StringReader(getStandard() + getLong()));
		baseReader.setEnforceSameLength(true);
		baseReader.read();
	}

}
