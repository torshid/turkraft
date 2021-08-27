package turkraft;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Map;

public class Networker implements Runnable
{
	public Turkraft main;
	
	private ServerSocket listener;
	
	private Map<Integer, Remoter> clients;
	
	private int counter;
	
	public Networker(Turkraft main)
	{
		this.main = main;
		
		this.clients = new Hashtable<Integer, Remoter>();
		
		try
		{
			this.listener = new ServerSocket(1333);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void run()
	{
	   while(true)
	   {
		   try
		   {
			   this.counter++;
			   
			   Socket client = this.listener.accept();
			   
			   Remoter him = new Remoter(this, this.counter, client);
			   
			   Thread adding = new Thread(him);
			   
			   him.thread = adding;
			   
			   this.clients.put(this.counter, him);
			   
			   adding.start();
		   }
		   catch (IOException ex)
		   {
			   ex.printStackTrace();
		   }
	   }
	}
	
	@SuppressWarnings("deprecation")
	public void remove(int id)
	{
		this.clients.get(id).thread.stop();

		this.clients.remove(id);
	}
	
	public void send(String content)
	{
		for (Map.Entry<Integer, Remoter> entry : this.clients.entrySet())
		{
			((Remoter)entry.getValue()).send(content);
		}
	}
}
