package net.emb.hcat.cli.codon;

import net.emb.hcat.cli.Sequence;

/**
 * Changes a sequence to a codon.
 *
 * @author OT Piccolo
 */
public class CodonTransformer {

	private static final char invalidChar = '?';

	private final CodonTransformationData data;
	private final Sequence sequence;

	/**
	 * Constructor.
	 *
	 * @param data
	 *            The data describing how to transform a sequence. Must not be
	 *            <code>null</code>.
	 * @param sequence
	 *            The sequence to transform. Must not be <code>null</code>.
	 */
	public CodonTransformer(final CodonTransformationData data, final Sequence sequence) {
		if (data == null) {
			throw new IllegalArgumentException("Data must not be null.");
		}
		if (sequence == null) {
			throw new IllegalArgumentException("Sequence must not be null.");
		}
		this.data = data;
		this.sequence = sequence;
	}

	/**
	 * Transforms the whole sequence, starting at offset 0. Same as
	 * transform(0).
	 *
	 * @return A new sequence, containing the Codon transformation.
	 * @see CodonTransformer#transform(int)
	 */
	public Sequence transform() {
		return transform(0);
	}

	/**
	 * Transforms the sequence, searching for the first start Codon, continuing
	 * from there. The start sequences searched depends on the
	 * {@link CodonTransformationData} used.
	 *
	 * @return A new sequence, containing the Codon transformation.
	 */
	public Sequence transformAuto() {
		final String value = getSequence().getValue();
		for (int i = 0; i + 2 < value.length(); i++) {
			final String sub = value.substring(i, i + 3);
			if (getData().start.containsKey(sub)) {
				return transform(i);
			}
		}
		return null;
	}

	/**
	 * Transforms the sequence from the given offset.
	 *
	 * @param offset
	 *            The offset when to begin the transformation. Must be a
	 *            non-negative number.
	 * @return A new sequence, containing the Codon transformation.
	 * @throws IndexOutOfBoundsException
	 *             If the offset is negative, or offset is not smaller than the
	 *             length of the sequence.
	 */
	public Sequence transform(final int offset) {
		if (offset < 0 || offset >= getSequence().getLength()) {
			throw new IndexOutOfBoundsException("Offset must be a non-negative number, not bigger than the sequence's length. Offset: " + offset);
		}

		final String value = getSequence().getValue();
		final String name = getSequence().getName();
		boolean startFound = false;
		final StringBuilder builder = new StringBuilder(value.length() / 3 + 1);

		for (int i = offset; i + 2 < value.length(); i = i + 3) {
			final String sub = value.substring(i, i + 3);
			if (!startFound && getData().start.containsKey(sub)) {
				builder.append(getData().start.get(sub));
				startFound = true;
			} else if (getData().codon.containsKey(sub)) {
				builder.append(getData().codon.get(sub));
			} else if (getData().end.containsKey(sub)) {
				builder.append(getData().end.get(sub));
			} else {
				builder.append(invalidChar);
			}
		}

		return new Sequence(builder.toString(), name == null ? null : "Codon Transformed: " + name);
	}

	/**
	 * Gets the data that contains information on how to transform a sequence.
	 *
	 * @return The data.
	 */
	public CodonTransformationData getData() {
		return data;
	}

	/**
	 * Gets the sequence used to transform.
	 *
	 * @return The sequence.
	 */
	public Sequence getSequence() {
		return sequence;
	}

}
