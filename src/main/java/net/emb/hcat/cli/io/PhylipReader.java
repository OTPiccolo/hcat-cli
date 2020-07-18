package net.emb.hcat.cli.io;

import java.io.IOException;
import java.io.Reader;

import net.emb.hcat.cli.ErrorCodeException;
import net.emb.hcat.cli.ErrorCodeException.EErrorCode;
import net.emb.hcat.cli.sequence.Sequence;

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
	protected Sequence readSequence() throws ErrorCodeException, IOException {
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

		if (builder.length() == 0) {
			throw new ErrorCodeException(EErrorCode.MISSING_VALUE, "Unexpected end reach. Sequence data is missing.");
		}

		final Sequence sequence = new Sequence(builder.toString(), id);
		return sequence;
	}

	@Override
	protected int getMaxLengthOfName() {
		return -1;
	}

}
