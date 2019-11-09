import java.io.File;
import java.io.InputStream;
import java.util.Scanner;

public class GenomeQuery {
	String filename;
	
	static String SAMTOOLS_PATH = "samtools";
	
	GenomeQuery(String filename) throws Exception
	{
		testSamtoolsInstalled();
		boolean validFile = new File(filename).exists();
		if(!validFile) {
			throw new Exception("geonome file does not exist: " + filename);
		}
		this.filename = filename;
	}
	
	/*
	 * Runs a simple samtools command and inspects the exit code to make sure it is installed
	 */
	void testSamtoolsInstalled() throws Exception
	{
		String samtoolsTestCommand = SAMTOOLS_PATH;
		Process child = Runtime.getRuntime().exec(samtoolsTestCommand);
        int seqExit = child.waitFor();
		
		// Make sure an exit code is output
        // Exit code > 1 means the command failed, usually because samtools is not installed or on path
        if(seqExit > 1)
        {
        	throw new Exception("samtools produced bad exit code (" 
        			+ seqExit + ") - perhaps it is not on your path: " + SAMTOOLS_PATH);
        }
	}
	
	String genomeSubstring(String chr, long startPos, long endPos) throws Exception
	{
		if(startPos > endPos)
		{
			return "";
		}
		String faidxCommand = String.format(SAMTOOLS_PATH + " faidx %s %s:%d-%d", filename, chr, startPos, endPos);
		Process child = Runtime.getRuntime().exec(faidxCommand);
        InputStream seqStream = child.getInputStream();
		Scanner seqInput = new Scanner(seqStream);
		
		// Make sure it produced an actual output
		if(!seqInput.hasNext())
        {
        	seqInput.close();
        	throw new Exception("samtools faidx did not produce an output: " + faidxCommand);
        }
		// Read in and ignore sequence name
		seqInput.next();
		
		// Make sure there's also a sequence
		if(!seqInput.hasNext())
		{
			seqInput.close();
        	throw new Exception("samtools faidx produced a sequence name but not an actual sequence: " + faidxCommand);
		}
		StringBuilder res = new StringBuilder("");
		while(seqInput.hasNext())
		{
			res.append(seqInput.next());
		}
		seqInput.close();
		return res.toString();
	}
}
