package net.emb.hcat.cli.io.sequence;

import java.io.Closeable;
import java.util.List;

import net.emb.hcat.cli.ErrorCodeException;
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
	 * @throws ErrorCodeException
	 *             An exception happened reading in the sequences.
	 */
	List<Sequence> read() throws ErrorCodeException;

}
