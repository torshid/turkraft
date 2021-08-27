package turkraft.events;

import turkraft.Turkraft;
import turkraft.common.Manipulation;

public class Rimer
{
	public Turkraft main;
	
	private boolean loop;
	
	private int taskID;
	
	public Rimer(Turkraft main)
	{
		this.main = main;
		
		this.loop = false;
	}
	
	public void start()
	{
		this.loop = true;
		
		this.repeat();
	}
	
	private void repeat()
	{
		this.taskID = this.main.getServer().getScheduler().scheduleSyncDelayedTask(this.main, new Runnable()
		{
			   public void run()
			   {
				   int selected = randomSelect();
				   
			       main.broadcastRimeText(selected);
				   
				   if (loop)
				   {
					   repeat();
				   }
			   }
		}, Manipulation.random(this.main.configuration.rimeInterval.x, this.main.configuration.rimeInterval.y));
	}
	
	public void stop()
	{
		this.loop = false;
		
		this.main.getServer().getScheduler().cancelTask(this.taskID);
	}
	
	private int randomSelect()
	{
		return this.main.getRimeIdByIndex(Manipulation.random(0, this.main.countRimes()));
	}
}
