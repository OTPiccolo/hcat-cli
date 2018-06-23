package net.emb.hcat.cli;

import java.util.ArrayList;
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
	 * Compares sequences to a master sequence.
	 *
	 * @param master
	 *            The master sequence to compare the other sequences to.
	 * @return A map containing for each found haplotype, all sequences that are
	 *         the same.
	 */
	public Map<Haplotype, List<Sequence>> compareToMaster(final Sequence master) {
		final Map<Haplotype, List<Sequence>> haplotypesMap = new LinkedHashMap<>();
		for (final Sequence sequence : compare) {
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
	 * Finds in all the sequences the haplotype with the most sequences to it.
	 *
	 * @return Finds in all the sequences the haplotype with the most sequences
	 *         to it. If two haplotypes have the same number of sequences, only
	 *         the first found haplotype is returned.
	 */
	public List<Sequence> findMostMatchHaplotype() {
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
