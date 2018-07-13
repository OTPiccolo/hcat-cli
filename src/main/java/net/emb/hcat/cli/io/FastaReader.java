package net.emb.hcat.cli.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import net.emb.hcat.cli.Sequence;

/**
 * A reader to read in sequences of DNA in FASTA format.
 *
 * @author OT Piccolo
 */
public class FastaReader {

	// FASTA format: https://de.wikipedia.org/wiki/FASTA-Format

	private static char ID_CHAR = '>';
	private static char COMMENT_CHAR = ';';

	private final BufferedReader reader;
	private boolean enforceSameLength;

	/**
	 * Constructor.
	 *
	 * @param reader
	 *            The reader data should be read from.
	 */
	public FastaReader(final Reader reader) {
		if (reader == null) {
			throw new IllegalArgumentException("Reader can't be null.");
		}
		this.reader = new BufferedReader(reader, 128);
	}

	/**
	 * Reads in sequences from the underlying reader.<br>
	 * <br>
	 * If {@link #isEnforceSameLength()} is <code>true</code>, and a sequence is
	 * encountered that doesn't match the length of a previous sequence, an
	 * IOException will be thrown.
	 *
	 * @return A list of sequences.
	 * @throws IOException
	 *             An I/O exception.
	 */
	public List<Sequence> read() throws IOException {
		final List<Sequence> sequences = new ArrayList<Sequence>();
		String line;
		String id = null;
		int previousSize = 0;
		final StringBuilder value = new StringBuilder(1024);

		while ((line = reader.readLine()) != null) {
			if (line.isEmpty() || line.charAt(0) == COMMENT_CHAR) {
				// Do nothing, just skip.
			} else if (line.charAt(0) == ID_CHAR) {
				if (id != null) {
					// Start of next sequence must have been found.
					final Sequence sequence = createSequence(value, id, previousSize);
					previousSize = sequence.getLength();
					sequences.add(sequence);
				}
				id = line;
			} else {
				// A sequence can be read over different lines.
				value.append(line);
			}
		}

		// Last sequence hasn't been stored yet, do this here.
		sequences.add(createSequence(value, id, previousSize));

		return sequences;
	}

	private Sequence createSequence(final StringBuilder builder, final String id, final int previousSize) throws IOException {
		final Sequence sequence = new Sequence(builder.toString());
		sequence.setName(id);
		builder.setLength(0);

		if (isEnforceSameLength() && previousSize > 0 && previousSize != sequence.getLength()) {
			throw new IOException("Sequence doesn't match in length with previous sequence. Name of sequence: " + id);
		}

		return sequence;
	}

	/**
	 * Closes the underlying reader.
	 *
	 * @throws IOException
	 *             An exception closing the reader.
	 * @see Reader#close()
	 */
	public void close() throws IOException {
		reader.close();
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
