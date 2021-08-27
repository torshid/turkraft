package turkraft.stockers;

import org.bukkit.entity.EntityType;

import turkraft.common.Manipulation;

public class MobMoney
{
	public int id;
	
	public EntityType type;
	
	public int min, max;
	
	public boolean congrat;
	
	public MobMoney(int id, EntityType type, int min, int max, boolean congrat)
	{
		this.id = id;
		
		this.type = type;
		
		this.min = min;
		
		this.max = max;
		
		this.congrat = congrat;
	}
	
	public int getMoney()
	{
		return Manipulation.random(this.min, this.max);
	}
}
