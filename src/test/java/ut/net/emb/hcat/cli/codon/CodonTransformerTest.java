package ut.net.emb.hcat.cli.codon;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import net.emb.hcat.cli.ErrorCodeException;
import net.emb.hcat.cli.codon.CodonTransformationData;
import net.emb.hcat.cli.codon.CodonTransformer;
import net.emb.hcat.cli.io.CodonTableReader;
import net.emb.hcat.cli.io.sequence.FastaReader;
import net.emb.hcat.cli.sequence.Sequence;

@SuppressWarnings("javadoc")
public class CodonTransformerTest {

	private static final Pattern CORRECT_TRANSFORMATION = Pattern.compile("^M[A-Z]+$");

	private static CodonTransformationData testCode;
	private static CodonTransformationData echinodermCode;
	private static List<Sequence> testSequences;

	@BeforeClass
	public static final void init() throws ErrorCodeException {
		testCode = new CodonTransformationData();
		testCode.name = "Test";
		testCode.number = 0;
		testCode.start.put("ABC", 'S');
		testCode.start.put("AAC", 'S');
		testCode.end.put("CBA", 'E');
		testCode.end.put("CCA", 'E');
		testCode.codon.put("AAA", 'A');
		testCode.codon.put("BBB", 'B');
		testCode.codon.put("CCC", 'C');
		testCode.codon.put("ABC", 'S');
		testCode.codon.put("CBA", 'E');
		testCode.codon.put("AAC", 'G');
		testCode.codon.put("CCA", 'H');

		final List<CodonTransformationData> codonData = CodonTableReader.readDefaultTable();
		for (final CodonTransformationData data : codonData) {
			if (data.number == 9) {
				echinodermCode = data;
				break;
			}
		}
		Assert.assertNotNull("Did not find Echinoderm Code in default table.", echinodermCode);

		try (FastaReader fasta = new FastaReader(new InputStreamReader(CodonTransformerTest.class.getResourceAsStream("/fasta-testdata1.txt"), StandardCharsets.UTF_8))) {
			fasta.setEnforceSameLength(true);
			testSequences = fasta.read();
		}
		Assert.assertFalse("No test sequences could be read.", testSequences.isEmpty());
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void negativeIndex() throws Exception {
		final Sequence sequence = new Sequence("ABCAAABBBCCCCBA");
		final CodonTransformer transformer = new CodonTransformer(testCode, sequence);
		transformer.transform(-1);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void tooLargeIndex() throws Exception {
		final Sequence sequence = new Sequence("ABCAAABBBCCCCBA");
		final CodonTransformer transformer = new CodonTransformer(testCode, sequence);
		transformer.transform(15);
	}

	@Test
	public void nullName() throws Exception {
		final Sequence sequence = new Sequence("ABCAAABBBCCCCBA");
		final CodonTransformer transformer = new CodonTransformer(testCode, sequence);
		Assert.assertNull(transformer.transform().getName());
	}

	@Test
	public void nonNullName() throws Exception {
		final Sequence sequence = new Sequence("ABCAAABBBCCCCBA", "TestSequence");
		final CodonTransformer transformer = new CodonTransformer(testCode, sequence);
		final String newName = transformer.transform().getName();
		Assert.assertNotNull("New name must not be null.", newName);
		Assert.assertTrue("Codon sequence doesn't have '" + sequence.getName() + "' in its name.", newName.contains(sequence.getName()));
	}

	@Test
	public void transform() throws Exception {
		final Sequence sequence = new Sequence("ABCAAABBBCCCCBA");
		final CodonTransformer transformer = new CodonTransformer(testCode, sequence);
		Assert.assertEquals("SABCE", transformer.transform().getValue());
	}

	@Test
	public void transformIndex() throws Exception {
		final Sequence sequence = new Sequence("AAABCAAABBBCCCCBA");
		final CodonTransformer transformer = new CodonTransformer(testCode, sequence);
		Assert.assertEquals("SABCE", transformer.transform(2).getValue());
	}

	@Test
	public void transformAuto() throws Exception {
		final Sequence sequence = new Sequence("AAABCAAABBBCCCCBA");
		final CodonTransformer transformer = new CodonTransformer(testCode, sequence);
		Assert.assertEquals("SABCE", transformer.transformAuto().getValue());
	}

	@Test
	public void transformAutoLeastEnds() throws Exception {
		final Sequence sequence = new Sequence("AAABCAAABBBCCCCBA");
		final CodonTransformer transformer = new CodonTransformer(testCode, sequence);
		Assert.assertEquals("SABCE", transformer.transformAuto().getValue());
	}

	@Test
	public void skipInvalidCodon() throws Exception {
		final Sequence sequence = new Sequence("CBAAABBBCCC");
		final CodonTransformer transformer = new CodonTransformer(testCode, sequence);
		Assert.assertEquals("ABC", transformer.transformAuto().getValue());
	}

	@Test
	public void invalid() throws Exception {
		final Sequence sequence = new Sequence("AABBCCDDD");
		final CodonTransformer transformer = new CodonTransformer(testCode, sequence);
		Assert.assertEquals("???", transformer.transformAuto().getValue());
	}

	@Test
	public void additionalStartAndEndNotGiven() throws Exception {
		final Sequence sequence = new Sequence("ABCAAABBBCCCCBAAAAAACAAABBBCCCCCA");
		final CodonTransformer transformer = new CodonTransformer(testCode, sequence);
		Assert.assertEquals("SABCEAGABCH", transformer.transform().getValue());
	}

	@Test
	public void additionalStartAndEnd() throws Exception {
		final Sequence sequence = new Sequence("ABCAAABBBCCCCBAAAAAACAAABBBCCCCCA");
		final CodonTransformer transformer = new CodonTransformer(testCode, sequence);
		transformer.getAlternativeStart().add(6);
		transformer.getAlternativeEnd().add(10);
		Assert.assertEquals("SABCEASABCE", transformer.transform().getValue());
	}

	@Test
	public void additionalStartAndEndWithNormalOffset() throws Exception {
		final Sequence sequence = new Sequence("ZABCAAABBBCCCCBAAAAAACAAABBBCCCCCA");
		final CodonTransformer transformer = new CodonTransformer(testCode, sequence);
		transformer.getAlternativeStart().add(6);
		transformer.getAlternativeEnd().add(10);
		Assert.assertEquals("SABCEASABCE", transformer.transform(1).getValue());
	}

	@Test
	public void additionalStartAndEndWithLargeOffset() throws Exception {
		final Sequence sequence = new Sequence("ZZZZABCAAABBBCCCCBAAAAAACAAABBBCCCCCA");
		final CodonTransformer transformer = new CodonTransformer(testCode, sequence);
		transformer.getAlternativeStart().add(6);
		transformer.getAlternativeEnd().add(10);
		Assert.assertEquals("SABCEASABCE", transformer.transform(4).getValue());
	}

	@Test
	public void wrongAdditionalStartAndEnd() throws Exception {
		final Sequence sequence = new Sequence("ABCAAABBBCCCCBAAAAAACAAABBBCCCCCA");
		final CodonTransformer transformer = new CodonTransformer(testCode, sequence);
		transformer.getAlternativeStart().add(5);
		transformer.getAlternativeEnd().add(9);
		Assert.assertEquals("SABCE?GAB?H", transformer.transform().getValue());
	}

	@Test
	public void invalidAdditionalStartAndEnd() throws Exception {
		final Sequence sequence = new Sequence("ABCAAABBBCCCCBAAAAAACAAABBBCCCCCA");
		final CodonTransformer transformer = new CodonTransformer(testCode, sequence);
		transformer.getAlternativeStart().add(-1);
		transformer.getAlternativeEnd().add(11);
		Assert.assertEquals("SABCEAGABCH", transformer.transform().getValue());
	}

	@Test
	public void echinodermTransform() throws Exception {
		for (final Sequence sequence : testSequences) {
			final CodonTransformer transformer = new CodonTransformer(echinodermCode, sequence);
			final Sequence codon = transformer.transform();
			Assert.assertTrue("Sequence " + sequence.getName() + " was not correctly transformed.", CORRECT_TRANSFORMATION.matcher(codon.getValue()).matches());
		}
	}

	@Test
	public void echinodermTransformAuto() throws Exception {
		for (final Sequence sequence : testSequences) {
			final CodonTransformer transformer = new CodonTransformer(echinodermCode, sequence);
			final Sequence codon = transformer.transformAuto();
			Assert.assertTrue("Sequence " + sequence.getName() + " was not correctly transformed.", CORRECT_TRANSFORMATION.matcher(codon.getValue()).matches());
			Assert.assertEquals(transformer.transform(), codon);
		}
	}

	@Test
	public void echinodermTransformIndexIncorrect() throws Exception {
		for (final Sequence sequence : testSequences) {
			final CodonTransformer transformer = new CodonTransformer(echinodermCode, sequence);
			Assert.assertNotEquals(transformer.transform(0), transformer.transform(1));
		}
	}

}
