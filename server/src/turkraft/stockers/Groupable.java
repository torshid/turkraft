package turkraft.stockers;

import java.util.ArrayList;

import org.bukkit.Location;

public class Groupable
{
	public ArrayList<Location> points;
	
	public Groupable()
	{
		this.points = new ArrayList<Location>();
	}
	
	public Groupable(ArrayList<Location> points)
	{
		this.points = points;
	}
	
	public boolean contains(Location point)
	{
		return this.points.contains(point);
	}
	
	public boolean contains(int x, int y, int z)
	{
		Location point;
		
		for (int i = 0; i < this.points.size(); i++)
		{
			point = this.points.get(i);
			
			if (point.getX() == x && point.getBlockY() == y && point.getBlockZ() == z)
			{
				return true;
			}
		}
		
		return false;
	}
	
	public boolean containsXZ(int x, int z)
	{
		Location point;
		
		for (int i = 0; i < this.points.size(); i++)
		{
			point = this.points.get(i);
			
			if (point.getX() == x && point.getBlockZ() == z)
			{
				return true;
			}
		}
		
		return false;
	}
}
