package ut.net.emb.hcat.cli.sequence;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import net.emb.hcat.cli.sequence.ATGC;

@SuppressWarnings("javadoc")
public class AtgcTest {

	@Test
	public void testIsAtgc() throws Exception {
		final Set<Character> allowedChars = new HashSet<Character>();
		allowedChars.add('A');
		allowedChars.add('a');
		allowedChars.add('C');
		allowedChars.add('c');
		allowedChars.add('G');
		allowedChars.add('g');
		allowedChars.add('T');
		allowedChars.add('t');

		for (char c = 0; c < 256; c++) {
			Assert.assertEquals("Wrong character: " + c, allowedChars.contains(c), ATGC.isAtgc(c));
		}
	}

	@Test
	public void testToAtgc() throws Exception {
		final Map<Character, ATGC> atgcMap = new HashMap<Character, ATGC>();
		atgcMap.put('A', ATGC.ADENINE);
		atgcMap.put('a', ATGC.ADENINE);
		atgcMap.put('C', ATGC.CYTOSINE);
		atgcMap.put('c', ATGC.CYTOSINE);
		atgcMap.put('G', ATGC.GUANINE);
		atgcMap.put('g', ATGC.GUANINE);
		atgcMap.put('T', ATGC.THYMINE);
		atgcMap.put('t', ATGC.THYMINE);

		for (char c = 0; c < 256; c++) {
			Assert.assertEquals("Wrong character: " + c, atgcMap.get(c), ATGC.toAtgc(c));
		}
	}

}
