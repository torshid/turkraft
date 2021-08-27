package turkraft.listeners;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import turkraft.Turkraft;
import turkraft.stockers.Gamer;

public class ChannelMoney implements PluginMessageListener
{
	Turkraft main;
	
	public ChannelMoney(Turkraft main)
	{
		this.main = main;
	}
	
	public void onPluginMessageReceived(String channel, Player player, byte[] message)
	{
		Gamer gamer = this.main.getGamer(player.getName(), true);
		
		if (!gamer.isPrison())
		{
			gamer.sendData("money", "%" + Integer.toString(gamer.information.money));
			
			return;
		}
		

		gamer.sendData("money", "#" + Integer.toString(gamer.information.prisonLeft));
	}
}