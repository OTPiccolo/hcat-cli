package net.emb.hcat.cli.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import net.emb.hcat.cli.sequence.Sequence;

/**
 * A base implementation of a sequence writer that writes down the name of each
 * sequence, if present, and then in the next line the whole sequence. It can be
 * configured to automatically line break a sequence.
 *
 * @author Heiko Mattes
 *
 */
public class BaseSequenceWriter implements ISequenceWriter, AutoCloseable {

	private final BufferedWriter writer;

	private int lineBreak;

	/**
	 * Constructor.
	 *
	 * @param writer
	 *            The writer data should be written to.
	 */
	public BaseSequenceWriter(final Writer writer) {
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

		writeHeader();

		writeSequences(sequences);

		writeFooter();

		getWriter().flush();
	}

	/**
	 * Writes the header for the sequence. Will be called before any sequence is
	 * written. Base implementation does nothing.
	 *
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	protected void writeHeader() throws IOException {
		// Do nothing in default implementation.
	}

	/**
	 * Writes the footer for the sequence. Will be called after all sequences
	 * have been written. Base implementation does nothing.
	 *
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	protected void writeFooter() throws IOException {
		// Do nothing in default implementation.
	}

	/**
	 * Writes all sequences. Will iterate of the sequences to write each one.
	 * Null sequence will be ignored.
	 *
	 * @param sequences
	 *            The sequences that should be written.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	protected void writeSequences(final List<Sequence> sequences) throws IOException {
		for (final Sequence sequence : sequences) {
			if (sequence != null) {
				writeSequence(sequence);
			}
		}
	}

	/**
	 * Writes a sequence. Base implementation writes name in one line, and
	 * sequence value in next line.
	 *
	 * @param sequence
	 *            The sequence to write.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	protected void writeSequence(final Sequence sequence) throws IOException {
		writeSeqName(sequence.getName());
		writeSeqValue(sequence.getValue());
	}

	/**
	 * Writes the name of a sequence. Line break will be inserted after name.
	 *
	 * @param name
	 *            The name of the sequence. May be <code>null</code>.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	protected void writeSeqName(final String name) throws IOException {
		if (name != null) {
			getWriter().append(name);
		}
		getWriter().newLine();
	}

	/**
	 * Writes the value of a sequence. Line break will be inserted after value.
	 * If {@link #getLineBreak()} is non-zero, after that many characters, a
	 * line break will be inserted.
	 *
	 * @param value
	 *            The value of the sequence. Must not be <code>null</code>.
	 * @throws IOException
	 *             If an I/O error occurs.
	 * @see #getLineBreak()
	 */
	protected void writeSeqValue(final String value) throws IOException {
		final BufferedWriter writer = getWriter();
		if (getLineBreak() == 0) {
			writer.append(value);
			writer.newLine();
		} else {
			final int length = value.length();
			final int size = getLineBreak();
			for (int start = 0; start < length; start += size) {
				final String chunk = value.substring(start, Math.min(length, start + size));
				writer.append(chunk);
				writer.newLine();
			}
		}
	}

	/**
	 * Convenience method to close the underlying writer.
	 */
	@Override
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

	/**
	 * Gets the writer to write the sequence to.
	 *
	 * @return The writer.
	 */
	protected BufferedWriter getWriter() {
		return writer;
	}

}
