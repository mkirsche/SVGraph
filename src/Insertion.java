/**
 * Representation of an insertion - this corresponds to the annotation
 * of an existing edge with a novel sequence
 */
public class Insertion extends Variant {
	
	Insertion(String chr, long pos, String seq)
	{
		this.start = pos;
		this.end = pos + 1;
		this.chr = chr;
		this.strand = "+-";
		this.seq = seq;
		this.type = "INS";
	}
	
	Insertion(VCFObject var)
	{
		super(var);
		this.end = var.pos + 1;
		this.strand = "+-";
		this.seq = ""; // TODO get actual sequence
	}

	@Override
	public NovelAdjacency[] getAdjacencies() {
		return new NovelAdjacency[] {
				new NovelAdjacency(chr, start, end, "+-", "", false),
				new NovelAdjacency(chr, start, end, "+-", seq, true),
		};
	}
	
	public String toString()
	{
		return "insertion in " + chr + " at position " + start + " of sequence " + seq;
	}
	
}
