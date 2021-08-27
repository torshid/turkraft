package turkraft.stockers;

import org.bukkit.Location;

public class Plot
{
	public Location pointA;
	
	public Location pointB;
	
	public int sizeX, sizeZ;
	
	public Plot(Location pointA, Location pointB)
	{
		this.updateStructure(pointA, pointB);
	}
	
	public void updateStructure(Location pointA, Location pointB)
	{
		if (pointB.getBlockX() < pointA.getBlockX())
		{
			int StockX = pointA.getBlockX();
			
			pointA.setX(pointB.getBlockX());
			
			pointB.setX(StockX);
		}
		
		if (pointB.getBlockZ() < pointA.getBlockZ())
		{
			int StockZ = pointA.getBlockZ();
			
			pointA.setZ(pointB.getBlockZ());
			
			pointB.setZ(StockZ);
		}
		
		this.pointA = pointA;
		
		this.pointB = pointB;
		
		this.sizeX = pointB.getBlockX()	- pointA.getBlockX();
		
		this.sizeZ = pointB.getBlockZ()	- pointA.getBlockZ();
	}

	public boolean inPlot(Location point, int padding)
	{
		return this.inPlot(point.getBlockX(), point.getBlockZ(), padding);
	}
	
	public boolean inPlot(Location point)
	{
		return this.inPlot(point, 0);
	}
	
	public boolean inPlot(int X, int Z, int padding)
	{
		if (X >= this.pointA.getBlockX() - padding && X <= this.pointB.getBlockX() + padding
				&& Z >= this.pointA.getBlockZ() - padding && Z <= this.pointB.getBlockZ() + padding)
		{
			return true;
		}
		
		return false;
	}
	
	public boolean inPlot(int X, int Z)
	{
		return this.inPlot(X, Z, 0);
	}
	
	public boolean touchPlot(Plot plot) //besoin d'optimiser en verifiant moins de bloc
	{
		for (int X = this.pointA.getBlockX(); X <= this.pointB.getBlockX(); X++)
		{
			for (int Z = this.pointA.getBlockZ(); Z <= this.pointB.getBlockZ(); Z++)
			{
				if (plot.inPlot(X, Z))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void dispose()
	{
		this.pointA = null;
		
		this.pointB = null;
	}
}
