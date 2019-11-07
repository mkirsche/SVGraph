/**
 * Code for testing different variant types
 */
public class TestVariantGraph {
	public static void main(String[] args)
	{
		String chrName = "testChr";
		String genome = "ACGTACGTACGTACGTACGT";
		
		System.out.println("Initial genome:\n" + genome + "\n");
		
		Chromosome chr = new Chromosome(chrName, genome);
		
		Variant[] vars = new Variant[] {
				new Insertion(chrName, 1, "GGGG"),
				//new Deletion(chrName, 3, 6),
				//new Insertion(chrName, genome.length(), "TTTTT"),
				new Inversion(chrName, 2, 5, "++"),
				new Inversion(chrName, 3, 6, "--"),
				new Duplication(chrName, 10, 15)
		};
		
		for(Variant var : vars)
		{
			System.out.println("Processing " + var);
			chr.processSVs(var.getAdjacenciesAsList());
		}
		
		String seq = chr.traverseGraph();
		
		System.out.println("\nFinal genome:\n" + seq);
	}
}
