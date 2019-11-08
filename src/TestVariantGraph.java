/**
 * Code for testing different variant types
 */
public class TestVariantGraph {
	public static void main(String[] args)
	{
		String chrName = "testChr";
		String genome = "ACGTACGTACGTACGTACGT";
		
		System.out.println("Initial genome:\n" + genome + "\n");
		
		Chromosome chr = new CompressedChromosome(chrName, genome);
		
		Variant[] vars = new Variant[] {
				new Insertion(chrName, 1, "GGGG"),
				new Deletion(chrName, 16, 2),
				new Inversion(chrName, 2, 5, "++"),
				new Inversion(chrName, 3, 6, "--"),
				new Duplication(chrName, 10, 15)
		};
		
		for(Variant var : vars)
		{
			System.out.println("Processing " + var);
			chr.processSVs(var.getAdjacenciesAsList());
		}
		
		chr.processAllSVs();
		
		String seq = chr.traverseGraph();
		
		System.out.println("\nFinal genome:\n" + seq);
	}
}
