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
 * A writer to write out haplotype table as text.
 *
 * @author Heiko Mattes
 */
public class HaplotypeTableWriter {

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
	public HaplotypeTableWriter(final Writer writer) {
		if (writer == null) {
			throw new IllegalArgumentException("Writer can't be null.");
		}
		this.writer = writer instanceof BufferedWriter ? (BufferedWriter) writer : new BufferedWriter(writer, 1024);
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
		final Haplotype masterHaplotype = Haplotype.find(master, result.keySet());
		if (masterHaplotype == null) {
			throw new IOException("Sequence does not belong to any haplotype. Can not write out haplotype table. Sequence name: " + master.getName());
		}

		// Calculate all positions.
		final Set<Integer> positions = new TreeSet<>();
		for (final Difference difference : result.values()) {
			positions.addAll(difference.getDifferencePosition());
		}

		final int positionLength = "Positions".length();

		// Write for each difference the name of all sequences.
		int maxLength = positionLength;
		final Map<Haplotype, StringBuilder> names = new LinkedHashMap<>();
		for (final Haplotype haplotype : result.keySet()) {
			final StringBuilder builder = new StringBuilder(maxLength);
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
			builder.ensureCapacity(maxLength);
			indent(maxLength - builder.length(), builder);
		}

		// Write all position numbers.
		writer.append("Positions");
		indent(maxLength - positionLength, writer);
		for (final Integer pos : positions) {
			writer.append('\t');
			writer.append(String.valueOf(pos.intValue() + 1));
		}
		writer.newLine();
		writer.flush();

		// Write master sequence.
		final String masterName = names.get(masterHaplotype).toString();
		writer.append(masterName);
		indent(maxLength - masterName.length(), writer);
		for (final Integer pos : positions) {
			writer.append('\t');
			writer.append(master.getValue().charAt(pos.intValue()));
		}
		writer.newLine();
		writer.flush();

		// Write out the differences.
		for (final Entry<Haplotype, StringBuilder> entry : names.entrySet()) {
			final Haplotype haplotype = entry.getKey();
			if (masterHaplotype == haplotype) {
				// Already written master haplotype.
				continue;
			}
			final String difference = result.get(haplotype).getDifference();
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
