/**
 * Representation of a deletion - this corresponds to an
 * adjacency being updated to "jump over" some reference nodes 
 */
public class Deletion extends Variant {
	
	Deletion(String chr, long pos, long length)
	{
		this.chr = chr;
		this.start = pos;
		this.end = pos + length + 1;
		this.strand = "+-";
		this.seq = "";
		this.type = "DEL";
	}
	
	Deletion(VCFObject var)
	{
		super(var);
		this.end = start + var.length + 1;
		this.strand = "+-";
	}

	@Override
	public NovelAdjacency[] getAdjacencies() {
		return new NovelAdjacency[] {
				new NovelAdjacency(chr, start, start + 1, strand, false),
				new NovelAdjacency(chr, end - 1, end, strand, false),
				new NovelAdjacency(chr, start, end, strand, true)
		};
	}
	
	public String toString()
	{
		return "deletion in " + chr + " at position " + start + " of length " + (end - start - 1);
	}

}
