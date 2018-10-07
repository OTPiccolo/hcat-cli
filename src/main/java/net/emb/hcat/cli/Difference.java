package net.emb.hcat.cli;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A difference describes the differences between two sequences
 *
 * @author OT Piccolo
 */
public class Difference {

	/**
	 * The default char to indicate that there is no difference in the slave
	 * sequence.
	 */
	public static final char DEFAULT_NO_DIFFERENCE = '.';

	private final String difference;
	private final Sequence master;
	private final Sequence slave;

	private char noDifference = DEFAULT_NO_DIFFERENCE;

	/**
	 * Constructor.
	 *
	 * @param master
	 *            The master sequence that is used as the base sequence to
	 *            compare to. Must not be <code>null</code>
	 * @param slave
	 *            The slave sequence that needs to be compared to the master
	 *            sequence. Must not be <code>null</code>
	 */
	public Difference(final Sequence master, final Sequence slave) {
		if (master == null) {
			throw new IllegalArgumentException("Master sequence must not be null.");
		}
		if (slave == null) {
			throw new IllegalArgumentException("Slave sequence must not be null.");
		}
		this.master = master;
		this.slave = slave;
		difference = difference(master, slave);
	}

	@Override
	public String toString() {
		return difference.toString();
	}

	/**
	 * Checks whether this difference is equal to another difference, ignoring
	 * all but the actual differences. Same as
	 * {@code getDifference().equals(otherDif.getDifference()}.
	 * 
	 * @param dif
	 *            The other difference to check.
	 * @return <code>true</code>, if both have the same differences,
	 *         <code>false</code> otherwise.
	 */
	public boolean equalDif(final Difference dif) {
		return dif == null ? false : difference.equals(dif.difference);
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
		final Difference other = (Difference) obj;
		if (difference == null) {
			if (other.difference != null) {
				return false;
			}
		} else if (!difference.equals(other.difference)) {
			return false;
		}
		if (master == null) {
			if (other.master != null) {
				return false;
			}
		} else if (!master.equals(other.master)) {
			return false;
		}
		if (slave == null) {
			if (other.slave != null) {
				return false;
			}
		} else if (!slave.equals(other.slave)) {
			return false;
		}
		if (noDifference != other.noDifference) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((difference == null) ? 0 : difference.hashCode());
		result = prime * result + ((master == null) ? 0 : master.hashCode());
		result = prime * result + ((slave == null) ? 0 : slave.hashCode());
		result = prime * result + noDifference;
		return result;
	}

	private String difference(final Sequence haplotype, final Sequence otherHaplotype) {
		final int length = haplotype.getLength();
		final int otherLength = otherHaplotype.getLength();
		final int minLength = Math.min(length, otherLength);

		final StringBuilder builder = new StringBuilder(Math.max(length, otherLength));
		for (int i = 0; i < minLength; i++) {
			if (haplotype.getValue().charAt(i) == otherHaplotype.getValue().charAt(i)) {
				builder.append(noDifference);
			} else {
				builder.append(otherHaplotype.getValue().charAt(i));
			}
		}

		for (int i = minLength; i < length; i++) {
			builder.append(' ');
		}

		for (int i = minLength; i < otherLength; i++) {
			builder.append(otherHaplotype.getValue().charAt(i));
		}

		return builder.toString();
	}

	/**
	 * Whether there is a difference between the two given sequences.
	 *
	 * @return <code>true</code>, is there is a difference, or
	 *         <code>false</code>, if there is no difference. If
	 *         <code>true</code>, {@link #getDifference()} will only contain
	 *         {@link #getNoDifference()}-characters.
	 */
	public boolean isDifferent() {
		for (int i = 0; i < getDifference().length(); i++) {
			if (getDifference().charAt(i) != noDifference) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets an ordered set that contains the positions of all the differences.
	 *
	 * @return An ordered set containing all the positions of all the
	 *         differences. Each number will correspond to a change in the
	 *         {@link #getDifference()}-string.
	 */
	public SortedSet<Integer> getDifferencePosition() {
		final TreeSet<Integer> pos = new TreeSet<>();

		for (int i = 0; i < difference.length(); i++) {
			if (difference.charAt(i) != noDifference) {
				pos.add(i);
			}
		}

		return pos;
	}

	/**
	 * Gets the distance of this haplotype. It is the number of differences to
	 * the master sequence.
	 *
	 * @return The distance. Can be zero if this haplotype is equal to its
	 *         master, or the length of the haplotype, if no position is equal.
	 */
	public int getDistance() {
		return getDifferencePosition().size();
	}

	/**
	 * Gets the difference to the master sequence.
	 *
	 * @return A sequence, displaying the differences. It will either show no
	 *         difference in a character, or the character from the slave
	 *         sequence that was used in the input. With it and the master
	 *         sequence, one can recompute the slave sequence.
	 * @see #getMaster()
	 * @see #getNoDifference()
	 */
	public String getDifference() {
		return difference;
	}

	/**
	 * The master sequence that was used to create the difference.
	 *
	 * @return The master sequence.
	 */
	public Sequence getMaster() {
		return master;
	}

	/**
	 * The slave sequence that was used to create the difference.
	 *
	 * @return The slave sequence.
	 */
	public Sequence getSlave() {
		return slave;
	}

	/**
	 * The char that will be used in the difference string to symbolize that
	 * there is no difference.
	 *
	 * @return The char that will be used in the difference string to symbolize
	 *         that there is no difference.
	 * @see #DEFAULT_NO_DIFFERENCE
	 */
	public char getNoDifference() {
		return noDifference;
	}

	/**
	 * The char that will be used in the difference string to symbolize that
	 * there is no difference.
	 *
	 * @param noDifference
	 *            The char that will be used in the difference string to
	 *            symbolize that there is no difference.
	 * @see #DEFAULT_NO_DIFFERENCE
	 */
	public void setNoDifference(final char noDifference) {
		this.noDifference = noDifference;
	}

}
