package net.emb.hcat.cli.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import net.emb.hcat.cli.haplotype.Haplotype;
import net.emb.hcat.cli.sequence.Difference;
import net.emb.hcat.cli.sequence.Sequence;

/**
 * A writer to write out haplotypes as text.
 *
 * @author Heiko Mattes
 */
public class HaplotypeWriter {

	// Indents the given Appendable by the given amount of spaces.
	private static void indent(final int times, final Appendable appendable) throws IOException {
		for (int i = 0; i < times; i++) {
			appendable.append(' ');
		}
	}

	private final BufferedWriter writer;

	/**
	 * Constructor.
	 *
	 * @param writer
	 *            The writer data should be written to.
	 */
	public HaplotypeWriter(final Writer writer) {
		if (writer == null) {
			throw new IllegalArgumentException("Writer can't be null.");
		}
		this.writer = new BufferedWriter(writer, 1024);
	}

	/**
	 * Writes the haplotypes. Only shows the difference, and not the positions
	 * where every haplotype is the same.
	 *
	 * @param master
	 *            The master sequence to write out fully, as a comparison to the
	 *            changes.
	 * @param result
	 *            A map containing for each found haplotype, all sequences that
	 *            are the same.
	 * @throws IOException
	 *             An I/O exception.
	 */
	public void write(final Sequence master, final Map<Haplotype, Difference> result) throws IOException {
		// Calculate all positions.
		final Set<Integer> positions = new TreeSet<>();
		for (final Difference difference : result.values()) {
			positions.addAll(difference.getDifferencePosition());
		}

		writer.append("Master sequence: ");
		writer.append(master.getName());
		writer.newLine();
		writer.flush();

		final int positionLength = "Positions".length();
		final int masterLength = "Master".length();

		// Write for each difference the name of all sequences.
		int maxLength = Math.max(positionLength, masterLength);
		final Map<Haplotype, StringBuilder> names = new LinkedHashMap<>();
		for (final Haplotype haplotype : result.keySet()) {
			final StringBuilder builder = new StringBuilder();
			names.put(haplotype, builder);
			for (final Sequence sequence : haplotype) {
				builder.append(sequence.getName());
				builder.append("; ");
			}
			builder.delete(builder.length() - 2, builder.length());
			maxLength = Math.max(maxLength, builder.length());
		}

		// Pretty print the names so all are the same length.
		for (final StringBuilder builder : names.values()) {
			indent(maxLength - builder.length(), builder);
			builder.append(':');
		}

		// Write all position numbers.
		writer.append("Positions");
		indent(maxLength - positionLength, writer);
		writer.append(':');
		for (final Integer pos : positions) {
			writer.append('\t');
			writer.append(String.valueOf(pos.intValue() + 1));
		}
		writer.newLine();
		writer.flush();

		// Write master sequence.
		writer.append("Master");
		indent(maxLength - masterLength, writer);
		writer.append(':');
		for (final Integer pos : positions) {
			writer.append('\t');
			writer.append(master.getValue().charAt(pos.intValue()));
		}
		writer.newLine();
		writer.flush();

		// Write out the differences.
		for (final Entry<Haplotype, StringBuilder> entry : names.entrySet()) {
			final String difference = result.get(entry.getKey()).getDifference();
			writer.append(entry.getValue().toString());
			for (final Integer pos : positions) {
				writer.append('\t');
				writer.append(difference.charAt(pos.intValue()));
			}
			writer.newLine();
			writer.flush();
		}
	}

	/**
	 * Convenience method to close the underlying writer.
	 *
	 * @see Writer#close()
	 */
	public void close() {
		try {
			writer.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
