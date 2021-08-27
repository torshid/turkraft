package turkraft.listeners;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import turkraft.Turkraft;
import turkraft.stockers.Gamer;

public class ChannelModify implements PluginMessageListener
{
	Turkraft main;
	
	public ChannelModify(Turkraft main)
	{
		this.main = main;
	}
	
	public void onPluginMessageReceived(String channel, Player player, byte[] message)
	{
		Gamer gamer = this.main.getGamer(player.getName(), true);
		
		String content = new String(message);
		
		if (content.equalsIgnoreCase("1") || content.equalsIgnoreCase("2"))
		{
			this.main.broadcastTextAbbrev("ban.cheat.xray", "<" + gamer.source.getDisplayName() + ">");
			
			gamer.ban();
			
			return;
		}
	}
}
