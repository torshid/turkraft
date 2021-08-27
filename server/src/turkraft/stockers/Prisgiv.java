package turkraft.stockers;

import org.bukkit.Location;

public class Prisgiv extends Canstock
{
	public Prison prison;
	
	public Location location;
	
	public Prisgiv(Prison prison, Location location)
	{
		this.prison = prison;
		
		this.location = location;
		
		this.id = 0;
		
		this.hasChanged = false;
		
		this.inDatabase = false;
	}
	
	public Prisgiv(int id, Prison prison, Location location)
	{
		this.id = id;
		
		this.prison = prison;
		
		this.location = location;
		
		this.hasChanged = false;
		
		this.inDatabase = true;
	}
}
