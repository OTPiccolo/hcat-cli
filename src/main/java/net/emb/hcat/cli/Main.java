package net.emb.hcat.cli;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

/**
 * Main class.
 *
 * @author OT Piccolo
 */
public class Main {

	private static final String LINEFEED = System.getProperty("line.separator");

	/**
	 * Entry method for the jar file.
	 *
	 * @param args
	 *            Arguments containing information on where to read the input
	 *            data and write the output data.<br>
	 *            <br>
	 *            The first argument is the file where to read the input
	 *            data.<br>
	 *            <br>
	 *            The second argument is the file where to write the output. If
	 *            omitted, it will be written to standard out.
	 */
	public static void main(final String[] args) {
		if (args.length == 0) {
			System.out.println("No arguments are given. Enter at least the file where the test data are, and optionally another file where the output should be written to.");
			System.exit(1);
		}

		FileInputStream inStream = null;
		FileOutputStream outStream = null;
		final Main main = new Main();

		try {
			inStream = new FileInputStream(args[0]);
			main.setIn(inStream);

			if (args.length > 1) {
				outStream = new FileOutputStream(args[1]);
				main.setOut(outStream);
			} else {
				main.setOut(System.out);
			}

			main.runFindMasterSplicer();
		} catch (final IOException e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
			if (outStream != null) {
				try {
					outStream.close();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.exit(0);
	}

	// Indents the given Appendable by the given amount of spaces.
	private static void indent(final int times, final Appendable appendable) throws IOException {
		for (int i = 0; i < times; i++) {
			appendable.append(' ');
		}
	}

	private InputStream in;
	private OutputStream out;

	private Main() {

	}

	private List<Sequence> readInputHandleError() throws IOException {
		if (in == null) {
			throw new IllegalArgumentException("No input stream given. Please set via setIn(InputStream).");
		}
		if (out == null) {
			throw new IllegalArgumentException("No output stream given. Please set via setOut(OutputStream).");
		}

		final List<Sequence> haplotypes = readInput(in);

		if (haplotypes.isEmpty()) {
			System.err.println("ERR: No definitions found. Exiting.");
			System.exit(1);
			return null;
		}
		return haplotypes;
	}

	private void runMasterSplicer() throws IOException {
		final List<Sequence> haplotypes = readInputHandleError();

		final Sequence master = haplotypes.get(0);

		final Splicer splicer = new Splicer();
		splicer.getCompare().addAll(haplotypes);
		final Map<Haplotype, List<Sequence>> result = splicer.compareToMaster(master);

		writeOutput(master, result, out);
	}

	private void runFindMasterSplicer() throws IOException {
		final List<Sequence> haplotypes = readInputHandleError();

		final Splicer splicer = new Splicer();
		splicer.getCompare().addAll(haplotypes);

		final Sequence master = splicer.findMostMatchHaplotype().get(0);
		writeOutput(master, splicer.compareToMaster(master), out);
	}

	private void writeOutput(final Sequence master, final Map<Haplotype, List<Sequence>> result, final OutputStream out) throws IOException {
		final OutputStreamWriter writer = new OutputStreamWriter(out);
		writer.append("There are ");
		writer.append(String.valueOf(result.size()));
		writer.append(" different haplotypes.");
		writer.append(LINEFEED);

		writer.flush();

		if (false) {
			writeFullHaplotypes(master, result, writer);
		} else {
			writeShortHaplotypes(master, result, writer);
		}

		writer.flush();
	}

	private void writeShortHaplotypes(final Sequence master, final Map<Haplotype, List<Sequence>> result, final OutputStreamWriter writer) throws IOException {
		// Calculate all positions.
		final Set<Integer> positions = new TreeSet<>();
		for (final Haplotype difference : result.keySet()) {
			positions.addAll(difference.getDifferencePosition());
		}

		writer.append("Master sequence: ");
		writer.append(master.getName());
		writer.append(LINEFEED);
		writer.flush();

		final int positionLength = "Positions".length();
		final int masterLength = "Master".length();

		// Write for each difference the name of all sequences.
		int maxLength = Math.max(positionLength, masterLength);
		final Map<Haplotype, StringBuilder> names = new LinkedHashMap<>();
		for (final Entry<Haplotype, List<Sequence>> entry : result.entrySet()) {
			final StringBuilder builder = new StringBuilder();
			names.put(entry.getKey(), builder);
			for (final Sequence haplotype : entry.getValue()) {
				builder.append(haplotype.getName());
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
		writer.append(LINEFEED);
		writer.flush();

		// Write master sequence.
		writer.append("Master");
		indent(maxLength - masterLength, writer);
		writer.append(':');
		for (final Integer pos : positions) {
			writer.append('\t');
			writer.append(master.getValue().charAt(pos.intValue()));
		}
		writer.append(LINEFEED);
		writer.flush();

		// Write out the differences.
		for (final Entry<Haplotype, StringBuilder> entry : names.entrySet()) {
			final String difference = entry.getKey().getDifference();
			writer.append(entry.getValue().toString());
			for (final Integer pos : positions) {
				writer.append('\t');
				writer.append(difference.charAt(pos.intValue()));
			}
			writer.append(LINEFEED);
			writer.flush();
		}
	}

	private void writeFullHaplotypes(final Sequence master, final Map<Haplotype, List<Sequence>> result, final OutputStreamWriter writer) throws IOException {
		final int masterSequenceLength = "Master sequence: ".length() + master.getName().length();
		// Write for each difference the name of all sequences.
		int maxLength = masterSequenceLength;
		final Map<Haplotype, StringBuilder> names = new LinkedHashMap<>();
		for (final Entry<Haplotype, List<Sequence>> entry : result.entrySet()) {
			final StringBuilder builder = new StringBuilder();
			names.put(entry.getKey(), builder);
			for (final Sequence haplotype : entry.getValue()) {
				builder.append(haplotype.getName());
				builder.append("; ");
			}
			builder.delete(builder.length() - 2, builder.length());
			maxLength = Math.max(maxLength, builder.length());
		}

		// Pretty print the names so all are the same length.
		for (final StringBuilder builder : names.values()) {
			indent(maxLength - builder.length(), builder);
			builder.append(": ");
		}

		writer.append("Master sequence: ");
		writer.append(master.getName());

		indent(maxLength - masterSequenceLength, writer);
		writer.append(": ");
		writer.append(master.getValue());
		writer.append(LINEFEED);

		writer.flush();

		// Write out the differences.
		for (final Entry<Haplotype, StringBuilder> entry : names.entrySet()) {
			writer.append(entry.getValue().toString());
			writer.append(entry.getKey().getDifference());
			writer.append(LINEFEED);
			writer.flush();
		}
	}

	private List<Sequence> readInput(final InputStream input) throws IOException {
		InputStreamReader streamReader;
		BufferedReader bufferedReader;

		final List<Sequence> haplotypes = new ArrayList<>();

		streamReader = new InputStreamReader(input);
		bufferedReader = new BufferedReader(streamReader);

		int i = 0;
		String name = null;
		String value = null;
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			i++;
			if (line.isEmpty()) {
				continue;
			}

			if (line.startsWith(">")) {
				if (name != null) {
					System.out.println("WARN: Two name definitions are following each other in line " + i + ": " + name + ", " + line.substring(1));
				}
				name = line.substring(1);
			} else {
				if (value != null) {
					System.out.println("WARN: Two haplotypes are following each other in line " + i + ": " + value + ", " + line);
				}
				value = line;
			}

			if (name != null && value != null) {
				final Sequence sequence = new Sequence(value);
				sequence.setName(name);
				haplotypes.add(sequence);
				name = null;
				value = null;
			}
		}

		return haplotypes;
	}

	/**
	 * Gets the input stream to read data from.
	 *
	 * @return The input stream.
	 */
	public InputStream getIn() {
		return in;
	}

	/**
	 * Sets the input stream to read data from.
	 *
	 * @param in
	 *            The input stream.
	 */
	public void setIn(final InputStream in) {
		this.in = in;
	}

	/**
	 * Gets the output stream to read data from.
	 *
	 * @return The output stream.
	 */
	public OutputStream getOut() {
		return out;
	}

	/**
	 * Sets the output stream to read data from.
	 *
	 * @param out
	 *            The output stream.
	 */
	public void setOut(final OutputStream out) {
		this.out = out;
	}

}
