package net.emb.hcat.cli.io;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import net.emb.hcat.cli.sequence.Sequence;

/**
 * An interface to read in sequences.
 *
 * @author Heiko Mattes
 */
public interface ISequenceReader extends Closeable {

	/**
	 * Reads in sequences.
	 *
	 * @return A list of sequences.
	 * @throws IOException
	 *             An I/O exception.
	 */
	List<Sequence> read() throws IOException;

}
