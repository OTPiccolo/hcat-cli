package net.emb.hcat.cli.io.sequence;

import java.io.Reader;
import java.io.Writer;

/**
 * Enum containing all known types on how to read/write sequences.
 *
 * @author OT Piccolo
 */
public enum ESequenceType {

	/** Fasta format */
	FASTA,
	/** Phylip format */
	PHYLIP,
	/** Phylip TCS format */
	PHYLIP_TCS,
	/** CSV (Comma Separated Value) format */
	CSV;

	/**
	 * Gets a reader that can read sequences in the format of this enum.
	 *
	 * @param reader
	 *            The underlying reader to read sequences from.
	 * @return The reader.
	 */
	public ISequenceReader createReader(final Reader reader) {
		switch (this) {
		case FASTA:
			return new FastaReader(reader);
		case PHYLIP:
			return new PhylipReader(reader);
		case PHYLIP_TCS:
			return new PhylipTcsReader(reader);
		case CSV:
			return new CsvReader(reader);
		default:
			// Will never happen.
			throw new IllegalStateException("Enum not fully implemented. Missing case: " + this);
		}
	}

	/**
	 * Gets a writer that can write sequences in the format of this enum.
	 *
	 * @param writer
	 *            The underlying writer to write sequences to.
	 * @return The writer.
	 */
	public ISequenceWriter createWriter(final Writer writer) {
		switch (this) {
		case FASTA:
			return new FastaWriter(writer);
		case PHYLIP:
			return new PhylipWriter(writer);
		case PHYLIP_TCS:
			return new PhylipTcsWriter(writer);
		case CSV:
			return new CsvWriter(writer);
		default:
			// Will never happen.
			throw new IllegalStateException("Enum not fully implemented. Missing case: " + this);
		}
	}

}
