package net.emb.hcat.cli.sequence;

/**
 * A sequence contains the information about the DNA string.
 *
 * @author Heiko Mattes
 */
public class Sequence {

	private final String value;
	private String name;

	/**
	 * Constructor.
	 *
	 * @param value
	 *            A sequence. Must not be <code>null</code>.
	 */
	public Sequence(final String value) {
		if (value == null) {
			throw new IllegalArgumentException("Value must not be null.");
		}
		this.value = value;
	}

	/**
	 * Constructor.
	 *
	 * @param value
	 *            A sequence. Must not be <code>null</code>.
	 * @param name
	 *            The name of the sequence.
	 * @see #setName(String)
	 */
	public Sequence(final String value, final String name) {
		this(value);
		setName(name);
	}

	/**
	 * Checks whether this sequence is equal to another sequence, ignoring its
	 * name.
	 * 
	 * @param seq
	 *            The other sequence to check.
	 * @return <code>true</code>, if both sequence values are the same,
	 *         <code>false</code> otherwise.
	 */
	public boolean equalSeq(final Sequence seq) {
		return seq == null ? false : value.equals(seq.value);
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
		final Sequence other = (Sequence) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public String toString() {
		if (name == null) {
			return value;
		}
		return name + " -> " + value;
	}

	/**
	 * Gets the length of the sequence.
	 *
	 * @return The length of the sequence.
	 */
	public int getLength() {
		return value.length();
	}

	/**
	 * Gets the sequence.
	 *
	 * @return The sequence.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Gets the name of the sequence.
	 *
	 * @return The name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the sequence. Should be unique among all sequences that
	 * are compared.
	 *
	 * @param name
	 *            The name.
	 */
	public void setName(final String name) {
		this.name = name;
	}

}
