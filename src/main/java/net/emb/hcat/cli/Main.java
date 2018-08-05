package net.emb.hcat.cli;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import com.jenkov.cliargs.CliArgs;

import net.emb.hcat.cli.haplotype.Haplotype;
import net.emb.hcat.cli.haplotype.HaplotypeTransformer;
import net.emb.hcat.cli.io.FastaReader;
import net.emb.hcat.cli.io.HaplotypeWriter;

/**
 * Main class.
 *
 * @author OT Piccolo
 */
public class Main {

	private static final String HAPLOTYPE_ARG = "-haplotype";
	private static final String CODON_ARG = "-codon";
	private static final String HELP_1_ARG = "-?";
	private static final String HELP_2_ARG = "-h";
	private static final String HELP_3_ARG = "--help";
	private static final String INPUT_ARG = "-i";
	private static final String INPUT_LONG_ARG = "--input";
	private static final String OUTPUT_ARG = "-o";
	private static final String OUTPUT_LONG_ARG = "--output";
	private static final String ENCODING_ARG = "-e";
	private static final String ENCODING_LONG_ARG = "--encoding";
	private static final String MASTER_SEQUENCE_ARG = "-ms";
	private static final String MASTER_SEQUENCE_LONG_ARG = "--masterseq";
	private static final String MASTER_ID_ARG = "-mi";
	private static final String MASTER_ID_LONG_ARG = "--masterid";

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
		final CliArgs cliArgs = new CliArgs(args);
		if (cliArgs.switchPresent(CODON_ARG)) {
			performCodon(cliArgs);
		} else if (cliArgs.switchPresent(HAPLOTYPE_ARG)) {
			performHaplotype(cliArgs);
		} else if (isHelp(cliArgs)) {
			performHelp();
		} else {
			performUnknown();
		}
	}

	private static final boolean isHelp(final CliArgs args) {
		return args.switchPresent(HELP_1_ARG) || args.switchPresent(HELP_2_ARG) || args.switchPresent(HELP_3_ARG);
	}

	private static final String getArg(final CliArgs args, final String shortOpt, final String longOpt) {
		String value = args.switchValue(shortOpt);
		if (value == null) {
			value = args.switchValue(longOpt);
		}
		return value;
	}

	private static final void performCodon(final CliArgs args) {
		if (isHelp(args)) {
			writeCodonHelp();
			System.exit(0);
		}
	}

	private static final void performHaplotype(final CliArgs args) {
		if (isHelp(args)) {
			writeHaplotypeHelp();
			System.exit(0);
		}

		// Read command line arguments.
		String input = getArg(args, INPUT_ARG, INPUT_LONG_ARG);
		final String output = getArg(args, OUTPUT_ARG, OUTPUT_LONG_ARG);
		final String encoding = getArg(args, ENCODING_ARG, ENCODING_LONG_ARG);
		final String seq = getArg(args, MASTER_SEQUENCE_ARG, MASTER_SEQUENCE_LONG_ARG);
		final String id = getArg(args, MASTER_ID_ARG, MASTER_ID_LONG_ARG);
		if (input == null) {
			final String[] targets = args.targets();
			if (targets.length > 0) {
				input = targets[0];
			} else {
				System.err.println("No input file specified.");
				System.exit(1);
			}
		}

		// Encoding charset.
		final Charset charset = encoding == null ? StandardCharsets.UTF_8 : Charset.forName(encoding);

		// Read input sequences
		final List<Sequence> sequences;
		try (Reader reader = new InputStreamReader(new FileInputStream(input), charset)) {
			final FastaReader fasta = new FastaReader(reader);
			fasta.setEnforceSameLength(true);
			sequences = fasta.read();
		} catch (final FileNotFoundException e) {
			System.err.println("Input file could not be found. Underlying error message: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
			return;
		} catch (final IOException e) {
			System.err.println("Error reading input file. Underlying error message: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
			return;
		}

		if (sequences.isEmpty()) {
			System.err.println("No sequences found in input file.");
			System.exit(1);
		}

		// Create haplotype analysis.
		final Sequence master;
		final Map<Haplotype, List<Sequence>> haplotypes;
		final HaplotypeTransformer transformer = new HaplotypeTransformer(sequences);
		if (id != null) {
			haplotypes = transformer.compareToMaster(id);
			if (haplotypes == null) {
				System.err.println("Master sequence with ID \"" + "\" not found in input file.");
				System.exit(1);
				return;
			}
			master = haplotypes.keySet().iterator().next().getMaster();
		} else {
			if (seq == null) {
				master = sequences.get(0);
			} else {
				master = new Sequence(seq, "Master Sequence");
			}
			haplotypes = transformer.compareToMaster(master);
		}

		// Create output writer.
		final Writer writer;
		if (output == null) {
			writer = new OutputStreamWriter(System.out, charset);
		} else {
			try {
				writer = new OutputStreamWriter(new FileOutputStream(output), charset);
			} catch (final FileNotFoundException e) {
				System.err.println("Could not write to output file. Underlying error message: " + e.getMessage());
				e.printStackTrace();
				System.exit(1);
				return;
			}
		}

		// Write output.
		final HaplotypeWriter haplotypeWriter = new HaplotypeWriter(writer);
		try {
			writer.append("There are ");
			writer.append(String.valueOf(haplotypes.size()));
			writer.append(" different haplotypes.");
			haplotypeWriter.write(master, haplotypes);
		} catch (final IOException e) {
			System.err.println("Error writing output file. Underlying error message: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
			return;
		}
	}

	private static final void performHelp() {
		writeFullHelp();
		System.exit(0);
	}

	private static final void performUnknown() {
		System.out.println("Could not detect what function to perform.");
		System.out.println();
		writeFullHelp();
		System.exit(1);
	}

	private static final void writeFullHelp() {
		System.out.println("The following functions are available:");
		System.out.println();

		System.out.println("\t* Performa a haplotype analysis.");
		System.out.println("\t\t" + HAPLOTYPE_ARG + " <args>");
		System.out.println("\t\tFor help use: " + HAPLOTYPE_ARG + " " + HELP_1_ARG);
		System.out.println();

		System.out.println("\t* Performa a codon transformation.");
		System.out.println("\t\t" + CODON_ARG + " <args>");
		System.out.println("\t\tFor help use: " + CODON_ARG + " " + HELP_1_ARG);
		System.out.println();
	}

	private static final void writeCodonHelp() {
		System.err.println("Codon transformation not yet implemented.");
	}

	private static final void writeHaplotypeHelp() {
		System.out.println("Performs an analysis of haploytpes. Reads in sequences in Fasta format and writes them out as haploytpes.");
		System.out.println();
		System.out.println("Usage: -haplotype [options] input_file");
		System.out.println("Options:");
		writeOptionLine(INPUT_ARG, INPUT_LONG_ARG, "\tPath to input file. If present, 'input_file' will be ignored.");
		writeOptionLine(OUTPUT_ARG, OUTPUT_LONG_ARG, "Path to output file. If not present, will be written to console.");
		writeOptionLine(ENCODING_ARG, ENCODING_LONG_ARG, "Encoding of input file and output. If not given, UTF-8 will be used.");
		writeOptionLine(MASTER_SEQUENCE_ARG, MASTER_SEQUENCE_LONG_ARG, "Master sequence to compare to. If neither master sequence nor ID is given, will use first sequence of input.");
		writeOptionLine(MASTER_ID_ARG, MASTER_ID_LONG_ARG, "ID of master sequence to compare to. If neither master sequence nor ID is given, will use first sequence of input.");
	}

	private static final void writeOptionLine(final String shortOpt, final String longOpt, final String text) {
		System.out.println("\t" + shortOpt + ",\t" + longOpt + "\t" + text);
	}

}
