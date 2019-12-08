package net.emb.hcat.cli.io;

import java.io.IOException;
import java.io.Writer;

/**
 * A writer to write out sequences of DNA in CSV format.
 *
 * @author Heiko Mattes
 */
public class CsvWriter extends BaseSequenceWriter {

	private static final char DEFAULT_DELIMITER = ',';

	private char delimiter;

	private boolean excelHeader;

	/**
	 * Constructor.
	 *
	 * @param writer
	 *            The writer data should be written to.
	 */
	public CsvWriter(final Writer writer) {
		this(writer, DEFAULT_DELIMITER);
	}

	/**
	 * Constructor.
	 *
	 * @param writer
	 *            The writer data should be written to.
	 * @param delimiter
	 *            The delimiter that separates the values.
	 */
	public CsvWriter(final Writer writer, final char delimiter) {
		super(writer);
		this.delimiter = delimiter;
	}

	@Override
	protected void writeHeader() throws IOException {
		if (isExcelHeader()) {
			getWriter().write("sep=");
			getWriter().write(getDelimiter());
			getWriter().newLine();
		}
	}

	@Override
	protected void writeSeqName(final String name) throws IOException {
		if (name != null) {
			getWriter().write(name);
		}
	}

	@Override
	protected void writeSeqValue(final String value) throws IOException {
		for (int i = 0; i < value.length(); i++) {
			getWriter().write(getDelimiter());
			getWriter().write(value.charAt(i));
		}
		getWriter().newLine();
	}

	/**
	 * Gets the delimiter that separates the values.
	 *
	 * @return The delimiter character.
	 */
	public char getDelimiter() {
		return delimiter;
	}

	/**
	 * Sets the delimiter that separates the values.
	 *
	 * @param delimiter
	 *            The delimiter character.
	 */
	public void setDelimiter(final char delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * Gets whether an Excel specific header should be written at the start of
	 * the file that declares the used delimiter.
	 * 
	 * @return <code>true</code>, if the header should be included,
	 *         <code>false</code> otherwise.
	 */
	public boolean isExcelHeader() {
		return excelHeader;
	}

	/**
	 * Sets whether an Excel specific header should be written at the start of
	 * the file that declares the used delimiter.
	 * 
	 * @param excelHeader
	 *            <code>true</code>, if the header should be included,
	 *            <code>false</code> otherwise.
	 */
	public void setExcelHeader(final boolean excelHeader) {
		this.excelHeader = excelHeader;
	}

}
