package turkraft.common;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

public class Constants
{
	private Map<String, String> values;
	
	private Logs log;
	
	public Constants(Logs log, String fileName)
	{
		this.log = log;
		
		this.values = new Hashtable<String, String>();
		
		try
		{
		    BufferedReader reader = new BufferedReader(new FileReader(fileName));
		    
		    String line;
		    
		    while ((line = reader.readLine()) != null)
		    {
		    	String[] parse = line.split("=");
		    	
		    	values.put(parse[0], parse[1]);
    		}
		    
		    reader.close();
		}
		catch (IOException ex)
		{
			this.log.error("Reading variables file from name " + fileName + ", stack trace: " + ex);
		}
	}

	public String get(String variable)
	{
		try
		{
			return this.values.get(variable);
		}
		catch (Exception ex)
		{
			this.log.error("Get constants error for variable name: " + variable + ", stack trace: " + ex);
			
			return "error";
		}
	}
}
