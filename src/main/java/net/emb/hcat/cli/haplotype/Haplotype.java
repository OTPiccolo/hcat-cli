package net.emb.hcat.cli.haplotype;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import net.emb.hcat.cli.sequence.Sequence;

/**
 * A haplotype is a set, containing all sequences that are identical in their
 * value attribute.
 *
 * @author Heiko Mattes
 */
public class Haplotype extends LinkedHashSet<Sequence> {

	private static final long serialVersionUID = 6822250160172188239L;

	/**
	 * Turns a collection of sequences into their corresponding haplotypes.
	 *
	 * @param sequences
	 *            The sequences to wrap into haplotypes.
	 * @return A list, containing all found haplotypes.
	 */
	public static final List<Haplotype> wrap(final Collection<Sequence> sequences) {
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
				final Haplotype haplotype = new Haplotype(sequence);
				haplotypes.add(haplotype);
				haplotype.setName((haplotypes.size() < 10 ? "Hap0" : "Hap") + haplotypes.size());
			}
		}
		return haplotypes;
	}

	/**
	 * Turns a collection of haplotypes into their corresponding sequences.
	 *
	 * @param haplotypes
	 *            The haplotypes to unwrap into sequences.
	 * @return A list, containing all sequences within the haplotypes.
	 */
	public static final List<Sequence> unwrap(final Collection<Haplotype> haplotypes) {
		if (haplotypes == null) {
			return null;
		}
		final List<Sequence> sequences = new ArrayList<Sequence>();
		for (final Haplotype haplotype : haplotypes) {
			sequences.addAll(haplotype);
		}
		return sequences;
	}

	private String name;

	/**
	 * Constructor.
	 */
	public Haplotype() {
		super();
	}

	/**
	 * Constructor. Initializes this empty haplotype with the given name.
	 * 
	 * @param name
	 *            The name for this haplotype.
	 */
	public Haplotype(final String name) {
		super();
		setName(name);
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

	/**
	 * Returns the first sequence in this haplotype.
	 *
	 * @return The first sequence in this haplotype, or <code>null</code>, if
	 *         this haplotype is still empty.
	 */
	public Sequence getFirstSequence() {
		return isEmpty() ? null : iterator().next();
	}

	/**
	 * Returns this haplotype as if it was a sequence. The name of the haplotype
	 * will be used for the name of the sequence.
	 *
	 * @return This haplotype as a sequence, or <code>null</code>, if this
	 *         haplotype is still empty.
	 */
	public Sequence asSequence() {
		return isEmpty() ? null : new Sequence(iterator().next().getValue(), getName());
	}

	/**
	 * Gets the name for this haplotype.
	 *
	 * @return The name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name for this haploytpe.
	 *
	 * @param name
	 *            The name.
	 */
	public void setName(final String name) {
		this.name = name;
	}

}
