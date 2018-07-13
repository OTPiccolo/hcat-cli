package net.emb.hcat.cli;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import net.emb.hcat.cli.io.FastaReader;
import net.emb.hcat.cli.io.HaplotypeWriter;

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

			main.runMasterSplicer();
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

		final List<Sequence> sequences = readInput(in);

		if (sequences.isEmpty()) {
			System.err.println("ERR: No definitions found. Exiting.");
			System.exit(1);
			return null;
		}
		return sequences;
	}

	private void runMasterSplicer() throws IOException {
		final List<Sequence> sequences = readInputHandleError();

		final Sequence master = sequences.get(0);

		final Splicer splicer = new Splicer();
		splicer.getCompare().addAll(sequences);
		final Map<Haplotype, List<Sequence>> result = splicer.compareToMaster(master);

		writeOutput(master, result, out);
	}

	private void writeOutput(final Sequence master, final Map<Haplotype, List<Sequence>> result, final OutputStream out) throws IOException {
		final OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
		writer.append("There are ");
		writer.append(String.valueOf(result.size()));
		writer.append(" different haplotypes.");
		writer.append(LINEFEED);

		writer.flush();

		final HaplotypeWriter haplotypeWriter = new HaplotypeWriter(writer);
		haplotypeWriter.write(master, result);
		writer.flush();
	}

	private List<Sequence> readInput(final InputStream input) throws IOException {
		final FastaReader reader = new FastaReader(new InputStreamReader(input));
		reader.setEnforceSameLength(true);

		try {
			return reader.read();
		} finally {
			reader.close();
		}
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
