package net.emb.hcat.cli.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import net.emb.hcat.cli.ErrorCodeException;
import net.emb.hcat.cli.ErrorCodeException.EErrorCode;
import net.emb.hcat.cli.sequence.Sequence;

/**
 * A base implementation of a sequence reader. Each sequence must be a line with
 * its name, followed by another line that contains the sequence value. Empty
 * lines are permitted.
 *
 * @author Heiko Mattes
 *
 */
public class BaseSequenceReader implements ISequenceReader {

	private static final Logger log = LoggerFactory.getLogger(BaseSequenceReader.class);

	private final BufferedReader reader;
	private int lineCount = 0;

	private boolean enforceSameLength;

	/**
	 * Constructor.
	 *
	 * @param reader
	 *            The reader data should be read from.
	 */
	public BaseSequenceReader(final Reader reader) {
		if (reader == null) {
			throw new IllegalArgumentException("Reader can't be null.");
		}
		this.reader = new BufferedReader(reader, 128);
	}

	@Override
	public List<Sequence> read() throws ErrorCodeException {
		log.info("Reading sequences.");
		try {
			readHeader();
			final List<Sequence> sequences = readSequences();
			log.info("Read {} sequence(s) successfully.", sequences.size());
			return sequences;
		} catch (final ErrorCodeException e) {
			throw e;
		} catch (final IOException e) {
			throw new ErrorCodeException(EErrorCode.GENERIC_READ, e, "Error reading sequences. Error message: {}", e.getMessage());
		} catch (final Exception e) {
			throw new ErrorCodeException(EErrorCode.UNEXPECTED, e, "Could not read sequences. Error message: {}", e.getMessage());
		}
	}

	/**
	 * Reads all sequences from the underlying reader. A check that enforces
	 * same length of all sequences is performed, if
	 * {@link #setEnforceSameLength(boolean)} is configured.
	 *
	 * @return A list containing all sequences. Will never return
	 *         <code>null</code>, but may be empty.
	 * @throws ErrorCodeException
	 *             An exception happened reading in the sequences.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	protected List<Sequence> readSequences() throws ErrorCodeException, IOException {
		int previousLength = 0;
		final ArrayList<Sequence> sequences = new ArrayList<>();
		Sequence sequence;

		while ((sequence = readSequence()) != null) {
			log.debug("Sequence read: {}", sequence);
			validateSequence(sequence);

			if (isEnforceSameLength() && previousLength > 0 && previousLength != sequence.getLength()) {
				final int lineIndex = getLineCount() - 1;
				final String msg = MessageFormatter.arrayFormat("Sequence doesn't match in length with previous sequence. Name of sequence: \"{}\"; Index: {}; Expected length: {}, Actual length: {}", new Object[] { sequence.getName(), lineIndex, previousLength, sequence.getLength() }).getMessage();
				throw new ErrorCodeException(EErrorCode.SEQUENCE_WRONG_LENGTH, msg, sequence, lineIndex, previousLength);
			}

			sequences.add(sequence);
			previousLength = sequence.getLength();

			if (sequence.getName() == null) {
				sequence.setName(String.valueOf(sequences.size()));
			}
		}
		return sequences;
	}

	/**
	 * Reads the header. Default implementation does nothing.
	 *
	 * @throws ErrorCodeException
	 *             An exception happened reading in the sequences.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	protected void readHeader() throws ErrorCodeException, IOException {
		// Default implementation does nothing.
	}

	/**
	 * Reads a single sequence from the underlying reader. Default
	 * implementation does read one line of data, which is considered the
	 * sequence.
	 *
	 * @return A sequence, or <code>null</code>, if no more sequences can be
	 *         read. Usually if the end of the underlying reader has been
	 *         reached.
	 * @throws ErrorCodeException
	 *             An exception happened reading in the sequences.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	protected Sequence readSequence() throws ErrorCodeException, IOException {
		final String value = readLine();
		if (value != null) {
			return new Sequence(value);
		}
		return null;
	}

	/**
	 * Reads a new line from the underlying stream. It will skip over lines that
	 * are not to be considered as data.
	 *
	 * @return The line read from the underlying stream. <code>null</code> if
	 *         the end of the stream has been reached.
	 * @throws IOException
	 *             If an I/O error occurs.
	 * @see #isData(String)
	 */
	protected String readLine() throws IOException {
		final String line = getReader().readLine();
		log.trace("Read line ({}): {}", lineCount, line);
		if (line == null) {
			// End of stream reached.
			return null;
		}

		lineCount++;
		if (isData(line)) {
			return line;
		}
		return readLine();
	}

	/**
	 * Validates the given sequence, that all constraints of the underlying
	 * format are correct. Default implementation does nothing.
	 *
	 * @param sequence
	 *            The sequence to validate.
	 * @throws ErrorCodeException
	 *             If the validation failed.
	 */
	protected void validateSequence(final Sequence sequence) throws ErrorCodeException {
		// Default implementation does nothing.
	}

	/**
	 * Checks whether the given line contains data. This is used to filter out
	 * empty lines or comments and such.
	 *
	 * @param line
	 *            The line to check. Will not be null.
	 * @return <code>true</code>, if the data is to be considered data,
	 *         <code>false</code> otherwise. Default implementation checks
	 *         whether the line is empty or contains only blank chars.
	 */
	protected boolean isData(final String line) {
		return !line.trim().isEmpty();
	}

	/**
	 * Gets the reader to read sequences from. Usually, the actual reader
	 * shouldn't be used directly. Instead, use the {@link #readLine()} method.
	 *
	 * @return The reader.
	 */
	protected BufferedReader getReader() {
		return reader;
	}

	/**
	 * Gets the currently read lines from the underlying reader.
	 *
	 * @return The currently read lines from the underlying reader. Is only
	 *         accurate if {@link #readLine()} is used.
	 */
	protected int getLineCount() {
		return lineCount;
	}

	/**
	 * Convenience method to close the underlying reader.
	 *
	 * @see Reader#close()
	 */
	@Override
	public void close() {
		try {
			reader.close();
		} catch (final IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * Gets whether each read sequence must be the same length. If this is
	 * <code>true</code>, and a sequence is encountered that doesn't match the
	 * length of a previous sequence, an IOException will be thrown. Default is
	 * <code>false</code>.
	 *
	 * @return <code>true</code>, if all sequences must be of the same length,
	 *         <code>false</code> otherwise.
	 */
	public boolean isEnforceSameLength() {
		return enforceSameLength;
	}

	/**
	 * Sets whether each read sequence must be the same length. If this is
	 * <code>true</code>, and a sequence is encountered that doesn't match the
	 * length of a previous sequence, an IOException will be thrown. Default is
	 * <code>false</code>.
	 *
	 * @param enforceSameLength
	 *            <code>true</code>, if all sequences must be of the same
	 *            length, <code>false</code> otherwise.
	 */
	public void setEnforceSameLength(final boolean enforceSameLength) {
		this.enforceSameLength = enforceSameLength;
	}

}
