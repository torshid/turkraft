package turkraft.stockers;

public class NameArgCommandInt
{
	public nameArgCommand result;
	
	public int value;
	
	public NameArgCommandInt(nameArgCommand result, int value)
	{
		this.result = result;
		
		this.value = value;
	}
	
	public NameArgCommandInt(nameArgCommand result)
	{
		this.result = result;
		
		this.value = 0;
	}
	
	public enum nameArgCommand
	{
		Ok,
		NotConnected,
		NoRights,
		Incorrect,
		You
	}
}