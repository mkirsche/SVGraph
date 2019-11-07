/**
 * A change to the genome graph in the form of an egde insertion or deletion
 */
public class NovelAdjacency {
	String chr;
	long pos1, pos2;
	String strand;
	String seq;
	boolean add; // True if adding an edge, false if deleting
	public NovelAdjacency(String chr, long pos1, long pos2, String strand, boolean add)
	{
		this.chr = chr;
		this.pos1 = pos1;
		this.pos2 = pos2;
		this.strand = strand;
		this.seq = "";
		this.add = add;
	}
	
	/*
	 * Optional sequence to annotate the edge with
	 */
	public NovelAdjacency(String chr, long pos1, long pos2, String strand, String seq, boolean add)
	{
		this.chr = chr;
		this.pos1 = pos1;
		this.pos2 = pos2;
		this.strand = strand;
		this.seq = seq;
		this.add = add;
	}
}
