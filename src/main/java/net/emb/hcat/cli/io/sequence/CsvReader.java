package net.emb.hcat.cli.io.sequence;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.emb.hcat.cli.ErrorCodeException;
import net.emb.hcat.cli.sequence.Sequence;

/**
 * A reader to read in sequences of DNA in CSV format.
 *
 * @author Heiko Mattes
 */
public class CsvReader extends BaseSequenceReader {

	private static final Logger log = LoggerFactory.getLogger(CsvReader.class);

	private static final char DEFAULT_DELIMITER = ',';

	private char delimiter;

	private boolean nameIncluded = true;

	/**
	 * Constructor.
	 *
	 * @param reader
	 *            The reader data should be read from.
	 */
	public CsvReader(final Reader reader) {
		this(reader, DEFAULT_DELIMITER);
	}

	/**
	 * Constructor.
	 *
	 * @param reader
	 *            The reader data should be read from.
	 * @param delimiter
	 *            The delimiter that separates the values.
	 */
	public CsvReader(final Reader reader, final char delimiter) {
		super(reader);
		this.setDelimiter(delimiter);
	}

	@Override
	public List<Sequence> read() throws ErrorCodeException {
		log.debug("Reading sequences with following parameters. Same length: {} / Name included: {} / Delimiter: '{}'", isEnforceSameLength(), isNameIncluded(), getDelimiter());
		return super.read();
	}

	@Override
	protected void readHeader() throws ErrorCodeException, IOException {
		// Some CSV files (Excel specific) can have a delimiter specificiation
		// in their first line in the form of: sep=,
		boolean delimiterFound = false;
		getReader().mark(5);

		final char[] header = new char[5];
		final int read = getReader().read(header);
		if (read == 5) {
			final String delimiterHeader = new String(header);
			if (delimiterHeader.startsWith("sep=")) {
				delimiterFound = true;
				setDelimiter(delimiterHeader.charAt(4));
				log.info("Found delimiter character in CSV file: '{}'", getDelimiter());
			}
		}

		if (!delimiterFound) {
			getReader().reset();
		}
	}

	@Override
	protected Sequence readSequence() throws ErrorCodeException, IOException {
		final String line = readLine();
		if (line != null) {
			return readSequence(line);
		}
		return null;
	}

	private Sequence readSequence(final String seqString) {
		final String[] split = seqString.split("\\" + getDelimiter());
		if (isNameIncluded()) {
			return new Sequence(toValue(split, 1, split.length - 1), split[0]);
		}
		return new Sequence(toValue(split, 0, split.length));
	}

	private String toValue(final String[] parts, final int from, final int length) {
		final StringBuilder builder = new StringBuilder(length);
		for (int i = from; i < from + length; i++) {
			builder.append(parts[i]);
		}
		return builder.toString();
	}

	/**
	 * Gets the delimiter that separates the values.
	 *
	 * @return The delimiter character.
	 */
	public char getDelimiter() {
		return delimiter;
	}

	/**
	 * Sets the delimiter that separates the values.
	 *
	 * @param delimiter
	 *            The delimiter character.
	 */
	public void setDelimiter(final char delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * Gets whether the name is included as first value in sequence. Default is
	 * <code>true</code>.
	 *
	 * @return <code>true</code>, if name is included, <code>false</code>
	 *         otherwise.
	 */
	public boolean isNameIncluded() {
		return nameIncluded;
	}

	/**
	 * Sets whether the name is included as first value in sequence. Default is
	 * <code>true</code>.
	 *
	 * @param nameIncluded
	 *            <code>true</code>, if name is included, <code>false</code>
	 *            otherwise.
	 */
	public void setNameIncluded(final boolean nameIncluded) {
		this.nameIncluded = nameIncluded;
	}

}
