package turkraft.stockers;

public class Ownable
{
	public String owner;
	
	public Ownable()
	{
		this.owner = "#noowner#";
	}
	
	public Ownable(String owner)
	{
		this.owner = owner;
	}
	
	public Ownable(Gamer owner)
	{
		this.owner = owner.information.name;
	}
	
	public boolean isOwner(String name)
	{
		return this.owner.equalsIgnoreCase(name);
	}
	
	public boolean isOwner(Gamer gamer)
	{
		return this.isOwner(gamer.information.name);
	}
	
	public boolean hasOwner()
	{
		return !this.owner.equalsIgnoreCase("#noowner#");
	}
}
