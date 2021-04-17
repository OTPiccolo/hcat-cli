package net.emb.hcat.cli.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.emb.hcat.cli.haplotype.DistanceMatrix;
import net.emb.hcat.cli.haplotype.Haplotype;

/**
 * Writes out the distance matrix in text form.
 *
 * @author OT Piccolo
 */
public class DistanceMatrixWriter {

	private static final Logger log = LoggerFactory.getLogger(DistanceMatrixWriter.class);

	private static final String HEADER = "HT-ID";

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
	public DistanceMatrixWriter(final Writer writer) {
		if (writer == null) {
			throw new IllegalArgumentException("Writer can't be null.");
		}
		this.writer = writer instanceof BufferedWriter ? (BufferedWriter) writer : new BufferedWriter(writer, 1024);
	}

	/**
	 * Writes the distance matrix.
	 *
	 * @param matrix
	 *            The distance matrix to write.
	 * @throws IOException
	 *             An I/O exception.
	 */
	public void write(final DistanceMatrix matrix) throws IOException {
		log.info("Writing distance matrix.");

		final Map<Haplotype, Map<Haplotype, Integer>> matrixMap = matrix.getMatrix();

		// Compute maximum length of names, so indentation can happen.
		int maxLength = HEADER.length();
		for (final Haplotype haplotype : matrixMap.keySet()) {
			final String name = haplotype.getName();
			maxLength = Math.max(maxLength, name != null ? name.length() : 4);
		}

		// Writing header line.
		writer.append(HEADER);
		indent(maxLength - HEADER.length(), writer);
		for (final Haplotype haplotype : matrixMap.keySet()) {
			writer.append('\t');
			final String name = haplotype.getName();
			writer.append(name);

			final int length = name != null ? name.length() : 4;
			if (length < 4) {
				indent(4 - length, writer);
			}
		}
		writer.newLine();
		writer.flush();

		// Writing data.
		for (final Haplotype haplotype : matrixMap.keySet()) {
			final String name = haplotype.getName();
			writer.append(name);
			final int length = name != null ? name.length() : 4;
			indent(maxLength - length, writer);
			final Map<Haplotype, Integer> distanceMap = matrixMap.get(haplotype);
			for (final Haplotype otherHaplotype : matrixMap.keySet()) {
				writer.append('\t');
				final String otherName = otherHaplotype.getName();
				final int otherLength = Math.max(4, otherName != null ? otherName.length() : 4);
				if (haplotype == otherHaplotype) {
					writer.append('-');
					indent(otherLength - 1, writer);
				} else {
					final Integer distance = distanceMap.get(otherHaplotype);
					final String distanceString = distance.toString();
					writer.append(distanceString);
					indent(otherLength - distanceString.length(), writer);
				}
			}
			writer.newLine();
			writer.flush();
		}

		log.info("{} entries successfully written.", matrixMap.size());
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
