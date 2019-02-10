package net.emb.hcat.cli.io;

import java.io.IOException;
import java.io.Reader;

import net.emb.hcat.cli.Sequence;

/**
 * A reader to read in sequences of DNA in FASTA format.
 *
 * @author OT Piccolo
 */
public class FastaReader extends BaseSequenceReader {

	// FASTA format: https://de.wikipedia.org/wiki/FASTA-Format

	private static final char ID_CHAR = '>';
	private static final char COMMENT_CHAR = ';';

	private final StringBuilder builder = new StringBuilder(1024);
	private String id;

	/**
	 * Constructor.
	 *
	 * @param reader
	 *            The reader data should be read from.
	 */
	public FastaReader(final Reader reader) {
		super(reader);
	}

	@Override
	protected Sequence readSequence() throws IOException {
		Sequence seq = null;
		String line;
		while ((line = readLine()) != null) {
			if (line.charAt(0) == ID_CHAR) {
				if (id == null) {
					// No ID has yet been encountered, so must have been the
					// very first ID.
					id = line.substring(1);
				} else {
					// Start of next sequence must have been found, as ID tag
					// has been encountered.
					break;
				}
			} else {
				// A sequence can be read over different lines.
				builder.append(line);
			}
		}

		seq = createSequence(builder, id);
		if (line != null) {
			// Store ID for next sequence.
			id = line.substring(1);
		}

		return seq;
	}

	@Override
	protected boolean isData(final String line) {
		return super.isData(line) && line.charAt(0) != COMMENT_CHAR;
	}

	private Sequence createSequence(final StringBuilder builder, final String id) {
		if (builder.length() == 0) {
			return null;
		}

		final Sequence sequence = new Sequence(builder.toString());
		sequence.setName(id);
		builder.setLength(0);
		return sequence;
	}

}
