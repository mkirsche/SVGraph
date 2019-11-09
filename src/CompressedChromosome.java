import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * A compressed chromosome representation which represents it as a graph
 * similarly to the uncompressed version.  However, rather than making every
 * position correspond to a pair of nodes, runs of positions with no novel 
 * adjacencies affecting them are compressed into a single pair of nodes.
 * 
 *  TODO Allow genomic substrings to be queried from a file with samtools
 *  As of now, the memory footprint is still very high since the sequence is stored.
 */
public class CompressedChromosome extends Chromosome {
	
	// These store the mapping from genomic positions to nodes and back
	int[] starts;
	HashMap<Integer, Integer> posToNode;
	
	// Used for querying genome from a file instead of storing it
	GenomeQuery gq;
	String chrFromFile;
	int startFromFile, endFromFile;
	
	// Store the list of novel adjacencies here until we have them all
	ArrayList<NovelAdjacency> toProcess;

	CompressedChromosome(String name, String chromosome) {
		super(name, chromosome);
		toProcess =	new ArrayList<NovelAdjacency>();
		gq = null;
	}
	
	CompressedChromosome(String genomeFn, String chr, int start, int end) throws Exception
	{
		super();
		toProcess =	new ArrayList<NovelAdjacency>();
		
		gq = new GenomeQuery(genomeFn);
		chrFromFile = chr;
		startFromFile = start;
		endFromFile = end;
		n = end - start + 1;
		name = chr;
	}
	
	/*
	 * Do nothing here because we don't have the breakpoints yet
	 * It gets initialized in initCompressedGraph() instead.
	 */
	void initGraph()
	{
	}	
	
	/*
	 * Don't actually process novel adjacencies until we have all of them.
	 * This is because the graph needs to be compressed first.
	 */
	void processSVs(ArrayList<NovelAdjacency> changes)
	{
		for(NovelAdjacency change : changes)
		{
			toProcess.add(change);
		}
	}
	
	/*
	 * Go through 
	 */
	void processAllSVs()
	{
		initCompressedGraph(toProcess);
		
		for(NovelAdjacency change : toProcess)
		{
			int atStrand = change.strand.charAt(0) == '-' ? 0 : 1;
			int toStrand = change.strand.charAt(1) == '-' ? 0 : 1;
			if(change.add)
			{
				//System.out.println("  Adding edge from " + change.pos1 + change.strand.charAt(0) + " to "
				//		+ change.pos2 + change.strand.charAt(1) + " with sequence \"" + change.seq + "\"");
				addEdge(atStrand, posToNode.get((int)change.pos1), toStrand,  
						posToNode.get((int)change.pos2), change.seq, true);
			}
			else
			{
				//System.out.println("  Removing edge from " + change.pos1 + change.strand.charAt(0) + " to "
				//		+ change.pos2 + change.strand.charAt(1) + " with sequence \"" + change.seq + "\"");
				removeEdge(atStrand, posToNode.get((int)change.pos1), 
						new UndirectedEdge(toStrand, posToNode.get((int)change.pos2), change.seq));
			}
		}
	}
	
	/*
	 * Find the breakpoints in a set of novel adjacencies and compress the graph
	 * to be broken only at those points instead of all positions
	 */
	void initCompressedGraph(ArrayList<NovelAdjacency> changes)
	{
		TreeSet<Long> breakpoints = new TreeSet<Long>();
		int[] borders = new int[] {0, 1, n, n+1};
		for(NovelAdjacency na : changes)
		{
			breakpoints.add(na.pos1);
			breakpoints.add(na.pos2);
			breakpoints.add(na.pos1 + 1);
			breakpoints.add(na.pos2+1);
		}
		for(int b : borders) breakpoints.add((long)b);
				
		posToNode = new HashMap<Integer, Integer>();
		starts = new int[breakpoints.size()];
		
		int idx = 0;
		for(long b : breakpoints)
		{
			starts[idx] = (int)b;
			posToNode.put((int)b, idx);
			idx++;
		}
		
		n = idx - 2;
		
		super.initGraph();
	}
	
	/*
	 * Get the sequence of an edge, filling it in from the genome if needed
	 * Overridden from Chromosome class to allow a node to represent a substring
	 */
	String getSeq(int atPos, int atStrand, Edge e) throws Exception
	{
		if(e.seq.length() > 0) return e.seq;
		if(atPos == e.toPos && atPos > 0 && atPos != n+1)
		{
			if(gq != null)
			{
				return gq.genomeSubstring(chrFromFile, startFromFile + starts[atPos]-1, startFromFile + starts[atPos+1] - 2);
			}
			return chromosome.substring(starts[atPos]-1, starts[atPos+1]-1);
		}
		return "";
	}
	
}
