package net.emb.hcat.cli.io;

import java.io.IOException;
import java.util.List;

import net.emb.hcat.cli.Sequence;

/**
 * An interface to read in sequences.
 *
 * @author OT Piccolo
 */
public interface ISequenceReader {

	/**
	 * Reads in sequences.
	 *
	 * @return A list of sequences.
	 * @throws IOException
	 *             An I/O exception.
	 */
	List<Sequence> read() throws IOException;

}
