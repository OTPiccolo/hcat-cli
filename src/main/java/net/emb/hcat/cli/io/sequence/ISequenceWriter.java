package net.emb.hcat.cli.io.sequence;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import net.emb.hcat.cli.sequence.Sequence;

/**
 * An interface to read in sequences.
 *
 * @author Heiko Mattes
 */
public interface ISequenceWriter extends Closeable {

	/**
	 * Writes all sequences.
	 *
	 * @param sequences
	 *            The sequences to write. Must not be <code>null</code>, but can
	 *            be empty, albeit then nothing will be written.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	void write(final List<Sequence> sequences) throws IOException;

	/**
	 * Writes all sequences.
	 *
	 * @param sequences
	 *            The sequences to write. Must not be <code>null</code>, but can
	 *            be empty, albeit then nothing will be written.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	default void write(final Sequence... sequences) throws IOException {
		if (sequences == null) {
			throw new IllegalArgumentException("Sequences must not be null.");
		}
		write(Arrays.asList(sequences));
	}

}
