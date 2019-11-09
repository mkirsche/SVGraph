import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * A representation of a FASTA file as a map from name to sequence
 */
public class FastaObject {
	HashMap<String, String> nameToSeq;
	ArrayList<String> names;
	
	FastaObject(String filename) throws Exception
	{
		nameToSeq = new HashMap<String, String>();
		names = new ArrayList<String>();
		Scanner input = new Scanner(new FileInputStream(new File(filename)));
		StringBuilder seq = new StringBuilder("");
		String lastName = "";
		while(input.hasNext())
		{
			String line = input.nextLine();
			if(line.startsWith(">"))
			{
				// Add the last contig to the map
				if(lastName.length() != 0)
				{
					names.add(lastName);
					nameToSeq.put(lastName, seq.toString());
				}
				
				// Start a new contig by resetting the sequence updating the name
				seq = new StringBuilder("");
				lastName = line.substring(1);
			}
			else
			{
				// Continue adding to the sequence
				seq.append(line);
			}
		}
		
		if(lastName.length() != 0)
		{
			names.add(lastName);
			nameToSeq.put(lastName, seq.toString());
		}
		
		input.close();
	}

}
