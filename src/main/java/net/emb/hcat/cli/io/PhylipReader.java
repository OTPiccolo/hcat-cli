package net.emb.hcat.cli.io;

import java.io.IOException;
import java.io.Reader;

import net.emb.hcat.cli.Sequence;

/**
 * A reader to read in sequences of DNA in Pyhlip format.
 *
 * @author Heiko Mattes
 */
public class PhylipReader extends PhylipTcsReader {

	/**
	 * Constructor.
	 *
	 * @param reader
	 *            The reader data should be read from.
	 */
	public PhylipReader(final Reader reader) {
		super(reader);
	}

	@Override
	protected Sequence readSequence() throws IOException {
		String line = readLine();
		if (line == null) {
			return null;
		}
		final String id = line;

		final StringBuilder builder = new StringBuilder(getExpectedSeqLength());
		while ((line = readLine()) != null) {
			builder.append(line);
			if (builder.length() >= getExpectedSeqLength()) {
				break;
			}
		}

		if (builder.length() != getExpectedSeqLength()) {
			throw new IOException("Sequence with name " + id + " has wrong length. Expected/Actual: " + getExpectedSeqLength() + "/" + builder.length());
		}

		return new Sequence(builder.toString(), id);
	}

}
