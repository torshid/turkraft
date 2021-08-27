package turkraft.stockers;

public class Indestructible extends Canstock
{
	public Ownable creator;
	
	public Plot plot;
	
	public Indestructible(int id, Ownable creator, Plot plot)
	{
		this.id = id;
		
		this.inDatabase = true;
		
		this.hasChanged = false;
		
		this.creator = creator;
		
		this.plot = plot;
	}
	
	public Indestructible(Ownable creator, Plot plot)
	{
		this.inDatabase = false;
		
		this.hasChanged = false;
		
		this.creator = creator;
		
		this.plot = plot;
	}
}
