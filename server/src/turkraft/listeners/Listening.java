package turkraft.listeners;

import org.bukkit.event.Listener;

import turkraft.Turkraft;

public class Listening implements Listener
{
	public Turkraft main;
	
	public Listening(Turkraft main)
	{
		this.main = main;
		
		this.main.getServer().getPluginManager().registerEvents(this, this.main);
	}
}
