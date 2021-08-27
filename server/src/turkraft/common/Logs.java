package turkraft.common;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.fusesource.jansi.Ansi;

import turkraft.Turkraft;

public class Logs
{
	public void log(String file, String text)
	{
		this.log(file, text, Turkraft.DEBUG);
	}
	
	public void log(String file, String text, boolean debug)
	{
		if (!debug)
		{
			return;
		}
		
		try
		{
			FileWriter writer = new FileWriter("logs/" + file + ".txt", true);
			
			BufferedWriter output = new BufferedWriter(writer);
			
			output.write("[" + new Date() + "] " + text + "\r\n");
		
			output.flush();
			
			output.close(); 
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}
	public void information(String text)
	{
		this.log("informations", text, true);
	}
	
	public void error(String text)
	{
		System.out.print(Ansi.ansi().fg(Ansi.Color.RED).bold().toString() + text + Ansi.ansi().fg(Ansi.Color.WHITE).boldOff().toString());
		
		this.log("errors", text, true);
	}
	
	public void error(Exception exception)
	{
		exception.printStackTrace();
		
		this.error(exception.toString());
	}
	
	public void error(String text, Exception exception)
	{
		this.error("[" + text + "] " + exception.toString());
		
		exception.printStackTrace();
	}
}
