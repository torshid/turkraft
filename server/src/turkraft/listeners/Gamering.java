package turkraft.listeners;

import java.util.Calendar;

import net.minecraft.server.v1_4_6.BlockContainer;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.inventory.ItemStack;

import turkraft.Turkraft;
import turkraft.Turkraft.collisionType;
import turkraft.common.Manipulation;
import turkraft.stockers.Clan;
import turkraft.stockers.Damevent;
import turkraft.stockers.Dinger;
import turkraft.stockers.Gamer;
import turkraft.stockers.Group;
import turkraft.stockers.Indestructible;
import turkraft.stockers.Information;
import turkraft.stockers.Language;
import turkraft.stockers.NameArgCommandInt;
import turkraft.stockers.Ownable;
import turkraft.stockers.Plot;
import turkraft.stockers.Prische;
import turkraft.stockers.Prisgiv;
import turkraft.stockers.Prison;
import turkraft.stockers.Prispos;
import turkraft.stockers.Shop;
import turkraft.stockers.Teleporter;
import turkraft.stockers.Terrain;
//import org.bukkit.entity.Wolf;

public class Gamering extends Listening
{
	public Gamering(Turkraft main)
	{
		super(main);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler (priority = EventPriority.NORMAL)
    public void onLogin(PlayerLoginEvent event) 
    {
		if (!Turkraft.noError)
		{
			event.disallow(Result.KICK_OTHER, "Server maintenance.");
			
			return;
		}
		
		String internalError = "Internal error.";
				
		Information information = this.main.database.getPlayerInformation(event.getPlayer().getName());
		
		if (information == null)
		{
			this.main.log.error("OnPlayerLoginEvent, information == true = true");
			
			event.disallow(Result.KICK_OTHER, internalError);
			
			return;
		}
		
		if (information.status == -1)
		{
			this.main.log.error("OnPlayerLoginEvent, informations.status == -1 = true");
			
			event.disallow(Result.KICK_OTHER, internalError);
			
			return;
		}
		
		if (information.status == 1)
		{
			if (this.main.refuseConnections)
			{
				event.disallow(Result.KICK_OTHER, this.main.getText(information.language, "nologin.server.busy"));
				
				return;
			}
			
			if (this.main.isRestarting)
			{
				event.disallow(Result.KICK_OTHER, this.main.getText(information.language, "nologin.server.restarting"));
				
				return;
			}
			
			if (!this.main.configuration.acceptConnections)
			{
				event.disallow(Result.KICK_OTHER, this.main.getText(information.language, "nologin.server.refuse"));

				return;
			}
			
			if (information.banned)
			{
				event.disallow(Result.KICK_OTHER, this.main.getText(information.language, "nologin.player.banned"));

				return;
			}
			
			if (!information.session && this.main.configuration.checkSession)
			{
				event.disallow(Result.KICK_OTHER, this.main.getText(information.language, "nologin.player.nosession"));
				
				return;
			}
			
			if (!information.white && this.main.configuration.whiteList)
			{
				event.disallow(Result.KICK_OTHER, this.main.getText(information.language, "nologin.server.whitelist"));
				
				return;
			}
			
			if (this.main.isBannedIp(event.getAddress()))
			{
				event.disallow(Result.KICK_OTHER, this.main.getText(information.language, "nologin.player.banned"));
				
				return;
			}
			
			if (this.main.isConnected(information.name, true))
			{
				event.disallow(Result.KICK_OTHER, this.main.getText(information.language, "nologin.player.alreadyconnected"));
				
				return;
			}
			
			if (information.genre == 0)
			{
				event.disallow(Result.KICK_OTHER, this.main.getText(information.language, "nologin.player.noclass"));
				
				return;
			}

			Gamer gamer = new Gamer(this.main, event.getPlayer(), information);
			
			if (!gamer.grad.canConnect)
			{
				event.disallow(Result.KICK_OTHER, this.main.getText(information.language, "nologin.grad.noconnection"));
				
				return;
			}
			
			/*if (!gamer.canLogAfterCombat())
			{
				event.disallow(Result.KICK_OTHER, this.main.getText(information.language, "nologin.grad.combat"));
				
				return;
			}*/
			
			if (!gamer.grad.canMultiIP)
			{
				if (this.main.isIPConnected(event.getAddress().getHostAddress()))
				{
					event.disallow(Result.KICK_OTHER, this.main.getText(information.language, "nologin.grad.nomultiip"));
					
					return;
				}
			}

			if (gamer.information.exition == null || Calendar.getInstance().get(Calendar.DAY_OF_MONTH) != gamer.information.exition.getDate())
			{
				gamer.information.playedMinutes = 0;
			}
			
			if (gamer.information.playedMinutes >= gamer.information.playableMinutes && gamer.information.playableMinutes > 0)
			{
				event.disallow(Result.KICK_OTHER, this.main.getText(information.language, "nologin.time.elapsed"));
				
				return;
			}
			
			this.main.addGamer(gamer);
			
			this.main.database.setGamerConnected(gamer.information.id);

			return;
		}
		
		switch (information.status)
		{
			case 2:
				event.disallow(Result.KICK_OTHER, "Please register on the website!");
				break;
			case 3:
				this.main.log.error("OnPlayerLoginEvent, case 3 switch (informations.status)");
				
				event.disallow(Result.KICK_OTHER, internalError);
				break;
			default:
				this.main.log.error("OnPlayerLoginEvent, default switch (informations.status)");
				
					event.disallow(Result.KICK_OTHER, internalError);
			break;
		}
    }
	
	@EventHandler(priority = EventPriority.NORMAL)
    public void onJoin(PlayerJoinEvent event)
    {
		event.setJoinMessage(null);
		
		Gamer gamer = this.main.getGamer(event.getPlayer().getName(), true);
		
		gamer.initialize();
		
		if (event.getPlayer().hasPlayedBefore())
		{
			this.main.broadcastTextAbbrev("join.message", ChatColor.DARK_GREEN, gamer.getNameColor());
		}
		else
		{
			this.main.broadcastTextAbbrev("join.firsttime", ChatColor.DARK_GREEN, gamer.getNameColor());
		}
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onQuit(PlayerQuitEvent event)
    {
    	Gamer gamer = this.main.getGamer(event.getPlayer().getName(), true);

		event.setQuitMessage(null);
    	
    	if (gamer == null)
    	{
    		return;
    	}
		
    	if (((System.currentTimeMillis() - gamer.information.lastDamage) / 1000) / 60 > this.main.configuration.fightInterval + gamer.information.combatCounter)
    	{
    		gamer.information.lastDamage = 0;
    	}
    	
		if (!gamer.canLogAfterCombat())
		{
			gamer.information.combatCounter++;
		}
		
		if (gamer.inGroup())
		{
			this.simulateGroupExit(gamer);
		}
		
		Clan clan = gamer.getClan();
		
		if (clan != null)
		{
			clan.connectedGamers.remove(gamer);
		}
    	
    	this.main.database.setGamerDisconnected(gamer);
    	
    	this.main.removeGamer(gamer.information.name);
		
		this.main.broadcastTextAbbrev("quit.message", ChatColor.DARK_RED, gamer.getNameColor());
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onChat(AsyncPlayerChatEvent event)
    {
    	Gamer gamer = this.main.getGamer(event.getPlayer().getName(), true);

    	gamer.setUnAFK();

    	if (gamer.information.muted)
    	{
    		gamer.sendMessage("chat.no.muted", this.main.colors.admin);
    		
    		event.setCancelled(true);
    		
    		return;
    	}
    	
    	if (this.main.sanguine)
    	{
    		if (Manipulation.random(1, 7) == 5)
    		{
    			event.setMessage(Manipulation.generateWords(3, 7, 2, 6));
    		}
    	}
    	
    	event.setFormat(gamer.format);
    	
    	this.main.networker.send(gamer.getNameTag() + ChatColor.WHITE + " " + event.getMessage());
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onKick(PlayerKickEvent event)
    {
    	Gamer gamer = this.main.getGamer(event.getPlayer().getName(), true);
    	
		event.setLeaveMessage(null);
		
		String broad = "quit.message";
		
    	if (gamer.isAFK() && event.getReason().equalsIgnoreCase("afkEnd"))
    	{
    		event.setReason(this.main.getText(gamer.information.language, "kick.afkend"));
    		
    		broad = "quit.afk";
    	}

		if (gamer.inGroup())
		{
			this.simulateGroupExit(gamer);
		}
		
		Clan clan = gamer.getClan();
		
		if (clan != null)
		{
			clan.connectedGamers.remove(gamer);
		}
    	
    	this.main.database.setGamerDisconnected(gamer);
    	
    	this.main.removeGamer(gamer.information.name);

		this.main.broadcastTextAbbrev(broad, ChatColor.DARK_RED, gamer.getNameColor());
    }

	@EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event)
	{
		if (event.getRightClicked().getType() != EntityType.ITEM_FRAME && event.getRightClicked().getType() != EntityType.PAINTING)
		{
			return;
		}
		
		Material material = Material.ITEM_FRAME;
		
		if (event.getRightClicked().getType() == EntityType.PAINTING)
		{
			material = Material.PAINTING;
		}
		
    	Gamer gamer = this.main.getGamer(event.getPlayer().getName(), true);
    	
    	if (!gamer.grad.canInteract)
    	{
    		event.setCancelled(true);
    		
    		return;
    	}

    	event.setCancelled(!(gamer.blockRightClick(event.getRightClicked().getLocation(), material, event.getPlayer().getItemInHand())));
	}

	@EventHandler(priority = EventPriority.NORMAL)
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event)
    {
		if (event.getRemover().getType() != EntityType.PLAYER)
		{
			event.setCancelled(!(this.main.monstering.modifyLocation(event.getEntity().getLocation())));
			
			return;
		}
		
		if (event.getEntity().getType() != EntityType.ITEM_FRAME && event.getEntity().getType() != EntityType.PAINTING)
		{
			return;
		}
		
		Material material = Material.ITEM_FRAME;
		
		if (event.getEntity().getType() == EntityType.PAINTING)
		{
			material = Material.PAINTING;
		}

    	Gamer gamer = this.main.getGamer(((Player)event.getRemover()).getName(), true);
    	
    	if (!gamer.grad.canBreak)
    	{
    		event.setCancelled(true);
    		
    		return;
    	}

    	event.setCancelled(!gamer.blockLeftClick(event.getEntity().getLocation(), material, gamer.source.getItemInHand().getType()));
    }

	@EventHandler(priority = EventPriority.NORMAL)
    public void onHangingPlace(HangingPlaceEvent event)
    {
		if (event.getEntity().getType() != EntityType.ITEM_FRAME && event.getEntity().getType() != EntityType.PAINTING)
		{
			return;
		}
		
		Material material = Material.ITEM_FRAME;
		
		if (event.getEntity().getType() == EntityType.PAINTING)
		{
			material = Material.PAINTING;
		}

    	Gamer gamer = this.main.getGamer(event.getPlayer().getName(), true);
    	
    	if (!gamer.grad.canPlace)
    	{
    		event.setCancelled(true);
    		
    		return;
    	}

    	event.setCancelled(!gamer.blockPlaceEvent(event.getEntity().getLocation(), material, gamer.source.getItemInHand().getType()));
    }
	
	@EventHandler(priority = EventPriority.NORMAL)
    public void onBucketEmpty(PlayerBucketEmptyEvent event)
    {
    	Gamer gamer = this.main.getGamer(event.getPlayer().getName(), true);

    	if (!gamer.grad.canInteract)
    	{
    		event.setCancelled(true);
    		
    		return;
    	}
    	
    	event.setCancelled(!gamer.blockPlaceEvent(event.getBlockClicked().getLocation(), event.getBucket(), gamer.source.getItemInHand().getType()));
    }
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onTeleport(PlayerTeleportEvent event)
	{
    	Gamer gamer = this.main.getGamer(event.getPlayer().getName(), true);

    	if (event.getCause() != TeleportCause.ENDER_PEARL)
    	{
    		return;
    	}
    	
    	if (gamer.isPrison() || this.main.collisionWithPlots(event.getTo()) == collisionType.Prison)
    	{
    		event.setCancelled(true);
    		
    		return;
    	}
	}
    	
	@EventHandler(priority = EventPriority.NORMAL)
    public void onInteract(PlayerInteractEvent event)
    {
    	Gamer gamer = this.main.getGamer(event.getPlayer().getName(), true);

    	gamer.setUnAFK();
    	
    	if (!gamer.grad.canInteract)
    	{
    		event.setCancelled(true);
    		
    		return;
    	}
    	
    	if (event.getAction() == Action.RIGHT_CLICK_AIR)
    	{
    		if (gamer.source.getItemInHand().getType() == Material.ENDER_PEARL)
    		{
    			if (gamer.isPrison())
    			{
    				event.setCancelled(true);
    				
    				return;
    			}
    		}
    	}

    	if (event.getAction() == Action.LEFT_CLICK_BLOCK)
    	{
    		event.setCancelled(!gamer.blockLeftClick(event.getClickedBlock().getLocation(), event.getClickedBlock().getType(), event.getMaterial()));

        	return;
    	}
    	
    	if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
    	{
    		event.setCancelled(!gamer.blockRightClick(event.getClickedBlock().getLocation(), event.getClickedBlock().getType(), event.getItem()));
    		
    		if (!event.isCancelled())
    		{
    	    	if (this.canBeInteracted(event.getClickedBlock().getType()))
    	    	{
    	    		if (event.getClickedBlock().getType() == Material.BED_BLOCK && this.main.sanguine)
	    	    	{
	    	    		gamer.sendMessage("nobed.sanguine", this.main.colors.diverse);
	    	    		
	    	    		event.setCancelled(true);
	    	    		
	    	    		return;
	    	    	}
    	    		
    	    		if (event.getItem() == null)
    	    		{
    	    			event.setCancelled(!gamer.blockInteractEvent(event.getClickedBlock().getLocation(), event.getClickedBlock().getType(), Material.AIR));
    	    		}
    	    		else
    	    		{
    	    			event.setCancelled(!gamer.blockInteractEvent(event.getClickedBlock().getLocation(), event.getClickedBlock().getType(), event.getItem().getType()));
    	    		}

    	    		if (event.isCancelled())
    	    		{
    	        		return;
    	    		}
    	    	}
    		}

        	return;
    	}
    	
    	if (event.getAction() == Action.PHYSICAL && event.getClickedBlock() != null && (event.getClickedBlock().getType() == Material.SOIL
    			|| event.getClickedBlock().getType() == Material.WOOD_PLATE || event.getClickedBlock().getType() == Material.STONE_PLATE))
    	{
    		if (event.getClickedBlock().getType() == Material.STONE_PLATE)
    		{
    			Teleporter teleporter = this.main.getTeleporter(event.getClickedBlock().getLocation());
    			
    			if (teleporter != null)
    			{
    				gamer.useTeleporter(teleporter);
    				
    				event.setCancelled(true);
    				
    				return;
    			}
    		}
    		
    		event.setCancelled(!gamer.blockSoilEvent(event));

    		return;
    	}
    }
	
	private boolean canBeInteracted(Material material)
	{
		return material == Material.CHEST || material == Material.WORKBENCH || material == Material.FURNACE
				|| material == Material.WOODEN_DOOR || material == Material.BED || material == Material.BOAT
				|| material == Material.STONE_BUTTON || material == Material.WOOD_BUTTON || material == Material.LEVER
				|| material == Material.ENCHANTMENT_TABLE || material == Material.JUKEBOX || material == Material.ENDER_PORTAL_FRAME
				|| material == Material.MINECART || material == Material.BURNING_FURNACE || material == Material.SAPLING
				|| material == Material.TRAP_DOOR || material == Material.CAULDRON || material == Material.BREWING_STAND
				|| material == Material.FLOWER_POT || material == Material.FENCE_GATE || material == Material.LOCKED_CHEST
				|| material == Material.ENDER_CHEST || material == Material.BEACON || material == Material.ANVIL
				|| material == Material.DIODE || material == Material.DIODE_BLOCK_OFF || material == Material.DIODE_BLOCK_ON
				|| material == Material.NOTE_BLOCK || material == Material.BED_BLOCK || material == Material.TNT
				|| material == Material.CAKE_BLOCK || material == Material.DISPENSER;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
    public void onMove(PlayerMoveEvent event)
    {
    	Gamer gamer = this.main.getGamer(event.getPlayer().getName(), true);

    	gamer.setUnAFK();
    	
    	if (!gamer.grad.canMove)
    	{
    		event.setCancelled(true);
    		
    		return;
    	}
    }
	
	@EventHandler(priority = EventPriority.NORMAL)
    public void onPortal(PlayerPortalEvent event)
    {
    	Gamer gamer = this.main.getGamer(event.getPlayer().getName(), true);
    	
    	if (!gamer.grad.canUsePortal)
    	{
    		event.setCancelled(true);
    		
    		return;
    	}
    	
    	gamer.refreshFlight();
    }
    	
	
	@EventHandler(priority = EventPriority.NORMAL)
    public void onToggleSneak(PlayerToggleSneakEvent event)
    {
    	Gamer gamer = this.main.getGamer(event.getPlayer().getName(), true);

    	gamer.setUnAFK();
    	
    	if (!gamer.grad.canSneak)
    	{
    		event.setCancelled(true);
    		
    		return;
    	}
    }
	
	@EventHandler(priority = EventPriority.NORMAL)
    public void onToggleSprint(PlayerToggleSprintEvent event)
    {
    	Gamer gamer = this.main.getGamer(event.getPlayer().getName(), true);
    	
    	if (!gamer.grad.canSprint)
    	{
    		event.setCancelled(true);
    		
    		return;
    	}
    }
	
	@EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryOpen(InventoryOpenEvent event)
    {
    	Gamer gamer = this.main.getGamer(event.getPlayer().getName(), true);

    	gamer.setUnAFK();
    	
    	if (!gamer.grad.canOpenInventory)
    	{
    		event.setCancelled(true);
    		
    		return;
    	}
    }
	
	@EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent event)
    {
    	Gamer gamer = this.main.getGamer(event.getPlayer().getName(), true);

    	gamer.setUnAFK();
    	
    	if (!gamer.grad.canBreak)
    	{
    		event.setCancelled(true);
    		
    		return;
    	}
    	
    	event.setCancelled(!gamer.blockBreakEvent(event.getBlock().getLocation(), event.getBlock().getType()));
    }
	
	@EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent event)
    {
    	Gamer gamer = this.main.getGamer(event.getPlayer().getName(), true);

    	gamer.setUnAFK();
    	
    	if (!gamer.grad.canPlace)
    	{
    		event.setCancelled(true);
    		
    		return;
    	}
    	
    	event.setCancelled(!gamer.blockPlaceEvent(event.getBlock().getLocation(), event.getBlock().getType(), gamer.source.getItemInHand().getType()));
    }
	
	@EventHandler(priority = EventPriority.NORMAL)
    public void onBlockDamage(BlockDamageEvent event)
    {
    	Gamer gamer = this.main.getGamer(event.getPlayer().getName(), true);

    	gamer.setUnAFK();
    	
		event.setInstaBreak(gamer.grad.isPowerful);
    }

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPotionSplash(PotionSplashEvent event)
	{
		if (event.getEntity().getShooter().getType() != EntityType.PLAYER)
		{
			return;
		}
		
		Gamer gamer = this.main.getGamer(((Player)event.getEntity().getShooter()).getName(), true);
		
		Damevent dame = gamer.launchPotion(event.getPotion(), event.getAffectedEntities());
		
		event.setCancelled(!dame.notCancelled);
		
		if (!event.isCancelled())
		{
			LivingEntity[] affectedEntities = event.getAffectedEntities().toArray(new LivingEntity[] {});
			
			for (int i = 0; i < affectedEntities.length; i++)
			{
				event.setIntensity(affectedEntities[i], event.getIntensity(affectedEntities[i]) * dame.damage);
				
				if (affectedEntities[i].getType() != EntityType.PLAYER)
				{
					continue;
				}
				
				Gamer target = this.main.getGamer(((Player)affectedEntities[i]).getName(), true);
				
				Damevent dameRec = target.receivePotion(gamer, event.getPotion(), event.getIntensity(affectedEntities[i]));
				
				if (!dameRec.notCancelled)
				{
					event.setIntensity(affectedEntities[i], 0);
				}
				else
				{
					event.setIntensity(affectedEntities[i], dameRec.damage);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamageEntity(EntityDamageByEntityEvent event)
	{
		if (event.getDamager().getType() != EntityType.PLAYER) //si l'attaqueur n'est pas un joueur
		{
			if (event.getEntityType() != EntityType.PLAYER) //si le receveur n'est pas un joueur
			{
				if (!(event.getDamager() instanceof Projectile))
				{
					if (!this.main.configuration.mobKillEachOther)
					{
						event.setCancelled(true); //les monstres peuvent pas se taper entre eux
					}
					
					return;
				}
				
				if (((Projectile)event.getDamager()).getShooter() == null)
				{
					return;
				}
				
				if (((Projectile)event.getDamager()).getShooter().getType() != EntityType.PLAYER)
				{
					if (!this.main.configuration.mobKillEachOther)
					{
						event.setCancelled(true); //les monstres peuvent pas se taper entre eux
					}
					
					return;
				}

				Gamer attacker = this.main.getGamer(((Player)((Projectile)event.getDamager()).getShooter()).getName(), true);
				
				if (!attacker.grad.canAttack)
				{
					event.setCancelled(true);
					
					return;
				}

		    	if (attacker.grad.canOneShot && event.getEntity() instanceof LivingEntity)
		    	{
		    		event.setDamage(((LivingEntity)event.getEntity()).getHealth());
		    	}
		    	
		    	Damevent dame = attacker.attackEntityEvent(event.getEntity(), event.getDamage());
		    	
		    	event.setCancelled(!dame.notCancelled); //retourne false pour mettre le event cancelled
		    	
		    	event.setDamage((int)dame.damage);
		    	
				return;
			}
			
			Gamer target = this.main.getGamer(((Player)event.getEntity()).getName(), true); //receveur
			
			if (target == null)
			{
				this.main.log.error("TARGET NULL ERROR: " + event.getEntityType() + " - " + event.getEntity().getType());
				
				return;
			}
			
			if (!target.grad.canBeAttacked) //si le receveur ne peut pas etre attaque
			{
				event.setCancelled(true);
				
				return;
			}

			if (!(event.getDamager() instanceof Projectile) && !(event.getDamager() instanceof BlockContainer)) //si l'attaqueur n'est pas un projectile
			{
				event.setDamage(target.attackedByEntityEvent(event.getDamager(), event.getDamage()));
		    	
		    	return;
			}

			Entity damager = null;
			
			if (event.getDamager() instanceof Projectile)
			{
				damager = ((Projectile)event.getDamager()).getShooter();
			}
			else if (event.getDamager() instanceof BlockContainer)
			{
				
			}
			else
			{
				System.out.println("DEBUG: SHOOTER IS NULL, DAMAGER TYPE " + event.getDamager().getType() + ", PLAYER " + target.information.name);
				
				return;
			}
			
			if (damager == null)
			{
				System.out.println("DEBUG: DAMAGER IS NULL, DAMAGER TYPE " + event.getDamager().getType());
				
				return;
			}
			
			if (damager.getType() == EntityType.PLAYER)
			{
				Gamer realDamager = this.main.getGamer(((Player)damager).getName(), true);
				
				if (!realDamager.grad.canAttack)
				{
					event.setCancelled(true);
					
					return;
				}
				
				if (realDamager.grad.canOneShot)
				{
					event.setDamage(target.source.getHealth());
				}

				Damevent dame = realDamager.attackGamerEvent(target, event.getDamage());
				
	    		event.setCancelled(!dame.notCancelled); //retourne false pour mettre le event cancelled
	    		
	    		event.setDamage((int)dame.damage);
	    		
	    		if (event.isCancelled()) //si c'est cancelled, on appelle pas le event pour celui qui est attaque
	    		{
	    			return;
	    		}
	    		
	    		event.setDamage(target.attackedByGamerEvent(realDamager, event.getDamage()));
		    	
		    	return;
			}
			
			event.setDamage(target.attackedByEntityEvent(damager, event.getDamage()));
	    	
			return;
		}

    	Gamer attacker = this.main.getGamer(((Player)event.getDamager()).getName(), true); //attaqueur
    	
    	if (!attacker.grad.canAttack) //si l'attaqueur ne peut pas attaquer
    	{
    		event.setCancelled(true);
    		
    		return;
    	}
    	
    	
    	if (event.getEntityType() == EntityType.PLAYER) //si le receveur est un joueur
    	{
    		Gamer target = this.main.getGamer(((Player)event.getEntity()).getName(), true); //receveur

    		if (target == null)
    		{
    			return;
    		}
    		
			if (!target.grad.canBeAttacked) //si le receveur ne peut pas etre attaque
			{
				event.setCancelled(true);
				
				return;
			}

	    	if (attacker.grad.canOneShot)
	    	{
	    		event.setDamage(target.source.getHealth());
	    	}
	    	
	    	Damevent dame = attacker.attackGamerEvent(target, event.getDamage());
	    	
    		event.setCancelled(!dame.notCancelled); //retourne false pour mettre le event cancelled
    		
    		event.setDamage((int)dame.damage);
    		
    		if (event.isCancelled()) //si c'est cancelled, on appelle pas le event pour celui qui est attaque
    		{
    			return;
    		}
    		
    		event.setDamage(target.attackedByGamerEvent(attacker, event.getDamage()));
    		
    		return;
    	}

    	if (attacker.grad.canOneShot && event.getEntity() instanceof LivingEntity)
    	{
    		event.setDamage(((LivingEntity)event.getEntity()).getHealth());
    	}
    	
    	Damevent dame = attacker.attackEntityEvent(event.getEntity(), event.getDamage());
    	
    	event.setCancelled(!dame.notCancelled); //retourne false pour mettre le event cancelled
    	
    	event.setDamage((int)dame.damage);
	}
	
	@EventHandler (priority = EventPriority.NORMAL)
	public void onDeath(PlayerDeathEvent event)
	{
    	Gamer killedGamer = this.main.getGamer(((Player)event.getEntity()).getName(), true);
    
    	event.setDeathMessage(null);
    	
    	if (killedGamer.source.getLastDamageCause() == null)
    	{
	    	this.main.broadcastTextAbbrev("death.player.normal", this.main.colors.diverse, killedGamer.getNameColor());
	    	
	    	return;
    	}
    	
    	DamageCause damageCause = killedGamer.source.getLastDamageCause().getCause();
    	
    	if (killedGamer.grad.keepDeathLevel)
    	{
    		event.setKeepLevel(true);
    	}
    	
    	Player killer = event.getEntity().getKiller();
    	
		if (killer == null)
		{
			if (damageCause != DamageCause.ENTITY_ATTACK && damageCause != DamageCause.ENTITY_EXPLOSION && damageCause != DamageCause.PROJECTILE/* && event.getEntity().getLastDamageCause().getEntity() == null*/) //si n'a pas ete tuer par un monstre ni un joueur
			{
				killedGamer.killedByNothing();

    	    	this.main.broadcastTextAbbrev("death.player." + damageCause.toString().toLowerCase(), this.main.colors.diverse, killedGamer.getNameColor());
		    	
				return;
			}

			EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();
			
	        if (damageEvent instanceof EntityDamageByEntityEvent)
	        {
				Language[] languages = this.main.getLanguages();
				
				if (!(((EntityDamageByEntityEvent) damageEvent).getDamager() instanceof Projectile))
				{
					killedGamer.killedByEntity(((EntityDamageByEntityEvent) damageEvent).getDamager());
				
					for (int i = 0; i < languages.length; i++)
					{
						this.main.broadcastSameLanguage(languages[i].id, this.main.getText(languages[i].id, "death.mob.kills", this.main.colors.diverse, killedGamer.getNameColor(),
								this.main.getText(languages[i].id, "mobs." + ((EntityDamageByEntityEvent) damageEvent).getDamager().getType().toString().toLowerCase(), this.main.colors.diverse)));
					}
				}
				else
				{
					killedGamer.killedByEntity(((Projectile)((EntityDamageByEntityEvent) damageEvent).getDamager()).getShooter());
				
					for (int i = 0; i < languages.length; i++)
					{
						this.main.broadcastSameLanguage(languages[i].id, this.main.getText(languages[i].id, "death.mob.kills", this.main.colors.diverse, killedGamer.getNameColor(),
								this.main.getText(languages[i].id, "mobs." + ((Projectile)((EntityDamageByEntityEvent) damageEvent).getDamager()).getShooter().getType().toString().toLowerCase(),
										this.main.colors.diverse)));
					}
				}
	        }
			
			return;
		}
		
		if (killer.getType() == EntityType.PLAYER)
		{
			Gamer killerGamer = this.main.getGamer(((Player)killer).getName(), true);
			
			killerGamer.killedGamer(killedGamer);
			
			killedGamer.killedByGamer(killerGamer);
			
	    	this.main.broadcastTextAbbrev("death.player.killedby", this.main.colors.diverse, killedGamer.getNameColor(), killerGamer.getNameColor());
			
			return;
        }
	}
	
	@EventHandler (priority = EventPriority.NORMAL)
	public void onEntityDeath(EntityDeathEvent event)
	{
		if (event.getEntityType() == EntityType.PLAYER)
		{
			return;
		}

		/*if (event.getEntity() != null)
		{
			System.out.println("ONE");
			
			if (event.getEntity().getLastDamageCause() != null)
			{
				System.out.println("TWO");
				
				if (event.getEntity().getLastDamageCause().getEntity() != null)
				{
					System.out.println("THREE " + event.getEntity().getLastDamageCause().getEntityType().toString());
				}
			}
			System.out.println(event.getEntityType().toString());
		}
		if (event.getEntity() != null && event.getEntity().getLastDamageCause() != null && event.getEntity().getLastDamageCause().getEntity() != null)
		{
			if ((event.getEntity().getLastDamageCause().getEntityType()) == EntityType.WOLF)
			{
				Gamer killer = this.main.getGamer(((Wolf)event.getEntity().getLastDamageCause()).getOwner().getName(), true);
				
				if (killer == null)
				{
					return;
				}
				
		    	killer.killedEntity(event.getEntity());
		    	
		    	return;
			}
		}*/
		
		if (event.getEntity().getKiller() == null)
		{
			return;
		}
		
		Gamer killer = this.main.getGamer(((Player)event.getEntity().getKiller()).getName(), true);
		
    	event.setDroppedExp((int)killer.killedEntity(event.getEntity(), event.getDroppedExp()));
	}
	
	@EventHandler (priority = EventPriority.NORMAL)
    public void onRespawn(PlayerRespawnEvent event)
	{
		Gamer gamer = this.main.getGamer(event.getPlayer().getName(), true);
		
		if (gamer.isPrison())
		{
			event.setRespawnLocation(gamer.getPrison().spawn);
		}
		else
		{
			return;
		}
		
		gamer.source.getInventory().addItem(new ItemStack(Material.WOOD_PICKAXE, 1));
		
		gamer.updateInventory();
		
		/*if (event.isBedSpawn())
		{
			return;
		}
		
		event.setRespawnLocation(this.main.configuration.spawnPoint);

    	gamer.refreshFlight();*/
	}

	@EventHandler (priority = EventPriority.NORMAL)
	public void onExplode(EntityExplodeEvent event)
	{
		event.getLocation().setX(event.getLocation().getBlockX());
		event.getLocation().setY(event.getLocation().getBlockY());
		event.getLocation().setZ(event.getLocation().getBlockZ());

		if (!this.main.tntPlaces.containsKey(event.getLocation()))
		{
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
			
			return;
		}

		event.setCancelled(!this.main.tntPlaces.get(event.getLocation()).explodeEvent(event));
	
		this.main.tntPlaces.remove(event.getLocation());
	}

	@EventHandler (priority = EventPriority.NORMAL)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		String message = event.getMessage();
		
		String head = message.substring(1, message.length());
		
		String content = "";

		if (head.contains(" "))
		{
			int index = head.indexOf(" ");
			
			content = head.substring(index, head.length());
			
			head = head.substring(0, index);
		}

		if (head.equalsIgnoreCase("help") || head.equalsIgnoreCase("h") || head.equalsIgnoreCase("?"))
		{
			event.setMessage("/invisiblehelp");
			
			return;
		}
		
		if (head.equalsIgnoreCase("tell") || head.equalsIgnoreCase("msg"))
		{
			event.setMessage("/pm" + content);
			
			return;
		}
		
		if (head.equalsIgnoreCase("plugins") || head.equalsIgnoreCase("pl"))
		{
			event.setMessage("/invisibleplugins");
			
			return;
		}
		
		if (head.equalsIgnoreCase("me"))
		{
			event.setMessage("/invisibleme");
			
			return;
		}

		if (head.equalsIgnoreCase("version") ||head.equalsIgnoreCase("ver"))
		{
			event.setMessage("/invisibleversion");
			
			return;
		}
		
		if (head.equalsIgnoreCase("seed"))
		{
			event.setMessage("/invisibleseed");
			
			return;
		}
		
		if (head.equalsIgnoreCase("suicide"))
		{
			event.setMessage("/kill");
			
			return;
		}
		
		if (head.equalsIgnoreCase("kick"))
		{
			event.setMessage("/invisiblekick" + content);
			
			return;
		}
		
		if (head.equalsIgnoreCase("reload"))
		{
			event.setMessage("/invisiblereload" + content);
			
			return;
		}
		
		if (head.equalsIgnoreCase("ban"))
		{
			event.setMessage("/invisibleban" + content);
			
			return;
		}
		
		if (head.equalsIgnoreCase("ban-ip") || head.equalsIgnoreCase("banip"))
		{
			event.setMessage("/invisiblebanip" + content);
			
			return;
		}
    }
	
	public commandState onCommand(Gamer gamer, String label, String[] args)
	{
		if (gamer == null)
		{
			return commandState.Accepted;
		}
		
    	gamer.setUnAFK();
		
		if (label.equalsIgnoreCase("invisibleplugins"))
		{
			gamer.sendMessage("command.plugins", this.main.colors.diverse);
			
			return commandState.Accepted;
		}
		
		if (label.equalsIgnoreCase("invisiblehelp"))
		{
			gamer.sendMessage("command.help", this.main.colors.diverse);
			
			return commandState.Accepted;
		}
		
		if (label.equalsIgnoreCase("invisibleversion"))
		{
			gamer.sendMessage("command.version", this.main.colors.diverse, Turkraft.VERSION);
			
			return commandState.Accepted;
		}
		
		if (label.equalsIgnoreCase("invisibleseed"))
		{
			gamer.sendMessage("command.seed", this.main.colors.diverse, Long.toString(gamer.source.getLocation().getWorld().getSeed()));
			
			return commandState.Accepted;
		}
		
		if (label.equalsIgnoreCase("l"))
		{
			if (args.length < 1)
			{
				gamer.sendMessage("command.language.typemore", this.main.colors.language);
				
				return commandState.Accepted;
			}

	    	if (gamer.information.muted)
	    	{
	    		gamer.sendMessage("chat.no.muted", this.main.colors.admin);
	    		
	    		return commandState.Accepted;
	    	}
	    	
			this.main.broadcastSameLanguage(gamer.information.language,
					Manipulation.chatFormat(this.main.colors.language, gamer.getNameTag(), Manipulation.charsFromArraySeparator(args, " "), false));
			
			return commandState.Accepted;
		}
		
		if (label.equalsIgnoreCase("r"))
		{
			if (!gamer.grad.canPrivateChat)
			{
				return commandState.NoRights;
			}
			
			if (gamer.chatResponsePlayer == null)
			{
				gamer.sendMessage("command.respond.nobody", this.main.colors.message);
				
				return commandState.Accepted;
			}
			
			if (!gamer.chatResponsePlayer.isConnected())
			{
				gamer.sendMessage("general.notconnected", gamer.chatResponsePlayer.getNameColor());
				
				return commandState.Accepted;
			}

			String message = Manipulation.chatFormat(this.main.colors.message, gamer.getNameTag(),  Manipulation.charsFromArraySeparator(args, " "), false);
			
			gamer.chatResponsePlayer.chatResponsePlayer = gamer;
			
			gamer.chatResponsePlayer.sendMessage(message);
			
			gamer.sendMessage(message);
			
			return commandState.Accepted;
		}
		
		if (label.equalsIgnoreCase("m"))
		{
			if (args.length == 0)
			{
				if (!gamer.grad.canMoney)
				{
					return commandState.NoRights;
				}
				
				gamer.sendMessage("command.money.show", this.main.colors.money, Integer.toString(gamer.information.money));
				
				return commandState.Accepted;
			}
			
			if (args.length == 2)
			{
				if (args[0].equalsIgnoreCase("see")) //voir l'argent
				{
					if (!gamer.grad.canSeeMoney)
					{
						return commandState.NoRights;
					}
					
					if (args[1].equalsIgnoreCase(gamer.information.name))
					{
						gamer.sendMessage("command.money.show", this.main.colors.money, Integer.toString(gamer.information.money));
						
						return commandState.Accepted;
					}
					
					Gamer receiver = this.main.getGamer(args[1], true);
					
					if (receiver == null)
					{
						gamer.sendMessage("general.notconnected", this.main.colors.money, args[1]);
						
						return commandState.Accepted;
					}

					gamer.sendMessage("command.money.see", this.main.colors.money, receiver.getNameColor(), Integer.toString(receiver.information.money));

					this.main.log.log("admin", "Money of " + receiver.information.name + " (" + receiver.information.money + " tires) seen by " + gamer.information.name);
					
					return commandState.Accepted;
				}
				
				return commandState.Incorrect;
			}
			
			if (args.length == 3)
			{
				if (args[0].equalsIgnoreCase("take"))
				{
					if (!gamer.grad.canGodMoney)
					{
						return commandState.NoRights;
					}
					
					Gamer receiver = this.main.getGamer(args[1], true);
					
					if (receiver == null)
					{
						gamer.sendMessage("general.notconnected", this.main.colors.money, args[1]);
						
						return commandState.Accepted;
					}
					
					int amount = 0;
					
					try
					{
						amount = Integer.parseInt(args[2]);
					}
					catch (Exception ex)
					{
						return commandState.Incorrect;
					}
					
					if (amount <= 0)
					{
						gamer.sendMessage("command.money.sendmore", this.main.colors.money, args[1]);
						
						return commandState.Accepted;
					}
					
					if (args[1].equalsIgnoreCase(gamer.information.name))
					{
						gamer.information.money -= amount;

						gamer.sendMessage("command.money.solo.godloose", this.main.colors.money, Integer.toString(amount));
						
						return commandState.Accepted;
					}
					
					if (receiver.information.money < amount)
					{
						amount = receiver.information.money;
						
						receiver.information.money = 0;
					}
					else
					{
						receiver.information.money -= amount;
					}
					
					receiver.sendMessage("command.money.godremove", this.main.colors.money, gamer.getNameColor(), Integer.toString(amount), ChatColor.YELLOW.toString());
					
					gamer.sendMessage("command.money.godloose", receiver.getNameColor(), Integer.toString(amount), ChatColor.YELLOW.toString());
					
					return commandState.Accepted;
				}
				
				if (args[0].equalsIgnoreCase("give"))
				{
					if (!gamer.grad.canGodMoney)
					{
						return commandState.NoRights;
					}
					
					Gamer receiver = this.main.getGamer(args[1], true);
					
					if (receiver == null)
					{
						gamer.sendMessage("general.notconnected", this.main.colors.money, args[1]);
						
						return commandState.Accepted;
					}
					
					int amount = 0;
					
					try
					{
						amount = Integer.parseInt(args[2]);
					}
					catch (Exception ex)
					{
						return commandState.Incorrect;
					}
					
					if (amount <= 0)
					{
						gamer.sendMessage("command.money.sendmore", this.main.colors.money, args[1]);
						
						return commandState.Accepted;
					}
					
					if (args[1].equalsIgnoreCase(gamer.information.name))
					{
						gamer.information.money += amount;

						gamer.sendMessage("command.money.solo.godsend", this.main.colors.money, Integer.toString(amount));
						
						return commandState.Accepted;
					}
						
					receiver.information.money += amount;
					
					receiver.sendMessage("command.money.godreceive", this.main.colors.money, gamer.getNameColor(), Integer.toString(amount), ChatColor.YELLOW.toString());
					
					gamer.sendMessage("command.money.godsend", this.main.colors.money, receiver.getNameColor(), Integer.toString(amount), ChatColor.YELLOW.toString());
					
					return commandState.Accepted;
				}
				
				if (args[0].equalsIgnoreCase("send")) //envoyer de l'argent
				{
					if (!gamer.grad.canSendMoney)
					{
						return commandState.NoRights;
					}
					
					Gamer receiver = this.main.getGamer(args[1], true);
					
					if (receiver == null)
					{
						gamer.sendMessage("general.notconnected", this.main.colors.money, args[1]);
						
						return commandState.Accepted;
					}
					
					if (args[1].equalsIgnoreCase(gamer.information.name))
					{
						gamer.sendMessage("command.money.cantyou", this.main.colors.money);
						
						return commandState.Accepted;
					}
					
					int amount = 0;
					
					try
					{
						amount = Integer.parseInt(args[2]);
					}
					catch (Exception ex)
					{
						return commandState.Incorrect;
					}
					
					if (amount <= 0)
					{
						gamer.sendMessage("command.money.sendmore", this.main.colors.money, args[1]);
						
						return commandState.Accepted;
					}
					
					if (gamer.information.money < amount)
					{
						gamer.sendMessage("command.money.only", this.main.colors.money, Integer.toString(gamer.information.money));
						
						return commandState.Accepted;
					}
					
					gamer.information.money -= amount;
					
					receiver.information.money += amount;
					
					receiver.sendMessage("command.money.receive", this.main.colors.money, gamer.getNameColor(), Integer.toString(amount), ChatColor.YELLOW.toString());
					
					gamer.sendMessage("command.money.sent", this.main.colors.money, receiver.getNameColor(), Integer.toString(amount), ChatColor.YELLOW.toString());
					
					return commandState.Accepted;
				}
				
				return commandState.Incorrect;
			}
			
			return commandState.Incorrect;
		}
		
		if (label.equalsIgnoreCase("spawn")) //retourner au point de spawn
		{
			if (!gamer.grad.canTeleportToSpawn)
			{
				return commandState.NoRights;
			}
			
			if (args.length == 0)
			{
				gamer.source.teleport(this.main.configuration.spawnPoint);
				
				gamer.sendMessage("command.spawn.ok", this.main.colors.diverse);

				this.main.broadcastAdminChat(ChatColor.RED + "-> " + gamer.getNameColorPersonnalized(ChatColor.RED) + " teleports to spawn.");
				
				return commandState.Accepted;
			}
			
			if (args.length == 1)
			{
				Gamer toSpawn = this.main.getGamer(args[0], true);

				if (toSpawn == null)
				{
					gamer.sendMessage("general.notconnected", this.main.colors.territory, args[0]);
					
					return commandState.Accepted;
				}
				
				toSpawn.source.teleport(this.main.configuration.spawnPoint);
				
				if (toSpawn.information.id == gamer.information.id)
				{
					gamer.sendMessage("command.spawn.ok", this.main.colors.diverse);
				}
				else
				{
					toSpawn.sendMessage("command.spawn.ok", this.main.colors.diverse);

					gamer.sendMessage("command.spawn.him", this.main.colors.diverse, toSpawn.getNameColor());
				}
				
				this.main.broadcastAdminChat(ChatColor.RED + "-> " + gamer.getNameColorPersonnalized(ChatColor.RED) + " teleports " + toSpawn.getNameColorPersonnalized(ChatColor.RED) + " to spawn.");
				
				return commandState.Accepted;
			}
			
			return commandState.Incorrect;
		}
		
		if (label.equalsIgnoreCase("setspawn")) //definir le point de spawn des joueurs
		{
			if (!gamer.grad.canChangeSpawn)
			{
				return commandState.NoRights;
			}
			
			if (gamer.stockedLeftClickLocation == null)
			{
				gamer.sendMessage("block.pleaseselect1", this.main.colors.diverse);
				
				return commandState.Accepted;
			}
			
			this.main.configuration.spawnPoint = gamer.stockedLeftClickLocation;
			
			gamer.sendMessage("command.setspawn.changed", this.main.colors.diverse,
					Integer.toString(this.main.configuration.spawnPoint.getBlockX()),
					Integer.toString(this.main.configuration.spawnPoint.getBlockY()),
					Integer.toString(this.main.configuration.spawnPoint.getBlockZ()));
			
			return commandState.Accepted;
		}
		
		if (label.equalsIgnoreCase("appear")) //faire apparaitre une entite
		{
			if (!gamer.grad.canSpawnElement)
			{
				return commandState.NoRights;
			}
			
			if (gamer.stockedLeftClickLocation == null)
			{
				gamer.sendMessage("block.pleaseselect1", this.main.colors.diverse);
				
				return commandState.Accepted;
			}
			
			if (args.length != 2)
			{
				return commandState.Incorrect;
			}
			
			EntityType entity = EntityType.fromName(args[0].toUpperCase());
			
			if (entity == null)
			{
				return commandState.Error;
			}

			int amount = 0;
			
			try
			{
				amount = Integer.parseInt(args[1]);
			}
			catch (Exception ex)
			{
				return commandState.Incorrect;
			}
			
			if (amount <= 0)
			{
				gamer.sendMessage("command.appear.morezero", this.main.colors.diverse);
				
				return commandState.Accepted;
			}
			
			Location spawnLocation = new Location(gamer.stockedLeftClickLocation.getWorld(),
					gamer.stockedLeftClickLocation.getBlockX(),
					gamer.stockedLeftClickLocation.getY() + 1,
					gamer.stockedLeftClickLocation.getZ());
			
			for (int i = 0; i < amount; i++)
			{
				gamer.stockedLeftClickLocation.getWorld().spawnEntity(spawnLocation, entity);
			}
			
			gamer.sendMessage("command.appear.ok", this.main.colors.diverse, Integer.toString(amount),
					this.main.getText(gamer.information.language, "mobs." + entity.toString().toLowerCase()));
			
			return commandState.Accepted;
		}
		
		if (label.equalsIgnoreCase("top"))
		{
			if (!gamer.grad.canTeleportTop)
			{
				return commandState.NoRights;
			}
			
			gamer.source.teleport(gamer.source.getLocation().getWorld().getHighestBlockAt(gamer.source.getLocation()).getLocation());
			
			gamer.sendMessage("command.top.ok", this.main.colors.diverse,
					Integer.toString(gamer.source.getLocation().getBlockX()),
					Integer.toString(gamer.source.getLocation().getBlockY()),
					Integer.toString(gamer.source.getLocation().getBlockZ()));
			
			return commandState.Accepted;
		}
		
		if (label.equalsIgnoreCase("bed"))
		{
			if (!gamer.grad.canTeleportBed)
			{
				return commandState.NoRights;
			}
			
			if (gamer.source.getBedSpawnLocation() == null)
			{
				gamer.sendMessage("command.bed.nobed", this.main.colors.diverse);
				
				return commandState.Accepted;
			}
			
			gamer.source.teleport(gamer.source.getBedSpawnLocation());
			
			gamer.sendMessage("command.bed.ok", this.main.colors.diverse,
					Integer.toString(gamer.source.getLocation().getBlockX()),
					Integer.toString(gamer.source.getLocation().getBlockY()),
					Integer.toString(gamer.source.getLocation().getBlockZ()));
			
			return commandState.Accepted;
		}
		
		if (label.equalsIgnoreCase("a"))
		{
			if (!gamer.grad.canAccessAdminChat)
			{
				return commandState.NoRights;
			}
			
			if (args.length <= 0)
			{
				return commandState.Incorrect;
			}
			
			this.main.broadcastAdminChat(Manipulation.chatFormat(this.main.colors.admin, gamer.getNameTag(), Manipulation.charsFromArraySeparator(args, " "), false));
			
			return commandState.Accepted;
		}
		
		if (label.equalsIgnoreCase("pm")) //message prive
		{
			if (!gamer.grad.canPrivateChat)
			{
				return commandState.NoRights;
			}
			
			if (args.length < 2)
			{
				gamer.sendMessage("command.pm.nothing", this.main.colors.message);
				
				return commandState.Accepted;
			}

			Gamer receiver = this.main.getGamer(args[0], true);

			if (receiver == null)
			{
				gamer.sendMessage("general.notconnected", this.main.colors.message, args[0]);
				
				return commandState.Accepted;
			}
			
			if (args[0].equalsIgnoreCase(gamer.information.name))
			{
				gamer.sendMessage("command.pm.noneed", this.main.colors.message, args[0]);
				
				return commandState.Accepted;
			}
			
			String message = Manipulation.chatFormat(this.main.colors.message, gamer.getNameTag(), Manipulation.charsFromArraySeparator(1, args, " "), false);
			
			gamer.chatResponsePlayer = receiver; //on enregistre pour faire /r sans attendre que la personne reponde
			
			receiver.chatResponsePlayer = gamer; //on enregistre celui qui a envoye le dernier message pour utiliser la commande /r
			
			receiver.sendMessage(message);
			
			gamer.sendMessage(message);
			
			return commandState.Accepted;
		}
		
		if (label.equalsIgnoreCase("location"))
		{
			if (!gamer.grad.canSeeLocations)
			{
				return commandState.NoRights;
			}
			
			if (args.length != 1)
			{
				return commandState.Incorrect;
			}
			
			if (args[0].equalsIgnoreCase(gamer.information.name))
			{
				gamer.sendMessage("command.location.yourposition", this.main.colors.diverse, 
						Integer.toString(gamer.source.getLocation().getBlockX()),
						Integer.toString(gamer.source.getLocation().getBlockY()),
						Integer.toString(gamer.source.getLocation().getBlockZ()));
				
				return commandState.Accepted;
			}
			
			Gamer locatedGamer = this.main.getGamer(args[0], true);
			
			if (locatedGamer == null)
			{
				gamer.sendMessage("general.notconnected", this.main.colors.diverse, args[0]);
				
				return commandState.Accepted;
			}
			
			gamer.sendMessage("command.location.hisposition", this.main.colors.diverse, 
					locatedGamer.getNameColor(),
					Integer.toString(locatedGamer.source.getLocation().getBlockX()),
					Integer.toString(locatedGamer.source.getLocation().getBlockY()),
					Integer.toString(locatedGamer.source.getLocation().getBlockZ()));
			
			return commandState.Accepted;
		}
		
		if (label.equalsIgnoreCase("extermine"))
		{
			if (!gamer.grad.canKill)
			{
				return commandState.NoRights;
			}
			
			if (args.length != 1)
			{
				return commandState.Incorrect;
			}

			Gamer tokillGamer = this.main.getGamer(args[0], true);

			if (tokillGamer == null)
			{
				gamer.sendMessage("general.notconnected", this.main.colors.diverse, args[0]);
				
				return commandState.Accepted;
			}
			
			tokillGamer.source.damage(tokillGamer.source.getHealth());
			
			return commandState.Accepted;
		}

		if (label.equalsIgnoreCase("sethealth"))
		{
			NameArgCommandInt resultCommand = this.effectuateNameArgCommand(gamer, gamer.grad.canSetFood, args, 0, 20);
			
			switch (resultCommand.result)
			{
				case Ok:
					Gamer targetGamer = this.main.getGamer(args[0], true);
					targetGamer.source.setHealth(resultCommand.value);
					targetGamer.sendMessage("command.sethealth.you", this.main.colors.diverse,
							Integer.toString(resultCommand.value));
					gamer.sendMessage("command.sethealth.him", this.main.colors.diverse,
							targetGamer.getNameColor(), Integer.toString(resultCommand.value));
					return commandState.Accepted;
					
				case You:
					gamer.source.setHealth(resultCommand.value);
					gamer.sendMessage("command.sethealth.you", this.main.colors.diverse, Integer.toString(resultCommand.value));
					return commandState.Accepted;
					
				case NotConnected:
					gamer.sendMessage("general.notconnected", this.main.colors.diverse, args[0]);
					return commandState.Accepted;
					
				case NoRights:
					return commandState.NoRights;
					
				case Incorrect:
					return commandState.Incorrect;
			}
			
			return commandState.Error;
		}

		if (label.equalsIgnoreCase("setfood"))
		{
			NameArgCommandInt resultCommand = this.effectuateNameArgCommand(gamer, gamer.grad.canSetFood, args, 0, 20);
			
			switch (resultCommand.result)
			{
				case Ok:
					Gamer targetGamer = this.main.getGamer(args[0], true);
					targetGamer.source.setFoodLevel(resultCommand.value);
					targetGamer.sendMessage("command.setfood.you", this.main.colors.diverse,
							Integer.toString(resultCommand.value));
					gamer.sendMessage("command.setfood.him", this.main.colors.diverse,
							targetGamer.getNameColor(), Integer.toString(resultCommand.value));
					return commandState.Accepted;
					
				case You:
					gamer.source.setFoodLevel(resultCommand.value);
					gamer.sendMessage("command.setfood.you", this.main.colors.diverse, Integer.toString(resultCommand.value));
					return commandState.Accepted;
					
				case NotConnected:
					gamer.sendMessage("general.notconnected", this.main.colors.diverse, args[0]);
					return commandState.Accepted;
					
				case NoRights:
					return commandState.NoRights;
					
				case Incorrect:
					return commandState.Incorrect;
			}
			
			return commandState.Error;
		}
		
		if (label.equalsIgnoreCase("setlevel"))
		{
			NameArgCommandInt resultCommand = this.effectuateNameArgCommand(gamer, gamer.grad.canSetLevel, args, 0, 0);
			
			switch (resultCommand.result)
			{
				case Ok:
					Gamer targetGamer = this.main.getGamer(args[0], true);
					targetGamer.source.setLevel(resultCommand.value);
					targetGamer.sendMessage("command.setlevel.you", this.main.colors.diverse,
							Integer.toString(resultCommand.value));
					gamer.sendMessage("command.setlevel.him", this.main.colors.diverse,
							targetGamer.getNameColor(), Integer.toString(resultCommand.value));
					return commandState.Accepted;
					
				case You:
					gamer.source.setLevel(resultCommand.value);
					gamer.sendMessage("command.setlevel.you", this.main.colors.diverse, Integer.toString(resultCommand.value));
					return commandState.Accepted;
					
				case NotConnected:
					gamer.sendMessage("general.notconnected", this.main.colors.diverse, args[0]);
					return commandState.Accepted;
					
				case NoRights:
					return commandState.NoRights;
					
				case Incorrect:
					return commandState.Incorrect;
			}
			
			return commandState.Error;
		}
		
		if (label.equalsIgnoreCase("givexp"))
		{
			NameArgCommandInt resultCommand = this.effectuateNameArgCommand(gamer, gamer.grad.canGiveExp, args, 1, 0);
			
			switch (resultCommand.result)
			{
				case Ok:
					Gamer targetGamer = this.main.getGamer(args[0], true);
					targetGamer.source.giveExp(resultCommand.value);
					targetGamer.sendMessage("command.givexp.you", this.main.colors.diverse,
							Integer.toString(resultCommand.value));
					gamer.sendMessage("command.givexp.him", this.main.colors.diverse,
							targetGamer.getNameColor(), Integer.toString(resultCommand.value));
					return commandState.Accepted;
					
				case You:
					gamer.source.giveExp(resultCommand.value);
					gamer.sendMessage("command.givexp.you", this.main.colors.diverse, Integer.toString(resultCommand.value));
					return commandState.Accepted;
					
				case NotConnected:
					gamer.sendMessage("general.notconnected", this.main.colors.diverse, args[0]);
					return commandState.Accepted;
					
				case NoRights:
					return commandState.NoRights;
					
				case Incorrect:
					return commandState.Incorrect;
			}
			
			return commandState.Error;
		}
		
		if (label.equalsIgnoreCase("back"))
		{
			if (!gamer.grad.canGotoDeath)
			{
				return commandState.NoRights;
			}
			
			if (args.length == 0)
			{
				if (gamer.deathLocation == null)
				{
					gamer.sendMessage("command.back.nodeath", this.main.colors.diverse);
					
					return commandState.Accepted;
				}
				
				gamer.source.teleport(gamer.deathLocation);
	
				gamer.sendMessage("command.back.ok", this.main.colors.diverse,
						Integer.toString(gamer.deathLocation.getBlockX()),
						Integer.toString(gamer.deathLocation.getBlockY()),
						Integer.toString(gamer.deathLocation.getBlockZ()));
				
				this.main.broadcastAdminChat(ChatColor.RED + "-> " + gamer.getNameColorPersonnalized(ChatColor.RED) + " teleports to last death point.");
				
				return commandState.Accepted;
			}
			
			if (args.length == 1)
			{
				Gamer toBack = this.main.getGamer(args[0], true);

				if (toBack == null)
				{
					gamer.sendMessage("general.notconnected", this.main.colors.territory, args[0]);
					
					return commandState.Accepted;
				}
				
				if (toBack.deathLocation == null)
				{
					gamer.sendMessage("command.back.him.nodeath", this.main.colors.diverse);
					
					return commandState.Accepted;
				}
				
				toBack.source.teleport(toBack.deathLocation);
	
				toBack.sendMessage("command.back.ok", this.main.colors.diverse,
						Integer.toString(toBack.deathLocation.getBlockX()),
						Integer.toString(toBack.deathLocation.getBlockY()),
						Integer.toString(toBack.deathLocation.getBlockZ()));

				gamer.sendMessage("command.back.him.msg", this.main.colors.diverse);
				
				this.main.broadcastAdminChat(ChatColor.RED + "-> " + gamer.getNameColorPersonnalized(ChatColor.RED) + " teleports " + toBack.getNameColorPersonnalized(ChatColor.RED) + " to last death point.");
				
				return commandState.Accepted;
			}
			
			return commandState.Incorrect;
		}
		
		if (label.equalsIgnoreCase("plot"))
		{
			if (!gamer.grad.canTerritory)
			{
				return commandState.NoRights;
			}

			if (args.length == 0)
			{
				if (gamer.stockedLeftClickLocation == null)
				{
					gamer.sendMessage("block.pleaseselect1", this.main.colors.territory);
					
					return commandState.Accepted;
				}
				
				Terrain getter = this.main.getTerrainFromLocation(gamer.stockedLeftClickLocation);
				
				if (getter == null)
				{
					gamer.sendMessage("command.terrain.notselected", this.main.colors.territory);
					
					return commandState.Accepted;
				}
				
				if (getter.pricable.isToSell())
				{
					if (!getter.owner.hasOwner())
					{
						gamer.sendMessage("command.terrain.info.tosell.noowner", this.main.colors.territory,
								Integer.toString(getter.plot.sizeX), Integer.toString(getter.plot.sizeZ), Integer.toString(getter.pricable.price));
						
						return commandState.Accepted;
					}
					
					if (getter.owner.isOwner(gamer))
					{
						gamer.sendMessage("command.terrain.info.tosell.you", this.main.colors.territory,
								Integer.toString(getter.plot.sizeX), Integer.toString(getter.plot.sizeZ), Integer.toString(getter.pricable.price));
						
						return commandState.Accepted;
					}

					gamer.sendMessage("command.terrain.info.tosell.him", this.main.colors.territory, getter.owner.owner,
							Integer.toString(getter.plot.sizeX), Integer.toString(getter.plot.sizeZ), Integer.toString(getter.pricable.price));
					
					return commandState.Accepted;
				}
				
				if (!getter.owner.hasOwner())
				{
					gamer.sendMessage("command.terrain.info.notsell.noowner", this.main.colors.territory,
							Integer.toString(getter.plot.sizeX), Integer.toString(getter.plot.sizeZ));
					
					return commandState.Accepted;
				}
				
				if (getter.owner.isOwner(gamer))
				{
					gamer.sendMessage("command.terrain.info.notsell.you", this.main.colors.territory,
							Integer.toString(getter.plot.sizeX), Integer.toString(getter.plot.sizeZ));
					
					return commandState.Accepted;
				}

				gamer.sendMessage("command.terrain.info.notsell.him", this.main.colors.territory, getter.owner.owner,
						Integer.toString(getter.plot.sizeX), Integer.toString(getter.plot.sizeZ));

				return commandState.Accepted;
			}
			
			if (args.length == 1 && !args[0].equalsIgnoreCase("create")) //on ne traite pas le arg "create" ici
			{
				if (args[0].equalsIgnoreCase("price"))
				{
					if (gamer.stockedLeftClickLocation == null || gamer.stockedRightClickLocation == null)
					{
						gamer.sendMessage("block.select.pleaseboth", this.main.colors.territory);
						
						return commandState.Accepted;
					}
					
					Terrain terrainPrice = new Terrain(null, new Plot(gamer.stockedLeftClickLocation, gamer.stockedRightClickLocation), true);

					gamer.sendMessage("command.plot.price", this.main.colors.territory, Integer.toString(terrainPrice.getEstimatedPrice()),
							Integer.toString(terrainPrice.plot.sizeX), Integer.toString(terrainPrice.plot.sizeZ));
					
					terrainPrice.dispose();
					
					return commandState.Accepted;
				}
				
				if (args[0].equalsIgnoreCase("buy"))
				{
					if (gamer.stockedLeftClickLocation == null)
					{
						gamer.sendMessage("block.pleaseselect1", this.main.colors.territory);
						
						return commandState.Accepted;
					}
					
					Terrain getter = this.main.getTerrainFromLocation(gamer.stockedLeftClickLocation);
					
					if (getter == null)
					{
						gamer.sendMessage("command.terrain.notselected", this.main.colors.territory);
						
						return commandState.Accepted;
					}
					
					if (!getter.pricable.isToSell())
					{
						gamer.sendMessage("command.terrain.buy.notsell", this.main.colors.territory);
						
						return commandState.Accepted;
					}
					
					if (getter.owner.isOwner(gamer))
					{
						gamer.sendMessage("command.terrain.buy.alreadybuyed", this.main.colors.territory);
						
						return commandState.Accepted;
					}
					
					if (gamer.information.money < getter.pricable.price)
					{
						gamer.sendMessage("command.terrain.buy.nomoney", this.main.colors.territory, Integer.toString(getter.pricable.price));
						
						return commandState.Accepted;
					}
					
					if (getter.owner.hasOwner())
					{
						Gamer ownerGamer = this.main.getGamer(getter.owner.owner, true);
						
						if (ownerGamer != null)
						{
							ownerGamer.sendMessage("command.terrain.selled", this.main.colors.territory, gamer.getNameColor(),
									Integer.toString(getter.pricable.price));
							
							ownerGamer.information.money += getter.pricable.price;
						}
						else
						{
							this.main.database.addPlayerMoney(getter.owner.owner, getter.pricable.price);
						}
					}
					
					getter.whitisble.whiteGamers.clear();
					
					gamer.information.money -= getter.pricable.price;
					
					getter.owner = new Ownable(gamer.information.name);

					gamer.sendMessage("command.terrain.buy.ok", this.main.colors.territory, Integer.toString(getter.pricable.price));
					
					getter.pricable.price = 0;
					
					getter.hasChanged = true;

					return commandState.Accepted;
				}
				
				if (args[0].equalsIgnoreCase("list"))
				{
					if (gamer.stockedLeftClickLocation == null)
					{
						gamer.sendMessage("block.pleaseselect1", this.main.colors.territory);
						
						return commandState.Accepted;
					}
	
					Terrain getter = this.main.getTerrainFromLocation(gamer.stockedLeftClickLocation);
					
					if (getter == null)
					{
						gamer.sendMessage("command.terrain.notselected", this.main.colors.territory);
						
						return commandState.Accepted;
					}
					
					if (!getter.owner.isOwner(gamer.information.name) && !getter.whitisble.canAccess(gamer) && !gamer.grad.canGodTerritories)
					{
						gamer.sendMessage("command.terrain.notowner", this.main.colors.territory);
						
						return commandState.Accepted;
					}
					
					gamer.sendMessage("command.terrain.list", this.main.colors.territory, getter.whitisble.getListString(getter.owner));
					
					return commandState.Accepted;
				}
				
				if (args[0].equalsIgnoreCase("delete"))
				{
					if (gamer.stockedLeftClickLocation == null)
					{
						gamer.sendMessage("block.pleaseselect1", this.main.colors.territory);
						
						return commandState.Accepted;
					}
					
					Terrain getter = this.main.getTerrainFromLocation(gamer.stockedLeftClickLocation);
					
					if (getter == null)
					{
						gamer.sendMessage("command.terrain.notselected", this.main.colors.territory);
						
						return commandState.Accepted;
					}
					
					if (!getter.owner.isOwner(gamer) && !gamer.grad.canGodTerritories)
					{
						gamer.sendMessage("command.terrain.notowner", this.main.colors.territory);
						
						return commandState.Accepted;
					}
					
					if (!getter.breakable && !gamer.grad.canGodTerritories)
					{
						gamer.sendMessage("command.terrain.delete.notbreak", this.main.colors.territory);
						
						return commandState.Accepted;
					}

					if (getter.inDatabase)
					{
						this.main.deleteTerrains.add(getter.id);
					}
					
					this.main.terrains.remove(getter);
					
					gamer.sendMessage("command.terrain.delete.ok", this.main.colors.territory);
					
					return commandState.Accepted;
				}
				
				return commandState.Incorrect;
			}
			
			if (args.length >= 1)
			{
				if (args[0].equalsIgnoreCase("remove")) //retirer un gars de sa liste
				{
					if (args.length <= 1)
					{
						return commandState.Incorrect;
					}
					
					if (gamer.stockedLeftClickLocation == null)
					{
						gamer.sendMessage("block.pleaseselect1", this.main.colors.territory);
						
						return commandState.Accepted;
					}

					Terrain getter = this.main.getTerrainFromLocation(gamer.stockedLeftClickLocation);
					
					if (getter == null)
					{
						gamer.sendMessage("command.terrain.notselected", this.main.colors.territory);
						
						return commandState.Accepted;
					}
					
					if (!getter.owner.isOwner(gamer.information.name) && !gamer.grad.canGodTerritories)
					{
						gamer.sendMessage("command.terrain.notowner", this.main.colors.territory);
						
						return commandState.Accepted;
					}
					
					if (args.length - 1 > getter.whitisble.whiteGamers.size())
					{
						return commandState.Incorrect;
					}
					
					int removeCount = 0;
					
					for (int i = 1; i < args.length; i++)
					{
						if (getter.whitisble.remove(args[i]))
						{
							removeCount++;
						}
					}
					
					if (removeCount > 0)
					{
						getter.hasChanged = true;
					}

					gamer.sendMessage("command.terrain.list.removed.ok", this.main.colors.territory, Integer.toString(removeCount));
					
					return commandState.Accepted;
				}
				
				if (args[0].equalsIgnoreCase("add")) //ajouter un pote a sa liste
				{
					if (args.length <= 1)
					{
						return commandState.Incorrect;
					}
					
					if (gamer.stockedLeftClickLocation == null)
					{
						gamer.sendMessage("block.pleaseselect1", this.main.colors.territory);
						
						return commandState.Accepted;
					}

					Terrain getter = this.main.getTerrainFromLocation(gamer.stockedLeftClickLocation);
					
					if (getter == null)
					{
						gamer.sendMessage("command.terrain.notselected", this.main.colors.territory);
						
						return commandState.Accepted;
					}
					
					if (!getter.owner.isOwner(gamer.information.name) && !gamer.grad.canGodTerritories)
					{
						gamer.sendMessage("command.terrain.notowner", this.main.colors.territory);
						
						return commandState.Accepted;
					}
					
					if (getter.whitisble.whiteGamers.size() + (args.length - 1) > this.main.configuration.whitelistMax)
					{
						gamer.sendMessage("command.terrain.list.add.toomuchpeople", this.main.colors.territory);
						
						return commandState.Accepted;
					}
					
					int addCount = 0;
					
					for (int i = 1; i < args.length; i++) //on verifie que les nom entre vont de 3 caracteres a 12 chacun
					{
						if (!this.main.isNameOk(args[i]))
						{
							return commandState.Incorrect;
						}
						
						if (getter.whitisble.canAccess(args[i]) || getter.owner.isOwner(args[i]))
						{
							continue;
						}
						
						getter.whitisble.whiteGamers.add(args[i]);
						
						addCount++;
					}
					
					if (addCount > 0)
					{
						getter.hasChanged = true;
					}

					gamer.sendMessage("command.terrain.list.add.ok", this.main.colors.territory, Integer.toString(addCount));
					
					return commandState.Accepted;
				}
				
				if (args[0].equalsIgnoreCase("set"))
				{
					if (args.length != 2)
					{
						return commandState.Incorrect;
					}
					
					if (gamer.stockedLeftClickLocation == null)
					{
						gamer.sendMessage("block.pleaseselect1", this.main.colors.territory);
						
						return commandState.Accepted;
					}
					
					int price = 0;
					
					try
					{
						price = Integer.parseInt(args[1]);
					}
					catch (Exception ex)
					{
						return commandState.Incorrect;
					}
					
					if (price < 0)
					{
						return commandState.Incorrect;
					}
					
					Terrain getter = this.main.getTerrainFromLocation(gamer.stockedLeftClickLocation);
					
					if (getter == null)
					{
						gamer.sendMessage("command.terrain.notselected", this.main.colors.territory);
						
						return commandState.Accepted;
					}

					if (!getter.owner.isOwner(gamer.information.name) && !gamer.grad.canGodTerritories)
					{
						gamer.sendMessage("command.terrain.notowner", this.main.colors.territory);
						
						return commandState.Accepted;
					}
					
					if (price != getter.pricable.price)
					{
						getter.hasChanged = true;
					}

					getter.pricable.price = price;

					if (price == 0)
					{
						gamer.sendMessage("command.terrain.set.zero", this.main.colors.territory);
					}
					else
					{
						gamer.sendMessage("command.terrain.list.set.price", this.main.colors.territory, Integer.toString(price));
					}
				
					return commandState.Accepted;
				}
				
				if (args[0].equalsIgnoreCase("create"))
				{
					if (gamer.stockedLeftClickLocation == null || gamer.stockedRightClickLocation == null)
					{
						gamer.sendMessage("block.select.pleaseboth", this.main.colors.territory);
						
						return commandState.Accepted;
					}
					
					Plot addPlot = new Plot(gamer.stockedLeftClickLocation, gamer.stockedRightClickLocation);
					
					if ((addPlot.sizeX < this.main.configuration.minimumLengthTerrain || addPlot.sizeZ < this.main.configuration.minimumLengthTerrain)
							&& !gamer.grad.canGodTerritories)
					{
						gamer.sendMessage("command.terrain.create.toosmall", this.main.colors.territory,
								Integer.toString(this.main.configuration.minimumLengthTerrain));
						
						return commandState.Accepted;
					}
					
					if ((addPlot.sizeX > this.main.configuration.maximumLengthTerrain || addPlot.sizeZ > this.main.configuration.maximumLengthTerrain)
							&& !gamer.grad.canGodTerritories)
					{
						gamer.sendMessage("command.terrain.create.toobig", this.main.colors.territory,
								Integer.toString(this.main.configuration.maximumLengthTerrain));
						
						return commandState.Accepted;
					}
					
					if (this.main.collisionWithPlots(addPlot) != Turkraft.collisionType.NoCollision)
					{
						gamer.sendMessage("command.terrain.touchesplot", this.main.colors.territory);
						
						return commandState.Accepted;
					}
					
					if (args.length == 2)
					{
						if (args[1].equalsIgnoreCase("not")) //peut creer des terrains achetable mais non detruisibles
						{
							if (!gamer.grad.canGodTerritories)
							{
								return commandState.NoRights;
							}
							
							this.main.terrains.add(new Terrain(new Ownable(), addPlot, false));

							gamer.sendMessage("command.terrain.created.ok.unbreak", this.main.colors.territory);
							
							return commandState.Accepted;
						}
						
						return commandState.Incorrect;
					}
					
					Terrain newCreation = new Terrain(new Ownable(gamer.information.name), addPlot, true);
					
					int estimatedPrice = newCreation.getEstimatedPrice();
					
					if (gamer.information.money < estimatedPrice)
					{
						gamer.sendMessage("command.terrain.create.nomoney", this.main.colors.territory,
								Integer.toString(estimatedPrice));
						
						return commandState.Accepted;
					}
					
					gamer.information.money -= estimatedPrice;
					
					this.main.terrains.add(newCreation);

					gamer.sendMessage("command.terrain.create.ok", this.main.colors.territory, 
							Integer.toString(addPlot.sizeX), Integer.toString(addPlot.sizeZ), Integer.toString(estimatedPrice));
					
					return commandState.Accepted;
				}
				
				return commandState.Incorrect;
			}
			
			return commandState.Incorrect;
		}
		
		if (label.equalsIgnoreCase("unbreak"))
		{
			if (!gamer.grad.canLock)
			{
				return commandState.NoRights;
			}
			
			if (args.length == 0)
			{
				if (gamer.stockedLeftClickLocation == null)
				{
					gamer.sendMessage("block.pleaseselect1", this.main.colors.territory);
					
					return commandState.Accepted;
				}
				
				Indestructible getter = this.main.getIndestructibleFromLocation(gamer.stockedLeftClickLocation);
				
				if (getter == null)
				{
					gamer.sendMessage("command.unbreak.notselected", this.main.colors.territory);
					
					return commandState.Accepted;
				}

				gamer.sendMessage("command.unbreak.info", this.main.colors.territory, getter.creator.owner,
						Integer.toString(getter.plot.sizeX), Integer.toString(getter.plot.sizeZ));

				return commandState.Accepted;
			}
			
			if (args.length == 1)
			{
				if (args[0].equalsIgnoreCase("create"))
				{
					if (gamer.stockedLeftClickLocation == null || gamer.stockedRightClickLocation == null)
					{
						gamer.sendMessage("block.select.pleaseboth", this.main.colors.territory);
						
						return commandState.Accepted;
					}
					
					Plot addPlot = new Plot(gamer.stockedLeftClickLocation, gamer.stockedRightClickLocation);
					
					if (this.main.collisionWithPlots(addPlot) != Turkraft.collisionType.NoCollision)
					{
						gamer.sendMessage("command.unbreak.touchesplot", this.main.colors.territory);
						
						return commandState.Accepted;
					}
					
					this.main.indestructibles.add(new Indestructible(new Ownable(gamer.information.name), addPlot));
					
					gamer.sendMessage("command.unbreak.ok", this.main.colors.territory, Integer.toString(addPlot.sizeX), Integer.toString(addPlot.sizeZ));
					
					return commandState.Accepted;
				}
				
				if (args[0].equalsIgnoreCase("delete"))
				{
					if (gamer.stockedLeftClickLocation == null)
					{
						gamer.sendMessage("block.pleaseselect1", this.main.colors.territory);
						
						return commandState.Accepted;
					}
					
					Indestructible getter = this.main.getIndestructibleFromLocation(gamer.stockedLeftClickLocation);
					
					if (getter == null)
					{
						gamer.sendMessage("command.unbreak.notselected", this.main.colors.territory);
						
						return commandState.Accepted;
					}
					
					if (getter.inDatabase)
					{
						this.main.deleteIndestructibles.add(getter.id);
					}
					
					this.main.indestructibles.remove(getter);
					
					gamer.sendMessage("command.unbreak.delete.ok", this.main.colors.territory);
					
					return commandState.Accepted;
				}
			}
			
			return commandState.Incorrect;
		}
		
		if (label.equalsIgnoreCase("to"))
		{
			if (!gamer.grad.canTeleport)
			{
				return commandState.NoRights;
			}
			
			if (args.length != 1)
			{
				return commandState.Incorrect;
			}
			
			if (args[0].equalsIgnoreCase(gamer.information.name))
			{
				return commandState.Incorrect;
			}
			
			Gamer teleportGamer = this.main.getGamer(args[0], true);

			if (teleportGamer == null)
			{
				gamer.sendMessage("general.notconnected", this.main.colors.territory, args[0]);
				
				return commandState.Accepted;
			}
			
			this.main.broadcastAdminChat(ChatColor.RED + "-> " + gamer.getNameColorPersonnalized(ChatColor.RED) + " teleports to " + teleportGamer.getNameColorPersonnalized(ChatColor.RED) + ".");
			
			gamer.source.teleport(teleportGamer.source);
			
			gamer.sendMessage("command.to.yourarenext", this.main.colors.territory, teleportGamer.getNameColor());
			
			return commandState.Accepted;
		}
		
		if (label.equalsIgnoreCase("g"))
		{
			if (!gamer.grad.canGroup)
			{
				return commandState.NoRights;
			}
			
			if (!gamer.inGroup())
			{
				gamer.sendMessage("command.g.nogroup", this.main.colors.group);
				
				return commandState.Accepted;
			}
			
			if (args.length <= 0)
			{
				return commandState.Incorrect;
			}
			
			this.main.getGroup(gamer.groupid).broadcast(Manipulation.chatFormat(this.main.colors.group,
					gamer.getNameTag(), Manipulation.charsFromArraySeparator(args, " "), false));
			
			return commandState.Accepted;
		}
		
		if (label.equalsIgnoreCase("group"))
		{
			if (!gamer.grad.canGroup)
			{
				return commandState.NoRights;
			}
			
			if (args.length == 0)
			{
				if (!gamer.inGroup())
				{
					gamer.sendMessage("command.g.nogroup", this.main.colors.group);
					
					return commandState.Accepted;
				}
				
				gamer.sendMessage("command.group.list", this.main.colors.group,
						this.main.getGroup(gamer.groupid).getList(), ChatColor.GREEN.toString());
				
				return commandState.Accepted;
			}
			
			if (args.length == 2)
			{
				if (args[0].equalsIgnoreCase("eject"))
				{
					if (!gamer.inGroup())
					{
						gamer.sendMessage("command.g.nogroup", this.main.colors.group);
						
						return commandState.Accepted;
					}
					
					if (!gamer.getGroup().isHost(gamer))
					{
						gamer.sendMessage("command.group.invite.onlyhost", this.main.colors.group);
						
						return commandState.Accepted;
					}
					
					if (args[1].equalsIgnoreCase(gamer.information.name))
					{
						gamer.sendMessage("command.group.eject.youarehost", this.main.colors.group);
						
						return commandState.Accepted;
					}
					
					if (!gamer.getGroup().isIn(args[1]))
					{
						gamer.sendMessage("command.group.eject.notingroup", this.main.colors.group, args[1], ChatColor.GREEN.toString());
						
						return commandState.Accepted;
					}
					
					Gamer toEjectGamer = this.main.getGamer(args[1], true);
					
					gamer.sendMessage("command.group.eject.ok", this.main.colors.group, 
							toEjectGamer.getNameColor(), ChatColor.GREEN.toString());

					for (int i = 0; i < gamer.getGroup().members.size(); i++)
					{
						gamer.getGroup().members.get(i).sendMessage("command.group.eject.ok", this.main.colors.group,
								toEjectGamer.getNameColor(), ChatColor.GREEN.toString());
					}
					
					gamer.getGroup().remove(toEjectGamer);
					
					return commandState.Accepted;
				}
				
				if (args[0].equalsIgnoreCase("invite"))
				{
					if (!gamer.inGroup())
					{
						gamer.sendMessage("command.g.nogroup", this.main.colors.group);
						
						return commandState.Accepted;
					}
					
					if (!this.main.getGroup(gamer.groupid).isHost(gamer))
					{
						gamer.sendMessage("command.group.invite.onlyhost", this.main.colors.group);
						
						return commandState.Accepted;
					}
					
					if (args[1].equalsIgnoreCase(gamer.information.name))
					{
						gamer.sendMessage("command.group.invite.youareinalready", this.main.colors.group);
						
						return commandState.Accepted;
					}
					
					if (this.main.getGroup(gamer.groupid).count() >= this.main.configuration.maximumGroupGamer)
					{
						gamer.sendMessage("command.group.invite.alreadymax", this.main.colors.group);
						
						return commandState.Accepted;
					}
					
					Gamer inviteGamer = this.main.getGamer(args[1], true);
					
					if (inviteGamer == null)
					{
						gamer.sendMessage("general.notconnected", this.main.colors.group, args[1]);
						
						return commandState.Accepted;
					}
					
					if (inviteGamer.inGroup())
					{
						gamer.sendMessage("command.group.invite.already", this.main.colors.group,
								inviteGamer.getNameColor(), ChatColor.GREEN.toString());
						
						return commandState.Accepted;
					}
					
					inviteGamer.lastGroupInvite = gamer.groupid;
					
					inviteGamer.sendMessage("command.invite.message", this.main.colors.group,
							gamer.getNameColor(), ChatColor.GREEN.toString());
					
					gamer.sendMessage("command.group.invite.sent", this.main.colors.group,
							inviteGamer.getNameColor(), ChatColor.GREEN.toString());
					
					return commandState.Accepted;
				}
			}
			
			if (args.length == 1)
			{
				if (args[0].equalsIgnoreCase("refuse"))
				{
					if (gamer.lastGroupInvite == 0)
					{
						gamer.sendMessage("command.group.accept.nobodyinvite", this.main.colors.group);
						
						return commandState.Accepted;
					}
					
					Group joinGroup = this.main.getGroup(gamer.lastGroupInvite);
					
					if (joinGroup == null)
					{
						gamer.lastGroupInvite = 0;
						
						gamer.sendMessage("command.group.accept.nolonger", this.main.colors.group);
						
						return commandState.Accepted;
					}

					gamer.sendMessage("command.group.refuse.ok", this.main.colors.group,
							joinGroup.host.getNameColor(), ChatColor.GREEN.toString());
					
					joinGroup.host.sendMessage("command.group.refuse.herefused", this.main.colors.group,
							gamer.getNameColor(), ChatColor.GREEN.toString());

					gamer.lastGroupInvite = 0;
					
					return commandState.Accepted;
				}
				if (args[0].equalsIgnoreCase("accept"))
				{
					if (gamer.lastGroupInvite == 0)
					{
						gamer.sendMessage("command.group.accept.nobodyinvite", this.main.colors.group);
						
						return commandState.Accepted;
					}
					
					Group joinGroup = this.main.getGroup(gamer.lastGroupInvite);
					
					if (joinGroup == null)
					{
						gamer.lastGroupInvite = 0;
						
						gamer.sendMessage("command.group.accept.nolonger", this.main.colors.group);
						
						return commandState.Accepted;
					}
					
					if (joinGroup.count() >= this.main.configuration.maximumGroupGamer)
					{
						gamer.sendMessage("command.group.accept.alreadymax", this.main.colors.group);
						
						gamer.lastGroupInvite = 0;
						
						return commandState.Accepted;
					}
					
					joinGroup.add(gamer);
					
					joinGroup.host.sendMessage("command.group.accept.ok", this.main.colors.group,
							gamer.getNameColor(), ChatColor.GREEN.toString());

					for (int i = 0; i < this.main.getGroup(gamer.groupid).members.size(); i++)
					{
						this.main.getGroup(gamer.groupid).members.get(i).sendMessage("command.group.accept.ok", this.main.colors.group,
								gamer.getNameColor(), ChatColor.GREEN.toString());
					}
					
					gamer.lastGroupInvite = 0;
					
					return commandState.Accepted;
				}
				
				if (args[0].equalsIgnoreCase("create"))
				{
					if (gamer.inGroup())
					{
						gamer.sendMessage("command.group.destroygroupcreate", this.main.colors.group);
						
						return commandState.Accepted;
					}
					
					int groupid = Manipulation.random(1453, 180796);
					
					this.main.addGroup(groupid, new Group(groupid, gamer));
					
					gamer.groupid = groupid;
					
					gamer.getGroup().host = gamer;
					
					gamer.sendMessage("command.group.created", this.main.colors.group);

					gamer.lastGroupInvite = 0;
					
					return commandState.Accepted;
				}
				
				if (args[0].equalsIgnoreCase("quit"))
				{
					if (!gamer.inGroup())
					{
						gamer.sendMessage("command.g.nogroup", this.main.colors.group);
					
						return commandState.Accepted;
					}
					
					this.simulateGroupExit(gamer);
					
					gamer.sendData("group", "4");
					
					gamer.lastGroupInvite = 0;
					
					return commandState.Accepted;
				}
			}
			
			return commandState.Incorrect;
		}

		if (label.equalsIgnoreCase("shop")) //on utilise AIR au lieu de ITEM_FRAME comme ce n'est pas un block de stocker de le location
		{
			if (args.length == 0) //information sur le magasin
			{
				if (gamer.stockedLeftClickLocation == null)
				{
					gamer.sendMessage("block.pleaseselect1", true);
					
					return commandState.Accepted;
				}
				
				if (gamer.stockedLeftClickLocation.getBlock().getType() != Material.SIGN
						&& gamer.stockedLeftClickLocation.getBlock().getType() != Material.SIGN_POST
						&& gamer.stockedLeftClickLocation.getBlock().getType() != Material.AIR)
				{
					gamer.sendMessage("command.shop.needsign", this.main.colors.shop);
					
					return commandState.Accepted;
				}
				
				Shop shop = this.main.shops.get(gamer.stockedLeftClickLocation);
				
				if (shop == null)
				{
					gamer.sendMessage("command.shop.inexistant", this.main.colors.shop);
					
					return commandState.Accepted;
				}
				

				if (!shop.pricable.isToSell())
				{
					gamer.sendMessage("shop.nottosell", this.main.colors.shop);
					
					return commandState.Accepted;
				}
				
				if (shop.material == null || shop.quantity == 0)
				{
					gamer.sendMessage("shop.empty", this.main.colors.shop);
					
					return commandState.Accepted;
				}
				
				gamer.sendMessage("command.shop.info", this.main.colors.shop,
						this.main.getMaterialName(gamer.information.language, shop.get().getType()),
						Integer.toString(shop.pricable.price), Integer.toString(shop.quantity));
				
				return commandState.Accepted;
			}
			
			if (args.length == 2 && !args[1].equalsIgnoreCase("not"))
			{
				if (args[0].equalsIgnoreCase("set"))
				{
					int price = 0;
					
					try
					{
						price = Integer.parseInt(args[1]);
					}
					catch (Exception ex)
					{
						return commandState.Incorrect;
					}
					
					if (price < 0)
					{
						return commandState.Incorrect;
					}
					
					if (gamer.stockedLeftClickLocation == null)
					{
						gamer.sendMessage("block.pleaseselect1", true);
						
						return commandState.Accepted;
					}
					
					if (gamer.stockedLeftClickLocation.getBlock().getType() != Material.SIGN
							&& gamer.stockedLeftClickLocation.getBlock().getType() != Material.SIGN_POST)
					{
						if (gamer.stockedLeftClickLocation.getBlock().getType() != Material.AIR)
						{
							gamer.sendMessage("command.shop.needsign", this.main.colors.shop);
							
							return commandState.Accepted;
						}
						else if (!gamer.grad.canGodSalables)
						{
							gamer.sendMessage("command.shop.needsign", this.main.colors.shop);
							
							return commandState.Accepted;
						}
					}
					
					Shop shop = this.main.shops.get(gamer.stockedLeftClickLocation);
					
					if (shop == null)
					{
						gamer.sendMessage("command.shop.inexistant", this.main.colors.shop);
						
						return commandState.Accepted;
					}
					
					if (!shop.ownable.isOwner(gamer) && !gamer.grad.canGodSalables)
					{
						gamer.sendMessage("command.shop.notowner", this.main.colors.shop);
						
						return commandState.Accepted;
					}
					
					shop.hasChanged = true;
					
					shop.pricable.price = price;
					
					if (price == 0)
					{
						gamer.sendMessage("command.shop.nomoretosell", this.main.colors.shop);
						
						return commandState.Accepted;
					}

					gamer.sendMessage("command.shop.priceupdated", this.main.colors.shop,
							Integer.toString(price));
					
					return commandState.Accepted;
				}
				
				return commandState.Incorrect;
			}
			
			if (args.length >= 1)
			{
				if (args.length > 2)
				{
					return commandState.Incorrect;
				}
				
				if (gamer.stockedLeftClickLocation == null)
				{
					gamer.sendMessage("block.pleaseselect1", true);
					
					return commandState.Accepted;
				}
				
				if (args[0].equalsIgnoreCase("create")) //creer un magasin
				{
					boolean isAdmin = false;
					
					if (args.length == 2 && args[1].equalsIgnoreCase("not") && gamer.grad.canGodSalables) //peut creer des terrains achetable mais non detruisibles
					{
						isAdmin = true;
					}
					else if (args.length == 2)
					{
						return commandState.Incorrect;
					}
					
					if (!gamer.grad.canSalable)
					{
						return commandState.NoRights;
					}
					
					if (gamer.stockedLeftClickLocation.getBlock().getType() != Material.SIGN
							&& gamer.stockedLeftClickLocation.getBlock().getType() != Material.SIGN_POST)
					{
						if (gamer.stockedLeftClickLocation.getBlock().getType() != Material.AIR)
						{
							gamer.sendMessage("command.shop.needsign", this.main.colors.shop);
							
							return commandState.Accepted;
						}
						else if (!gamer.grad.canGodSalables)
						{
							gamer.sendMessage("command.shop.needsign", this.main.colors.shop);
							
							return commandState.Accepted;
						}
					}
					
					if (this.main.shops.containsKey(gamer.stockedLeftClickLocation))
					{
						gamer.sendMessage("command.shop.create.already", this.main.colors.shop);
						
						return commandState.Accepted;
					}
					
					Terrain getTerrain = this.main.getTerrainFromLocation(gamer.stockedLeftClickLocation);
					
					if (getTerrain == null && !gamer.grad.canGodSalables)
					{
						gamer.sendMessage("command.shop.create.needterrain", this.main.colors.shop);
						
						return commandState.Accepted;
					}
					
					if (getTerrain != null && !getTerrain.owner.isOwner(gamer) && !gamer.grad.canGodSalables)
					{
						gamer.sendMessage("command.shop.create.notterrainowner", this.main.colors.shop);
						
						return commandState.Accepted;
					}
					
					if (!isAdmin)
					{
						this.main.shops.put(gamer.stockedLeftClickLocation, new Shop(new Ownable(gamer), true, gamer.stockedLeftClickLocation));
					}
					else
					{
						this.main.shops.put(gamer.stockedLeftClickLocation, new Shop(new Ownable(), false, gamer.stockedLeftClickLocation));
					}

					gamer.sendMessage("command.shop.create.ok", this.main.colors.shop);
					
					return commandState.Accepted;
				}
				
				if (args.length != 1)
				{
					return commandState.Incorrect;
				}
				
				if (args[0].equalsIgnoreCase("delete")) //supprimer un magasin
				{
					if (gamer.stockedLeftClickLocation.getBlock().getType() != Material.SIGN
							&& gamer.stockedLeftClickLocation.getBlock().getType() != Material.SIGN_POST)
					{
						if (gamer.stockedLeftClickLocation.getBlock().getType() != Material.AIR)
						{
							gamer.sendMessage("command.shop.needsign", this.main.colors.shop);
							
							return commandState.Accepted;
						}
						else if (!gamer.grad.canGodSalables)
						{
							gamer.sendMessage("command.shop.needsign", this.main.colors.shop);
							
							return commandState.Accepted;
						}
					}
					
					Shop shop = this.main.shops.get(gamer.stockedLeftClickLocation);
					
					if (shop == null)
					{
						gamer.sendMessage("command.shop.inexistant", this.main.colors.shop);
						
						return commandState.Accepted;
					}
					
					if (!shop.ownable.isOwner(gamer) && !gamer.grad.canGodSalables)
					{
						gamer.sendMessage("command.shop.notowner", this.main.colors.shop);
						
						return commandState.Accepted;
					}
					
					if (shop.material != null & shop.quantity > 0)
					{
						shop.position.getWorld().dropItem(shop.position, new ItemStack(shop.material, shop.quantity));
					}
					
					if (shop.inDatabase)
					{
						this.main.deleteShops.add(shop.id);
					}
					
					this.main.shops.remove(shop.position);

					gamer.sendMessage("command.shop.deleted.ok", this.main.colors.shop);
					
					return commandState.Accepted;
				}
				
				return commandState.Incorrect;
			}
			
			return commandState.Incorrect;
		}
		
		if (label.equalsIgnoreCase("c"))
		{
			if (args.length <= 0)
			{
				return commandState.Incorrect;
			}
			
			Clan clan = gamer.getClan();
			
			if (clan == null)
			{
				gamer.sendMessage("command.c.noclan", this.main.colors.clan);
				
				return commandState.Accepted;
			}
			
			clan.broadcast(Manipulation.chatFormat(this.main.colors.clan, gamer.getNameTag(), Manipulation.charsFromArraySeparator(args, " "), false));
			
			return commandState.Accepted;
		}
		
		if (label.equalsIgnoreCase("tc"))
		{
			Clan clan = gamer.getClan();
			
			if (clan == null)
			{
				gamer.sendMessage("command.c.noclan", this.main.colors.clan);
				
				return commandState.Accepted;
			}
			
			if (clan.spawn == null)
			{
				gamer.sendMessage("command.clan.tp.nospawn", this.main.colors.clan);
				
				return commandState.Accepted;
			}
			
			if (gamer.isPrison())
			{
				return commandState.NoRights;
			}
			
			if (gamer.isRecentDamage())
			{
				gamer.sendMessage("damage.too.recent", this.main.colors.clan);
				
				return commandState.Accepted;
			}
			
			gamer.source.teleport(clan.spawn);

			gamer.sendMessage("command.clan.tp.ok", this.main.colors.clan,
					Integer.toString(clan.spawn.getBlockX()),
					Integer.toString(clan.spawn.getBlockY()),
					Integer.toString(clan.spawn.getBlockZ()));
			
			return commandState.Accepted;
		}
		
		if (label.equalsIgnoreCase("world"))
		{
			if (!gamer.grad.canWorldTp)
			{
				return commandState.NoRights;
			}
			
			if (args.length != 1)
			{
				return commandState.Incorrect;
			}
			
			World world = this.main.getServer().getWorld(args[0]);
			
			if (world == null)
			{
				gamer.sendMessage("command.world.inexistant", this.main.colors.diverse);
				
				return commandState.Accepted;
			}
			
			gamer.source.teleport(world.getSpawnLocation());
			
			gamer.sendMessage("command.world.ok", this.main.colors.diverse, world.getName());

			return commandState.Accepted;
		}
			

		if (label.equalsIgnoreCase("teleporter"))
		{
			if (!gamer.grad.canCreateTeleporter)
			{
				return commandState.NoRights;
			}
			
			if (args.length == 1 && args[0].equalsIgnoreCase("delete"))
			{
				if (gamer.stockedLeftClickLocation == null)
				{
					gamer.sendMessage("block.pleaseselect1", this.main.colors.diverse);
					
					return commandState.Accepted;
				}
				
				Teleporter teleporter = this.main.getTeleporter(gamer.stockedLeftClickLocation);
				
				if (teleporter == null)
				{
					gamer.sendMessage("command.teleporter.inexistant", this.main.colors.diverse);
					
					return commandState.Accepted;
				}
				
				this.main.teleporters.remove(teleporter);
				
				gamer.sendMessage("command.teleporter.delete.ok", this.main.colors.diverse, teleporter.name);
				
				return commandState.Accepted;
			}
			else if (args.length == 2 && args[0].equalsIgnoreCase("create"))
			{
				if (gamer.stockedLeftClickLocation == null || gamer.stockedRightClickLocation == null)
				{
					gamer.sendMessage("block.select.pleaseboth", this.main.colors.diverse);
					
					return commandState.Accepted;
				}
				
				if (this.main.teleporterExists(gamer.stockedLeftClickLocation) || this.main.teleporterExists(gamer.stockedRightClickLocation))
				{
					gamer.sendMessage("command.teleporter.create.existant", this.main.colors.diverse);
					
					return commandState.Accepted;
				}
				
				this.main.teleporters.add(new Teleporter(args[1], gamer.stockedLeftClickLocation, gamer.stockedRightClickLocation));
				
				gamer.sendMessage("command.teleporter.create.ok", this.main.colors.diverse);
				
				return commandState.Accepted;
			}
			
			return commandState.Incorrect;
		}
		
		if (label.equalsIgnoreCase("clan"))
		{
			Clan clan = gamer.getClan();
	
			if (args.length >= 1 && args[0].equalsIgnoreCase("create"))
			{
				if (args.length != 2)
				{
					return commandState.Incorrect;
				}
				
				if (clan != null)
				{
					gamer.sendMessage("command.clan.create.alreadyclan", this.main.colors.clan);
					
					return commandState.Accepted;
				}
				
				if (!this.main.isNameOk(args[1]))
				{
					gamer.sendMessage("command.clan.create.incorrectname", this.main.colors.clan);
					
					return commandState.Accepted;
				}
				
				if (this.main.clanExist(args[1]))
				{
					gamer.sendMessage("command.clan.create.nametaken", this.main.colors.clan);

					return commandState.Accepted;
				}
				
				if (gamer.stockedLeftClickLocation == null || gamer.stockedRightClickLocation == null)
				{
					gamer.sendMessage("block.select.pleaseboth", this.main.colors.clan);
					
					return commandState.Accepted;
				}
				
				Plot addPlot = new Plot(gamer.stockedLeftClickLocation, gamer.stockedRightClickLocation);
				
				if (addPlot.sizeX < this.main.configuration.minimumLengthClan || addPlot.sizeZ < this.main.configuration.minimumLengthClan)
				{
					gamer.sendMessage("command.clan.create.toosmall", this.main.colors.clan,
							Integer.toString(this.main.configuration.minimumLengthClan));
					
					return commandState.Accepted;
				}
				
				if (this.main.collisionWithPlots(addPlot) != Turkraft.collisionType.NoCollision)
				{
					gamer.sendMessage("command.clan.touchesplot", this.main.colors.clan);
					
					return commandState.Accepted;
				}
				
				Clan newClan = new Clan(args[1], addPlot, gamer.information.id);
				
				newClan.addNew(gamer);

				int price = newClan.getEstimatedPrice();
				
				if (gamer.information.money < price)
				{
					gamer.sendMessage("command.clan.create.nomoney", this.main.colors.clan,
							Integer.toString(price));
					
					newClan.dispose();
					
					return commandState.Accepted;
				}
				
				gamer.information.money -= price;
				
				this.main.clans.put(newClan.virtual, newClan);
				
				gamer.refreshName();
				
				this.main.broadcastTextAbbrev("command.clan.create.publish", this.main.colors.clan, gamer.getNameColor(), newClan.name, Integer.toString(price));

				//gamer.sendMessage("command.clan.create.created", this.main.colors.clan, newClan.name, Integer.toString(price));
				
				return commandState.Accepted;
			}
			
			if (clan == null)
			{
				if (args.length == 1)
				{
					if (args[0].equalsIgnoreCase("price"))
					{
						if (gamer.stockedLeftClickLocation == null || gamer.stockedRightClickLocation == null)
						{
							gamer.sendMessage("block.select.pleaseboth", true);
							
							return commandState.Accepted;
						}
						
						Clan clanPrice = new Clan(null, new Plot(gamer.stockedLeftClickLocation, gamer.stockedRightClickLocation), 0);
						
						gamer.sendMessage("command.clan.price", this.main.colors.clan, Integer.toString(clanPrice.getEstimatedPrice()),
								Integer.toString(clanPrice.plot.sizeX), Integer.toString(clanPrice.plot.sizeZ));
						
						clanPrice.dispose();
						
						return commandState.Accepted;
					}
					
					if (args[0].equalsIgnoreCase("accept"))
					{
						Clan toJoin = this.main.clans.get(gamer.lastClanInvite);
						
						if (toJoin == null)
						{
							gamer.sendMessage("command.clan.accept.nobodyinvite", this.main.colors.clan);
							
							return commandState.Accepted;
						}
						
						if (toJoin.countMembers >= this.main.configuration.maximumClanMembers)
						{
							gamer.sendMessage("command.clan.accept.maximumcapacity", this.main.colors.clan);
							
							return commandState.Accepted;
						}
						
						toJoin.addNew(gamer);
						
						toJoin.broadcast(this.main, "command.clan.accept.ok", gamer.getNameColor());
						
						gamer.lastClanInvite = 0;
						
						return commandState.Accepted;
					}
					
					if (args[0].equalsIgnoreCase("refuse"))
					{
						Clan toRefuse = this.main.clans.get(gamer.lastClanInvite);
						
						if (toRefuse == null)
						{
							gamer.sendMessage("command.clan.accept.nobodyinvite", this.main.colors.clan);
							
							return commandState.Accepted;
						}
						
						gamer.lastClanInvite = 0;
						
						Gamer clanChief = toRefuse.getChief();
						
						if (clanChief == null)
						{
							return commandState.Accepted;
						}
						
						gamer.sendMessage("command.clan.refuse.ok", this.main.colors.clan, clanChief.getNameColor());
						
						clanChief.sendMessage("command.clan.refuse.tochief", this.main.colors.clan, gamer.getNameColor());
						
						return commandState.Accepted;
					}
				}
				
				gamer.sendMessage("command.c.noclan", this.main.colors.clan);
				
				return commandState.Accepted;
			}
			
			if (args.length == 0)
			{
				gamer.sendMessage("command.clan.list", this.main.colors.clan,
						clan.getList());
				
				return commandState.Accepted;
			}
			
			if (args.length == 1)
			{
				if (args[0].equalsIgnoreCase("quit"))
				{
					if (clan.isChief(gamer)) //le meneur ne peut pas quitter le clan
					{
						gamer.sendMessage("command.clan.quit.chiefno", this.main.colors.clan);
						
						return commandState.Accepted;
					}

					clan.broadcast(this.main, "command.clan.quit.ok", gamer.getNameColor());
					
					clan.remove(gamer);
					
					return commandState.Accepted;
				}
				
				if (args[0].equalsIgnoreCase("info"))
				{
					gamer.sendMessage("command.clan.info", this.main.colors.clan, Integer.toString(clan.money), Integer.toString(clan.countMembers), Integer.toString(clan.score), Integer.toString(clan.taken));
					
					return commandState.Accepted;
				}
				
				if (args[0].equalsIgnoreCase("delete"))
				{
					if (!clan.isChief(gamer))
					{
						return commandState.NoRights;
					}

					clan.broadcast(this.main, "command.clan.delete.ok", clan.getChief().getNameColor());
					
					if (clan.inDatabase)
					{
						this.main.deleteClans.add(clan.virtual);
					}
					
					this.main.clans.remove(clan.virtual);
					
					this.main.log.log("clans", clan.getChief().source.getName() + " disbands his clan " + clan.name);
					
					clan.dispose();
					
					return commandState.Accepted;
				}
				
				if (args[0].equalsIgnoreCase("set")) //choisir le point de spawn
				{
					if (!clan.isChief(gamer))
					{
						return commandState.NoRights;
					}
					
					if (gamer.stockedLeftClickLocation == null)
					{
						gamer.sendMessage("block.pleaseselect1", true);
						
						return commandState.Accepted;
					}
					
					if (!clan.plot.inPlot(gamer.stockedLeftClickLocation))
					{
						gamer.sendMessage("command.clan.set.locationnotplot", this.main.colors.clan);
						
						return commandState.Accepted;
					}
					
					clan.hasChanged = true;
					
					clan.spawn = gamer.stockedLeftClickLocation;

					gamer.sendMessage("command.clan.set.updated", this.main.colors.clan);
					
					return commandState.Accepted;
				}
				
				return commandState.Incorrect;
			}
			
			if (args.length >= 2 && !args[0].equalsIgnoreCase("give") && !args[0].equalsIgnoreCase("rank"))
			{
				if (args[0].equalsIgnoreCase("offer"))
				{
					if (gamer.isPrison())
					{
						return commandState.NoRights;
					}
					
					int money = 0;
					
					try
					{
						money = Integer.parseInt(args[1]);
					}
					catch (Exception ex)
					{
						return commandState.Incorrect;
					}
					
					if (money <= 0)
					{
						return commandState.Incorrect;
					}
					
					if (gamer.information.money < money)
					{
						gamer.sendMessage("command.clan.offer.notenough", this.main.colors.clan,
								Integer.toString(money));
						
						return commandState.Accepted;
					}
					
					gamer.information.money -= money;
					
					clan.hasChanged = true;
					
					clan.money += money;

					clan.broadcast(this.main, "command.clan.offer.ok", gamer.getNameColor(), Integer.toString(money));
					
					return commandState.Accepted;
				}
				
				Gamer target = this.main.getGamer(args[1], true);
				
				if (target == null)
				{
					gamer.sendMessage("general.notconnected", this.main.colors.clan, args[1]);
					
					return commandState.Accepted;
				}
				
				if (args[0].equalsIgnoreCase("invite"))
				{
					if (!clan.isChief(gamer) && !gamer.isCochief())
					{
						return commandState.NoRights;
					}
					
					if (clan.countMembers >= this.main.configuration.maximumClanMembers)
					{
						gamer.sendMessage("command.clan.invite.maxcapacity", this.main.colors.clan);
						
						return commandState.Accepted;
					}
					
					if (target.getClan() != null)
					{
						gamer.sendMessage("command.clan.invite.alreadyclan", this.main.colors.clan, target.getNameColor());
						
						return commandState.Accepted;
					}
					
					target.lastClanInvite = clan.virtual;

					gamer.sendMessage("command.clan.invite.sended", this.main.colors.clan, target.getNameColor());
					
					target.sendMessage("command.clan.heinvite", this.main.colors.clan, gamer.getNameColor(), clan.name);

					return commandState.Accepted;
				}
				
				if (args[0].equalsIgnoreCase("kick"))
				{
					if (!clan.isChief(gamer))
					{
						return commandState.NoRights;
					}
					
					if (target.information.clan != gamer.information.clan)
					{
						gamer.sendMessage("command.clan.kick.notclan", this.main.colors.clan, target.getNameColor());
						
						return commandState.Accepted;
					}

					clan.broadcast(this.main, "command.clan.kick.ok", target.getNameColor());
					
					clan.remove(target);
					
					return commandState.Accepted;
				}
				
				return commandState.Incorrect;
			}
			
			if (args.length == 3)
			{
				if (args[0].equalsIgnoreCase("rank"))
				{
					if (!clan.isChief(gamer))
					{
						return commandState.NoRights;
					}
					
					Gamer target = this.main.getGamer(args[1], true);
					
					if (target == null)
					{
						gamer.sendMessage("general.notconnected", this.main.colors.clan, args[1]);
						
						return commandState.Accepted;
					}
					
					if (target.information.id == gamer.information.id)
					{
						return commandState.Incorrect;
					}
					
					if (target.information.clan != gamer.information.clan)
					{
						gamer.sendMessage("command.clan.kick.notclan", this.main.colors.clan, target.getNameColor());
						
						return commandState.Accepted;
					}
					
					if (!args[2].equalsIgnoreCase("chief") && !args[2].equalsIgnoreCase("normal"))
					{
						return commandState.Incorrect;
					}
					
					if (args[2].equalsIgnoreCase("chief"))
					{
						if (target.information.clanRank == 1)
						{
							gamer.sendMessage("command.clan.rank.already", this.main.colors.clan, target.getNameColor());
							
							return commandState.Accepted;
						}
						
						target.information.clanRank = 1;
						
						clan.broadcast(this.main, "command.clan.rank.chiefok", target.getNameColor());
						
						return commandState.Accepted;
					}
					
					if (args[2].equalsIgnoreCase("normal"))
					{
						if (target.information.clanRank == 0)
						{
							gamer.sendMessage("command.clan.rank.already", this.main.colors.clan, target.getNameColor());
							
							return commandState.Accepted;
						}
						
						target.information.clanRank = 0;
						
						clan.broadcast(this.main, "command.clan.rank.normalok", target.getNameColor());
						
						return commandState.Accepted;
					}
					
					return commandState.Incorrect;
				}
				
				if (args[0].equalsIgnoreCase("give"))
				{
					if (!clan.isChief(gamer) && !gamer.isCochief())
					{
						return commandState.NoRights;
					}
					
					Gamer target = this.main.getGamer(args[1], true);
					
					if (target == null)
					{
						gamer.sendMessage("general.notconnected", this.main.colors.clan, args[1]);
						
						return commandState.Accepted;
					}
					
					if (target.information.clan != gamer.information.clan)
					{
						gamer.sendMessage("command.clan.kick.notclan", this.main.colors.clan, target.getNameColor());
						
						return commandState.Accepted;
					}
					
					int money = 0;
					
					try
					{
						money = Integer.parseInt(args[2]);
					}
					catch (Exception ex)
					{
						return commandState.Incorrect;
					}
					
					if (money <= 0)
					{
						return commandState.Incorrect;
					}
					
					if (clan.money < money)
					{
						target.sendMessage("command.clan.give.nomoney", this.main.colors.clan, Integer.toString(money));
						
						return commandState.Accepted;
					}
					
					target.information.money += money;
					
					clan.hasChanged = true;
					
					clan.money -= money;

					clan.broadcast(this.main, "command.clan.give.ok", gamer.getNameColor(), target.getNameColor(), Integer.toString(money));
					
					return commandState.Accepted;
				}
			}

			return commandState.Incorrect;
		}
		
		if (label.equalsIgnoreCase("spawner"))
		{
			if (!gamer.grad.canSpawner)
			{
				return commandState.NoRights;
			}
			
			if (args.length != 1)
			{
				return commandState.Incorrect;
			}
			
			EntityType entity = EntityType.fromName(args[0].toUpperCase());
			
			if (entity == null)
			{
				return commandState.Error;
			}
			
			if (gamer.stockedLeftClickLocation == null)
			{
				gamer.sendMessage("block.pleaseselect1", true);
				
				return commandState.Accepted;
			}
			
			gamer.stockedLeftClickLocation.getBlock().setType(Material.MOB_SPAWNER);
		    
		    ((CreatureSpawner) gamer.stockedLeftClickLocation.getBlock().getState()).setSpawnedType(entity);

			gamer.stockedLeftClickLocation.getBlock().setData((byte)entity.getTypeId());
			
			gamer.sendMessage("command.spawner.ok", true);
			
			return commandState.Accepted;
		}
		
		if (label.equalsIgnoreCase("invisibleban"))
		{
			if (!gamer.grad.canBan)
			{
				return commandState.NoRights;
			}
			
			if (args.length != 1)
			{
				return commandState.Incorrect;
			}
			
			Gamer target = this.main.getGamer(args[0], true);
			
			if (target == null)
			{
				gamer.sendMessage("general.notconnected", this.main.colors.diverse, args[0]);
				
				return commandState.Accepted;
			}
			
			this.main.broadcastAdminChat(ChatColor.RED + "-> " + gamer.getNameColorPersonnalized(ChatColor.RED) + " bans " + target.getNameColorPersonnalized(ChatColor.RED) + ".");
			
			target.ban();
			
			this.main.log.log("admin", "Ban of " + target.information.name + " by " + gamer.information.name + ".");
			
			return commandState.Accepted;
		}

		if (label.equalsIgnoreCase("invisiblekick"))
		{
			if (!gamer.grad.canKick)
			{
				return commandState.NoRights;
			}

			if (args.length < 1)
			{
				return commandState.Incorrect;
			}
			
			Gamer target = this.main.getGamer(args[0], true);
			
			if (target == null)
			{
				gamer.sendMessage("general.notconnected", this.main.colors.diverse, args[0]);
				
				return commandState.Accepted;
			}
			
			if (target.grad.cantBeKicked)
			{
				return commandState.NoRights;
			}
			
			String message = "Kicked by an operator.";
			
			if (args.length > 1)
			{
				message = Manipulation.charsFromArraySeparator(1, args, " ");
			}
			
			this.main.broadcastAdminChat(ChatColor.RED + "-> " + gamer.getNameColorPersonnalized(ChatColor.RED) + " has kicked " + target.getNameColorPersonnalized() + " with reason \"" + message + "\".");
			
			target.source.kickPlayer(message);
			
			this.main.log.log("admin", "Kick of " + target.information.name + " by " + gamer.information.name + " for reason " + message);
			
			return commandState.Accepted;
		}
		
		if (label.equalsIgnoreCase("reset"))
		{
			if (!gamer.grad.canResetClass)
			{
				return commandState.NoRights;
			}
			
			if (args.length != 1)
			{
				return commandState.Incorrect;
			}
			
			Gamer target = this.main.getGamer(args[0], true);
			
			if (target == null)
			{
				gamer.sendMessage("general.notconnected", this.main.colors.diverse, args[0]);
				
				return commandState.Accepted;
			}
			
			this.main.database.resetGamerGenre(target);
			
			if (gamer.information.id != target.information.id)
			{
				gamer.sendMessage("command.reset.ok", this.main.colors.diverse, target.getNameColor());
			}
			
			this.main.broadcastAdminChat(ChatColor.RED + "-> " + gamer.getNameColorPersonnalized(ChatColor.RED) + " has reset the class of " + target.getNameColorPersonnalized() + ".");
			
			target.source.kickPlayer(this.main.getText(target.information.language, "command.reset.do"));
			
			return commandState.Accepted;
		}

		if (label.equalsIgnoreCase("sanguine"))
		{
			if (args.length != 1)
			{
				return commandState.Incorrect;
			}
			
			if (args[0].equalsIgnoreCase("1"))
			{
				if (!gamer.grad.canPutSanguine)
				{
					return commandState.NoRights;
				}
				
				this.main.sanguineStart();
				
				//gamer.sendMessage("command.sanguine.ok", this.main.colors.diverse);
				
				this.main.broadcastAdminChat(ChatColor.RED + "-> " + gamer.getNameColorPersonnalized(ChatColor.RED) + " has started Blood Moon.");
				
				return commandState.Accepted;
			}
			else if (args[0].equalsIgnoreCase("0"))
			{
				if (!gamer.grad.canRemoveSanguine)
				{
					return commandState.NoRights;
				}
				
				this.main.sanguineEnd();
				
				this.main.broadcastAdminChat(ChatColor.RED + "-> " + gamer.getNameColorPersonnalized(ChatColor.RED) + " has stopped Blood Moon.");
				
				//gamer.sendMessage("command.sanguine.stop", this.main.colors.diverse);
				
				return commandState.Accepted;
			}
			
			return commandState.Incorrect;
		}
		
		if (label.equalsIgnoreCase("mute"))
		{
			if (!gamer.grad.canMute)
			{
				return commandState.NoRights;
			}
			
			if (args.length != 1)
			{
				return commandState.Incorrect;
			}
			
			Gamer locatedGamer = this.main.getGamer(args[0], true);
			
			if (locatedGamer == null)
			{
				gamer.sendMessage("general.notconnected", this.main.colors.diverse, args[0]);
				
				return commandState.Accepted;
			}

			this.main.broadcastAdminChat(ChatColor.RED + "-> " + gamer.getNameColorPersonnalized(ChatColor.RED) + " mutes " + locatedGamer.getNameColorPersonnalized(ChatColor.RED) + ".");
			
			locatedGamer.mute();
			
			return commandState.Accepted;
		}
		
		if (label.equalsIgnoreCase("unmute"))
		{
			if (!gamer.grad.canMute)
			{
				return commandState.NoRights;
			}
			
			if (args.length != 1)
			{
				return commandState.Incorrect;
			}
			
			Gamer locatedGamer = this.main.getGamer(args[0], true);
			
			if (locatedGamer == null)
			{
				gamer.sendMessage("general.notconnected", this.main.colors.diverse, args[0]);
				
				return commandState.Accepted;
			}

			this.main.broadcastAdminChat(ChatColor.RED + "-> " + gamer.getNameColorPersonnalized(ChatColor.RED) + " unmutes " + locatedGamer.getNameColorPersonnalized(ChatColor.RED) + ".");
			
			locatedGamer.unmute();
			
			return commandState.Accepted;
		}
		
		if (label.equalsIgnoreCase("open"))
		{
			if (!gamer.grad.canOpenPlayerInventory)
			{
				return commandState.NoRights;
			}
			
			if (args.length != 1)
			{
				return commandState.Incorrect;
			}

			Gamer openGamer = this.main.getGamer(args[0], true);
			
			if (openGamer == null)
			{
				gamer.sendMessage("general.notconnected", this.main.colors.diverse, args[0]);
				
				return commandState.Accepted;
			}
			
			gamer.source.openInventory(openGamer.source.getInventory());

			this.main.broadcastAdminChat(ChatColor.RED + "-> " + gamer.getNameColorPersonnalized(ChatColor.RED) + " opens the inventory of " + openGamer.getNameColorPersonnalized(ChatColor.RED) + ".");
			
			return commandState.Accepted;
		}
		
		if (label.equalsIgnoreCase("afk"))
		{
			if (args.length != 0)
			{
				return commandState.Incorrect;
			}
			
			if (gamer.isRecentDamage())
			{
				gamer.sendMessage("damage.too.recent", this.main.colors.diverse);
				
				return commandState.Accepted;
			}
			
			if (gamer.grad.canAFK)
			{
				gamer.setAFK();
				
				return commandState.Accepted;
			}
			
			return commandState.NoRights;
		}
		
		if (label.equalsIgnoreCase("restart"))
		{
			if (!gamer.grad.canRestartServer)
			{
				return commandState.NoRights;
			}
			
			this.main.alertRestart();
			
			this.main.log.log("admin", "Restart by " + gamer.information.name);
			
			this.main.broadcastAdminChat(ChatColor.RED + "-> " + gamer.getNameColorPersonnalized(ChatColor.RED) + " makes a server restart.");
			
			return commandState.Accepted;
		}
		
		if (label.equalsIgnoreCase("chat"))
		{
			return commandState.Error;
		}
		
		if (label.equalsIgnoreCase("lag"))
		{
			if (!gamer.grad.canLag)
			{
				return commandState.NoRights;
			}
			
			Runtime runtime = Runtime.getRuntime();

		    long maxMemory = runtime.maxMemory();
		    long allocatedMemory = runtime.totalMemory();
		    long freeMemory = runtime.freeMemory();
	    
			gamer.sendMessage("command.lag.ok", this.main.colors.diverse,
					Long.toString(freeMemory / 1024),
					Long.toString(allocatedMemory / 1024 / 1024),
					Long.toString(maxMemory / 1024),
					Long.toString((freeMemory + (maxMemory - allocatedMemory)) / 1024),
					Integer.toString(this.main.getServer().getWorld("world").getEntities().size()));
			
			return commandState.Accepted;
		}

		if (label.equalsIgnoreCase("maj"))
		{
			if (!gamer.grad.canBroadcastMaj)
			{
				return commandState.NoRights;
			}
			
			this.main.broadcastTextAbbrev("broadcast.maj", this.main.colors.server);
			
			this.main.log.log("admin", "Update broadcast by " + gamer.information.name);
			
			this.main.broadcastAdminChat(ChatColor.RED + "-> " + gamer.getNameColorPersonnalized(ChatColor.RED) + " makes an update broadcast.");
			
			return commandState.Accepted;
		}
		
		if (label.equalsIgnoreCase("invisible"))
		{
			if (!gamer.grad.canInvisible)
			{
				return commandState.NoRights;
			}
			
			gamer.setInvisible();
			
			//gamer.sendMessage("command.invisible.ok", this.main.colors.diverse);
			
			this.main.broadcastAdminChat(ChatColor.RED + "-> " + gamer.getNameColorPersonnalized(ChatColor.RED) + " changes his invisible status to " + gamer.isInvisible() + ".");
			
			return commandState.Accepted;
		}
		
		if (label.equalsIgnoreCase("distance"))
		{
			if (!gamer.grad.canSeeDistance)
			{
				return commandState.NoRights;
			}
			
			if (args.length != 1)
			{
				return commandState.Incorrect;
			}
			
			if (args[0].equalsIgnoreCase(gamer.information.name))
			{
				return commandState.Incorrect;
			}
			
			Gamer locatedGamer = this.main.getGamer(args[0], true);
			
			if (locatedGamer == null)
			{
				gamer.sendMessage("general.notconnected", this.main.colors.diverse, args[0]);
				
				return commandState.Accepted;
			}
			
			gamer.sendMessage("command.distance.ok", this.main.colors.diverse, 
					locatedGamer.getNameColor(),
					Integer.toString((int)locatedGamer.source.getLocation().distance(gamer.source.getLocation())));
			
			return commandState.Accepted;
		}
		
		if (label.equalsIgnoreCase("invisiblereload"))
		{
			if (args.length == 1)
			{
				if (args[0].equalsIgnoreCase("grads"))
				{
					if (!gamer.grad.canReloadGrads)
					{
						return commandState.NoRights;
					}
					
					this.main.grads = this.main.database.getGrads();

					this.main.reloadGrads();
					
					this.main.broadcastAdminChat(ChatColor.RED + "-> " + gamer.getNameColorPersonnalized(ChatColor.RED) + " reloads grads.");
					
					return commandState.Accepted;
				}
				
				return commandState.Incorrect;
			}

			if (args.length == 2)
			{
				if (args[0].equalsIgnoreCase("pgrad"))
				{
					if (!gamer.grad.canReloadGrads)
					{
						return commandState.NoRights;
					}

					Gamer reloadGamer = this.main.getGamer(args[1], true);
					
					if (reloadGamer == null)
					{
						gamer.sendMessage("general.notconnected", this.main.colors.diverse, args[1]);
						
						return commandState.Accepted;
					}
					
					reloadGamer.information.grad = this.main.database.getPlayerGrad(reloadGamer);
					
					reloadGamer.reloadGrad();
					
					this.main.broadcastAdminChat(ChatColor.RED + "-> " + gamer.getNameColorPersonnalized(ChatColor.RED) + " reloads the grad of " + reloadGamer.getNameColorPersonnalized(ChatColor.RED) + ".");
					
					return commandState.Accepted;
				}
				
				return commandState.Incorrect;
			}
			
			return commandState.Incorrect;
		}

		if (label.equalsIgnoreCase("ip"))
		{
			if (!gamer.grad.canSeeIp)
			{
				return commandState.NoRights;
			}
			
			if (args.length != 1)
			{
				return commandState.Incorrect;
			}
			
			if (args[0].equalsIgnoreCase(gamer.information.name))
			{
				gamer.sendMessage("command.ip.you", this.main.colors.diverse, gamer.source.getAddress().getAddress().getHostAddress());
				
				return commandState.Accepted;
			}

			Gamer ipGamer = this.main.getGamer(args[0], true);
			
			if (ipGamer == null)
			{
				gamer.sendMessage("general.notconnected", this.main.colors.diverse, args[0]);
				
				return commandState.Accepted;
			}
			
			gamer.sendMessage("command.ip.him", this.main.colors.diverse, ipGamer.getNameColor(), ipGamer.source.getAddress().getAddress().getHostAddress());
			
			return commandState.Accepted;
		}
		
		if (label.equalsIgnoreCase("prison"))
		{
			if (!gamer.grad.canGodJails)
			{
				return commandState.NoRights;
			}
			
			if (args.length == 0)
			{
				if (gamer.stockedLeftClickLocation == null)
				{
					gamer.sendMessage("block.pleaseselect1", this.main.colors.prison);
					
					return commandState.Accepted;
				}
				
				Prison prison = this.main.getPrisonFromLocation(gamer.stockedLeftClickLocation);
				
				if (prison == null)
				{
					gamer.sendMessage("command.prison.inexistant", this.main.colors.diverse);
				}
				
				gamer.sendMessage("command.prison.see", this.main.colors.diverse, prison.variable, Integer.toString(prison.counter));
				
				return commandState.Accepted;
			}
			
			if (args[0].equalsIgnoreCase("create"))
			{
				if (args.length != 2)
				{
					return commandState.Incorrect;
				}

				if (gamer.stockedLeftClickLocation == null || gamer.stockedRightClickLocation == null)
				{
					gamer.sendMessage("block.select.pleaseboth", this.main.colors.prison);
					
					return commandState.Accepted;
				}
				
				Plot plot = new Plot(gamer.stockedLeftClickLocation, gamer.stockedRightClickLocation);
				
				if (this.main.collisionWithPlots(plot) != Turkraft.collisionType.NoCollision)
				{
					gamer.sendMessage("command.terrain.touchesplot", this.main.colors.prison);
					
					return commandState.Accepted;
				}
				
				this.main.prisons.add(new Prison(this.main, args[1], plot));
				
				gamer.sendMessage("command.prison.create.ok", this.main.colors.prison, args[1]);
				
				return commandState.Accepted;
			}
			
			if (args[0].equalsIgnoreCase("delete"))
			{
				if (gamer.stockedLeftClickLocation == null)
				{
					gamer.sendMessage("block.pleaseselect1", this.main.colors.prison);
					
					return commandState.Accepted;
				}
				
				Prison prison = this.main.getPrisonFromLocation(gamer.stockedLeftClickLocation);
				
				if (prison == null)
				{
					gamer.sendMessage("command.prison.inexistant", this.main.colors.prison);
				}
				
				if (prison.inDatabase)
				{
					this.main.deletePrisons.add(prison.id);
				}
				
				this.main.terrains.remove(prison);
				
				gamer.sendMessage("command.prison.delete.ok", this.main.colors.prison);
				
				return commandState.Accepted;
			}
			
			if (args[0].equalsIgnoreCase("spawn"))
			{
				if (args.length != 1)
				{
					return commandState.Incorrect;
				}
				
				if (gamer.stockedLeftClickLocation == null || gamer.stockedRightClickLocation == null)
				{
					gamer.sendMessage("block.select.pleaseboth", this.main.colors.prison);
					
					return commandState.Accepted;
				}
				
				Prison prison = this.main.getPrisonFromLocation(gamer.stockedLeftClickLocation);
				
				if (prison == null)
				{
					gamer.sendMessage("command.prison.inexistant", this.main.colors.prison);
				}
				
				prison.spawn = gamer.stockedLeftClickLocation;
				
				prison.spawn.setY(prison.spawn.getY() + 1);
				
				prison.hasChanged = true;
				
				gamer.sendMessage("command.prison.spawn.ok", this.main.colors.diverse, prison.variable);
				
				return commandState.Accepted;
			}
			
			if (args[0].equalsIgnoreCase("exit"))
			{
				if (args.length != 1)
				{
					return commandState.Incorrect;
				}
				
				if (gamer.stockedLeftClickLocation == null || gamer.stockedRightClickLocation == null)
				{
					gamer.sendMessage("block.select.pleaseboth", this.main.colors.prison);
					
					return commandState.Accepted;
				}
				
				Prison prison = this.main.getPrisonFromLocation(gamer.stockedLeftClickLocation);
				
				if (prison == null)
				{
					gamer.sendMessage("command.prison.inexistant", this.main.colors.prison);
				}
				
				prison.exit = gamer.stockedLeftClickLocation;
				
				prison.exit.setY(prison.exit.getY() + 1);
				
				prison.hasChanged = true;
				
				gamer.sendMessage("command.prison.exit.ok", this.main.colors.diverse, prison.variable);
				
				return commandState.Accepted;
			}
			
			if (args[0].equalsIgnoreCase("give"))
			{
				if (args.length != 1)
				{
					return commandState.Incorrect;
				}
				
				if (gamer.stockedLeftClickLocation == null)
				{
					gamer.sendMessage("block.pleaseselect1", this.main.colors.prison);
					
					return commandState.Accepted;
				}
				
				if (gamer.stockedLeftClickLocation.getBlock().getType() != Material.CHEST)
				{
					gamer.sendMessage("command.prison.needchest", this.main.colors.prison);
					
					return commandState.Accepted;
				}
				
				Prison prison = this.main.getPrisonFromLocation(gamer.stockedLeftClickLocation);
				
				if (prison == null)
				{
					gamer.sendMessage("command.prison.inexistant", this.main.colors.prison);
				}
				
				this.main.prisgivs.put(gamer.stockedLeftClickLocation, new Prisgiv(prison, gamer.stockedLeftClickLocation));
				
				gamer.sendMessage("command.prison.give.ok", this.main.colors.prison);
				
				return commandState.Accepted;
			}
			
			if (args[0].equalsIgnoreCase("chest"))
			{
				if (args.length != 3)
				{
					return commandState.Incorrect;
				}
				
				int inter1 = 0, inter2 = 0;
				
				try
				{
					inter1 = Integer.parseInt(args[1]);
				}
				catch (Exception ex)
				{
					return commandState.Incorrect;
				}
				
				try
				{
					inter2 = Integer.parseInt(args[2]);
				}
				catch (Exception ex)
				{
					return commandState.Incorrect;
				}
				
				if (inter1 < 0 || inter2 < 0)
				{
					return commandState.Incorrect;
				}
				
				if (gamer.stockedLeftClickLocation == null)
				{
					gamer.sendMessage("block.pleaseselect1", this.main.colors.prison);
					
					return commandState.Accepted;
				}
				
				if (gamer.stockedLeftClickLocation.getBlock().getType() != Material.CHEST)
				{
					gamer.sendMessage("command.prison.needchest", this.main.colors.prison);
					
					return commandState.Accepted;
				}
				
				Prison prison = this.main.getPrisonFromLocation(gamer.stockedLeftClickLocation);
				
				if (prison == null)
				{
					gamer.sendMessage("command.prison.inexistant", this.main.colors.prison);
				}
				
				this.main.prisches.put(gamer.stockedLeftClickLocation, new Prische(prison, gamer.stockedLeftClickLocation, new Dinger(inter1, inter2)));
				
				gamer.sendMessage("command.prison.chest.ok", this.main.colors.prison, prison.variable);
				
				return commandState.Accepted;
			}
			
			if (args[0].equalsIgnoreCase("respawn"))
			{
				if (args.length != 3)
				{
					return commandState.Incorrect;
				}
				
				int inter1 = 0, inter2 = 0;
				
				try
				{
					inter1 = Integer.parseInt(args[1]);
				}
				catch (Exception ex)
				{
					return commandState.Incorrect;
				}
				
				try
				{
					inter2 = Integer.parseInt(args[2]);
				}
				catch (Exception ex)
				{
					return commandState.Incorrect;
				}
				
				if (inter1 < 0 || inter2 < 0)
				{
					return commandState.Incorrect;
				}
				
				if (gamer.source.getItemInHand().getType() == Material.AIR)
				{
					gamer.sendMessage("command.prison.respawn.incorrect", this.main.colors.prison);
					
					return commandState.Accepted;
				}
				
				if (gamer.stockedLeftClickLocation == null)
				{
					gamer.sendMessage("block.pleaseselect1", this.main.colors.prison);
					
					return commandState.Accepted;
				}
				
				Prison prison = this.main.getPrisonFromLocation(gamer.stockedLeftClickLocation);
				
				if (prison == null)
				{
					gamer.sendMessage("command.prison.inexistant", this.main.colors.prison);
				}
				
				this.main.prispos.put(gamer.stockedLeftClickLocation, new Prispos(prison, gamer.stockedLeftClickLocation, new Dinger(inter1, inter2), gamer.source.getItemInHand().getType()));
				
				gamer.sendMessage("command.prison.respawn.ok", this.main.colors.prison, prison.variable);
				
				return commandState.Accepted;
			}
			
			return commandState.Incorrect;
		}

		if (label.equalsIgnoreCase("jail"))
		{
			if (!gamer.grad.canJail)
			{
				return commandState.NoRights;
			}
			
			if (args.length != 3)
			{
				return commandState.Incorrect;
			}
			
			int blocks = 0;
			
			try
			{
				blocks = Integer.parseInt(args[1]);
			}
			catch (Exception ex)
			{
				return commandState.Incorrect;
			}

			Gamer jailed = this.main.getGamer(args[0], true);
			
			if (jailed == null)
			{
				gamer.sendMessage("general.notconnected", this.main.colors.prison, args[0]);
				
				return commandState.Accepted;
			}
			
			Prison prison = this.main.getPrisonFromName(args[2]);
			
			if (prison == null)
			{
				gamer.sendMessage("command.jail.inexistant", this.main.colors.prison, args[2]);
				
				return commandState.Accepted;
			}
			
			jailed.setInJail(prison, blocks);
			
			jailed.refreshName();
			
			if (!gamer.is(jailed))
			{
				gamer.sendMessage("command.jail.ok", this.main.colors.prison, jailed.getNameColor(),
						this.main.getText(gamer.information.language, this.main.colors.prison, prison.getAbbreviation()), Integer.toString(blocks));
			}
			
			this.main.broadcastAdminChat(ChatColor.RED + "-> " + gamer.getNameColorPersonnalized(ChatColor.RED) + " jails " 
					+ jailed.getNameColorPersonnalized(ChatColor.RED) + " in " +  args[2] + " with " + blocks + " ores.");
			
			return commandState.Accepted;
		}
		
		if (label.equalsIgnoreCase("unjail"))
		{
			if (!gamer.grad.canUnjail)
			{
				return commandState.NoRights;
			}
			
			if (args.length != 1)
			{
				return commandState.Incorrect;
			}

			Gamer jailed = this.main.getGamer(args[0], true);
			
			if (jailed == null)
			{
				gamer.sendMessage("general.notconnected", this.main.colors.prison, args[0]);
				
				return commandState.Accepted;
			}
			
			if (!jailed.isPrison())
			{
				gamer.sendMessage("command.unjail.notin", this.main.colors.prison, jailed.getNameColor());
				
				return commandState.Accepted;
			}

			if (!gamer.is(jailed))
			{
				gamer.sendMessage("command.unjail.ok", this.main.colors.prison, jailed.getNameColor(),
						this.main.getText(gamer.information.language, jailed.getPrison().getAbbreviation()));
			}
			
			jailed.unJail();
			
			jailed.refreshName();
			
			this.main.broadcastAdminChat(ChatColor.RED + "-> " + gamer.getNameColorPersonnalized(ChatColor.RED) + " unjails " + jailed.getNameColorPersonnalized(ChatColor.RED) + ".");
			
			return commandState.Accepted;
		}
			
		return commandState.Error;
	}
	
	private void simulateGroupExit(Gamer gamer)
	{
		if (this.main.getGroup(gamer.groupid).isHost(gamer))
		{
			Gamer newHostGamer = this.main.getGroup(gamer.groupid).hostOut();
			
			if (newHostGamer == null)
			{
				this.main.removeGroup(gamer.groupid);
			}
			else
			{
				newHostGamer.sendMessage("command.group.youarenewhost", this.main.colors.group,
						gamer.getNameColor(), ChatColor.GREEN.toString());
	
				for (int i = 0; i < newHostGamer.getGroup().members.size(); i++)
				{
					newHostGamer.getGroup().members.get(i).sendMessage("command.group.newhost", this.main.colors.group, gamer.getNameColor(),
							newHostGamer.getNameColor(), ChatColor.GREEN.toString());
				}
			}
			
			gamer.groupid = 0;
			
			gamer.sendMessage("command.group.quit", this.main.colors.group);
			
			return;
		}
	
		int hisGroupId = gamer.groupid;
		//peut etre ameliorer en envoyant le meme message a celui qui se barre que les autres membres du groupe
		this.main.getGroup(gamer.groupid).remove(gamer);
		
		this.main.getGroup(hisGroupId).getHost().sendMessage("command.group.hasquit", this.main.colors.group,
				gamer.getNameColor(), ChatColor.GREEN.toString());
	
		for (int i = 0; i < this.main.getGroup(hisGroupId).members.size(); i++)
		{
			this.main.getGroup(hisGroupId).members.get(i).sendMessage("command.group.hasquit", this.main.colors.group,
					gamer.getNameColor(), ChatColor.GREEN.toString());
		}
	
		gamer.sendMessage("command.group.quit", this.main.colors.group);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockSpread(BlockSpreadEvent event)
	{
		if (event.getNewState().getType() == Material.FIRE)
		{
			if (this.main.collisionWithPlots(event.getBlock().getLocation()) != collisionType.NoCollision)
			{
				event.setCancelled(true);
			}
		}

		return;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onFoodLevelChange(FoodLevelChangeEvent event)
	{
		Gamer gamer = this.main.getGamer(event.getEntity().getName(), true);
		
		Damevent dame = gamer.foodChanged(event.getFoodLevel());
		
		event.setCancelled(!dame.notCancelled);
		
		event.setFoodLevel((int)dame.damage);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onIgniteFire(BlockIgniteEvent event)
	{
		if (event.getPlayer() == null)
		{
			/*Indestructible indestructible = this.main.getIndestructibleFromLocation(event.getBlock().getLocation());
			
			if (indestructible != null)
			{
				event.setCancelled(true);
			}*/
		
			if (this.main.collisionWithPlots(event.getBlock().getLocation()) != collisionType.NoCollision)
			{
				event.setCancelled(true);
			}
			
			return;
		}
		
		Gamer gamer = this.main.getGamer(event.getPlayer().getName(), true);
		
		Indestructible indestructible = this.main.getIndestructibleFromLocation(event.getBlock().getLocation(), 1);
		
		if (indestructible != null)
		{
			if (!gamer.grad.canBreakWhereLocked)
			{
				event.setCancelled(true);
			}
			
			return;
		}
		
		Terrain terrain = this.main.getTerrainFromLocation(event.getBlock().getLocation(), 1);
		
		if (terrain != null)
		{
			if (!terrain.owner.isOwner(gamer) && !terrain.whitisble.canAccess(gamer) && !gamer.grad.canBreakWhereReserved)
			{
				event.setCancelled(true);
			}
			
			return;
		}
		
		Prison prison = this.main.getPrisonFromLocation(event.getBlock().getLocation(), 1);
		
		if (prison != null)
		{
			if (!gamer.grad.canGodJails)
			{
				event.setCancelled(true);
			}
			
			return;
		}
		
		Clan clan = this.main.getClanFromLocation(event.getBlock().getLocation(), 1);
		
		if (clan != null)
		{
			Clan myClan = gamer.getClan();
			
			if (myClan == null)
			{
				event.setCancelled(true);
				
				return;
			}
			
			if (myClan.connectedGamers.size() < this.main.configuration.clanMinimumInteract)
			{
				event.setCancelled(true);
				
				return;
			}
			
			if (clan.connectedGamers.size() < this.main.configuration.clanMinimumInteract)
			{
				event.setCancelled(true);
				
				return;
			}
			
			return;
		}
		
		/*collisionType collision = this.main.collisionWithPlots(event.getBlock().getLocation());
		
		if (collision != collisionType.Clan)
		{
			if (collision != collisionType.NoCollision)
			{
				event.setCancelled(true);
				
				return;
			}
		}
		else
		{
			if (event.getPlayer() == null)
			{
				return;
			}
			
			Gamer gamer = this.main.getGamer(event.getPlayer().getName(), true);
			
			Clan myClan = gamer.getClan();
			
			if (myClan == null)
			{
				event.setCancelled(true);
				
				return;
			}
			
			if (myClan.connectedGamers.size() <= this.main.configuration.clanMinimumInteract)
			{
				event.setCancelled(true);
				
				return;
			}
			
			Clan clan = this.main.getClanFromLocation(event.getBlock().getLocation());
			
			if (clan.connectedGamers.size() <= this.main.configuration.clanMinimumInteract)
			{
				event.setCancelled(true);
				
				return;
			}
		}*/
	}
	
	private NameArgCommandInt effectuateNameArgCommand(Gamer sender, boolean rights, String[] args, int min, int max)
	{
		if (!rights)
		{
			return new NameArgCommandInt(NameArgCommandInt.nameArgCommand.NoRights);
		}
		
		if (args.length != 2)
		{
			return new NameArgCommandInt(NameArgCommandInt.nameArgCommand.Incorrect);
		}

		if (!this.main.isConnected(args[0], true))
		{
			return new NameArgCommandInt(NameArgCommandInt.nameArgCommand.NotConnected);
		}

		int number = 0;
		
		try
		{
			number = Integer.parseInt(args[1]);
		}
		catch (Exception ex)
		{
			return new NameArgCommandInt(NameArgCommandInt.nameArgCommand.Incorrect);
		}
		
		if (max == 0)
		{
			if (number < min)
			{
				return new NameArgCommandInt(NameArgCommandInt.nameArgCommand.Incorrect);
			}
		}
		else if (number < min || number > max)
		{
			return new NameArgCommandInt(NameArgCommandInt.nameArgCommand.Incorrect);
		}
		
		if (args[0].equalsIgnoreCase(sender.information.name))
		{
			return new NameArgCommandInt(NameArgCommandInt.nameArgCommand.You, number);
		}

		return new NameArgCommandInt(NameArgCommandInt.nameArgCommand.Ok, number);
	}
	
	public enum commandState
	{
		Accepted,
		Error,
		NoRights,
		Incorrect
	}
}