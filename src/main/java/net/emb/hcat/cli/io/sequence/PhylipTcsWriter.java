package net.emb.hcat.cli.io.sequence;

import java.io.IOException;
import java.io.Writer;

/**
 * A writer to write out sequences of DNA in Phylip TCS format.
 *
 * @author Heiko Mattes
 */
public class PhylipTcsWriter extends PhylipWriter {

	private static final int MAX_LENGTH_NAME = 9;

	/**
	 * Constructor.
	 *
	 * @param writer
	 *            The writer data should be written to.
	 */
	public PhylipTcsWriter(final Writer writer) {
		super(writer);
		// Reset line break, as TCS format writes each sequence in a whole line.
		setLineBreak(0);
	}

	@Override
	protected void writeSeqName(final String name) throws IOException {
		String correctName = name;
		if (name != null && name.length() > MAX_LENGTH_NAME) {
			correctName = name.substring(0, MAX_LENGTH_NAME);
		}
		super.writeSeqName(correctName);
	}

}
