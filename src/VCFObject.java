/**
 * A representation of a VCF line which allows easy access to its fields
 */
import java.util.HashMap;

public class VCFObject {
	public String chr;
	public long pos;
	public long length;
	public String id;
	public String ref;
	public String alt;
	public String type;
	public HashMap<String, String> infoFields;
	String[] genotype;
	
	public VCFObject(String line)
	{
		String[] tokens = line.split("\t");
		chr = tokens[0];
		pos = Long.parseLong(tokens[1]);
		id = tokens[2];
		ref = tokens[3];
		alt = tokens[4];
		
		// Fill info fields
		infoFields = new HashMap<String, String>();
		String[] keyValuePairs = tokens[7].split(";");
		for(String keyValue : keyValuePairs)
		{
			int equalsIdx = keyValue.indexOf('=');
			if(equalsIdx == -1)
			{
				continue;
			}
			String key = keyValue.substring(0, equalsIdx);
			String value = keyValue.substring(equalsIdx + 1);
			infoFields.put(key, value);
		}
		
		if(tokens.length > 8)
		{
			genotype = tokens[8].split(":");
		}
		else
		{
			genotype = new String[0];
		}
		
		if(alt.startsWith("<"))
		{
			type = alt.substring(1, alt.length()-1);
			length = Long.parseLong(getInfo("SVLEN"));
		}
		else
		{
			type = getInfo("SVTYPE");
			length = alt.length() - ref.length();
		}
	}
	
	public String getInfo(String key)
	{
		return infoFields.containsKey(key) ? infoFields.get(key) : "";
	}
	
}
