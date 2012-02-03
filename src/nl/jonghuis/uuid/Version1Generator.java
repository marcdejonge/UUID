package nl.jonghuis.uuid;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Random;

/**
 * This {@link UUIDGenerator} generates version 1 UUIDs as specified by http://www.ietf.org/rfc/rfc4122.txt
 * 
 * @author Marc de Jonge
 * @version 3 February 2012
 */
class Version1Generator implements UUIDGenerator {
	// Offset got from http://www.famkruithof.net/guid-uuid-timebased.html
	private static final long UTC_TIME_OFFSET = 0x1B21DD213814000L;

	private static final int MILLISECOND = 10000;

	/**
	 * @return The current number of 100 nanosecond intervals since 15 October 1582
	 */
	public static long getCurrentTime() {
		return System.currentTimeMillis() * MILLISECOND + UTC_TIME_OFFSET;
	}

	/**
	 * @return A long number representing this node. This usually is based on the first MAC address found, but when that does not work, it will return a random value. 
	 */
	public static long getNode() {
	 try {
			final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			if (interfaces != null && interfaces.hasMoreElements()) {
				for (NetworkInterface iface = interfaces.nextElement(); interfaces.hasMoreElements(); iface = interfaces.nextElement()) {
					try {
						// Try to find a real interface to use the MAC address from
						if (!iface.isLoopback() && !iface.isVirtual()) {
							final byte[] address = iface.getHardwareAddress();
							if(address != null) {
							long result = 0;
							for (final byte x : address) {
								result = result << 8 | x & 0xFF;
							}
							return  result;
							}
						}
					} catch (final SocketException ex) {
						// Ignore any socket exception and continue
					}
				}
			}
		} catch (final SocketException e) {
			// Ignore any socket exception and try the next method
		}

		// If we can not find anything, just use a random number
		return new Random(System.nanoTime()).nextLong() | 0x800000000000L;
	}
	
	private volatile long lastTime;
	private volatile int clockSeq;

	private final long node;

	/**
	 * Creates a new {@link Version1Generator} based on a random clock sequence and node identifier as determined by the {@link #getNode()} method.
	 */
	public Version1Generator() {
		this(new Random(System.nanoTime()).nextInt(), getNode());
	}

	/**
	 * Creates a new {@link Version1Generator} based on a given clock sequence and node identifier.
	 * @param clockSeq The clock sequence, of which only the last 14 bits will be used.
	 * @param node The node identifier of which only the last 48 bits will be used.
	 */
	public Version1Generator(int clockSeq, long node) {
		lastTime = 0;
		this.clockSeq = clockSeq;
		this.node = 0x8000000000000000L | node & 0xFFFFFFFFFFFFL;
	}
	
	/**
	 * @return a newly generated Version 1 UUID.
	 * @see nl.jonghuis.uuid.UUIDGenerator#next()
	 */
	public UUID next() {
	    long nextTime;
	    int nextSeq;

	    synchronized (this) {
		    nextTime = getCurrentTime();
		    nextSeq = clockSeq;

		    if (nextTime <= lastTime) {
			    if (lastTime - nextTime < MILLISECOND) {
				    lastTime++;
				    nextTime = lastTime;
			    } else {
				    clockSeq++;
				    nextSeq++;
				    lastTime = nextTime;
			    }
		    } else {
			    lastTime = nextTime;
		    }
	    }

	    // Time low
	    long time = nextTime << 32;
	    // Time mid
	    time |= (nextTime & 0xFFFF00000000L) >>> 16;
	    // Time high
	    time |= nextTime >>> 48 & 0x0FFFl;
	    // Version 1
	    time |= 0x1000;

	    final long node = this.node | (nextSeq & 0x3FFFL) << 48;

	    return new UUID(time, node);
    }
}
