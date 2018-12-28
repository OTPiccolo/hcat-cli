package net.emb.hcat.cli.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.emb.hcat.cli.Sequence;

/**
 * A reader to read in sequences of DNA in Pyhlip TCS format.
 *
 * @author OT Piccolo
 */
public class PhylipTcsReader implements ISequenceReader {

	private static final int MAX_LENGTH_NAME = 9;
	private static final String HEADER_REGEX = "^(\\d++)    (\\d++)$";

	private final BufferedReader reader;

	/**
	 * Constructor.
	 *
	 * @param reader
	 *            The reader data should be read from.
	 */
	public PhylipTcsReader(final Reader reader) {
		if (reader == null) {
			throw new IllegalArgumentException("Reader can't be null.");
		}
		this.reader = new BufferedReader(reader, 128);
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

	@Override
	public List<Sequence> read() throws IOException {
		final List<Sequence> sequences = new ArrayList<Sequence>();
		String line;

		line = reader.readLine();
		if (line == null) {
			throw new IOException("No header found. Empty stream.");
		}
		final Matcher matcher = Pattern.compile(HEADER_REGEX).matcher(line);
		if (!matcher.matches()) {
			throw new IOException("Reading first line doesn't comply to Phylip TCS format. Should be sequence length, followed by four spaces, and finished with sequence count. Found instead (without quotation marks): \"" + line + "\"");
		}

		final int expectedSeqCount = Integer.parseInt(matcher.group(1));
		final int expectedSeqLength = Integer.parseInt(matcher.group(2));

		String name;
		String seq;
		while ((line = reader.readLine()) != null) {
			name = line;
			if (name.length() > MAX_LENGTH_NAME) {
				throw new IOException("Sequence with name \"" + name + "\" is too long. It can be at maximum " + MAX_LENGTH_NAME + " characters long.");
			}
			seq = reader.readLine();
			if (seq == null) {
				throw new IOException("Unexpected end of stream reached. Sequence missing.");
			}
			if (seq.length() != expectedSeqLength) {
				throw new IOException("Sequence with name " + name + " has wrong length. Expected/Actual: " + expectedSeqLength + "/" + seq.length());
			}
			sequences.add(new Sequence(seq, name));
		}

		if (sequences.size() != expectedSeqCount) {
			throw new IOException("Wrong number of sequences read. Expected/Actual: " + expectedSeqCount + "/" + sequences.size());
		}

		return sequences;
	}

}
