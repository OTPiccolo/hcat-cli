package net.emb.hcat.cli.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

import net.emb.hcat.cli.Sequence;

/**
 * A writer to write out sequences of DNA in FASTA format.
 *
 * @author OT Piccolo
 */
public class FastaWriter {

	private static final char ID_CHAR = '>';

	private final BufferedWriter writer;
	private int lineBreak;

	/**
	 * Constructor.
	 *
	 * @param writer
	 *            The writer data should be written to.
	 */
	public FastaWriter(final Writer writer) {
		if (writer == null) {
			throw new IllegalArgumentException("Writer can't be null.");
		}
		this.writer = new BufferedWriter(writer, 128);
	}

	/**
	 * Write a sequence to the underlying writer.
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

		writer.append(ID_CHAR);
		if (sequence.getName() != null) {
			writer.append(sequence.getName());
		}
		writer.newLine();

		final String value = sequence.getValue();
		if (getLineBreak() == 0) {
			writer.append(value);
			writer.newLine();
		} else {
			final int length = sequence.getLength();
			final int size = getLineBreak();
			for (int start = 0; start < length; start += size) {
				final String chunk = value.substring(start, Math.min(length, start + size));
				writer.append(chunk);
				writer.newLine();
			}
		}

		writer.newLine();
		writer.flush();
	}

	/**
	 * Write all sequences to the underlying writer.
	 *
	 * @param sequences
	 *            The sequences to write. Must not be <code>null</code>, but can
	 *            be empty, albeit then nothing will be written.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public void write(final Iterable<Sequence> sequences) throws IOException {
		for (final Sequence sequence : sequences) {
			write(sequence);
		}
	}

	/**
	 * Write all sequences to the underlying writer.
	 *
	 * @param sequences
	 *            The sequences to write. Must not be <code>null</code>, but can
	 *            be empty, albeit then nothing will be written.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public void write(final Sequence... sequences) throws IOException {
		for (final Sequence sequence : sequences) {
			write(sequence);
		}
	}

	/**
	 * Convenience method to close the underlying writer.
	 *
	 * @see Writer#close()
	 */
	public void close() {
		try {
			writer.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the amount of characters written of the sequence before a line break
	 * happens. Zero means that a full sequence is written before a line break
	 * happens. Default is zero.
	 *
	 * @return The amount of characters written before a line break happens.
	 */
	public int getLineBreak() {
		return lineBreak;
	}

	/**
	 * Sets the amount of characters written of the sequence before a line break
	 * happens. Zero means that a full sequence is written before a line break
	 * happens. Default is zero.
	 *
	 * @param lineBreak
	 *            The amount of characters written before a line break happens.
	 *            This must be a non-negative number.
	 * @throws IllegalArgumentException
	 *             If the given value is negative.
	 */
	public void setLineBreak(final int lineBreak) {
		if (lineBreak < 0) {
			throw new IllegalArgumentException("Line break number must be non-negative.");
		}
		this.lineBreak = lineBreak;
	}

}
