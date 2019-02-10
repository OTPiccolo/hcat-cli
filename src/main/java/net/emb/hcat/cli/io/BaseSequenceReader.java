package net.emb.hcat.cli.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import net.emb.hcat.cli.Sequence;

/**
 * A base implementation of a sequence reader. Each sequence must be a line with
 * its name, followed by another line that contains the sequence value. Empty
 * lines are permitted.
 *
 * @author OT Piccolo
 *
 */
public class BaseSequenceReader implements ISequenceReader {

	private final BufferedReader reader;

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
	public List<Sequence> read() throws IOException {
		readHeader();
		return readSequences();
	}

	/**
	 * Reads all sequences from the underlying reader. A check that enforces
	 * same length of all sequences is performed, if
	 * {@link #setEnforceSameLength(boolean)} is configured.
	 *
	 * @return A list containing all sequences. Will never return
	 *         <code>null</code>, but may be empty.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	protected List<Sequence> readSequences() throws IOException {
		int previousSize = 0;
		final ArrayList<Sequence> sequences = new ArrayList<Sequence>();
		Sequence sequence;

		while ((sequence = readSequence()) != null) {
			if (isEnforceSameLength() && previousSize > 0 && previousSize != sequence.getLength()) {
				throw new IOException("Sequence doesn't match in length with previous sequence. Name of sequence: " + sequence.getName());
			}
			sequences.add(sequence);
			previousSize = sequence.getLength();
		}
		return sequences;
	}

	/**
	 * Reads the header. Default implementation does nothing.
	 * 
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	protected void readHeader() throws IOException {
		// Do nothing in default implementation.
	}

	/**
	 * Reads a single sequence from the underlying reader.
	 * 
	 * @return A sequence, or <code>null</code>, if no more sequences can be
	 *         read. Usually if the end of the underlying reader has been
	 *         reached.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	protected Sequence readSequence() throws IOException {
		final String name = readLine();
		final String value = readLine();

		if (name != null && value == null) {
			throw new IOException("Unexpected end reached. Sequence missing.");
		}

		if (value != null) {
			return new Sequence(value, name);
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
		if (line == null || isData(line)) {
			return line;
		}
		return readLine();
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
	 * Gets the reader to read sequences from.
	 *
	 * @return The reader.
	 */
	protected BufferedReader getReader() {
		return reader;
	}

	/**
	 * Convenience method to close the underlying reader.
	 *
	 * @see Reader#close()
	 */
	public void close() {
		try {
			reader.close();
		} catch (final IOException e) {
			e.printStackTrace();
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
