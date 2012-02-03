package nl.jonghuis.uuid;

import java.util.Random;

/**
 * This {@link UUIDGenerator} generates version 4 UUIDs (random) as specified by http://www.ietf.org/rfc/rfc4122.txt
 * 
 * @author Marc de Jonge
 * @version 3 February 2012
 */
public class Version4Generator implements UUIDGenerator {
	private final Random random;

	/**
	 * Creates a new {@link Version4Generator} using a new {@link Random}.
	 */
	public Version4Generator() {
		this(new Random());
	}
	
	/**
	 * Creates a new {@link Version4Generator} using the given {@link Random} implementation
	 * @param random The {@link Random} object that will be used
	 */
	public Version4Generator(Random random) {
		this.random =random;
	}

	public UUID next() {
		long time = random.nextLong() & 0xFFFFFFFFFFFF0FFFL | 0x4000L;
		long node = random.nextLong() & 0x3FFFFFFFFFFFFFFFL | 0x8000000000000000L;
		return new UUID(time, node);
	}
}
