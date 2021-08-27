package turkraft.stockers;

import java.util.ArrayList;

import org.bukkit.ChatColor;

import turkraft.Turkraft;
import turkraft.common.Manipulation;

public class Group
{
	public int id;
	
	public Gamer host;
	
	public ArrayList<Gamer> members;
	
	/* 	ids:
	 * 	-1: tu crees un groupe (client: set host you) | ON ENVOIE LA COULEUR
	 *  -2: tu ajoutes le joueur a ta liste (client: add gamer) | ON ENVOIE LA COULEUR
	 *  -3: tu rejoints (client: s'ajoute lui meme a sa liste et set le host) | ON ENVOIE LA COULEUR
	 *  -4: reset (client: supprimer le groupe [n'est plus dans un groupe])
	 *  -5: joueur qui sort (client: retire le joueur de sa liste)
	 *  -6: le host sort (client: supprime le host, assigne le nouveau et l'enleve de sa liste) | ON ENVOIE LA COULEUR
	 */
	
	public Group(int id, Gamer host)
	{
		this.id = id;
		
		this.host = host;
		
		this.members = new ArrayList<Gamer>();

		host.sendData("group", "1:" + host.information.name + ":" + host.getNameColor());
	}
	
	public void broadcast(String message)
	{
		this.host.sendMessage(message);
		
		for (int i = 0; i < this.members.size(); i++)
		{
			this.members.get(i).sendMessage(message);
		}
	}
	
	public void broadcast(Turkraft main, String abbreviation, String ... args)
	{
		this.broadcast(abbreviation, main.colors.group, args);
	}
	
	public void broadcast(String abbreviation, ChatColor color, String ... args)
	{
		this.host.sendMessage(abbreviation, color, args);
		
		for (int i = 0; i < this.members.size(); i++)
		{
			this.members.get(i).sendMessage(abbreviation, color, args);
		}
	}
	
	public void add(Gamer gamer)
	{
		gamer.groupid = this.id;
		
		gamer.sendData("group", "3:" + gamer.information.name + ":" + gamer.getNameColor() + 
				":" + host.information.name + ":" + host.getNameColor());

		for (int i = 0; i < this.members.size(); i++)
		{
			gamer.sendData("group", "2:" + this.members.get(i).information.name + ":" + this.members.get(i).getNameColor());
		}
		
		this.sendPacketEveryone("2:" + gamer.information.name + ":" + gamer.getNameColor());
		
		this.members.add(gamer);
	}
	
	public void remove(String name)
	{
		for (int i = 0; i < this.members.size(); i++)
		{
			if (this.members.get(i).information.name.equalsIgnoreCase(name))
			{
				this.members.get(i).sendData("group", "4");
				
				this.members.remove(i);
				
				break;
			}
		}
		
		this.sendPacketEveryone("5:" + name);
	}
	
	public void remove(Gamer gamer)
	{
		gamer.groupid = 0;
		
		this.remove(gamer.information.name);
	}
	
	public int count()
	{
		return this.members.size() + 1;
	}
	
	public boolean isHost(String name)
	{
		return this.host.information.name.equalsIgnoreCase(name);
	}
	
	public boolean isHost(Gamer gamer)
	{
		return this.host.information.name.equalsIgnoreCase(gamer.information.name);
	}
	
	public Gamer hostOut()
	{
		this.host.groupid = 0;
		
		this.host.sendData("group", "4");
		
		if (this.count() == 1)
		{
			return null;
		}
		//modifications a faire ici
		
		this.host = this.members.get(Manipulation.random(0, this.members.size()));
		
		this.sendPacketEveryone("6:" + this.host.information.name + ":" + this.host.getNameColor()); //on envoie le nouveau host
		
		this.members.remove(this.host);
		
		return this.host;
	}
	
	public Gamer getHost()
	{
		return this.host;
	}
	
	public boolean isIn(String name)
	{
		if (this.isHost(name))
		{
			return true;
		}

		for (int i = 0; i < this.members.size(); i++)
		{
			if (this.members.get(i).information.name.equalsIgnoreCase(name))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public String getList()
	{
		String toReturn =  this.host.source.getDisplayName();

		for (int i = 0; i < this.members.size(); i++)
		{
			toReturn += ChatColor.GREEN + ", " + this.members.get(i).getNameColor();
		}
		
		return toReturn;
	}
	
	public void sendPacketEveryone(String message)
	{
		this.host.sendData("group", message);
		
		for (int i = 0; i < this.members.size(); i++)
		{
			this.members.get(i).sendData("group", message);
		}
	}
	
	public ArrayList<Gamer> allGamers()
	{
		ArrayList<Gamer> toReturn = new ArrayList<Gamer>();
		
		toReturn.add(this.host);
		
		for (int i = 0; i < this.members.size(); i++)
		{
			toReturn.add(this.members.get(i));
		}
		
		return toReturn;
	}
}
