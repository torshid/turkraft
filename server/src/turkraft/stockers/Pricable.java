package turkraft.stockers;

public class Pricable
{
	public int price;
	
	public Pricable(int price)
	{
		this.price = price;
	}
	
	public boolean isToSell()
	{
		return this.price > 0;
	}
}
