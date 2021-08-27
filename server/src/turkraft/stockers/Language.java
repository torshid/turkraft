package turkraft.stockers;

import java.util.Map;

public class Language
{
	public int id;
	
	public String abbrev;
	
	public String detail;
	
	public Map<String, String> texts;
	
	public Language(int id, String abbrev, String detail, Map<String, String> texts)
	{
		this.id = id;
		
		this.abbrev = abbrev;
		
		this.detail = detail;
		
		this.texts = texts;
	}
}
