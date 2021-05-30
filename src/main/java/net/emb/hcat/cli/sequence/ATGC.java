package net.emb.hcat.cli.sequence;

/**
 * Enum defining DNA nucleotides.
 *
 * @author OT Piccolo
 */
public enum ATGC {

	/** Nucleotide Adenine (A). */
	ADENINE,
	/** Nucleotide Cytosine (C). */
	CYTOSINE,
	/** Nucleotide Guanine (G). */
	GUANINE,
	/** Nucleotide Thymine (T). */
	THYMINE;

	/**
	 * Checks whether the given character is an ATGC nucleotide.
	 *
	 * @param c
	 *            The character to check (case-insensitive).
	 * @return <code>true</code>, if the given character is an ATGC nucleotide,
	 *         <code>false</code> otherwise.
	 */
	public static boolean isAtgc(final char c) {
		switch (c) {
		case 'A':
		case 'C':
		case 'G':
		case 'T':
		case 'a':
		case 'c':
		case 'g':
		case 't':
			return true;
		default:
			return false;
		}
	}

	/**
	 * Transforms the given character to anATGC nucleotide.
	 *
	 * @param c
	 *            The character to transform (case-insensitive).
	 * @return The corresponding enum, or null if the given character was not an
	 *         ATGC nucleotide.
	 */
	public static ATGC toAtgc(final char c) {
		switch (c) {
		case 'A':
		case 'a':
			return ADENINE;
		case 'C':
		case 'c':
			return CYTOSINE;
		case 'G':
		case 'g':
			return GUANINE;
		case 'T':
		case 't':
			return THYMINE;
		default:
			return null;
		}
	}

}