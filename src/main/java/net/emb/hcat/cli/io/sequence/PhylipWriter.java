package net.emb.hcat.cli.io.sequence;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.emb.hcat.cli.sequence.Sequence;

/**
 * A writer to write out sequences of DNA in Phylip format.
 *
 * @author Heiko Mattes
 */
public class PhylipWriter extends BaseSequenceWriter {

	private static final Logger log = LoggerFactory.getLogger(PhylipWriter.class);

	private static final int MAX_LINE_LENGTH = 60;

	private int seqSize;
	private int seqLength;

	/**
	 * Constructor.
	 *
	 * @param writer
	 *            The writer data should be written to.
	 */
	public PhylipWriter(final Writer writer) {
		super(writer);
		setLineBreak(MAX_LINE_LENGTH);
	}

	@Override
	public void write(final List<Sequence> sequences) throws IOException {
		if (sequences == null) {
			throw new IllegalArgumentException("Sequences must not be null.");
		}

		seqSize = sequences.size();
		seqLength = seqSize == 0 ? 0 : sequences.get(0).getLength();

		log.debug("Writing sequences with following parameters. Line break after: {} / Sequence length: {} / Sequence size: {}", getLineBreak(), seqLength, seqSize);
		super.write(sequences);
	}

	@Override
	protected void writeHeader() throws IOException {
		final BufferedWriter writer = getWriter();
		writer.append(String.valueOf(seqSize));
		writer.append("    ");
		writer.append(String.valueOf(seqLength));
		writer.newLine();
	}

	@Override
	protected void writeSequence(final Sequence sequence) throws IOException {
		if (seqLength != sequence.getLength()) {
			throw new IOException("Sequence with name \"" + sequence.getName() + "\" has not the correct length. Expected/Actual: " + seqLength + "/" + sequence.getLength());
		}

		super.writeSequence(sequence);
	}

}
