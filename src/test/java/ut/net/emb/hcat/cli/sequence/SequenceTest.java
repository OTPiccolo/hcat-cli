package ut.net.emb.hcat.cli.sequence;

import org.junit.Assert;
import org.junit.Test;

import net.emb.hcat.cli.sequence.Sequence;

@SuppressWarnings("javadoc")
public class SequenceTest {

	@Test
	public void testIsAtgc_Valid() throws Exception {
		final Sequence seq = new Sequence("ATGCatgc");
		Assert.assertTrue(Sequence.isAtgc(seq));
	}

	@Test
	public void testIsAtgc_Invalid() throws Exception {
		final Sequence seq = new Sequence("ATGCbatgc");
		Assert.assertFalse(Sequence.isAtgc(seq));
	}

	@Test
	public void testIsAtgc_SpecialChar() throws Exception {
		final Sequence seq = new Sequence("ATGC!atgc");
		Assert.assertFalse(Sequence.isAtgc(seq));
	}

	@Test
	public void testIsAtgc_NullSafe() throws Exception {
		Assert.assertFalse(Sequence.isAtgc(null));
	}

}
