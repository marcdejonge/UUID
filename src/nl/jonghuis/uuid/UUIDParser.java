package nl.jonghuis.uuid;

import java.text.ParseException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The UUIDParser is a helper class to parse UUIDs
 * 
 * @author Marc de Jonge
 * @version 3 February 2012
 */
public class UUIDParser {

	/**
	 * Creates a new UUIDParser
	 */
	public UUIDParser() {
	}

	private long parseFromString(String uuid, AtomicInteger startIx) throws ParseException {
		long value = 0;
		for (int ix = 0; ix < 16;) {
			if (startIx.get() >= uuid.length()) {
				throw new ParseException("Not enough values found for parsing", startIx.get());
			}
			final char c = uuid.charAt(startIx.getAndIncrement());
			if (c >= '0' && c <= '9') {
				value = value << 4 | c - '0';
				ix++;
			} else if (c >= 'a' && c <= 'f') {
				value = value << 4 | c - 'a' + 10;
				ix++;
			} else if (c >= 'A' && c <= 'F') {
				value = value << 4 | c - 'A' + 10;
				ix++;
			}
		}
		return value;
	}

	/**
	 * Parses the given hexadecimal string. It is guaranteed that for any {@link UUID} the following holds:
	 * <code>parseUUID(uuid.toString()).equals(uuid)</code>
	 * 
	 * @param uuid
	 *            A String representation of a UUID.
	 * @return A {@link UUID} that was represented by the uuid String.
	 * @throws ParseException
	 *             When there were not enough hexadecimal characters to create a valid UUID.
	 */
	public UUID parseUUID(String uuid) throws ParseException {
		final AtomicInteger ix = new AtomicInteger();
		final long time = parseFromString(uuid, ix);
		final long node = parseFromString(uuid, ix);

		return new UUID(time, node);
	}
}
