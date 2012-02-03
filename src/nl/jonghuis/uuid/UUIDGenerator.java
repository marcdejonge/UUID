package nl.jonghuis.uuid;

/**
 * A {@link UUIDGenerator} is a simple interface for different types of generators.
 * 
 * @author Marc de Jonge
 * @version 3 February 2012
 */
public interface UUIDGenerator {
	/**
	 * @return The next generated UUID
	 */
	UUID next();
}
