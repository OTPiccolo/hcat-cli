package net.emb.hcat.cli;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Splicer can compare sequences with each other to compute differences.
 *
 * @author OT Piccolo
 */
public class Splicer {

	private final List<Sequence> compare = new ArrayList<>();

	/**
	 * Constructor.
	 */
	public Splicer() {
		// Do nothing;
	}

	/**
	 * Constructor.
	 *
	 * @param sequences
	 *            A collection of sequences that should be compared.
	 * @see #getCompare()
	 */
	public Splicer(final Collection<Sequence> sequences) {
		getCompare().addAll(sequences);
	}

	/**
	 * Compares sequences to the master sequence with the given ID. This will
	 * search through all given sequence.
	 *
	 * @param masterId
	 *            The ID of the master sequence to use.
	 * @return A map containing for each found haplotype, all sequences that are
	 *         the same. Returns <code>null</code> if a sequence with the given
	 *         ID could not be found.
	 * @see #getCompare()
	 */
	public Map<Haplotype, List<Sequence>> compareToMaster(final String masterId) {
		if (masterId == null) {
			return null;
		}

		for (final Sequence sequence : getCompare()) {
			if (masterId.equals(sequence.getName())) {
				return compareToMaster(sequence);
			}
		}
		return null;
	}

	/**
	 * Compares sequences to a master sequence.
	 *
	 * @param master
	 *            The master sequence to compare the other sequences to. Must
	 *            not be <code>null</code>.
	 * @return A map containing for each found haplotype, all sequences that are
	 *         the same.
	 */
	public Map<Haplotype, List<Sequence>> compareToMaster(final Sequence master) {
		if (master == null) {
			throw new IllegalArgumentException("Master sequence must not be null.");
		}

		final Map<Haplotype, List<Sequence>> haplotypesMap = new LinkedHashMap<>();
		for (final Sequence sequence : getCompare()) {
			if (master.getLength() != sequence.getLength()) {
				System.out.println("WARN: Sequence '" + sequence.getName() + "' has different length to master sequence. Expected length: " + master.getLength() + ". Actual length: " + sequence.getLength());
				continue;
			}
			final Haplotype difference = new Haplotype(master, sequence);
			List<Sequence> haplotypes = haplotypesMap.get(difference);
			if (haplotypes == null) {
				haplotypes = new ArrayList<>();
				haplotypesMap.put(difference, haplotypes);
			}
			haplotypes.add(sequence);
		}
		return haplotypesMap;
	}

	/**
	 * Finds in all the sequences the most sequences that are equal to each
	 * other.
	 *
	 * @return Finds in all the sequences the most sequences that are equal to
	 *         each other. If two sequence types are tied for the most
	 *         sequences, only one is returned
	 */
	public List<Sequence> findMostMatchSequences() {
		List<Sequence> most = Collections.emptyList();
		int maxHaplotypes = 0;
		for (final Sequence haplotype : getCompare()) {
			for (final List<Sequence> list : compareToMaster(haplotype).values()) {
				if (list.size() > maxHaplotypes) {
					most = list;
					maxHaplotypes = list.size();
				}
			}
		}
		return most;
	}

	/**
	 * Gets the list that will be used in the splicer.
	 *
	 * @return The list.
	 */
	public List<Sequence> getCompare() {
		return compare;
	}

}
