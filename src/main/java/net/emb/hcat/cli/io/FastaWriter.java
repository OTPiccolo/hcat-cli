package net.emb.hcat.cli.io;

import java.io.IOException;
import java.io.Writer;

import net.emb.hcat.cli.Sequence;

/**
 * A writer to write out sequences of DNA in FASTA format.
 *
 * @author OT Piccolo
 */
public class FastaWriter extends BaseSequenceWriter {

	private static final char ID_CHAR = '>';

	/**
	 * Constructor.
	 *
	 * @param writer
	 *            The writer data should be written to.
	 */
	public FastaWriter(final Writer writer) {
		super(writer);
	}

	@Override
	protected void writeSeqName(final String name) throws IOException {
		getWriter().append(ID_CHAR);
		super.writeSeqName(name);
	}

	/**
	 * Writes a sequence.
	 *
	 * @param sequence
	 *            The sequence to write. Must not be <code>null</code>.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public void write(final Sequence sequence) throws IOException {
		if (sequence == null) {
			throw new IllegalArgumentException("Sequence must not be null.");
		}

		writeSequence(sequence);

		getWriter().flush();
	}

}
