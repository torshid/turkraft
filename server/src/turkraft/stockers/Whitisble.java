package turkraft.stockers;

import java.util.ArrayList;

public class Whitisble
{
	//liste des joueurs qui ont acces
	public ArrayList<String> whiteGamers;
	
	public Whitisble()
	{
		this.whiteGamers = new ArrayList<String>();
	}
	
	public Whitisble(ArrayList<String> whiteGamers)
	{
		this.whiteGamers = whiteGamers;
	}
	
	//fonction qui retourne si le joueur entre a acces ou pas
	public boolean canAccess(String name)
	{
		for (int i = 0; i < this.whiteGamers.size(); i++)
		{
			if (this.whiteGamers.get(i).equalsIgnoreCase(name))
			{
				return true;
			}
		}
		
		return false;
	}

	//fonction qui retourne si le joueur entre a acces ou pas
	public boolean canAccess(Gamer gamer)
	{
		return canAccess(gamer.source.getName());
	}

	public String getListString(Ownable owner)
	{
		String toReturn = owner.owner;

		for (int i = 0; i < this.whiteGamers.size(); i++)
		{
			if (this.whiteGamers.get(i).equalsIgnoreCase(owner.owner))
			{
				continue;
			}
			
			toReturn += ", " + this.whiteGamers.get(i);
		}
		
		return toReturn;
	}
	
	public boolean remove(String name)
	{
		return this.whiteGamers.remove(name);
	}
}
