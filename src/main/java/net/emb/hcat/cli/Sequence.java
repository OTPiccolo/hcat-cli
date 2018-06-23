package net.emb.hcat.cli;

/**
 * A sequence contains the information about the DNA string.
 *
 * @author OT Piccolo
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

	@Override
	public String toString() {
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
