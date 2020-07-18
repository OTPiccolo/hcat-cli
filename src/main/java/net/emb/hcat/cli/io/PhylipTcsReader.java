package net.emb.hcat.cli.io;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import net.emb.hcat.cli.ErrorCodeException;
import net.emb.hcat.cli.ErrorCodeException.EErrorCode;
import net.emb.hcat.cli.sequence.Sequence;

/**
 * A reader to read in sequences of DNA in Pyhlip TCS format.
 *
 * @author Heiko Mattes
 */
public class PhylipTcsReader extends BaseSequenceReader {

	private static final Logger log = LoggerFactory.getLogger(PhylipTcsReader.class);

	private static final int MAX_LENGTH_NAME = 9;
	private static final String HEADER_REGEX = "^(\\d++)    (\\d++)$";

	private int expectedSeqCount;
	private int expectedSeqLength;

	/**
	 * Constructor.
	 *
	 * @param reader
	 *            The reader data should be read from.
	 */
	public PhylipTcsReader(final Reader reader) {
		super(reader);
	}

	@Override
	public List<Sequence> read() throws ErrorCodeException {
		log.debug("Reading sequences with following parameters. Same length: {}", isEnforceSameLength());
		return super.read();
	}

	@Override
	protected void readHeader() throws ErrorCodeException, IOException {
		final String header = readLine();
		if (header == null) {
			throw new ErrorCodeException(EErrorCode.INVALID_HEADER, "No header found. Empty stream.", (Object) null);
		}

		final Matcher matcher = Pattern.compile(HEADER_REGEX).matcher(header);
		if (!matcher.matches()) {
			throw new ErrorCodeException(EErrorCode.INVALID_HEADER, "Invalid header format for Phylip TCS format found. Header (without quotation marks): {}", header);
		}

		expectedSeqCount = Integer.parseInt(matcher.group(1));
		expectedSeqLength = Integer.parseInt(matcher.group(2));
		log.info("Expecting {} sequence(s), each being {} character(s) long.", expectedSeqCount, expectedSeqLength);
	}

	@Override
	protected Sequence readSequence() throws ErrorCodeException, IOException {
		final String name = readLine();
		final String value = readLine();

		if (name != null && value == null) {
			throw new ErrorCodeException(EErrorCode.MISSING_VALUE, "Unexpected end reach. Sequence data is missing.");
		}

		final Sequence sequence = value == null ? null : new Sequence(value, name);
		return sequence;
	}

	@Override
	protected List<Sequence> readSequences() throws ErrorCodeException, IOException {
		final List<Sequence> sequences = super.readSequences();
		if (sequences.size() != getExpectedSeqCount()) {
			throw new ErrorCodeException(EErrorCode.SEQUENCES_WRONG_AMOUNT, "Wrong number of sequences read. Expected/Actual: {}/{}", getExpectedSeqCount(), sequences.size());
		}
		return sequences;
	}

	@Override
	protected void validateSequence(final Sequence sequence) throws ErrorCodeException {
		super.validateSequence(sequence);

		if (getMaxLengthOfName() != -1 && sequence.getName().length() > MAX_LENGTH_NAME) {
			final int lineIndex = getLineCount() - 1;
			final String msg = MessageFormatter.basicArrayFormat("Sequence name is too long. Name of sequence: \"{}\"; Index: {}; Maximum length: {}", new Object[] { sequence.getName(), lineIndex, MAX_LENGTH_NAME });
			throw new ErrorCodeException(EErrorCode.SEQUENCE_WRONG_NAME, msg, sequence, lineIndex, MAX_LENGTH_NAME);
		}

		if (sequence.getLength() != getExpectedSeqLength()) {
			final int lineIndex = getLineCount() - 1;
			final String msg = MessageFormatter.basicArrayFormat("Sequence has unexpected length. Name of sequence: \"{}\"; Index: {}; Expected length: {}, Actual length: {}", new Object[] { sequence.getName(), lineIndex, getExpectedSeqLength(), sequence.getLength() });
			throw new ErrorCodeException(EErrorCode.SEQUENCE_WRONG_LENGTH, msg, sequence, lineIndex, getExpectedSeqLength());
		}
	}

	/**
	 * Returns the maximum length that a name of a sequence is allowed to have.
	 *
	 * @return The maximum length that is allowed. -1 if no constraints are on
	 *         the length of name.
	 */
	protected int getMaxLengthOfName() {
		return MAX_LENGTH_NAME;
	}

	/**
	 * Gets the expected sequence count of all sequences read by this reader.
	 * Information is read from header.
	 *
	 * @return The expected sequence count.
	 */
	protected int getExpectedSeqCount() {
		return expectedSeqCount;
	}

	/**
	 * Gets the expected sequence length for each sequence read by this reader.
	 * Information is read from header.
	 *
	 * @return The expected sequence length.
	 */
	protected int getExpectedSeqLength() {
		return expectedSeqLength;
	}

}
