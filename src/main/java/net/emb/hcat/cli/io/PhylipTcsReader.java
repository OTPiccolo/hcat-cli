package net.emb.hcat.cli.io;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	public List<Sequence> read() throws IOException {
		log.debug("Reading sequences with following parameters. Same length: {}", isEnforceSameLength());
		return super.read();
	}

	@Override
	protected void readHeader() throws IOException {
		final String header = readLine();
		if (header == null) {
			throw new IOException("No header found. Empty stream.");
		}

		final Matcher matcher = Pattern.compile(HEADER_REGEX).matcher(header);
		if (!matcher.matches()) {
			throw new IOException("Reading first line doesn't comply to Phylip format. Should be sequence length, followed by four spaces, and finished with sequence count. Found instead (without quotation marks): \"" + header + "\"");
		}

		expectedSeqCount = Integer.parseInt(matcher.group(1));
		expectedSeqLength = Integer.parseInt(matcher.group(2));
		log.info("Expecting {} sequence(s), each being {} character(s) long.", expectedSeqCount, expectedSeqLength);
	}

	@Override
	protected Sequence readSequence() throws IOException {
		final Sequence sequence = super.readSequence();
		if (sequence != null) {
			if (sequence.getName().length() > MAX_LENGTH_NAME) {
				throw new IOException("Sequence with name \"" + sequence.getName() + "\" is too long. It can be at maximum " + MAX_LENGTH_NAME + " characters long.");
			}
			if (sequence.getLength() != getExpectedSeqLength()) {
				throw new IOException("Sequence with name " + sequence.getName() + " has wrong length. Expected/Actual: " + getExpectedSeqLength() + "/" + sequence.getLength());
			}
		}
		return sequence;
	}

	@Override
	protected List<Sequence> readSequences() throws IOException {
		final List<Sequence> sequences = super.readSequences();
		if (sequences.size() != getExpectedSeqCount()) {
			throw new IOException("Wrong number of sequences read. Expected/Actual: " + getExpectedSeqCount() + "/" + sequences.size());
		}
		return sequences;
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
