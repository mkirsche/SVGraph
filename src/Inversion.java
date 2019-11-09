/**
 * Representation of an inversion - this corresponds to a
 * new adjacency from the start of the inversion to the end
 */
public class Inversion extends Variant {
	Inversion(String chr, long pos, long end, String strand)
	{
		this.start = pos;
		this.end = end;
		this.chr = chr;
		this.strand = strand;
		this.type = "INV";
	}
	
	Inversion(VCFObject var)
	{
		super(var);
		this.end = var.pos + Math.abs(var.length);
		this.strand = var.getInfo("STRANDS");
	}

	@Override
	public NovelAdjacency[] getAdjacencies() {
		if(strand.equals("--"))
		{
			return new NovelAdjacency[] {
					new NovelAdjacency(chr, start, end, "--", "", true),
					new NovelAdjacency(chr, start-1, end-1, "++", "", true),
					new NovelAdjacency(chr, start-1, start, "+-", "", false),
					new NovelAdjacency(chr, end-1, end, "+-", "", false)
			};
		}
		else
		{
			return new NovelAdjacency[] {
					new NovelAdjacency(chr, start, end, "++", "", true),
					new NovelAdjacency(chr, start+1, end+1, "--", "", true),
					new NovelAdjacency(chr, end, end+1, "+-", "", false),
					new NovelAdjacency(chr, start, start+1, "+-", "", false)
			};
		}
	}
	
	public String toString()
	{
		return "inversion signature in " + chr + " from position " + start + " to " + end
				+ " with strand " + strand;
	}
}
