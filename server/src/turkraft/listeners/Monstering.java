package turkraft.listeners;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import turkraft.Turkraft;
import turkraft.Turkraft.collisionType;
import turkraft.common.Manipulation;

public class Monstering extends Listening
{
	public Monstering(Turkraft main)
	{
		super(main);
	}
	
	@EventHandler (priority = EventPriority.NORMAL)
	public void onSpawn(CreatureSpawnEvent event)
	{
		if (event.getSpawnReason() == SpawnReason.SPAWNER) //modifier pour faire que les admin peuvent spawn de partout
		{
			event.getEntity().setMetadata("isSpawner", new FixedMetadataValue(this.main, true));

			return;
		}
		
		if (Manipulation.isAnimal(event.getEntityType()))
		{
			return;
		}

		if (event.getEntity().getType() == EntityType.CREEPER)
		{
			int chance = Manipulation.random(1, 8);
			
			if (chance != 5)
			{
				event.setCancelled(true);
				
				return;
			}
		}
		
		collisionType collision = this.main.collisionWithPlots(event.getEntity().getLocation());
		
		if (collision != collisionType.Prison)
		{
			if (!this.main.sanguine)
			{
				event.setCancelled(collision != collisionType.NoCollision);
			}
			else
			{
				if (!Manipulation.isAnimal(event.getEntity().getType()))
				{
					if (Manipulation.random(1, 10) == 5)
					{
						event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), event.getEntityType());
						
						event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), EntityType.BAT);
					}
				}
			}
		}
		else
		{
			if (event.getEntity().getType() == EntityType.CREEPER)
			{
				event.setCancelled(true);
			}
			else
			{
				event.setCancelled(false);
			}
		}
		
		if (event.isCancelled())
		{
			return;
		}
		
		if (this.main.sanguine)
		{
			if (!Manipulation.isAnimal(event.getEntity().getType()))
			{
				if (Manipulation.random(1, 15) == 10)
				{
					event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), event.getEntityType());
				}
				
				if (Manipulation.random(1, 4) == 3)
				{
					event.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 60 * Manipulation.random(1, 8), Manipulation.random(1, 2)));
				}
				
				if (Manipulation.random(1, 4) == 3)
				{
					event.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 60 *  Manipulation.random(1, 8), Manipulation.random(1, 2)));
				}
				
				if (Manipulation.random(1, 4) == 3)
				{
					event.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 60 *  Manipulation.random(1, 8), Manipulation.random(1, 2)));
				}
				
				if (Manipulation.random(1, 4) == 3)
				{
					event.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 60 *  Manipulation.random(1, 8), Manipulation.random(1, 2)));
				}
				
				if (Manipulation.random(1, 10) == 5)
				{
					event.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 60 *  Manipulation.random(1, 8), Manipulation.random(1, 2)));
				}
			}
		}
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onExplode(EntityExplodeEvent event)
	{
		event.getLocation().setX(event.getLocation().getBlockX());
		event.getLocation().setY(event.getLocation().getBlockY());
		event.getLocation().setZ(event.getLocation().getBlockZ());
		
		if (this.main.tntPlaces.containsKey(event.getLocation()) || event.getEntityType() == EntityType.PRIMED_TNT)
		{
			return;
		}

		event.setCancelled(this.main.collisionWithPlots(event.getLocation()) != collisionType.NoCollision);
		
		if (event.isCancelled())
		{
			return;
		}
		
		for (int i = 0; i < event.blockList().size(); i++)
		{
			if (this.main.collisionWithPlots(event.blockList().get(i).getLocation()) != collisionType.NoCollision)
			{
				event.setCancelled(true);
				
				return;
			}
		}
	}

	@EventHandler (priority = EventPriority.NORMAL)
	public void onChangeBlock(EntityChangeBlockEvent event)
	{
		if (event.getEntityType() == EntityType.PLAYER)
		{
			return;
		}
		
		if (event.getEntityType() == EntityType.ENDERMAN)
		{
			event.setCancelled(true);
			
			return;
		}
		
		if (event.getEntityType() == EntityType.FALLING_BLOCK || Manipulation.isAnimal(event.getEntityType()))
		{
			return;
		}

		event.setCancelled(!this.modifyLocation(event.getBlock().getLocation()));
	}
	
	public boolean modifyLocation(Location location)
	{
		return this.main.collisionWithPlots(location) == collisionType.NoCollision;
	}


	@EventHandler (priority = EventPriority.NORMAL)
	public void onInteract(EntityInteractEvent event)
	{
		if (event.getEntityType() == EntityType.PLAYER)
		{
			return;
		}
	
		event.setCancelled(this.main.collisionWithPlots(event.getBlock().getLocation()) != collisionType.NoCollision);
	}
}