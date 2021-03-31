package ut.net.emb.hcat.cli.io;

import java.io.StringReader;
import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;

import net.emb.hcat.cli.io.sequence.ESequenceType;
import net.emb.hcat.cli.io.sequence.FastaReader;
import net.emb.hcat.cli.io.sequence.FastaWriter;
import net.emb.hcat.cli.io.sequence.ISequenceReader;
import net.emb.hcat.cli.io.sequence.ISequenceWriter;
import net.emb.hcat.cli.io.sequence.PhylipReader;
import net.emb.hcat.cli.io.sequence.PhylipTcsReader;
import net.emb.hcat.cli.io.sequence.PhylipTcsWriter;
import net.emb.hcat.cli.io.sequence.PhylipWriter;

@SuppressWarnings("javadoc")
public class SequenceTypeTest {

	@Test
	public void fastaReader() throws Exception {
		final ISequenceReader reader = ESequenceType.FASTA.createReader(new StringReader(""));
		Assert.assertSame(reader.getClass(), FastaReader.class);
	}

	@Test
	public void fastaWriter() throws Exception {
		final ISequenceWriter writer = ESequenceType.FASTA.createWriter(new StringWriter());
		Assert.assertSame(writer.getClass(), FastaWriter.class);
	}

	@Test
	public void phylipReader() throws Exception {
		final ISequenceReader reader = ESequenceType.PHYLIP.createReader(new StringReader(""));
		Assert.assertSame(reader.getClass(), PhylipReader.class);
	}

	@Test
	public void phylipWriter() throws Exception {
		final ISequenceWriter writer = ESequenceType.PHYLIP.createWriter(new StringWriter());
		Assert.assertSame(writer.getClass(), PhylipWriter.class);
	}

	@Test
	public void phylipTcsReader() throws Exception {
		final ISequenceReader reader = ESequenceType.PHYLIP_TCS.createReader(new StringReader(""));
		Assert.assertSame(reader.getClass(), PhylipTcsReader.class);
	}

	@Test
	public void phylipTcsWriter() throws Exception {
		final ISequenceWriter writer = ESequenceType.PHYLIP_TCS.createWriter(new StringWriter());
		Assert.assertSame(writer.getClass(), PhylipTcsWriter.class);
	}

}
