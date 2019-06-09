package net.emb.hcat.cli.codon;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Data to perform a Codon transformation.
 * 
 * @author Heiko Mattes
 */
public class CodonTransformationData {

	/** Name of the Codon transformation. */
	public String name;
	/** Unique number of the Codon transformation. */
	public int number;
	/**
	 * Contains for each triple of ACGT into which codon it will be transformed.
	 */
	public final SortedMap<String, Character> codon = new TreeMap<String, Character>();
	/**
	 * Contains for each triple of ACGT into which start codon it will be
	 * transformed.
	 */
	public final SortedMap<String, Character> start = new TreeMap<String, Character>();
	/**
	 * Contains for each triple of ACGT into which end codon it will be
	 * transformed.
	 */
	public final SortedMap<String, Character> end = new TreeMap<String, Character>();

	@Override
	public String toString() {
		return "CodonTransformationData [name=" + name + ", number=" + number + ", codon=" + codon + ", start=" + start + ", end=" + end + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final CodonTransformationData other = (CodonTransformationData) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

}
