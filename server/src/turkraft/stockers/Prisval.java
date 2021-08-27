package turkraft.stockers;

import org.bukkit.Material;

public class Prisval
{
	public int id;
	
	public Material item;
	
	public int value;
	
	public Prisval(int id, Material item, int value)
	{
		this.id = id;
		
		this.item = item;
		
		this.value = value;
	}
}
