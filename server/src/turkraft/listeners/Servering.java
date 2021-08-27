package turkraft.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.server.ServerListPingEvent;

import turkraft.Turkraft;

public class Servering extends Listening
{
	public Servering(Turkraft main)
	{
		super(main);
	}

	@EventHandler (priority = EventPriority.NORMAL)
	public void onServerListPing(ServerListPingEvent Event)
	{
		Event.setMotd("Please use the Turkraft launcher!");
		
		//Event.setMaxPlayers(this.main.configuration.maximumPlayers);
	}
}