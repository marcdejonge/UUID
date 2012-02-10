package nl.jonghuis.uuid;

/**
 * A UUID representation. While the {@link #generate()} method creates version 1 UUIDs, this object can represent any
 * type of UUID.
 * 
 * @author Marc de Jonge
 * @version 3 February 2012
 */
public final class UUID implements Comparable<UUID>, java.io.Serializable, Cloneable {
	/* This version uid will never change */
	private static final long serialVersionUID = -5280842971319245505L;

	/* Use a private generator to generate new UUIDs */
	private final static UUIDGenerator GENERATOR = UUIDGeneratorFactory.getFactory().createGenerator(Version.VERSION1);

	/* Use a private parser instance to parse UUIDs */
	private final static UUIDParser PARSER = new UUIDParser();

	/**
	 * @return A newly generated Version 1 UUID
	 */
	public static UUID generate() {
		return GENERATOR.next();
	}

	/**
	 * Parses the input String as a UUID. This parser is very forgiving and doesn't really care about other characters
	 * that are placed in between the hexadecimal digits.
	 * 
	 * @param uuid
	 *            The String representation of the UUID
	 * @return A UUID object that represents the same values as the given String input.
	 * @throws java.text.ParseException
	 *             When the input could not be parsed correctly.
	 */
	public static UUID parse(String uuid) throws java.text.ParseException {
		return PARSER.parseUUID(uuid);
	}

	/*
	 * The private values. This object is effectively immutable, but these are not final for the readObject method
	 */
	private long time, node;

	/**
	 * Creates a new UUID from the 16 bytes that are given.
	 * 
	 * @param bytes
	 *            The 16 bytes that represent this UUID.
	 * @throws ArrayIndexOutOfBoundsException
	 *             When there are less than 16 bytes available.
	 */
	public UUID(byte[] bytes) {
		long time = 0;
		for (int ix = 0; ix < 8; ix++) {
			time = time << 8 | bytes[ix] & 0xff;
		}
		this.time = time;

		long node = 0;
		for (int ix = 8; ix < 16; ix++) {
			node = node << 8 | bytes[ix] & 0xff;
		}
		this.node = node;
	}

	/**
	 * Creates a new UUID from two 64-bit values.
	 * 
	 * @param time
	 *            The upper 64 bits of the UUID
	 * @param node
	 *            The lower 64 bits of the UUID
	 */
	public UUID(long time, long node) {
		this.time = time;
		this.node = node;
	}

	/**
	 * Simply creates a new UUID with the exact same value.
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected UUID clone() {
		return new UUID(time, node);
	}

	/**
	 * Compares this UUID to the other given UUID. Return 0 when they are equal, -1 when this UUID is smaller and 1 when
	 * this UUID is larger.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(UUID other) {
		if (this == other) {
			return 0;
		} else if (time > other.time) {
			return 1;
		} else if (time < other.time) {
			return -1;
		} else if (node > other.node) {
			return 1;
		} else if (node < other.node) {
			return -1;
		} else {
			return 0;
		}
	}

	/**
	 * Compares this UUID with the given one.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		} else if (other == null || getClass() != other.getClass()) {
			return false;
		} else {
			return equals((UUID) other);
		}
	}

	/**
	 * Compares this UUID with the one given. This is a faster method for comparing than the generic one, since we don't
	 * have to do any type checking.
	 * 
	 * @param other
	 *            The UUID to compare against.
	 * @return True when the UUIDs are equal, false otherwise. See {@link #equals(Object)}.
	 */
	public boolean equals(UUID other) {
		return node == other.node && time == other.time;
	}

	/**
	 * @return The lower 64 bits of this UUID
	 */
	public long getNode() {
		return node;
	}

	/**
	 * @return The upper 64 bits of this UUID
	 */
	public long getTime() {
		return time;
	}

	/**
	 * @return The version of this UUID. UUIDs created using the {@link #generate()} method will always return 1 here.
	 */
	public int getVersion() {
		return ((int) time & 0xF000) >> 12;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return (int) (17 * node + 59 * (node >>> 32) + 103 * time + 419 * (time >>> 32));
	}

	/* Needed for the Serializable interface */
	private void readObject(java.io.ObjectInputStream in) throws java.io.IOException {
		time = in.readLong();
		node = in.readLong();
	}

	/**
	 * @return A byte array that represents this UUID, which is always 16 bytes long.
	 */
	public byte[] toBytes() {
		byte[] result = new byte[16];
		long x = time;
		for (int ix = 0; ix < 8; ix++) {
			result[7 - ix] = (byte) x;
			x >>>= 8;
		}
		x = node;
		for (int ix = 0; ix < 8; ix++) {
			result[15 - ix] = (byte) x;
			x >>>= 8;
		}
		return result;
	}

	/**
	 * @return Generates a String representation of this UUID in the form
	 *         <code>xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx</code>.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return PARSER.toString(this);
	}

	/* Needed for the Serializable interface */
	private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
		out.writeLong(time);
		out.writeLong(node);
	}
}
