package net.emb.hcat.cli;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.jenkov.cliargs.CliArgs;

import net.emb.hcat.cli.codon.CodonTransformationData;
import net.emb.hcat.cli.codon.CodonTransformer;
import net.emb.hcat.cli.haplotype.Haplotype;
import net.emb.hcat.cli.haplotype.HaplotypeTransformer;
import net.emb.hcat.cli.io.CodonTableReader;
import net.emb.hcat.cli.io.HaplotypeTableWriter;
import net.emb.hcat.cli.io.sequence.BaseSequenceReader;
import net.emb.hcat.cli.io.sequence.ESequenceType;
import net.emb.hcat.cli.io.sequence.ISequenceReader;
import net.emb.hcat.cli.io.sequence.ISequenceWriter;
import net.emb.hcat.cli.sequence.Difference;
import net.emb.hcat.cli.sequence.Sequence;

/**
 * Main class.
 *
 * @author Heiko Mattes
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
	private static final String FILE_FORMAT_ARG = "-f";
	private static final String FILE_FORMAT_LONG_ARG = "--fileformat";
	private static final String ENCODING_ARG = "-e";
	private static final String ENCODING_LONG_ARG = "--encoding";
	private static final String MASTER_SEQUENCE_ARG = "-ms";
	private static final String MASTER_SEQUENCE_LONG_ARG = "--masterseq";
	private static final String MASTER_ID_ARG = "-mi";
	private static final String MASTER_ID_LONG_ARG = "--masterid";
	private static final String CODON_DATA_NUMBER_ARG = "-cn";
	private static final String CODON_DATA_NUMBER_LONG_ARG = "--codonnumber";

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

	private static final ESequenceType getFileFormat(final String fileTypeArg) {
		switch (fileTypeArg.toLowerCase()) {
		case "fasta":
			return ESequenceType.FASTA;
		case "phylip":
			return ESequenceType.PHYLIP;
		case "tcs":
			return ESequenceType.PHYLIP_TCS;
		case "csv":
			return ESequenceType.CSV;
		default:
			System.err.println("File Type argument is unknown: " + fileTypeArg);
			return null;
		}
	}

	private static List<Sequence> readSequences(final String input, final ESequenceType seqType, final Charset charset) {
		final List<Sequence> sequences;
		try (ISequenceReader reader = seqType.createReader(new InputStreamReader(new FileInputStream(input), charset))) {
			if (reader instanceof BaseSequenceReader) {
				((BaseSequenceReader) reader).setEnforceSameLength(true);
			}
			sequences = reader.read();
		} catch (final FileNotFoundException e) {
			System.err.println("Input file could not be found. Underlying error message: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
			return null;
		} catch (final ErrorCodeException | IOException e) {
			System.err.println("Error reading input file. Underlying error message: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
			return null;
		}

		if (sequences.isEmpty()) {
			System.err.println("No sequences found in input file.");
			System.exit(1);
		}

		return sequences;
	}

	private static void writeOutput(final String output, final Charset charset, final Consumer<Writer> data) {
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

		try {
			data.accept(writer);
		} finally {
			try {
				writer.close();
			} catch (final IOException e) {
				System.err.println("Could not close writer. Underlying error mesage: " + e.getMessage());
			}
		}
	}

	private static final void performCodon(final CliArgs args) {
		if (isHelp(args)) {
			writeCodonHelp();
			System.exit(0);
		}

		// Read command line arguments.
		String codonNumber = getArg(args, CODON_DATA_NUMBER_ARG, CODON_DATA_NUMBER_LONG_ARG);
		String input = getArg(args, INPUT_ARG, INPUT_LONG_ARG);
		String output = getArg(args, OUTPUT_ARG, OUTPUT_LONG_ARG);
		final String fileFormat = getArg(args, FILE_FORMAT_ARG, FILE_FORMAT_LONG_ARG);
		final String encoding = getArg(args, ENCODING_ARG, ENCODING_LONG_ARG);
		if (codonNumber == null) {
			final String[] targets = args.targets();
			if (targets.length > 0) {
				codonNumber = targets[0];
			} else {
				System.err.println("No codon number specified.");
				System.exit(1);
			}
		}
		if (input == null) {
			final String[] targets = args.targets();
			if (targets.length > 1) {
				input = targets[1];
			} else {
				System.err.println("No input file specified.");
				System.exit(1);
			}
		}
		if (output == null) {
			final String[] targets = args.targets();
			if (targets.length > 2) {
				output = targets[2];
			}
		}

		final ESequenceType seqType = fileFormat != null ? getFileFormat(fileFormat) : ESequenceType.byFileEnding(input);
		if (seqType == null) {
			System.err.println("Could not determine file format by file name and no valid file format was specified.");
			System.exit(1);
		}

		if (codonNumber == null) {
			System.err.println("No codon number given.");
			System.exit(1);
		}
		int parsedCodonNumber = -1;
		try {
			parsedCodonNumber = Integer.parseInt(codonNumber);
		} catch (final NumberFormatException e) {
			System.err.println("Codon number parameter is not a number. Given parameter: " + codonNumber);
			System.exit(1);
		}

		// Get correct codon data.
		CodonTransformationData data = null;
		final List<CodonTransformationData> codonTable = CodonTableReader.readDefaultTable();
		for (final CodonTransformationData codonData : codonTable) {
			if (codonData.number == parsedCodonNumber) {
				data = codonData;
				break;
			}
		}
		if (data == null) {
			System.err.println("No codon transformation data with number " + codonNumber + " known.");
			System.exit(1);
		}

		// Encoding charset.
		final Charset charset = encoding == null ? StandardCharsets.UTF_8 : Charset.forName(encoding);

		// Read input sequences
		final List<Sequence> sequences = readSequences(input, seqType, charset);

		// Transform sequences.
		final List<Sequence> transformedSeqs = new ArrayList<Sequence>(sequences.size());
		for (final Sequence seq : sequences) {
			final CodonTransformer transformer = new CodonTransformer(data, seq);
			transformedSeqs.add(transformer.transformAuto());
		}

		// Write transformed output.
		writeOutput(output, charset, writer -> {
			final ISequenceWriter seqWriter = seqType.createWriter(writer);
			try {
				seqWriter.write(transformedSeqs);
			} catch (final IOException e) {
				System.err.println("Error writing output file. Underlying error message: " + e.getMessage());
				e.printStackTrace();
				System.exit(1);
				return;
			}
		});
	}

	private static final void performHaplotype(final CliArgs args) {
		if (isHelp(args)) {
			writeHaplotypeHelp();
			System.exit(0);
		}

		// Read command line arguments.
		String input = getArg(args, INPUT_ARG, INPUT_LONG_ARG);
		String output = getArg(args, OUTPUT_ARG, OUTPUT_LONG_ARG);
		final String fileFormat = getArg(args, FILE_FORMAT_ARG, FILE_FORMAT_LONG_ARG);
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
		if (output == null) {
			final String[] targets = args.targets();
			if (targets.length > 1) {
				output = targets[1];
			}
		}

		final ESequenceType seqType = fileFormat != null ? getFileFormat(fileFormat) : ESequenceType.byFileEnding(input);
		if (seqType == null) {
			System.err.println("Could not determine file format by file name and no valid file format was specified.");
			System.exit(1);
		}

		// Encoding charset.
		final Charset charset = encoding == null ? StandardCharsets.UTF_8 : Charset.forName(encoding);

		// Read input sequences
		final List<Sequence> sequences = readSequences(input, seqType, charset);

		// Create haplotypes.
		final List<Haplotype> haplotypes = Haplotype.wrap(sequences);

		// Get master sequence to compare to.
		Sequence master = null;
		if (id != null) {
			for (final Haplotype haplotype : haplotypes) {
				for (final Sequence sequence : haplotype) {
					if (id.equals(sequence.getName())) {
						master = sequence;
						break;
					}
				}
				if (master != null) {
					break;
				}
			}
			if (master == null) {
				System.err.println("Master sequence with ID \"" + id + "\" not found in input file.");
				System.exit(1);
				return;
			}
		} else {
			if (seq == null) {
				master = sequences.get(0);
			} else {
				master = new Sequence(seq, "Master Sequence");
			}
		}

		// Create haplotype analysis.
		final Sequence usedMaster = master;
		final Map<Haplotype, Difference> haplotypeMap;
		final HaplotypeTransformer transformer = new HaplotypeTransformer(haplotypes);
		haplotypeMap = transformer.compareToMaster(usedMaster);

		// Write output.
		writeOutput(output, charset, writer -> {
			final HaplotypeTableWriter haplotypeWriter = new HaplotypeTableWriter(writer);
			try {
				haplotypeWriter.write(usedMaster, haplotypeMap);
			} catch (final IOException e) {
				System.err.println("Error writing output file. Underlying error message: " + e.getMessage());
				e.printStackTrace();
				System.exit(1);
				return;
			}
		});
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

		System.out.println("\t* Perform a haplotype analysis.");
		System.out.println("\t\t" + HAPLOTYPE_ARG + " <args>");
		System.out.println("\t\tFor help use: " + HAPLOTYPE_ARG + " " + HELP_1_ARG);
		System.out.println();

		System.out.println("\t* Perform a codon transformation.");
		System.out.println("\t\t" + CODON_ARG + " <args>");
		System.out.println("\t\tFor help use: " + CODON_ARG + " " + HELP_1_ARG);
		System.out.println();
	}

	private static final void writeCodonHelp() {
		System.out.println("Performs a codon translation. Reads in sequences and translates them via a codon table.");
		System.out.println();
		System.out.println("Usage: -codon [options] codon_number input_file [output_file]");
		System.out.println("Example: -codon 1 input_seq.fas");
		System.out.println("Example: -codon -cn 26 -i input_seq.fas -o codon.txt");
		System.out.println("Options:");

		writeOptionLine(CODON_DATA_NUMBER_ARG, CODON_DATA_NUMBER_LONG_ARG, "Uses the codon translation with the defined number. If present, 'codon_number' will be ignored.");
		writeOptionLine(INPUT_ARG, INPUT_LONG_ARG, "\tPath to input file. If present, 'input_file' will be ignored.");
		writeOptionLine(OUTPUT_ARG, OUTPUT_LONG_ARG, "Path to output file. If present, 'output_file' will be ignored. If not present and 'output_file' is not present either, will be written to console.");
		writeOptionLine(FILE_FORMAT_ARG, FILE_FORMAT_LONG_ARG, "File format of input file. Possible values are: \"fasta\" for Fasta-, \"phylip\" for Phylip-, \"tcs\" for Phylip TCS- and \"csv\" for CSV/Excel-format. If not given, file type will be estimated according to the file ending.");
		writeOptionLine(ENCODING_ARG, ENCODING_LONG_ARG, "Encoding of input file and output. If not given, UTF-8 will be used.");
	}

	private static final void writeHaplotypeHelp() {
		System.out.println("Performs an analysis of haploytpes. Reads in sequences and writes them out as haploytpes.");
		System.out.println();
		System.out.println("Usage: -haplotype [options] input_file [output_file]");
		System.out.println("Example: -haplotype input_seq.fas");
		System.out.println("Example: -haplotype -ms ACGTGTCAC -i input_seq.fas -o haplotype.txt");
		System.out.println("Options:");

		writeOptionLine(INPUT_ARG, INPUT_LONG_ARG, "\tPath to input file. If present, 'input_file' will be ignored.");
		writeOptionLine(OUTPUT_ARG, OUTPUT_LONG_ARG, "Path to output file. If present, 'output_file' will be ignored. If not present and 'output_file' is not present either, will be written to console.");
		writeOptionLine(FILE_FORMAT_ARG, FILE_FORMAT_LONG_ARG, "File format of input file. Possible values are: \"fasta\" for Fasta-, \"phylip\" for Phylip-, \"tcs\" for Phylip TCS- and \"csv\" for CSV/Excel-format. If not given, file type will be estimated according to the file ending.");
		writeOptionLine(ENCODING_ARG, ENCODING_LONG_ARG, "Encoding of input file and output. If not given, UTF-8 will be used.");
		writeOptionLine(MASTER_SEQUENCE_ARG, MASTER_SEQUENCE_LONG_ARG, "Master sequence to compare to. If neither master sequence nor ID is given, will use first sequence of input.");
		writeOptionLine(MASTER_ID_ARG, MASTER_ID_LONG_ARG, "ID of master sequence to compare to. If neither master sequence nor ID is given, will use first sequence of input.");
	}

	private static final void writeOptionLine(final String shortOpt, final String longOpt, final String text) {
		System.out.println("\t" + shortOpt + ",\t" + longOpt + "\t" + text);
	}

}
