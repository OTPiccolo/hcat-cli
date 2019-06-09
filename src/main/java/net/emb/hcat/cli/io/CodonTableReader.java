package net.emb.hcat.cli.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.emb.hcat.cli.codon.CodonTransformationData;

/**
 * A reader to read Codon data from a table, according to:
 * https://www.ncbi.nlm.nih.gov/Taxonomy/Utils/wprintgc.cgi
 *
 * @author Heiko Mattes
 */
public class CodonTableReader {

	// Reading data from:
	// https://www.ncbi.nlm.nih.gov/Taxonomy/Utils/wprintgc.cgi

	private static final class CodonData {
		public String name;
		public int number;
		public String aa;
		public String start;
		public String base1;
		public String base2;
		public String base3;
	}

	private static Pattern NAME_PATTERN = Pattern.compile("\\s*(\\d+)\\.?\\s*(.+)");
	private static Pattern AA_PATTERN = createPattern("AAs", "A-Z\\*");
	private static Pattern STARTS_PATTERN = createPattern("Starts", "M\\-\\*");
	private static Pattern BASE_PATTERN = createPattern("Base1|Base2|Base3", "ACGT");

	private static final Pattern createPattern(final String name, final String value) {
		return Pattern.compile("\\s*(" + name + ")\\s*=\\s*([" + value + "]{64})\\s*");
	}

	/**
	 * Reads the default table that comes prepackaged with this program.
	 *
	 * @return A list containing all found {@link CodonTransformationData}.
	 */
	public static final List<CodonTransformationData> readDefaultTable() {
		try (Reader reader = new InputStreamReader(CodonTableReader.class.getResourceAsStream("/codonTable.txt"), StandardCharsets.UTF_8)) {
			final CodonTableReader codonReader = new CodonTableReader(reader);
			return codonReader.read();
		} catch (final IOException e) {
			// Should never happen.
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	private final BufferedReader reader;

	/**
	 * Constructor.
	 *
	 * @param reader
	 *            The reader data should be read from.
	 */
	public CodonTableReader(final Reader reader) {
		if (reader == null) {
			throw new IllegalArgumentException("Reader must not be null.");
		}
		this.reader = new BufferedReader(reader);
	}

	/**
	 * Reads a table from a reader and transforms it into
	 * {@link CodonTransformationData}.
	 *
	 * @return A list containing all found {@link CodonTransformationData}.
	 * @throws IOException
	 *             An I/O exception.
	 */
	public List<CodonTransformationData> read() throws IOException {
		final ArrayList<CodonTransformationData> list = new ArrayList<CodonTransformationData>();

		CodonData data = new CodonData();
		boolean matched = false;
		String line;
		while ((line = reader.readLine()) != null) {
			matched = false;
			Matcher matcher = NAME_PATTERN.matcher(line);
			if (matcher.matches()) {
				data.number = Integer.parseInt(matcher.group(1));
				data.name = matcher.group(2);
				matched = true;
			}

			if (!matched) {
				matcher = AA_PATTERN.matcher(line);
				if (matcher.matches()) {
					data.aa = matcher.group(2);
					matched = true;
				}
			}

			if (!matched) {
				matcher = STARTS_PATTERN.matcher(line);
				if (matcher.matches()) {
					data.start = matcher.group(2);
					matched = true;
				}
			}

			if (!matched) {
				matcher = BASE_PATTERN.matcher(line);
				if (matcher.matches()) {
					final String base = matcher.group(1);
					if ("Base1".equals(base)) {
						data.base1 = matcher.group(2);
					} else if ("Base2".equals(base)) {
						data.base2 = matcher.group(2);
					} else {
						data.base3 = matcher.group(2);
					}
					matched = true;
				}
			}

			if (isAllData(data)) {
				list.add(createData(data));
				data = new CodonData();
			}
		}

		return list;
	}

	private boolean isAllData(final CodonData data) {
		if (data.name == null) {
			return false;
		}
		if (data.aa == null) {
			return false;
		}
		if (data.start == null) {
			return false;
		}
		if (data.base1 == null) {
			return false;
		}
		if (data.base2 == null) {
			return false;
		}
		if (data.base3 == null) {
			return false;
		}
		return true;
	}

	private CodonTransformationData createData(final CodonData data) {
		final CodonTransformationData transData = new CodonTransformationData();

		transData.name = data.name;
		transData.number = data.number;

		for (int i = 0; i < 64; i++) {
			final String base = new String(new char[] { data.base1.charAt(i), data.base2.charAt(i), data.base3.charAt(i) });
			transData.codon.put(base, data.aa.charAt(i));
			final char startStop = data.start.charAt(i);
			if (startStop == 'M') {
				transData.start.put(base, 'M');
			} else if (startStop == '*') {
				transData.end.put(base, '*');
			}
		}

		return transData;
	}

	/**
	 * Convenience method to close the underlying reader.
	 *
	 * @see Reader#close()
	 */
	public void close() {
		try {
			reader.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
