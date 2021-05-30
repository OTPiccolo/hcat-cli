package net.emb.hcat.cli.codon;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.emb.hcat.cli.sequence.Sequence;

/**
 * Changes a sequence to a codon.
 *
 * @author Heiko Mattes
 */
public class CodonTransformer {

	private static final Logger log = LoggerFactory.getLogger(CodonTransformer.class);

	private static final char invalidChar = '?';

	private final CodonTransformationData data;
	private final Sequence sequence;

	private final Set<Integer> alternativeStart = new HashSet<>();
	private final Set<Integer> alternativeEnd = new HashSet<>();

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
	 * {@link CodonTransformationData} used. If no start Codon was found in the
	 * first three offsets, instead the transformation that produces the least
	 * end codons and no invalid codons will be returned.
	 *
	 * @return A new sequence, containing the Codon transformation.
	 */
	public Sequence transformAuto() {
		log.debug("Automatic transformation.");
		final int offset = findOffset();
		return offset == -1 ? transform() : transform(offset);
	}

	/**
	 * Find the offset that is most likely to produce a valid transformation.
	 *
	 * @return The offset, or -1 if no valid transformation could be found.
	 */
	public int findOffset() {
		log.debug("Checking probable offset.");
		// Sequence too short.
		if (getSequence().getValue().length() < 5) {
			log.debug("Sequence length too short. Starting at offset zero.");
			return 0;
		}

		// Look for start codon.
		int offset = findStartOffset();
		if (offset == -1) {
			// If not found, look for sequence with least end codons.
			offset = findLeastEndOffset();
		}
		log.debug("Probable offset: {}", offset);
		return offset;
	}

	// Look for sequence with start codon.
	private int findStartOffset() {
		log.debug("Looking for start codons.");
		final String value = getSequence().getValue();
		for (int i = 0; i < 3; i++) {
			final String sub = value.substring(i, i + 3);
			if (getData().start.containsKey(sub)) {
				log.debug("Found start codon at offset: {}", i);
				return i;
			}
		}
		return -1;
	}

	// Look for sequence that contains the least end codons and no invalid
	// codons.
	private int findLeastEndOffset() {
		log.debug("Looking for least end codons.");
		int offset = -1;
		int endCount = Integer.MAX_VALUE;
		final Map<String, Character> end = getData().end;

		for (int i = 0; i < 3; i++) {
			final Sequence transform = transform(i);
			final String value = transform.getValue();
			int currentCount = 0;
			for (int j = 0; j < value.length(); j++) {
				final char c = value.charAt(j);
				// An invalid character encountered, so cannot be a valid
				// offset.
				if (c == invalidChar) {
					currentCount = Integer.MAX_VALUE;
					break;
				}
				if (end.containsValue(c)) {
					currentCount++;
				}
			}
			log.debug("At offset {}, found {} end codon(s)", i, currentCount);
			if (currentCount < endCount) {
				offset = i;
				endCount = currentCount;
				// No end codons found, can't get better than that.
				if (endCount == 0) {
					break;
				}
			}
		}

		return offset;
	}

	/**
	 * Transforms the sequence from the given offset.
	 *
	 * @param offset
	 *            The offset when to begin the transformation. Must be a
	 *            non-negative number.
	 * @return A new sequence, containing the Codon transformation. Trailing
	 *         chars that can't be converted to a Codon will be dropped.
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
		log.debug("Transforming sequence with name \"{}\" from offset {}: {}", name, offset, value);

		final boolean checkAdditionalStart = !getAlternativeStart().isEmpty();
		final boolean checkAdditionalEnd = !getAlternativeEnd().isEmpty();
		final StringBuilder builder = new StringBuilder(value.length() / 3 + 1);

		for (int i = offset; i + 2 < value.length(); i = i + 3) {
			final String sub = value.substring(i, i + 3);
			if (checkAdditionalStart && getAlternativeStart().contains((i - offset) / 3)) {
				builder.append(getData().start.containsKey(sub) ? (char) getData().start.get(sub) : invalidChar);
			} else if (checkAdditionalEnd && getAlternativeEnd().contains((i - offset) / 3)) {
				builder.append(getData().end.containsKey(sub) ? (char) getData().end.get(sub) : invalidChar);
			} else if (getData().codon.containsKey(sub)) {
				builder.append(getData().codon.get(sub));
			} else {
				builder.append(invalidChar);
			}
		}

		final Sequence transformedSeq = new Sequence(builder.toString(), name == null ? null : "Codon Transformed: " + name);
		log.debug("Transformed sequence: {}", transformedSeq);
		return transformedSeq;
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

	/**
	 * Defines positions where an alternate start codon is supposed to appear.
	 * Usually, alternative start codons will not be translated. If at those
	 * positions no start codons are encountered, invalid codons will be
	 * translated.
	 *
	 * @return A set containing all additional start codon positions. The
	 *         positions must be given for after the translation. For example,
	 *         if you expect the 'AAA' codon in the sequence 'AAABBBCCC' to be a
	 *         start codon, use '0'. If it should be 'BBB', use '1'. Offsets in
	 *         the start sequence will be ignored. So if 'BCD' is supposed to be
	 *         a start codon in the sequence 'ABCD', still use '0'.
	 */
	public Set<Integer> getAlternativeStart() {
		return alternativeStart;
	}

	/**
	 * Defines positions where an alternate end codon is supposed to appear.
	 * Usually, alternative end codons will not be translated. If at those
	 * positions no end codons are encountered, invalid codons will be
	 * translated.
	 *
	 * @return A set containing all alternative end codon positions. The
	 *         positions must be given for after the translation. For example,
	 *         if you expect the 'BBB' codon in the sequence 'AAABBBCCC' to be
	 *         an end codon, use '1'. If it should be 'CCC', use '2'. So if
	 *         'BCD' is supposed to be an end codon in the sequence 'ABCD',
	 *         still use '0'.
	 */
	public Set<Integer> getAlternativeEnd() {
		return alternativeEnd;
	}

}
