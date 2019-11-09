/**
 * This test looks at the region in chr16 from 34,570,000 to 34,600,000
 * It has 48 SVs in ENC003, many of which are nested inversion signatures
 * Treating this area as a genome graph allows it to (hopefully!) be resolved.
 */
import java.util.*;
import java.io.*;
public class TestRealData {
@SuppressWarnings("resource")
public static void main(String[] args) throws Exception
{
	String vcfFn = "chr16_34.vcf";
	if(args.length == 1)
	{
		vcfFn = args[0];
	}

	Scanner vcfInput = new Scanner(new FileInputStream(new File(vcfFn)));
	
	int totalInserted = 0, totalDeleted = 0;
	int countPlus = 0, countMinus = 0;
	int totalPlus = 0, totalMinus = 0;
	
	ArrayList<Variant> toProcess = new ArrayList<Variant>();
	
	int minStart = Integer.MAX_VALUE;
	int maxEnd = 0;
	
	while(vcfInput.hasNext())
	{
		String line = vcfInput.nextLine();
		if(line.length() == 0)
		{
			continue;
		}
		VCFObject curEntry = new VCFObject(line);
		Variant cur = SVTyper.variantFromEntry(curEntry);
		if(cur == null)
		{
			continue;
		}
		if(cur.type.equals("INS"))
		{
			totalInserted += cur.seq.length();
		}
		else if(cur.type.equals("DEL"))
		{
			totalDeleted += cur.end - cur.start - 1;
		}
		else if(cur.type.equals("INV"))
		{
			if(cur.strand.equals("++"))
			{
				countPlus++;
				totalPlus += cur.end - cur.start;
			}
			else
			{
				countMinus++;
				totalMinus += cur.end - cur.start;
			}
		}
		
		minStart = Math.min(minStart,  (int)cur.start - 5);
		maxEnd = Math.max(maxEnd, (int)cur.end + 5);
		toProcess.add(cur);
	}
	
	Chromosome chr = new CompressedChromosome("/home/mkirsche/references/genome.fa", "chr16", minStart, maxEnd);
	System.out.println("Initialized chromosome of length " + chr.n);
	
	for(Variant cur : toProcess)
	{
		System.out.println("Processing " + cur);
		cur.start -= minStart;
		cur.end -= minStart;
		chr.processSVs(cur.getAdjacenciesAsList());
		
	}
		
	
	chr.processAllSVs();
	
	String finalSeq = chr.traverseGraph();
	
	System.out.println("Expected final length is about " + (chr.n + totalInserted - Math.abs(totalDeleted)));
	System.out.println("++ count " + countPlus + " total length " + totalPlus);
	System.out.println("-- count " + countMinus +  " total length " + totalMinus);
	System.out.println("\nFinal genome has length: " + finalSeq.length());
}
}
