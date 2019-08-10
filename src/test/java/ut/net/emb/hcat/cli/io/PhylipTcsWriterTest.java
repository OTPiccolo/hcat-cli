package ut.net.emb.hcat.cli.io;

import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;

import net.emb.hcat.cli.io.PhylipTcsWriter;
import net.emb.hcat.cli.sequence.Sequence;

@SuppressWarnings("javadoc")
public class PhylipTcsWriterTest {

	private static final String LINEBREAK = System.getProperty("line.separator");

	private static final Sequence SEQUENCE_NAME_TOO_LONG = new Sequence("ABCD", "NameIsTooLong");

	@Test
	public void writeNameTooLong() throws Exception {
		final StringWriter writer = new StringWriter();
		final PhylipTcsWriter phylipTcsWriter = new PhylipTcsWriter(writer);
		phylipTcsWriter.write(SEQUENCE_NAME_TOO_LONG);
		phylipTcsWriter.close();
		final String output = writer.toString();
		Assert.assertEquals("1    4\nNameIsToo\nABCD\n".replace("\n", LINEBREAK), output);
	}

}
