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
	 * Estimates what sequence type the file represents, by checking the file
	 * ending.
	 *
	 * @param fileName
	 *            The name of the file, or the file ending.
	 * @return The estimated sequence type, or <code>null</code>, if no sequence
	 *         type could be estimated.
	 */
	public static final ESequenceType byFileEnding(final String fileName) {
		if (fileName == null) {
			return null;
		}

		final int indexEnding = fileName.lastIndexOf('.');
		final String ending = indexEnding == -1 ? fileName.toLowerCase() : fileName.substring(indexEnding + 1).toLowerCase();

		switch (ending) {
		case "fas":
		case "fasta":
		case "txt":
			return FASTA;
		case "phy":
		case "phylip":
			return PHYLIP;
		case "tcs":
			return PHYLIP_TCS;
		case "csv":
			return CSV;
		default:
			return null;
		}
	}

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
