package nl.jonghuis.uuid;

import java.util.concurrent.atomic.AtomicLong;

public final class Version6Generator implements UUIDGenerator {
	
	private final static Version6Generator instance = new Version6Generator();
	
	public static Version6Generator getInstance() {
		return instance;
	}

	public static long getNode() {
		long node = Version1Generator.getNode();
		long pid = getProcessNode();
		return pid << 48 | node;
	}

	/**
	 * @return A 14-bit id that describes the current process.
	 */
	public static long getProcessNode() {
		int processId = (int) (Math.random() * 0xFFFF);
		try {
			String processname = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
			processId = Integer.parseInt(processname.substring(0, processname.indexOf('@')));
		} catch(Exception ex) {
			// Just use the random one
		}
		int loaderId = System.identityHashCode(Version6Generator.class.getClassLoader());
		return processId * loaderId & 0x3FFF;
	}

	/**
	 * @return The number of milliseconds since epoch as a 48-bits number
	 */
	public static long getTime() {
		return (System.currentTimeMillis() & 0xFFFFFFFFFFFFL) << 16 | 0x6000;
	}

	private final long node;
	private final AtomicLong time;
	
	private Version6Generator() {
		node = getNode() | 0x8000000000000000L;
		time = new AtomicLong(getTime());
	}

	public UUID next() {
		while(true) {
			long nextTime = getTime();
			long lastTime = time.get();
			if((time.get() & 0xFFFFFFFFFFFFF000L) <= nextTime) {
				nextTime = lastTime + 1;
			}
			if(time.compareAndSet(lastTime, nextTime)) {
				return new UUID(nextTime, node);
			}
		}
	}
}
