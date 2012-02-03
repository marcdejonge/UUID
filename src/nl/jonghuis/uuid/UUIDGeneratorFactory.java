package nl.jonghuis.uuid;

import java.util.Random;

/**
 * This {@link UUIDGeneratorFactory} can be used for creating new {@link UUIDGenerator} instances
 * 
 * @author Marc de Jonge
 * @version 3 February 2012
 */
public class UUIDGeneratorFactory {
	private final static UUIDGeneratorFactory factory = new UUIDGeneratorFactory();

	/**
	 * @return The only instance of the {@link UUIDGeneratorFactory}
	 */
	public final static UUIDGeneratorFactory getFactory() {
		return factory;
	}

	/* Private to make sure there is only one instance */
	private UUIDGeneratorFactory() {
	}

	/**
	 * @param clockSeq
	 *            The clock sequence, of which only the last 14 bits will be used
	 * @param node
	 *            The node identifier of which only the last 48 bits will be used
	 * @return a new version 1 {@link UUIDGenerator} based on the clock sequence and node numbers
	 */
	public UUIDGenerator createGenerator(int clockSeq, long node) {
		return new Version1Generator(clockSeq, node);
	}

	/**
	 * @param random
	 *            The {@link Random} implementation that will be used for generating UUIDs
	 * @return a new version 4 {@link UUIDGenerator} based on the given {@link Random} implementation
	 */
	public UUIDGenerator createGenerator(Random random) {
		return new Version4Generator(random);
	}

	/**
	 * @param version
	 *            The {@link Version} which determines the type of UUIDs that will be generated
	 * @return a new {@link UUIDGenerator} that will behave according to the version given
	 */
	public UUIDGenerator createGenerator(Version version) {
		switch (version) {
		case VERSION1:
			return new Version1Generator();
		case VERSION4:
			return new Version4Generator();
		default:
			throw new java.lang.UnsupportedOperationException("Version " + version + " is not implemented");
		}
	}
}
