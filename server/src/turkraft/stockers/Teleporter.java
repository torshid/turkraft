package turkraft.stockers;

import org.bukkit.Location;

public class Teleporter extends Canstock
{
	public String name;
	
	public Location initial, destination;
	
	public Teleporter(int id, String name, Location initial, Location destination)
	{
		this.id = id;
		
		this.name = name;
		
		this.initial = initial;
		
		this.destination = destination;
		
		this.inDatabase = true;
		
		this.hasChanged = false;
	}
	
	public Teleporter(String name, Location initial, Location destination)
	{
		this.name = name;
		
		this.initial = initial;
		
		this.destination = destination;
		
		this.inDatabase = false;
		
		this.hasChanged = false;
	}
	
	public void changed()
	{
		this.hasChanged = true;
	}
	
	public void dispose()
	{
		this.id = 0;
		
		this.name = "";
		
		this.initial = null;
		
		this.destination = null;
	}
}
