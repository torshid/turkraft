package turkraft.stockers;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import turkraft.Turkraft;
import turkraft.common.Manipulation;

public class Clan extends Canstock
{
	public int virtual;
	
	public String name;
	
	public Plot plot;
	
	public int score;
	
	public int chiefId;
	
	public int money;
	
	public int taken;
	
	public ArrayList<Gamer> connectedGamers;
	
	public Location spawn;
	
	public int countMembers;
	
	public Clan(int virtual, String name, Plot plot, int score, int chiefId, int money, int taken, Location spawn, int countMembers)
	{
		this.virtual = virtual;
		
		this.name = name;
		
		this.plot = plot;

		this.score = score;
		
		this.chiefId = chiefId;
		
		this.money = money;
		
		this.taken = taken;
		
		this.spawn = spawn;
		
		this.countMembers = countMembers;
		
		this.inDatabase = true;
		
		this.hasChanged = false;
		
		this.connectedGamers = new ArrayList<Gamer>();
	}
	
	public Clan(String name, Plot plot, int chiefId)
	{
		this.name = name;
		
		this.plot = plot;
		
		this.score = 30;
		
		this.chiefId = chiefId;
		
		this.spawn = null;
		
		this.inDatabase = false;
			
		this.hasChanged = false;
		
		this.virtual = Manipulation.random(1, Integer.MAX_VALUE);
		
		this.money = 0;
		
		this.taken = 0;
		
		this.connectedGamers = new ArrayList<Gamer>();
		
		this.countMembers = 0;
	}
	
	public void broadcast(String message)
	{
		for (int i = 0; i < this.connectedGamers.size(); i++)
		{
			this.connectedGamers.get(i).sendMessage(message);
		}
	}
	
	public void broadcast(Turkraft main, String abbreviation, String ... args)
	{
		for (int i = 0; i < this.connectedGamers.size(); i++)
		{
			this.connectedGamers.get(i).sendMessage(abbreviation, main.colors.clan, args);
		}
	}
	
	public Gamer getChief()
	{
		for (int i = 0; i < this.connectedGamers.size(); i++)
		{
			if (this.connectedGamers.get(i).information.id == this.chiefId)
			{
				return this.connectedGamers.get(i);
			}
		}
		
		return null;
	}
	
	public String getList()
	{
		String toReturn = "";
		
		for (int i = 0; i < this.connectedGamers.size(); i++)
		{
			toReturn += this.connectedGamers.get(i).getNameColor() + ChatColor.AQUA + ", ";
		}
		
		if (this.connectedGamers.size() > 0)
		{
			toReturn = toReturn.substring(0, toReturn.length() - 2);
		}
		
		return toReturn;
	}
	
	public boolean isIn(String name)
	{
		for (int i = 0; i < this.connectedGamers.size(); i++)
		{
			if (this.connectedGamers.get(i).information.name.equalsIgnoreCase(name))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isIn(Gamer gamer)
	{
		return this.connectedGamers.contains(gamer);
	}
	
	public int getEstimatedPrice()
	{
		return (this.plot.sizeX * this.plot.sizeZ) * 15;
	}
	
	public void addNew(Gamer gamer)
	{
		gamer.information.clanRank = 0;
		
		gamer.information.clan = this.virtual;
		
		this.connectedGamers.add(gamer);
		
		this.countMembers++;
		
		gamer.refreshName();
		
		this.hasChanged = true;
	}
	
	public void remove(Gamer gamer)
	{
		if (gamer != null)
		{
			gamer.information.clanRank = 0;
			
			gamer.information.clan = 0;
			
			gamer.refreshName();
			
			this.countMembers--;
			
			this.connectedGamers.remove(gamer);
		}
		
		this.hasChanged = true;
	}
	
	public boolean isChief(Gamer gamer)
	{
		return gamer.information.id == this.chiefId;
	}
	
	public void dispose()
	{
		this.virtual = 0;

		for (int i = 0; i < this.connectedGamers.size(); i++)
		{
			this.remove(this.connectedGamers.get(i));
		}
		
		this.plot.dispose();
	}
}
