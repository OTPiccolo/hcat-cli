package net.emb.hcat.cli.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import net.emb.hcat.cli.Sequence;

/**
 * A writer to write out sequences of DNA in Phylip TCS format.
 *
 * @author OT Piccolo
 */
public class PhylipTcsWriter implements ISequenceWriter {

	private static final int MAX_LENGTH_NAME = 9;

	private final BufferedWriter writer;

	/**
	 * Constructor.
	 *
	 * @param writer
	 *            The writer data should be written to.
	 */
	public PhylipTcsWriter(final Writer writer) {
		if (writer == null) {
			throw new IllegalArgumentException("Writer can't be null.");
		}
		this.writer = new BufferedWriter(writer, 128);
	}

	@Override
	public void write(final List<Sequence> sequences) throws IOException {
		if (sequences == null) {
			throw new IllegalArgumentException("Sequences must not be null.");
		}

		final int seqSize = sequences.size();
		final int seqLength = seqSize == 0 ? 0 : sequences.get(0).getLength();

		writeHeader(seqSize, seqLength);

		for (final Sequence sequence : sequences) {
			writeSequence(sequence, seqLength);
		}

		writer.flush();
	}

	private void writeHeader(final int seqSize, final int seqLength) throws IOException {
		writer.append(String.valueOf(seqSize));
		writer.append("    ");
		writer.append(String.valueOf(seqLength));
		writer.newLine();
	}

	private void writeSequence(final Sequence sequence, final int seqLength) throws IOException {
		if (seqLength != sequence.getLength()) {
			throw new IOException("Sequence with name \"" + sequence.getName() + "\" has not the correct length. Expected/Actual: " + seqLength + "/" + sequence.getLength());
		}

		writeSeqName(sequence.getName());
		writeSeqBody(sequence.getValue());
	}

	private void writeSeqName(final String name) throws IOException {
		if (name != null) {
			if (name.length() > MAX_LENGTH_NAME) {
				writer.append(name.substring(0, MAX_LENGTH_NAME));
			} else {
				writer.append(name);
			}
		}
		writer.newLine();
	}

	private void writeSeqBody(final String body) throws IOException {
		writer.append(body);
		writer.newLine();
	}

}
