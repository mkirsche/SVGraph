import java.util.ArrayList;

/**
 * Provides base for variants represented in terms of their
 * changes to basepair adjacencies - also allows checking variant equality
 */
public abstract class Variant
{
	String type;
	String seq;
	long start, end;
	String chr, chr2;
	String strand;
	static boolean CHECK_STRAND = true;
	static boolean CHECK_TYPE = true;
	static long MAX_DIST = 1000;
	
	Variant(){}
	
	Variant(VCFObject var)
	{
		this.start = var.pos;
		this.chr = var.chr;
		this.type = var.type;
		this.seq = "";
	}
	
	public String getType()
	{
		return type;
	}
	
	public String getSeq()
	{
		return seq;
	}
	
	public long getStart()
	{
		return start;
	}
	
	public long getEnd()
	{
		return end;
	}
	
	public String getStrand()
	{
		return strand;
	}
	
	public boolean equals(Variant o)
	{
		boolean same = true;
		if(CHECK_STRAND)
		{
			same &= getStrand().equals(o.getStrand());
		}
		if(CHECK_TYPE)
		{
			same &= getType().equals(o.getType());
		}
		long startDist = Math.abs(getStart() - o.getStart());
		long endDist = Math.abs(getEnd() - o.getEnd());
		if(startDist +  endDist > MAX_DIST)
		{
			same = false;
		}
		return same;
	}
	
	public abstract NovelAdjacency[] getAdjacencies();
	
	public ArrayList<NovelAdjacency> getAdjacenciesAsList() {
		ArrayList<NovelAdjacency> res = new ArrayList<NovelAdjacency>();
		NovelAdjacency[] array = getAdjacencies();
		for(NovelAdjacency na : array)
		{
			res.add(na);
		}
		return res;
	}
}
