package net.emb.hcat.cli.haplotype;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.emb.hcat.cli.sequence.Difference;
import net.emb.hcat.cli.sequence.Sequence;

/**
 * Splicer can compare sequences with each other to compute differences.
 *
 * @author Heiko Mattes
 */
public class HaplotypeTransformer {

	private static final Logger log = LoggerFactory.getLogger(HaplotypeTransformer.class);

	private final List<Haplotype> compare = new ArrayList<>();

	/**
	 * Constructor.
	 */
	public HaplotypeTransformer() {
		// Do nothing;
	}

	/**
	 * Constructor.
	 *
	 * @param haplotypes
	 *            A collection of haplotypes that should be compared.
	 * @see #getCompare()
	 */
	public HaplotypeTransformer(final Collection<Haplotype> haplotypes) {
		getCompare().addAll(haplotypes);
	}

	/**
	 * Compares haplotypes to a master sequence with the given ID. This will
	 * search through all given haplotypes and their containing sequences.
	 *
	 * @param masterId
	 *            The ID of the master sequence to use.
	 * @return A map containing for each haplotype, the corresponding difference
	 *         to the master sequence. Returns <code>null</code> if a sequence
	 *         with the given ID could not be found.
	 * @see #getCompare()
	 */
	public Map<Haplotype, Difference> compareToMaster(final String masterId) {
		if (masterId == null) {
			return null;
		}

		for (final Haplotype haplotype : getCompare()) {
			for (final Sequence sequence : haplotype) {
				if (masterId.equals(sequence.getName())) {
					return compareToMaster(sequence);
				}
			}
		}

		log.warn("No sequence found with master ID: {}", masterId);
		return null;
	}

	/**
	 * Compares haplotypes to a master sequence.
	 *
	 * @param master
	 *            The master sequence to compare the haplotypes to. Must not be
	 *            <code>null</code>.
	 * @return A map containing for each haplotype, the corresponding
	 *         differences.
	 */
	public Map<Haplotype, Difference> compareToMaster(final Sequence master) {
		if (master == null) {
			throw new IllegalArgumentException("Master sequence must not be null.");
		}

		final Map<Haplotype, Difference> haplotypesMap = new LinkedHashMap<>();
		for (final Haplotype haplotype : getCompare()) {
			if (haplotype.isEmpty()) {
				continue;
			}
			final Sequence sequence = haplotype.iterator().next();
			if (master.getLength() != sequence.getLength()) {
				log.warn("Sequence '{}' has different length to master sequence. Expected length: {} / Actual length: {}", sequence.getName(), master.getLength(), sequence.getLength());
				continue;
			}
			final Difference difference = new Difference(master, sequence);
			haplotypesMap.put(haplotype, difference);
		}
		return haplotypesMap;
	}

	/**
	 * Gets the list that will be used in the transformer.
	 *
	 * @return The list.
	 */
	public List<Haplotype> getCompare() {
		return compare;
	}

}
