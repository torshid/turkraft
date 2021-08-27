package turkraft;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Remoter implements Runnable
{
	private Networker networker;
	
	private int id;
	
	private Socket client;
	
	private BufferedReader in;
	
    private PrintWriter out;

	public Thread thread;
	
	private boolean stopped;
	
	public Remoter(Networker networker, int id, Socket client)
	{
		this.networker = networker;
		
		this.id = id;
		
		this.client = client;
		
		try
		{
			this.in = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			
			this.stop();
		}
		
		try
		{
			this.out = new PrintWriter(this.client.getOutputStream());
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			
			this.stop();
		}
		
		System.out.println("Remoter with ID " + this.id + " connected");
	}
	
	public void run()
	{
		while(true)
		{
			try
			{
				String line = this.in.readLine();

				this.networker.main.getServer().dispatchCommand(this.networker.main.getServer().getConsoleSender(), line);
			}
			catch (Exception ex)
			{
				this.stop();
			}
		}
	}
	
	public void send(String content)
	{
		this.out.print(content);
		
		this.out.flush();
	}
	
	public void stop()
	{
		if (!this.stopped)
		{
			System.out.println("Remoter with ID " + this.id + " disconnected");
		
			this.networker.remove(this.id);
			
			this.stopped = true;
		}
		
		try
		{
			this.client.close();
			
			this.in = null;
			
			this.out = null;
		}
		catch (Exception ex)
		{
			
		}
	}
}