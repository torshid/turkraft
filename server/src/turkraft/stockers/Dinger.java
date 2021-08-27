package turkraft.stockers;

//double integer (x,y)
public class Dinger
{
	public int x, y;
	
	public Dinger(int x, int y)
	{
		this.x = x;
		
		this.y = y;
	}
	
	public boolean equals(Dinger dinger)
	{
		if (dinger.x == this.x && dinger.y == this.y)
		{
			return true;
		}
		
		return false;
	}
}
