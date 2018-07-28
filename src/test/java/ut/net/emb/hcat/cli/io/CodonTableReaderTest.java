package ut.net.emb.hcat.cli.io;

import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import net.emb.hcat.cli.codon.CodonTransformationData;
import net.emb.hcat.cli.io.CodonTableReader;

@SuppressWarnings("javadoc")
public class CodonTableReaderTest {

	private static final Pattern ACGT_TRIPLE = Pattern.compile("^[ACGT]{3}$");

	private static final String NAME = "1. Test\n";
	private static final String AAS = "AAs  = FFLLSSSSYY**CC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG\n";
	private static final String STARTS = "Starts = ---M------**--*----M---------------M----------------------------\n";
	private static final String BASE1 = "Base1  = TTTTTTTTTTTTTTTTCCCCCCCCCCCCCCCCAAAAAAAAAAAAAAAAGGGGGGGGGGGGGGGG\n";
	private static final String BASE2 = "Base2  = TTTTCCCCAAAAGGGGTTTTCCCCAAAAGGGGTTTTCCCCAAAAGGGGTTTTCCCCAAAAGGGG\n";
	private static final String BASE3 = "Base3  = TCAGTCAGTCAGTCAGTCAGTCAGTCAGTCAGTCAGTCAGTCAGTCAGTCAGTCAGTCAGTCAG\n";

	@Test
	public void read() throws Exception {
		final StringBuilder builder = new StringBuilder();
		builder.append(NAME);
		builder.append(AAS);
		builder.append(STARTS);
		builder.append(BASE1);
		builder.append(BASE2);
		builder.append(BASE3);

		final StringReader reader = new StringReader(builder.toString());
		final CodonTableReader codonReader = new CodonTableReader(reader);
		final List<CodonTransformationData> list = codonReader.read();

		Assert.assertEquals(1, list.size());
		final CodonTransformationData data = list.get(0);
		Assert.assertEquals("Test", data.name);
		Assert.assertEquals(1, data.number);
		Assert.assertEquals(64, data.codon.size());
		Assert.assertEquals(3, data.start.size());
		Assert.assertEquals(3, data.end.size());

		for (final String key : data.codon.keySet()) {
			Assert.assertTrue(ACGT_TRIPLE.matcher(key).matches());
		}
		for (final String key : data.start.keySet()) {
			Assert.assertTrue(ACGT_TRIPLE.matcher(key).matches());
		}
		for (final String key : data.end.keySet()) {
			Assert.assertTrue(ACGT_TRIPLE.matcher(key).matches());
		}
	}

	@Test
	public void readAnyOrder() throws Exception {
		final StringBuilder builder = new StringBuilder();
		builder.append(BASE3);
		builder.append(BASE2);
		builder.append(BASE1);
		builder.append(STARTS);
		builder.append(AAS);
		builder.append(NAME);

		final StringReader reader = new StringReader(builder.toString());
		final CodonTableReader codonReader = new CodonTableReader(reader);
		final List<CodonTransformationData> list = codonReader.read();

		Assert.assertEquals(1, list.size());
		final CodonTransformationData data = list.get(0);
		Assert.assertEquals("Test", data.name);
		Assert.assertEquals(1, data.number);
		Assert.assertEquals(64, data.codon.size());
		Assert.assertEquals(3, data.start.size());
		Assert.assertEquals(3, data.end.size());

		for (final String key : data.codon.keySet()) {
			Assert.assertTrue(ACGT_TRIPLE.matcher(key).matches());
		}
		for (final String key : data.start.keySet()) {
			Assert.assertTrue(ACGT_TRIPLE.matcher(key).matches());
		}
		for (final String key : data.end.keySet()) {
			Assert.assertTrue(ACGT_TRIPLE.matcher(key).matches());
		}
	}

	@Test
	public void readNameMissing() throws Exception {
		final StringBuilder builder = new StringBuilder();
		builder.append(AAS);
		builder.append(STARTS);
		builder.append(BASE1);
		builder.append(BASE2);
		builder.append(BASE3);

		final StringReader reader = new StringReader(builder.toString());
		final CodonTableReader codonReader = new CodonTableReader(reader);
		final List<CodonTransformationData> list = codonReader.read();

		Assert.assertEquals(0, list.size());
	}

	@Test
	public void readAAsMissing() throws Exception {
		final StringBuilder builder = new StringBuilder();
		builder.append(NAME);
		builder.append(STARTS);
		builder.append(BASE1);
		builder.append(BASE2);
		builder.append(BASE3);

		final StringReader reader = new StringReader(builder.toString());
		final CodonTableReader codonReader = new CodonTableReader(reader);
		final List<CodonTransformationData> list = codonReader.read();

		Assert.assertEquals(0, list.size());
	}

	@Test
	public void readStartsMissing() throws Exception {
		final StringBuilder builder = new StringBuilder();
		builder.append(NAME);
		builder.append(AAS);
		builder.append(BASE1);
		builder.append(BASE2);
		builder.append(BASE3);

		final StringReader reader = new StringReader(builder.toString());
		final CodonTableReader codonReader = new CodonTableReader(reader);
		final List<CodonTransformationData> list = codonReader.read();

		Assert.assertEquals(0, list.size());
	}

	@Test
	public void readBase1Missing() throws Exception {
		final StringBuilder builder = new StringBuilder();
		builder.append(NAME);
		builder.append(AAS);
		builder.append(STARTS);
		builder.append(BASE2);
		builder.append(BASE3);

		final StringReader reader = new StringReader(builder.toString());
		final CodonTableReader codonReader = new CodonTableReader(reader);
		final List<CodonTransformationData> list = codonReader.read();

		Assert.assertEquals(0, list.size());
	}

	@Test
	public void readBase2Missing() throws Exception {
		final StringBuilder builder = new StringBuilder();
		builder.append(NAME);
		builder.append(AAS);
		builder.append(STARTS);
		builder.append(BASE1);
		builder.append(BASE3);

		final StringReader reader = new StringReader(builder.toString());
		final CodonTableReader codonReader = new CodonTableReader(reader);
		final List<CodonTransformationData> list = codonReader.read();

		Assert.assertEquals(0, list.size());
	}

	@Test
	public void readBase3Missing() throws Exception {
		final StringBuilder builder = new StringBuilder();
		builder.append(NAME);
		builder.append(AAS);
		builder.append(STARTS);
		builder.append(BASE1);
		builder.append(BASE2);

		final StringReader reader = new StringReader(builder.toString());
		final CodonTableReader codonReader = new CodonTableReader(reader);
		final List<CodonTransformationData> list = codonReader.read();

		Assert.assertEquals(0, list.size());
	}

	@Test
	public void readAAsTooLong() throws Exception {
		final StringBuilder builder = new StringBuilder();
		builder.append(NAME);
		builder.append(AAS + 'F');
		builder.append(STARTS);
		builder.append(BASE1);
		builder.append(BASE2);
		builder.append(BASE3);

		final StringReader reader = new StringReader(builder.toString());
		final CodonTableReader codonReader = new CodonTableReader(reader);
		final List<CodonTransformationData> list = codonReader.read();

		Assert.assertEquals(0, list.size());
	}

	@Test
	public void readAAsTooShort() throws Exception {
		final StringBuilder builder = new StringBuilder();
		builder.append(NAME);
		builder.append(AAS.substring(0, AAS.length() - 1));
		builder.append(STARTS);
		builder.append(BASE1);
		builder.append(BASE2);
		builder.append(BASE3);

		final StringReader reader = new StringReader(builder.toString());
		final CodonTableReader codonReader = new CodonTableReader(reader);
		final List<CodonTransformationData> list = codonReader.read();

		Assert.assertEquals(0, list.size());
	}

	@Test
	public void readStartsTooLong() throws Exception {
		final StringBuilder builder = new StringBuilder();
		builder.append(NAME);
		builder.append(AAS);
		builder.append(STARTS + '-');
		builder.append(BASE1);
		builder.append(BASE2);
		builder.append(BASE3);

		final StringReader reader = new StringReader(builder.toString());
		final CodonTableReader codonReader = new CodonTableReader(reader);
		final List<CodonTransformationData> list = codonReader.read();

		Assert.assertEquals(0, list.size());
	}

	@Test
	public void readStartsTooShort() throws Exception {
		final StringBuilder builder = new StringBuilder();
		builder.append(NAME);
		builder.append(AAS);
		builder.append(STARTS.substring(0, STARTS.length() - 1));
		builder.append(BASE1);
		builder.append(BASE2);
		builder.append(BASE3);

		final StringReader reader = new StringReader(builder.toString());
		final CodonTableReader codonReader = new CodonTableReader(reader);
		final List<CodonTransformationData> list = codonReader.read();

		Assert.assertEquals(0, list.size());
	}

	@Test
	public void readBase1TooLong() throws Exception {
		final StringBuilder builder = new StringBuilder();
		builder.append(NAME);
		builder.append(AAS);
		builder.append(STARTS);
		builder.append(BASE1 + 'A');
		builder.append(BASE2);
		builder.append(BASE3);

		final StringReader reader = new StringReader(builder.toString());
		final CodonTableReader codonReader = new CodonTableReader(reader);
		final List<CodonTransformationData> list = codonReader.read();

		Assert.assertEquals(0, list.size());
	}

	@Test
	public void readBase1TooShort() throws Exception {
		final StringBuilder builder = new StringBuilder();
		builder.append(NAME);
		builder.append(AAS);
		builder.append(STARTS);
		builder.append(BASE1.substring(0, BASE1.length() - 1));
		builder.append(BASE2);
		builder.append(BASE3);

		final StringReader reader = new StringReader(builder.toString());
		final CodonTableReader codonReader = new CodonTableReader(reader);
		final List<CodonTransformationData> list = codonReader.read();

		Assert.assertEquals(0, list.size());
	}

	@Test
	public void readBase2TooLong() throws Exception {
		final StringBuilder builder = new StringBuilder();
		builder.append(NAME);
		builder.append(AAS);
		builder.append(STARTS);
		builder.append(BASE2 + 'A');
		builder.append(BASE1);
		builder.append(BASE3);

		final StringReader reader = new StringReader(builder.toString());
		final CodonTableReader codonReader = new CodonTableReader(reader);
		final List<CodonTransformationData> list = codonReader.read();

		Assert.assertEquals(0, list.size());
	}

	@Test
	public void readBase2TooShort() throws Exception {
		final StringBuilder builder = new StringBuilder();
		builder.append(NAME);
		builder.append(AAS);
		builder.append(STARTS);
		builder.append(BASE2.substring(0, BASE2.length() - 1));
		builder.append(BASE1);
		builder.append(BASE3);

		final StringReader reader = new StringReader(builder.toString());
		final CodonTableReader codonReader = new CodonTableReader(reader);
		final List<CodonTransformationData> list = codonReader.read();

		Assert.assertEquals(0, list.size());
	}

	@Test
	public void readBase3TooLong() throws Exception {
		final StringBuilder builder = new StringBuilder();
		builder.append(NAME);
		builder.append(AAS);
		builder.append(STARTS);
		builder.append(BASE3 + 'A');
		builder.append(BASE1);
		builder.append(BASE2);

		final StringReader reader = new StringReader(builder.toString());
		final CodonTableReader codonReader = new CodonTableReader(reader);
		final List<CodonTransformationData> list = codonReader.read();

		Assert.assertEquals(0, list.size());
	}

	@Test
	public void readBase3TooShort() throws Exception {
		final StringBuilder builder = new StringBuilder();
		builder.append(NAME);
		builder.append(AAS);
		builder.append(STARTS);
		builder.append(BASE3.substring(0, BASE3.length() - 1));
		builder.append(BASE1);
		builder.append(BASE2);

		final StringReader reader = new StringReader(builder.toString());
		final CodonTableReader codonReader = new CodonTableReader(reader);
		final List<CodonTransformationData> list = codonReader.read();

		Assert.assertEquals(0, list.size());
	}

	@Test
	public void readStartsWrongChar() throws Exception {
		final StringBuilder builder = new StringBuilder();
		builder.append(NAME);
		builder.append(AAS);
		builder.append(STARTS.substring(0, STARTS.length() - 1) + 'Z');
		builder.append(BASE1);
		builder.append(BASE2);
		builder.append(BASE3);

		final StringReader reader = new StringReader(builder.toString());
		final CodonTableReader codonReader = new CodonTableReader(reader);
		final List<CodonTransformationData> list = codonReader.read();

		Assert.assertEquals(0, list.size());
	}

	@Test
	public void readBase1WrongChar() throws Exception {
		final StringBuilder builder = new StringBuilder();
		builder.append(NAME);
		builder.append(AAS);
		builder.append(STARTS);
		builder.append(BASE1.substring(0, BASE1.length() - 1) + 'Z');
		builder.append(BASE2);
		builder.append(BASE3);

		final StringReader reader = new StringReader(builder.toString());
		final CodonTableReader codonReader = new CodonTableReader(reader);
		final List<CodonTransformationData> list = codonReader.read();

		Assert.assertEquals(0, list.size());
	}

	@Test
	public void readBase2WrongChar() throws Exception {
		final StringBuilder builder = new StringBuilder();
		builder.append(NAME);
		builder.append(AAS);
		builder.append(STARTS);
		builder.append(BASE1);
		builder.append(BASE2.substring(0, BASE2.length() - 1) + 'Z');
		builder.append(BASE3);

		final StringReader reader = new StringReader(builder.toString());
		final CodonTableReader codonReader = new CodonTableReader(reader);
		final List<CodonTransformationData> list = codonReader.read();

		Assert.assertEquals(0, list.size());
	}

	@Test
	public void readBase3WrongChar() throws Exception {
		final StringBuilder builder = new StringBuilder();
		builder.append(NAME);
		builder.append(AAS);
		builder.append(STARTS);
		builder.append(BASE1);
		builder.append(BASE2);
		builder.append(BASE3.substring(0, BASE3.length() - 1) + 'Z');

		final StringReader reader = new StringReader(builder.toString());
		final CodonTableReader codonReader = new CodonTableReader(reader);
		final List<CodonTransformationData> list = codonReader.read();

		Assert.assertEquals(0, list.size());
	}

	@Test
	public void readDefaultTable() throws Exception {
		final List<CodonTransformationData> list = CodonTableReader.readDefaultTable();

		Assert.assertEquals(24, list.size());
		// Assure each transformation has a unique number.
		final Set<Integer> numbers = new HashSet<Integer>(24);
		for (final CodonTransformationData data : list) {
			Assert.assertTrue("\"" + data.name + "\" has its number (" + data.number + ") already in use.", numbers.add(data.number));
		}
	}

}
