/**
 * Utility class for creating a variant from a VCF entry
 * Looks at the type field to determine which variant type to make
 */
public class SVTyper {
	static Variant variantFromEntry(VCFObject ve)
	{
		String type = ve.type;
		if(type.equals("INS"))
		{
			return new Insertion(ve);
		}
		else if(type.equals("DEL"))
		{
			return new Deletion(ve);
		}
		else if(type.equals("DUP"))
		{
			return new Duplication(ve);
		}
		else if(type.equals("INV"))
		{
			return new Inversion(ve);
		}
		else
		{
			return null;
		}
	}
}
