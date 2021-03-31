package net.emb.hcat.cli.io.sequence;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.emb.hcat.cli.sequence.Sequence;

/**
 * A writer to write out sequences of DNA in FASTA format.
 *
 * @author Heiko Mattes
 */
public class FastaWriter extends BaseSequenceWriter {

	private static final Logger log = LoggerFactory.getLogger(FastaWriter.class);

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
	public void write(final List<Sequence> sequences) throws IOException {
		log.debug("Writing sequences with following parameters. Line break after: {}", getLineBreak());
		super.write(sequences);
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
