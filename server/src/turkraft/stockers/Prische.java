package turkraft.stockers;

import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

public class Prische extends Canstock
{
	public Prison prison;
	
	public Location location;
	
	public Dinger interval;
	
	public Prische(Prison prison, Location location, Dinger interval)
	{
		this.id = 0;
		
		this.prison = prison;
		
		this.location = location;
		
		this.interval = interval;
	
		this.hasChanged = false;
		
		this.inDatabase = false;
	}
	
	public Prische(int id, Prison prison, Location location, Dinger interval)
	{
		this.id = id;
		
		this.prison = prison;
		
		this.location = location;
		
		this.interval = interval;
		
		this.hasChanged = false;
		
		this.inDatabase = true;
	}
	
	public void fill(Genned[] generations, boolean clear)
	{
		Chest chest = (Chest) this.location.getBlock().getState();
		
		if (clear)
		{
			chest.getBlockInventory().clear();
		}
		
		for (int i = 0; i < generations.length; i++)
		{
			chest.getInventory().addItem(new ItemStack(generations[i].item, generations[i].amount));
		}
	}
	
	public void fill(Genned[] generations)
	{
		this.fill(generations, false);
	}
	
	public void clear()
	{
		((Chest)this.location.getBlock().getState()).getBlockInventory().clear();
	}
}
