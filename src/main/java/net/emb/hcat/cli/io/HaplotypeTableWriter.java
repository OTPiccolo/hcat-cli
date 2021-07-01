package net.emb.hcat.cli.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.emb.hcat.cli.haplotype.Haplotype;
import net.emb.hcat.cli.sequence.Difference;
import net.emb.hcat.cli.sequence.Sequence;

/**
 * A writer to write out haplotype table as text.
 *
 * @author Heiko Mattes
 */
public class HaplotypeTableWriter {

	private static final Logger log = LoggerFactory.getLogger(HaplotypeTableWriter.class);

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

		log.info("Writing haplotype table with master haplotype: {}", masterHaplotype.getName());
		// Calculate all positions.
		final Set<Integer> positions = new TreeSet<>();
		for (final Difference difference : result.values()) {
			positions.addAll(difference.getDifferencePosition());
		}

		final int hapLength = "Haplotype".length();
		final int seqLength = "Sequences".length();

		// Determine the max length of haplotype names.
		int hapMaxLength = hapLength;
		for (final Haplotype hap : result.keySet()) {
			final String hapName = hap.getName();
			hapMaxLength = Math.max(hapMaxLength, hapName == null ? 0 : hapName.length());
		}

		// Determine for each difference the name of all sequences.
		int seqMaxLength = seqLength;
		final Map<Haplotype, StringBuilder> seqNames = new LinkedHashMap<>();
		for (final Haplotype haplotype : result.keySet()) {
			final StringBuilder builder = new StringBuilder(seqMaxLength);
			seqNames.put(haplotype, builder);
			for (final Sequence sequence : haplotype) {
				builder.append(sequence.getName());
				builder.append("; ");
			}
			builder.delete(builder.length() - 2, builder.length());
			seqMaxLength = Math.max(seqMaxLength, builder.length());
		}

		// Pretty print the names so all are the same length.
		for (final StringBuilder builder : seqNames.values()) {
			builder.ensureCapacity(seqMaxLength);
			indent(seqMaxLength - builder.length(), builder);
		}

		// Write headers.
		writer.append("Haplotype");
		indent(hapMaxLength - hapLength, writer);
		writer.append("\tSequences");
		indent(seqMaxLength - seqLength, writer);
		writer.append("\tCount");
		for (final Integer pos : positions) {
			writer.append('\t');
			writer.append(String.valueOf(pos.intValue() + 1));
		}
		writer.newLine();
		writer.flush();

		// Write master sequence.
		final String hapMasterName = masterHaplotype.getName() == null ? "" : masterHaplotype.getName();
		writer.append(hapMasterName);
		indent(hapMaxLength - hapMasterName.length(), writer);
		writer.append('\t');
		final String seqMasterName = seqNames.get(masterHaplotype).toString();
		writer.append(seqMasterName);
		indent(seqMaxLength - seqMasterName.length(), writer);
		writer.append('\t');
		writer.append(Integer.toString(masterHaplotype.size()));
		for (final Integer pos : positions) {
			writer.append('\t');
			writer.append(master.getValue().charAt(pos.intValue()));
		}
		writer.newLine();
		writer.flush();

		// Write out the differences.
		for (final Entry<Haplotype, StringBuilder> entry : seqNames.entrySet()) {
			final Haplotype haplotype = entry.getKey();
			if (masterHaplotype == haplotype) {
				// Already written master haplotype.
				continue;
			}
			final String hapName = haplotype.getName() == null ? "" : haplotype.getName();
			writer.append(hapName);
			indent(hapMaxLength - hapName.length(), writer);
			writer.append('\t');
			writer.append(entry.getValue().toString());
			writer.append('\t');
			writer.append(Integer.toString(entry.getKey().size()));
			final String difference = result.get(haplotype).getDifference();
			for (final Integer pos : positions) {
				writer.append('\t');
				writer.append(difference.charAt(pos.intValue()));
			}
			writer.newLine();
			writer.flush();
		}

		log.info("{} entries successfully written.", result.size());
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
