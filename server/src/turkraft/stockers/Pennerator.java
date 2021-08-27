package turkraft.stockers;

import java.util.ArrayList;
import java.util.Map;

import org.bukkit.Location;

import turkraft.Turkraft;
import turkraft.common.Manipulation;

public class Pennerator
{
	Turkraft main;
	
	private Genned[] genneds;
	
	private int maxRandom;
	
	public Pennerator(Turkraft main, Genned[] genneds)
	{
		this.main = main;
		
		this.genneds = genneds;

        for (int i = 0; i < genneds.length; i++)
        {
        	genneds[i].interA = this.maxRandom;

            this.maxRandom += genneds[i].chance;

            genneds[i].interB = this.maxRandom;
        }

		for (Map.Entry<Location, Prische> entry : this.main.prisches.entrySet())
        {
            this.start(entry.getValue());
        }
	}
	
	public void start(final Prische prische)
	{
		this.main.getServer().getScheduler().scheduleSyncDelayedTask(this.main, new Runnable()
		{
			   public void run()
			   {
				   setPrison(prische);
					
				   start(prische);
			   }
		}, Manipulation.random(prische.interval.x, prische.interval.y));
	}
	
	public Genned[] getGenerated(Prison prison)
	{
		ArrayList<Genned> generations = new ArrayList<Genned>();
		
		int players = this.main.countGamersOfPrison(prison);
		
		int score = Manipulation.random(100 - players * 5, 250 + players * 10);
		
		if (score <= 0 || players == 0)
		{
			return new Genned[] {};
		}
		
		int force = 0;

        while (score > 0)
        {
            int selected = Manipulation.random(0, this.maxRandom);

            for (int i = 0; i < this.genneds.length; i++)
            {
            	if (this.genneds[i].inInterval(selected))
            	{
            		selected = i;
            		
            		break;
            	}
            }

            if (score - this.genneds[selected].value >= 0)
            {
            	score -= this.genneds[selected].value;

                boolean found = false;

                for (int i = 0; i < generations.size(); i++)
                {
                    if (generations.get(i).id == this.genneds[selected].id)
                    {
                    	generations.get(i).amount++;

                        found = true;

                        break;
                    }
                }

                if (!found)
                {
                	generations.add(new Genned(this.genneds[selected].id, this.genneds[selected].chance,
                			this.genneds[selected].value, 1, this.genneds[selected].item));
                }
            }
            else
            {
                force++;

                if (force >= 3)
                {
                    break;
                }
            }
        }
		
		return generations.toArray(new Genned[] {});
	}
	
	public void resetPrisonsChests()
	{
		for (Map.Entry<Location, Prische> entry : this.main.prisches.entrySet())
        {
            entry.getValue().clear();
        }
	}
	
	public void setPrison(Prische prische)
	{
		prische.fill(this.getGenerated(prische.prison));
	}
}