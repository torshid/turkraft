package turkraft.stockers;


public class Terrain extends Canstock
{
	public Ownable owner;
	
	public Plot plot;
	
	public Whitisble whitisble;
	
	public Pricable pricable;
	
	public boolean breakable;
	
	public Terrain(int id, Ownable owner, Plot plot, Whitisble whitisble, Pricable pricable, boolean breakable)
	{
		this.id = id;
		
		this.inDatabase = true;
		
		this.hasChanged = false;
		
		this.owner = owner;
		
		this.plot = plot;
		
		this.whitisble = whitisble;
		
		this.pricable = pricable;
		
		this.breakable = breakable;
	}
	
	public Terrain(Ownable owner, Plot plot, boolean breakable)
	{
		this.inDatabase = false;
		
		this.hasChanged = false;
		
		this.owner = owner;
		
		this.plot = plot;
		
		this.breakable = breakable;
		
		this.pricable = new Pricable(0);
		
		this.whitisble = new Whitisble();
	}
	
	public int getEstimatedPrice()
	{
		return (this.plot.sizeX * this.plot.sizeZ) * 7;
	}
	
	public void dispose()
	{
		this.owner = null;
		
		this.plot = null;
		
		this.whitisble = null;
		
		this.pricable = null;
	}
}