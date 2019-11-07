/**
 * Representation of a duplication - this corresponds to the addition
 * of an edge from the - strand of the start to the + strand of the end
 */
public class Duplication extends Variant {
	
	Duplication(String chr, long pos, long end)
	{
		this.start = pos;
		this.end = end;
		this.chr = chr;
		this.strand = "-+";
		this.seq = "";
		this.type = "DUP";
	}
	
	Duplication(VCFObject var)
	{
		super(var);
		this.end = var.pos + var.length - 1;
		this.strand = "-+";
	}

	@Override
	public NovelAdjacency[] getAdjacencies() {
		return new NovelAdjacency[] {
				new NovelAdjacency(chr, start, end, "-+", "", true), // TODO duplicate intermediate edges?
		};
	}
	
	public String toString()
	{
		return "duplication in " + chr + " at position " + start + " of sequence " + seq;
	}
	
}
