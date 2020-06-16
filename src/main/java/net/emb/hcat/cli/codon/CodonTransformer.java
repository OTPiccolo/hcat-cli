package net.emb.hcat.cli.codon;

import java.util.Map;

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
		log.debug("Automatic transformation. Checking offsets.");
		final String value = getSequence().getValue();
		// Sequence too short.
		if (value.length() < 5) {
			log.debug("Sequence length too short. Starting at offset zero.");
			return transform();
		}

		// Look for start codon.
		Sequence transformed = findStart();
		if (transformed == null) {
			// If not found, look for sequence with least end codons.
			transformed = findLeastEnd();
		}
		return transformed;
	}

	// Look for sequence with start codon.
	private Sequence findStart() {
		final String value = getSequence().getValue();
		for (int i = 0; i < 3; i++) {
			final String sub = value.substring(i, i + 3);
			if (getData().start.containsKey(sub)) {
				log.debug("Found start codon at offset: {}", i);
				return transform(i);
			}
		}
		return null;
	}

	// Look for sequence that contains the least end codons and no invalid
	// codons.
	private Sequence findLeastEnd() {
		log.debug("Looking for least end codons.");
		final Map<String, Character> end = getData().end;
		Sequence transformed = null;
		int endCount = Integer.MAX_VALUE;
		for (int i = 0; i < 3; i++) {
			final Sequence transform = transform(i);
			final String value = transform.getValue();
			int currentCount = 0;
			for (int j = 0; j < value.length(); j++) {
				final char c = value.charAt(j);
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
				transformed = transform;
				endCount = currentCount;
				// No end codons found, can't get better than that.
				if (endCount == 0) {
					break;
				}
			}
		}

		// All sequences contained invalid characters, so just use offset zero.
		if (transformed == null) {
			transformed = transform();
		}
		return transformed;
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

}
