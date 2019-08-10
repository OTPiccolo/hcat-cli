package net.emb.hcat.cli.io;

import java.io.Reader;
import java.io.Writer;

/**
 * Enum containing all known types on how to read/write sequences.
 *
 * @author OT Piccolo
 */
public enum ESequenceType {

	FASTA, PHYLIP, PHYLIP_TCS;

	public ISequenceReader createReader(final Reader reader) {
		switch (this) {
		case FASTA:
			return new FastaReader(reader);
		case PHYLIP:
			return new PhylipReader(reader);
		case PHYLIP_TCS:
			return new PhylipTcsReader(reader);
		default:
			// Will never happen.
			throw new IllegalStateException("Enum not fully implemented. Missing case: " + this);
		}
	}

	public ISequenceWriter createWriter(final Writer writer) {
		switch (this) {
		case FASTA:
			return new FastaWriter(writer);
		case PHYLIP:
			return new PhylipWriter(writer);
		case PHYLIP_TCS:
			return new PhylipTcsWriter(writer);
		default:
			// Will never happen.
			throw new IllegalStateException("Enum not fully implemented. Missing case: " + this);
		}
	}

}
