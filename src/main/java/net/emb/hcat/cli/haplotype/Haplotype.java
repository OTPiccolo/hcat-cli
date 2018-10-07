package net.emb.hcat.cli.haplotype;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import net.emb.hcat.cli.Sequence;

/**
 * A haplotype is a set, containing all sequences that are identical in their
 * value attribute.
 *
 * @author OT Piccolo
 */
public class Haplotype extends LinkedHashSet<Sequence> {

	private static final long serialVersionUID = 6822250160172188239L;

	/**
	 * Turns a collection of sequences into their corresponding haplotypes.
	 *
	 * @param sequences
	 *            The sequences to compactify.
	 * @return A list, containing all found haplotypes.
	 */
	public static final List<Haplotype> createHaplotypes(final Collection<Sequence> sequences) {
		if (sequences == null) {
			return null;
		}
		final List<Haplotype> haplotypes = new ArrayList<Haplotype>();
		for (final Sequence sequence : sequences) {
			boolean foundHaplotype = false;
			for (final Haplotype haplotype : haplotypes) {
				if (haplotype.belongsToHaplotype(sequence)) {
					haplotype.add(sequence);
					foundHaplotype = true;
					break;
				}
			}
			if (!foundHaplotype) {
				haplotypes.add(new Haplotype(sequence));
			}
		}
		return haplotypes;
	}

	/**
	 * Constructor.
	 */
	public Haplotype() {
		super();
	}

	/**
	 * Constructor. Initializes it with a sequence already belonging to this
	 * haplotype.
	 *
	 * @param sequence
	 *            The sequence to add. Must not be <code>null</code>.
	 */
	public Haplotype(final Sequence sequence) {
		super();
		add(sequence);
	}

	/**
	 * Checks whether the given sequence does belong to this haplotype. For
	 * this, the sequence value must be identical to the other sequences already
	 * in this haplotype.
	 *
	 * @param sequence
	 *            The sequence to check.
	 * @return <code>true</code>, if the given sequence belongs to this
	 *         haplotype, <code>false</code> otherwise. If this haplotype is
	 *         empty so far, this always returns <code>true</code>.
	 */
	public boolean belongsToHaplotype(final Sequence sequence) {
		if (sequence == null) {
			return false;
		}
		if (isEmpty()) {
			return true;
		}
		final Sequence compTo = iterator().next();
		return sequence.equalSeq(compTo);
	}

	@Override
	public boolean add(final Sequence sequence) {
		if (sequence == null) {
			throw new IllegalArgumentException("Sequence must not be null.");
		}
		if (!belongsToHaplotype(sequence)) {
			throw new IllegalArgumentException("Sequence " + sequence + " does not belong to this haplotype.");
		}
		return super.add(sequence);
	}

}
