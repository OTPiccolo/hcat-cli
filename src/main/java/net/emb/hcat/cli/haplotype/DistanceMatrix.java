package net.emb.hcat.cli.haplotype;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.emb.hcat.cli.Difference;
import net.emb.hcat.cli.Sequence;

/**
 * A distance matrix describes the distance of each haplotype to each other
 * haplotypes.
 *
 * @author Heiko Mattes
 */
public class DistanceMatrix {

	private final Map<Haplotype, Map<Haplotype, Integer>> matrix;

	/**
	 * Constructor.
	 *
	 * @param haplotypes
	 *            A collection of haplotypes that should be compared. No
	 *            haplotype should be empty (as in, having no sequences to it)
	 *            or it will be skipped.
	 */
	public DistanceMatrix(final List<Haplotype> haplotypes) {
		if (haplotypes == null) {
			throw new IllegalArgumentException("Haplotype list must not be null.");
		}
		matrix = createMatrix(haplotypes);
	}

	private Map<Haplotype, Map<Haplotype, Integer>> createMatrix(final List<Haplotype> haplotypes) {
		final Map<Haplotype, Map<Haplotype, Integer>> map = new LinkedHashMap<>(2 * haplotypes.size());
		for (int i = 0; i < haplotypes.size(); i++) {
			final Haplotype haplotype1 = haplotypes.get(i);
			final Sequence seq1 = haplotype1.getFirstSequence();
			if (seq1 == null) {
				continue;
			}
			final Map<Haplotype, Integer> innerMap1 = getInnerMap(map, haplotype1);

			for (int j = i + 1; j < haplotypes.size(); j++) {
				final Haplotype haplotype2 = haplotypes.get(j);
				final Sequence seq2 = haplotype2.getFirstSequence();
				if (seq2 == null) {
					continue;
				}
				final Map<Haplotype, Integer> innerMap2 = getInnerMap(map, haplotype2);

				final Difference difference = new Difference(seq1, seq2);
				final int distance = difference.getDistance();
				innerMap1.put(haplotype2, distance);
				innerMap2.put(haplotype1, distance);
			}
		}
		return map;
	}

	private Map<Haplotype, Integer> getInnerMap(final Map<Haplotype, Map<Haplotype, Integer>> map, final Haplotype haplotype) {
		Map<Haplotype, Integer> innerMap = map.get(haplotype);
		if (innerMap == null) {
			innerMap = new LinkedHashMap<>();
			map.put(haplotype, innerMap);
		}
		return innerMap;
	}

	/**
	 * Gets the distance between the two given haplotypes. If either is null, or
	 * one can't be found, null is returned.
	 *
	 * @param haplotype1
	 *            The first haplotype.
	 * @param haplotype2
	 *            The second haplotype.
	 * @return The distance between the two haplotypes.
	 */
	public Integer getDistance(final Haplotype haplotype1, final Haplotype haplotype2) {
		final Map<Haplotype, Integer> map = matrix.get(haplotype1);
		if (map != null) {
			return map.get(haplotype2);
		}
		return null;
	}

	/**
	 * Gets all distances for a given haplotype.
	 *
	 * @param haplotype
	 *            The haplotype to get all distances for.
	 * @return An unmodifable map containing for each other haplotype the
	 *         distance to the given haplotype.
	 */
	public Map<Haplotype, Integer> getDistances(final Haplotype haplotype) {
		final Map<Haplotype, Integer> map = matrix.get(haplotype);
		if (map != null) {
			return Collections.unmodifiableMap(map);
		}
		return null;
	}

	/**
	 * Gets the complete distance matrix.
	 *
	 * @return A copy of the internal map, containing for each haplotype the
	 *         distance to each other haplotype.
	 */
	public Map<Haplotype, Map<Haplotype, Integer>> getMatrix() {
		final Map<Haplotype, Map<Haplotype, Integer>> copy = new LinkedHashMap<>();
		for (final Entry<Haplotype, Map<Haplotype, Integer>> entry : matrix.entrySet()) {
			copy.put(entry.getKey(), new LinkedHashMap<Haplotype, Integer>(entry.getValue()));
		}
		return copy;
	}

}
