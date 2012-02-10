package nl.jonghuis.uuid;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import org.junit.Test;

public class UUIDTest {

	@Test
	public void toBytesTest() {
		for (int i = 0; i < 10000; i++) {
			UUID u1 = UUID.generate();
			UUID u2 = new UUID(u1.toBytes());
			assertEquals(u1, u2);
		}
	}

	@Test
	public void toStringTest() throws ParseException {
		for (int i = 0; i < 10000; i++) {
			UUID u1 = UUID.generate();
			UUID u2 = UUID.parse(u1.toString());
			assertEquals(u1, u2);
		}
	}

	@Test
	public void versionTest() {
		for (int i = 0; i < 10000; i++) {
			assertEquals(UUID.generate().getVersion(), 1);
		}
	}
}
