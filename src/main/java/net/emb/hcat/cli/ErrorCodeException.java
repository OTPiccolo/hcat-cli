package net.emb.hcat.cli;

import org.slf4j.helpers.MessageFormatter;

/**
 * A typed exception that has additional information on what did go wrong, so it
 * can be better handled and not only display an untranslated error message to a
 * clueless user.
 *
 * @author OT Piccolo
 */
public class ErrorCodeException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 8857542586545858414L;

	/**
	 * Error codes describing the different errors.
	 *
	 * @author OT Piccolo
	 */
	public enum EErrorCode {
		/** An unexpected error. First value: Error message. */
		UNEXPECTED,
		/** An error while reading something. First value: Error message. */
		GENERIC_READ,
		/** An error while writing something. First value: Error message. */
		GENERIC_WRITE,
		/**
		 * Indicates that the length of the sequence was wrong. First value:
		 * Sequence. Second value: Index of sequence. Third value: Expected
		 * length.
		 */
		SEQUENCE_WRONG_LENGTH,
		/**
		 * Indicates that the length of the name of the sequence was wrong.
		 * First value: Sequence. Second value: Index of sequence. Third value:
		 * Maximum length of name.
		 */
		SEQUENCE_WRONG_NAME,
		/**
		 * Indicates that the wrong amount of sequences was encountered. First
		 * value: Expected number. Second value: Actual number.
		 */
		SEQUENCES_WRONG_AMOUNT,
		/**
		 * Indicates that the read stream did not have all expected values. No
		 * additional values.
		 */
		MISSING_VALUE,
		/**
		 * Indicates that the read header was invalid. First value: Wrong header
		 * line.
		 */
		INVALID_HEADER;
	}

	private final EErrorCode errorCode;
	private final Object[] values;

	/**
	 * Constructor.
	 *
	 * @param errorCode
	 *            The error code.
	 * @param message
	 *            The message to display. Will automatically be formatted with
	 *            the given values.
	 * @param values
	 *            Additional values to be reported with the exception.
	 * @see MessageFormatter
	 */
	public ErrorCodeException(final EErrorCode errorCode, final String message, final Object... values) {
		this(errorCode, null, message, values);
	}

	/**
	 * Constructor.
	 *
	 * @param errorCode
	 *            The error code.
	 * @param exception
	 *            The underlying exception.
	 * @param message
	 *            The message to display. Will automatically be formatted with
	 *            the given values.
	 * @param values
	 *            Additional values to be reported with the exception.
	 * @see MessageFormatter
	 */
	public ErrorCodeException(final EErrorCode errorCode, final Exception exception, final String message, final Object... values) {
		super(values == null || values.length == 0 ? message : MessageFormatter.basicArrayFormat(message, values), exception);
		this.errorCode = errorCode;
		this.values = values == null ? new Object[0] : values;
	}

	/**
	 * Gets the error code.
	 *
	 * @return The error code.
	 */
	public EErrorCode getErrorCode() {
		return errorCode;
	}

	/**
	 * Gets an array of values that contain additional information.
	 *
	 * @return The array of values. Never null, but might have zero length.
	 */
	public Object[] getValues() {
		return values;
	}

}
