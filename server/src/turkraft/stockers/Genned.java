package turkraft.stockers;

import org.bukkit.Material;

public class Genned
{
	public int id;
	
	public int chance;
	
	public int amount;
	
	public int value;
	
	public int interA, interB;
	
	public Material item;
	
	public Genned(int id, int chance, int value, Material item)
	{
		this.id = id;
		
		this.chance = chance;
		
		this.value = value;
		
		this.amount = 0;
		
		this.interA = 0;
		
		this.interB = 0;
		
		this.item = item;
	}
	
	public Genned(int id, int chance, int value, int amount, Material item)
	{
		this.id = id;
		
		this.chance = chance;
		
		this.value = value;
		
		this.amount = amount;
		
		this.interA = 0;
		
		this.interB = 0;
		
		this.item = item;
	}
	
	public boolean inInterval(int pattern)
	{
		return pattern >= this.interA && pattern <= this.interB;
	}
}
