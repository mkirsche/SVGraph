/**
 * Representation of a chromosome as a graph
 * Each nucleotide consists of two nodes - one for the "left" side, or - strand,
 * and one for the "right" side, or + strand.  These two nodes are connected with
 * an edge containing the base at that genomic position, and then the + strand of
 * each basepair has an edge to the - strand of the next base.  Finally, both
 * ends of the chromosome are represented by their own pair of nodes.
 * 
 * This chromosome representation also enables integration of SV signatures, and
 * traversal of the resulting graph (currently heuristic-based when traversal is
 * not obvious) and report the modified sequence
 */
import java.util.ArrayList;
import java.util.TreeMap;

/*
 * TODO compress runs of reference adjacencies to reduce memory overhead
 */
public class Chromosome {
	String name;
	int n; // The length of the chromosome in basepairs
	TreeMap<Edge, Integer>[][] graph;
	String chromosome;
	
	Chromosome(String name, String chromosome)
	{
		this.name = name;
		this.n = chromosome.length();
		this.chromosome = chromosome;
		
		initGraph();
	}
	
	/*
	 * Construct the initial graph
	 */
	@SuppressWarnings("unchecked")
	void initGraph()
	{
		// Allocate data structure
				graph = new TreeMap[2][n+2];
				for(int i = 0; i<graph.length; i++)
				{
					for(int j = 0; j<graph[i].length; j++)
					{
						graph[i][j] = new TreeMap<Edge, Integer>();
					}
				}
				for(int i = 0; i <= n + 1; i++)
				{
					// Edge from - to +
					if(i >= 1 && i <= n)
					{
						addEdge(0, i, 1, i, "", true);
					}
					else
					{
						addEdge(0, i, 1, i, "", true);
					}
					
					// Edge from + to next -
					if(i != n + 1)
					{
						addEdge(1, i, 0, i+1, "", true);
					}
				}
	}
	
	/*
	 * Process a list of novel adjacencies which were generated from SV signatures
	 */
	void processSVs(ArrayList<NovelAdjacency> changes)
	{
		for(NovelAdjacency change : changes)
		{
			int atStrand = change.strand.charAt(0) == '-' ? 0 : 1;
			int toStrand = change.strand.charAt(1) == '-' ? 0 : 1;
			if(change.add)
			{
				System.out.println("  Adding edge from " + change.pos1 + change.strand.charAt(0) + " to "
						+ change.pos2 + change.strand.charAt(1) + " with sequence \"" + change.seq + "\"");
				addEdge(atStrand, (int)change.pos1, toStrand, (int)change.pos2, change.seq, true);
			}
			else
			{
				System.out.println("  Removing edge from " + change.pos1 + change.strand.charAt(0) + " to "
						+ change.pos2 + change.strand.charAt(1) + " with sequence \"" + change.seq + "\"");
				removeEdge(atStrand, (int)change.pos1, new UndirectedEdge(toStrand, (int)change.pos2, change.seq));
			}
		}
	}
	
	// Useful for overriding classes which need to process all SVs at once (e.g., to compress the graph)
	void processAllSVs() {}
	
	/*
	 * Add an edge to the graph
	 */
	void addEdge(int fromStrand, int fromPos, int toStrand, int toPos, String seq, boolean undirected)
	{
		TreeMap<Edge, Integer> fromMap = graph[fromStrand][fromPos];
		Edge forwardEdge = undirected ? new UndirectedEdge(toStrand, toPos, seq) : new Edge(toStrand, toPos, seq);
		int oldCount = fromMap.containsKey(forwardEdge) ? fromMap.get(forwardEdge) : 0;
		fromMap.put(forwardEdge,  oldCount + 1);
		if(undirected)
		{
			TreeMap<Edge, Integer> toMap = graph[toStrand][toPos];
			Edge backEdge = new UndirectedEdge(fromStrand, fromPos, seq); // TODO rev comp?
			oldCount = toMap.containsKey(backEdge) ? toMap.get(backEdge) : 0;
			toMap.put(backEdge, oldCount + 1);
		}
	}
	
	/*
	 * Remove an edge from the graph
	 */
	void removeEdge(int fromStrand, int fromPos, Edge e)
	{
		TreeMap<Edge, Integer> fromMap = graph[fromStrand][fromPos];
		int oldCount = fromMap.containsKey(e) ? fromMap.get(e) : 0;
		if(oldCount == 0)
		{
			System.out.println("    Edge already removed so doing nothing");
			return;
		}
		if(oldCount == 1)
		{
			fromMap.remove(e);
		}
		else
		{
			fromMap.put(e, oldCount - 1);
		}
		
		// If it is undirected, also remove the corresponding back-edge
		if(e.undirected)
		{
			TreeMap<Edge, Integer> toMap = graph[e.toStrand][e.toPos];
			Edge rev = new UndirectedEdge(fromStrand, fromPos, e.seq); // TODO rev comp?
			oldCount = toMap.containsKey(rev) ? toMap.get(rev) : 0;
			if(oldCount == 0)
			{
				return;
			}
			if(oldCount == 1)
			{
				toMap.remove(rev);
			}
			else
			{
				toMap.put(rev, oldCount - 1);
			}
		}
	}
	
	/*
	 * This just goes through the edges in order, which won't always work, but will be fine
	 * in simple cases without weird overlapping variants
	 * TODO update this with fancy algorithm
	 */
	String traverseGraph()
	{
		int atStrand = 0;
		int atPos = 0;
		
		StringBuilder pathSequence = new StringBuilder("");
		
		while(atStrand != 1 || atPos != n + 1)
		{
			TreeMap<Edge, Integer> options = graph[atStrand][atPos];
			if(options.size() == 0)
			{
				if(atStrand == 0)
				{
					atStrand = 1;
					if(atPos != 0)
					{
						pathSequence.append(getSeq(atPos, 0, new Edge(1, atPos)));
					}
					continue;
				}
				else
				{
					atPos++;
					atStrand = 0;
					continue;
				}
				//break;
			}
			
			// Get the first edge
			Edge first = options.firstKey();
			
			removeEdge(atStrand, atPos, first);
			pathSequence.append(getSeq(atPos, atStrand, first));
			atStrand = first.toStrand;
			atPos = first.toPos;
		}
		
		return pathSequence.toString();
	}
	
	/*
	 * Get the sequence of an edge, filling it in from the genome if needed
	 */
	String getSeq(int atPos, int atStrand, Edge e)
	{
		if(e.seq.length() > 0) return e.seq;
		if(atPos == e.toPos && atPos > 0 && atPos != n+1)
		{
			return chromosome.charAt(atPos - 1) + "";
		}
		return "";
	}
	
	/*
	 * Represents a directed edge:
	 *   toStrand and toPos give information about the destination of the edge
	 *   seq allows the edge to be annotated with a genomic sequence
	 */
	public static class Edge implements Comparable<Edge>
	{
		public int toStrand;
		public int toPos;
		public String seq;
		public boolean undirected = false;
		
		protected Edge(int toStrand, int toPos)
		{
			this.toStrand = toStrand;
			this.toPos = toPos;
			this.seq = "";
		}
		protected Edge(int toStrand, int toPos, String seq)
		{
			this.toStrand = toStrand;
			this.toPos = toPos;
			this.seq = seq;
		}
		@Override
		public int compareTo(Edge o) {
			if(toPos != o.toPos)
			{
				return Long.compare(toPos, o.toPos);
			}
			else if(toStrand != o.toStrand)
			{
				return toStrand - o.toStrand;
			}
			else if(undirected != o.undirected)
			{
				return undirected ? -1 : 1;
			}
			else
			{
				return seq.compareTo(o.seq);
			}
		}
	}
	
	public static class UndirectedEdge extends Edge
	{
		protected UndirectedEdge(int toStrand, int toPos)
		{
			super(toStrand, toPos);
			undirected = true;
		}
		protected UndirectedEdge(int toStrand, int toPos, String seq)
		{
			super(toStrand, toPos, seq);
			undirected = true;
		}
	}
}
