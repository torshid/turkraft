package turkraft.stockers;

import org.bukkit.Location;
import org.bukkit.Material;

public class Prispos extends Prische
{
	public Material item;
	
	public Prispos(Prison prison, Location location, Dinger interval, Material item)
	{
		super(prison, location, interval);
		
		this.item = item;
	}
	
	public Prispos(int id, Prison prison, Location location, Dinger interval, Material item)
	{
		super(id, prison, location, interval);
		
		this.item = item;
	}
	
	public void setBlock()
	{
		this.location.getBlock().setType(this.item);
	}
}
