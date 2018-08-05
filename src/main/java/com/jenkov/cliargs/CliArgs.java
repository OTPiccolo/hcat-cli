package com.jenkov.cliargs;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.TreeSet;

@SuppressWarnings("javadoc")
public class CliArgs {

	private String[] args = null;

	private final HashMap<String, Integer> switchIndexes = new HashMap<String, Integer>();
	private final TreeSet<Integer> takenIndexes = new TreeSet<Integer>();

	public CliArgs(final String[] args) {
		parse(args);
	}

	public void parse(final String[] arguments) {
		this.args = arguments;
		// locate switches.
		switchIndexes.clear();
		takenIndexes.clear();
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("-")) {
				switchIndexes.put(args[i], i);
				takenIndexes.add(i);
			}
		}
	}

	public String[] args() {
		return args;
	}

	public String arg(final int index) {
		return args[index];
	}

	public boolean switchPresent(final String switchName) {
		return switchIndexes.containsKey(switchName);
	}

	public String switchValue(final String switchName) {
		return switchValue(switchName, null);
	}

	public String switchValue(final String switchName, final String defaultValue) {
		if (!switchIndexes.containsKey(switchName)) {
			return defaultValue;
		}

		final int switchIndex = switchIndexes.get(switchName);
		if (switchIndex + 1 < args.length) {
			takenIndexes.add(switchIndex + 1);
			return args[switchIndex + 1];
		}
		return defaultValue;
	}

	public Long switchLongValue(final String switchName) {
		return switchLongValue(switchName, null);
	}

	public Long switchLongValue(final String switchName, final Long defaultValue) {
		final String switchValue = switchValue(switchName, null);

		if (switchValue == null) {
			return defaultValue;
		}
		return Long.parseLong(switchValue);
	}

	public Double switchDoubleValue(final String switchName) {
		return switchDoubleValue(switchName, null);
	}

	public Double switchDoubleValue(final String switchName, final Double defaultValue) {
		final String switchValue = switchValue(switchName, null);

		if (switchValue == null) {
			return defaultValue;
		}
		return Double.parseDouble(switchValue);
	}

	public String[] switchValues(final String switchName) {
		if (!switchIndexes.containsKey(switchName)) {
			return new String[0];
		}

		final int switchIndex = switchIndexes.get(switchName);

		int nextArgIndex = switchIndex + 1;
		while (nextArgIndex < args.length && !args[nextArgIndex].startsWith("-")) {
			takenIndexes.add(nextArgIndex);
			nextArgIndex++;
		}

		final String[] values = new String[nextArgIndex - switchIndex - 1];
		for (int j = 0; j < values.length; j++) {
			values[j] = args[switchIndex + j + 1];
		}
		return values;
	}

	public <T> T switchPojo(final Class<T> pojoClass) {
		try {
			final T pojo = pojoClass.newInstance();

			final Field[] fields = pojoClass.getFields();
			for (final Field field : fields) {
				final Class<?> fieldType = field.getType();
				final String fieldName = "-" + field.getName().replace('_', '-');

				if (fieldType.equals(Boolean.class) || fieldType.equals(boolean.class)) {
					field.set(pojo, switchPresent(fieldName));
				} else if (fieldType.equals(String.class)) {
					if (switchValue(fieldName) != null) {
						field.set(pojo, switchValue(fieldName));
					}
				} else if (fieldType.equals(Long.class) || fieldType.equals(long.class)) {
					if (switchLongValue(fieldName) != null) {
						field.set(pojo, switchLongValue(fieldName));
					}
				} else if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
					if (switchLongValue(fieldName) != null) {
						field.set(pojo, switchLongValue(fieldName).intValue());
					}
				} else if (fieldType.equals(Short.class) || fieldType.equals(short.class)) {
					if (switchLongValue(fieldName) != null) {
						field.set(pojo, switchLongValue(fieldName).shortValue());
					}
				} else if (fieldType.equals(Byte.class) || fieldType.equals(byte.class)) {
					if (switchLongValue(fieldName) != null) {
						field.set(pojo, switchLongValue(fieldName).byteValue());
					}
				} else if (fieldType.equals(Double.class) || fieldType.equals(double.class)) {
					if (switchDoubleValue(fieldName) != null) {
						field.set(pojo, switchDoubleValue(fieldName));
					}
				} else if (fieldType.equals(Float.class) || fieldType.equals(float.class)) {
					if (switchDoubleValue(fieldName) != null) {
						field.set(pojo, switchDoubleValue(fieldName).floatValue());
					}
				} else if (fieldType.equals(String[].class)) {
					final String[] values = switchValues(fieldName);
					if (values.length != 0) {
						field.set(pojo, values);
					}
				}
			}

			return pojo;
		} catch (final Exception e) {
			throw new RuntimeException("Error creating switch POJO", e);
		}
	}

	public String[] targets() {
		final String[] targetArray = new String[args.length - takenIndexes.size()];
		int targetIndex = 0;
		for (int i = 0; i < args.length; i++) {
			if (!takenIndexes.contains(i)) {
				targetArray[targetIndex++] = args[i];
			}
		}

		return targetArray;
	}

}