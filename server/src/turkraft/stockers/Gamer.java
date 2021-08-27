package turkraft.stockers;

import java.util.Collection;

import net.minecraft.server.v1_4_6.EntityPlayer;
import net.minecraft.server.v1_4_6.Packet250CustomPayload;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_4_6.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import turkraft.Turkraft;
import turkraft.common.Manipulation;

public class Gamer
{
	private Turkraft main;
	
	public Player source;
	
	public Grad grad;

	public Information information;
	
	public Gamer chatResponsePlayer;
	
	public boolean isSpawnProtected;
	
	public Location stockedLeftClickLocation, stockedRightClickLocation;
	
	public int groupid;
	
	public int lastGroupInvite;
	
	public Location deathLocation;
	
	private long joinTime;
	
	public int lastClanInvite;
	
	public String format;
	
	public Gamer lastDamager;
	
	private boolean invisible;
	
	private boolean isAFK;
	
	private int afkThreadId;
	
	public Gamer(Turkraft main, Player source, Information information)
	{
		this.main = main;
		
		this.source = source;
		
		this.information = information;
		
		this.format = "<%1$s> %2$s";
		
		this.isSpawnProtected = false;
		
		this.reloadGrad();

		this.refreshJoinTime();
	}
	
	public void reloadGrad()
	{
		this.grad = this.main.getGradFromId(this.information.grad);
	}
	
	public String getNameColor()
	{
		return this.grad.color + this.information.name;
	}
	
	public String getNameColorPersonnalized()
	{
		return this.getNameColorPersonnalized(ChatColor.WHITE);
	}
	
	public String getNameColorPersonnalized(ChatColor style)
	{
		return this.getNameColor() + style;
	}
	
	public String getNameTag()
	{
		if (this.getClan() == null)
		{
			return ChatColor.WHITE + "<" + this.getNameColorPersonnalized(ChatColor.WHITE) + ">";
		}
		else
		{
			return ChatColor.WHITE + "[" + this.main.colors.clan + this.getClan().name + ChatColor.WHITE + "]<" + this.getNameColorPersonnalized(ChatColor.WHITE) + ">";
		}
	}
	
	public void refreshName()
	{
		String chatColor = "";
		
		if (this.isPrison())
		{
			chatColor = this.main.colors.prison.toString();
		}
		
		if (this.getClan() == null)
		{
			this.source.setDisplayName(this.getNameColorPersonnalized(ChatColor.WHITE));

			this.format = "<%1$s> " + chatColor + "%2$s"; 
		}
		else
		{
			this.source.setDisplayName(this.getNameTag());

			this.format = "%1$s " + chatColor + "%2$s"; 
		}

		this.source.setPlayerListName(this.getNameColorPersonnalized(ChatColor.WHITE));
	}
	
	public void refreshFlight()
	{
    	if (this.source.getGameMode() != GameMode.CREATIVE)
    	{
    		this.source.setAllowFlight(this.grad.canFly);
    	}
    	else
    	{
    		this.source.setAllowFlight(true);
    	}
	}
	
	public void setInvisible()
	{
		this.setInvisible(!this.invisible);
	}
	
	public void setInvisible(boolean invisible)
	{
		if (!invisible)
		{
		    for (Player player : this.main.getServer().getOnlinePlayers())
		    {
		    	if (!player.canSee(this.source))
		    	{
		    		player.showPlayer(this.source);
		    	}
		    }
		    //this.source.removePotionEffect(PotionEffectType.INVISIBILITY);
		}
		else
		{
		    for (Player player : this.main.getServer().getOnlinePlayers())
		    {
		    	if (player.canSee(this.source))
		    	{
		    		player.hidePlayer(this.source);
		    	}
		    }
		    //this.source.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999999, 1));
		}
		
		this.invisible = invisible;
	}
	
	public void initialize()
	{
		this.invisible = false;
		
		Clan clan = this.getClan();
		
		if (clan != null)
		{
			clan.connectedGamers.add(this);
		}
		else
		{
			this.information.clan = 0;
		}
		
		if (this.information.prisonLeft <= 0 || this.information.prisonIn <= 0)
		{
			this.information.prisonLeft = 0;
			
			this.information.prisonIn = 0;
		}
		
		this.refreshName();

    	this.refreshFlight();
    	
		this.source.setWalkSpeed((float)0.2);
		
		this.source.setMaxHealth(20);
    	
    	if (this.IsType(this.main.genres.Warrior))
    	{
    		this.source.setWalkSpeed((float)(this.source.getWalkSpeed() / 1.07));
    		
    		this.source.setMaxHealth((int)(this.source.getMaxHealth() * 1.2));
    	}
    	else if (this.IsType(this.main.genres.Thief))
    	{
    		this.source.setWalkSpeed((float)(this.source.getWalkSpeed() * 1.085));
    	}
    	
    	if (this.isPrison())
    	{
    		Prison prison = this.getPrison();
    		
    		this.source.teleport(prison.spawn);
    		
    		this.sendMessage("prison.still", this.main.colors.prison,
    				this.main.getText(this.information.language, this.main.colors.prison, prison.getAbbreviation()));
    	}
    	
    	if (this.main.sanguine)
    	{
    		this.sendData("sanguine", "1");
    	}
	}
	
	public void sendMessage(String message)
	{
		this.source.sendMessage(message);
	}
	
	public void sendMessage(String abbrev, boolean isAbbrev)
	{
		if (isAbbrev)
		{
			this.sendMessage(abbrev, new String [] {});
		}
		else
		{
			this.sendMessage(abbrev);
		}
	}
	
	public void sendMessage(String abbreviation, ChatColor color)
	{
		this.sendMessage(abbreviation, color, new String [] {});
	}
	
	public void sendMessage(String abbreviation, String ... args)
	{
		this.sendMessage(abbreviation, ChatColor.WHITE, args);
	}
	
	public void sendMessage(String abbreviation, ChatColor color, String ... args)
	{
		this.sendMessage(this.main.getText(this.information.language, abbreviation, color, args));
	}
	
	public boolean isConnected()
	{
		return this.source.isOnline();
	}
	
	public boolean inGroup()
	{
		return this.groupid != 0;
	}
	
	public Group getGroup()
	{
		if (this.groupid == 0)
		{
			return null;
		}
		
		return this.main.getGroup(this.groupid);
	}
	
	public Clan getClan()
	{
		if (this.information.clan == 0)
		{
			return null;
		}
		
		return this.main.clans.get(this.information.clan);
	}
	
	public Damevent attackGamerEvent(Gamer target, int damage)
	{
    	this.setUnAFK();
    	
		if (this.isSpawnProtected)
		{
			this.sendMessage("player.attack.youarespawntime", this.main.colors.diverse);
			
			return new Damevent(false, damage);
		}
		
		if (target.isSpawnProtected)
		{
			this.sendMessage("player.attack.heisspawntime", this.main.colors.diverse, target.getNameColor());

			return new Damevent(false, damage);
		}
		
		if (target.isAFK)
		{
			this.sendMessage("player.attack.isafk", this.main.colors.diverse, target.getNameColor());

			return new Damevent(false, damage);
		}
		
		if (this.inGroup())
		{
			if (target.groupid == this.groupid)
			{
				return new Damevent(false, damage);
			}
		}
		
		this.information.lastDamage = System.currentTimeMillis();
		
		this.lastDamager = target;
		
		if (this.IsType(this.main.genres.Warrior) && Manipulation.isSword(this.source.getItemInHand().getType()))
		{
			damage *= 1.1;
		}
		else if (this.IsType(this.main.genres.Archer))
		{
			if (this.source.getItemInHand().getType() == Material.BOW)
			{
				damage *= 1.1;
			}
			else if (Manipulation.isSword(this.source.getItemInHand().getType()))
			{
				damage /= 1.1;
			}
		}
		else if (this.IsType(this.main.genres.Thief) && Manipulation.isHoe(this.source.getItemInHand().getType())
				&& this.source.hasPotionEffect(PotionEffectType.INVISIBILITY))
		{
			damage = Manipulation.damageFromHoe(this.source.getItemInHand().getType());
			
			//if (target.source.canSee(this.source))
			//{
				if (damage <= 5)
				{
					int chanceOf = Manipulation.random(0, 20);
					
					if (chanceOf == 2)
					{
						int tires = Manipulation.random(2, 30);
						
						if (tires >= target.information.money)
						{
							tires = target.information.money;
						}
						
						this.information.money += tires;
						
						target.information.money -= tires;
						
						target.sendMessage("player.killedby.player.loosem", this.main.colors.money, this.getNameColor(),
								Integer.toString(tires));
				
						this.sendMessage("player.kills.player.lootm", this.main.colors.money, target.getNameColor(),
								Integer.toString(tires));
					}
				}
			//}
		}

		return new Damevent(true, damage);
	}
	
	public Damevent attackEntityEvent(Entity target, int damage)
	{
    	this.setUnAFK();
    	
		if (Manipulation.isAnimal(target.getType()))
		{
			Terrain targetTerrain = this.main.getTerrainFromLocation(target.getLocation());
			
			if (targetTerrain != null && !targetTerrain.owner.isOwner(this) && !targetTerrain.whitisble.canAccess(this))
			{
				this.sendMessage("player.attack.animal.notinotherterrain", this.main.colors.diverse);

				return new Damevent(false, damage);
			}
		}
		
		this.information.lastDamage = System.currentTimeMillis();
		
		this.lastDamager = null;
		
		if (this.IsType(this.main.genres.Warrior) && Manipulation.isSword(this.source.getItemInHand().getType()))
		{
			damage *= 1.2;
		}
		else if (this.IsType(this.main.genres.Archer))
		{
			if (this.source.getItemInHand().getType() == Material.BOW)
			{
				damage *= 1.1;
			}
			else if (Manipulation.isSword(this.source.getItemInHand().getType()))
			{
				damage /= 1.2;
			}
		}
		else if (this.IsType(this.main.genres.Thief) && Manipulation.isHoe(this.source.getItemInHand().getType())
				&& this.source.hasPotionEffect(PotionEffectType.INVISIBILITY))
		{
			damage *= Manipulation.random(0, 17);
		}

		return new Damevent(true, damage);
	}
	
	public int attackedByGamerEvent(Gamer damager, int damage)
	{
		this.information.lastDamage = System.currentTimeMillis();
		
		this.lastDamager = damager;
		
		if (this.IsType(this.main.genres.Wizard))
		{
			return (int)(damage / 1.2);
		}
		else if (this.IsType(this.main.genres.Thief))
		{
			return (int)(damage * 1.3);
		}
		
		return damage;
	}
	
	public int attackedByEntityEvent(Entity damager, int damage)
	{
		if (this.isAFK)
		{
			return 0;
		}
		
		this.information.lastDamage = System.currentTimeMillis();
		
		this.lastDamager = null;
		
		if (this.IsType(this.main.genres.Wizard))
		{
			return (int)(damage / 1.2);
		}
		else if (this.IsType(this.main.genres.Thief))
		{
			return (int)(damage * 1.3);
		}
		
		return damage;
	}
	
	public void killedByNothing()
	{
		if (!this.isPrison())
		{
			this.information.nothingDie++;
		}
		
		this.deathLocation = this.source.getLocation();
		
		this.main.log.log("kills", this.source.getName() + " killed by nothing");
	}
	
	public void killedByGamer(Gamer killer)
	{
		this.deathLocation = this.source.getLocation();
		
		if (this.is(killer))
		{
			return;
		}
		
		if (!this.isPrison())
		{
			this.information.humanDie++;
		}

		this.isSpawnProtected = true;
		
		this.main.getServer().getScheduler().scheduleSyncDelayedTask(this.main, new Runnable()
		{
			public void run()
			{
				isSpawnProtected = false;
			}
		}, this.isPrison() ? this.main.configuration.protectedSpawnTime : this.main.configuration.protectedSpawnTime / 2);
		
		this.main.log.log("kills", this.source.getName() + " killed by " + killer.source.getName() + " (P)");
	}
	
	public void killedByEntity(Entity killer)
	{
		if (killer.getType() == EntityType.PLAYER)
		{
			return;
		}
		
		if (!this.isPrison())
		{
			this.information.monsterDie++;
		}
		
		this.deathLocation = this.source.getLocation();
		
		this.main.log.log("kills", this.source.getName() + " killed by " + killer.getType() + " (E)");
	}
	
	public void killedGamer(Gamer target)
	{
		if (this.is(target))
		{
			return;
		}
		
		if (!this.isPrison())
		{
			this.information.humanKill++;
		}
		
		if (this.lastDamager == target)
		{
			this.information.lastDamage = 0;
		}

		int moneyReceive = target.information.money / Manipulation.random(
				this.main.configuration.killMoneyPourcentage - this.main.configuration.killPourcentageRandom,
				this.main.configuration.killMoneyPourcentage + this.main.configuration.killPourcentageRandom);
		
		if (moneyReceive == 0)
		{
			return;
		}

		Group group = this.getGroup();
		
		int each = 0;
		
		if (group != null)
		{
			each = moneyReceive / group.count();
		}
		
		target.information.money -= moneyReceive;
		
		if (group == null || group.count() <= 1 || each < 1)
		{
			this.information.money += moneyReceive;
			
			target.sendMessage("player.killedby.player.loosemoney", this.main.colors.money, this.getNameColor(),
					Integer.toString(moneyReceive));
	
			this.sendMessage("player.kills.player.gainmoney", this.main.colors.money, target.getNameColor(),
					Integer.toString(moneyReceive)); //<-- premier arg faux? this au lieu de target?
		}
		else
		{
			group.broadcast("player.kills.player.gainmoneygroup",
						this.main.colors.money, target.getNameColor(), Integer.toString(each));
			
			target.sendMessage("player.killedby.player.loosemoneygroup",
					this.main.colors.money, this.getNameColor(), Integer.toString(moneyReceive));
		}
		
		Clan myClan = this.getClan();
		
		if (myClan != null && myClan.connectedGamers.size() > this.main.configuration.clanMinimumInteract)
		{
			Clan targetClan = target.getClan();
			
			if (targetClan != null && targetClan.connectedGamers.size() > this.main.configuration.clanMinimumInteract)
			{
				if (myClan.plot.inPlot(this.source.getLocation(), 10) || myClan.plot.inPlot(target.source.getLocation(), 10)
						|| targetClan.plot.inPlot(this.source.getLocation(), 10) || targetClan.plot.inPlot(target.source.getLocation(), 10))
				{
					targetClan.hasChanged = true;
					
					myClan.hasChanged = true;
					
					targetClan.score--;
					
					myClan.score++;
					
					if (targetClan.score <= 0)
					{
						if (targetClan.inDatabase)
						{
							this.main.deleteClans.add(targetClan.virtual);
						}
						
						this.main.clans.remove(targetClan.virtual);
						
						this.main.broadcastTextAbbrev("clan.destroyed", this.main.colors.server, targetClan.name);
						
						this.main.log.log("clans", "Clan " + targetClan.name + " destroyed by clan " + myClan.name);
						
						targetClan.dispose();
						
						myClan.taken++;
					}
					else if (targetClan.score <= this.main.configuration.alertRestClan)
					{
						targetClan.broadcast(main, "clan.attacked.risk", Integer.toString(targetClan.score));
					}
				}
			}
		}
		
		this.checkBiggestKiller(moneyReceive);
	}
	
	public float killedEntity(Entity target, float xp)
	{
		MobMoney mobMoney = this.main.getMobMoney(target.getType());
		
		if (mobMoney == null || target.hasMetadata("isSpawner"))
		{
			if (target.hasMetadata("isSpawner"))
			{
				return xp / 2;
			}
			
			//pas la peine d'afficher le kill quand le mob fait rien gagner -->
	    	//this.sendMessage(this.main.getText(this.information.language, "player.kills.mob",
	    	//		this.main.getText(this.information.language, "mobs." + target.getType().toString().toLowerCase())));
			
			return xp;
		}

		if (!this.isPrison())
		{
			this.information.monsterKill++; //il n'y a que les entites qui donnent de l'argent qui augmentent le nombre de kill
		}

		int money = mobMoney.getMoney();
		
		if (this.inGroup() && this.getGroup().count() > 1)
		{
			Group group = this.getGroup();
			
			int each = money / group.count();
			
			if (each < 1)
			{
				this.killMob(target, mobMoney, money);
				
				return xp;
			}
			
			int xpEach = (int)((float)(xp /this.getGroup().count()));
			
			for (int i = 0; i < group.allGamers().size(); i++)
			{
				group.allGamers().get(i).information.money += each;
				
				group.allGamers().get(i).source.giveExp(xpEach);
			}
			
			if (!mobMoney.congrat)
			{
				group.broadcast("command.group.kill.gain", this.main.colors.money, Integer.toString(each));
			}
			else
			{
				this.main.log.log("kills", "Congratulation to " + group.getList() + " for killing " + target.getType() + " for " + each + " tires each");
				
				Language[] languages = this.main.getLanguages();

				String abbrev = "player.gain.money.kill.groupcongrat";
				
				for (int i = 0; i < languages.length; i++)
				{
					this.main.broadcastSameLanguage(languages[i].id, this.main.getText(languages[i].id, abbrev, this.main.colors.server, this.getNameColor(),
							this.main.getText(languages[i].id, "mobs." + target.getType().toString().toLowerCase(), this.main.colors.server), Integer.toString(money)));
				}
			}
			
			return 0;
		}
		
		this.killMob(target, mobMoney, money);
		
		return xp;
	}
	
	private void killMob(Entity target, MobMoney mobMoney, int money)
	{
		this.information.money += money;
		
		if (!mobMoney.congrat)
		{
			this.sendMessage("player.gain.money.kill.mob", this.main.colors.money,
					this.main.getText(this.information.language, "mobs." + target.getType().toString().toLowerCase(), this.main.colors.money), Integer.toString(money));
			
			return;
		}

		this.main.log.log("kills", "Congratulations to " + this.source.getName() + " for killing " + target.getType().getName() + " for " + money + " tires");

		//on envoie le message de felication a tout le monde -->
		
		Language[] languages = this.main.getLanguages();
		
		String abbrev = "player.gain.money.kill.mobcongrat";
		
		for (int i = 0; i < languages.length; i++)
		{
			this.main.broadcastSameLanguage(languages[i].id, this.main.getText(languages[i].id, abbrev, this.main.colors.server, this.getNameColor(),
					this.main.getText(languages[i].id, "mobs." + target.getType().toString().toLowerCase(), this.main.colors.server), Integer.toString(money)));
		}
		
		this.checkBiggestKiller(money);
	}
	
	public Damevent launchPotion(ThrownPotion poition, Collection<LivingEntity> entities) //Damevent.damage = multiplicateur
	{
		if (this.IsType(this.main.genres.Wizard))
		{
			return new Damevent(true, 1.3);
		}
		
		return new Damevent(true, 1);
	}
	
	public Damevent receivePotion(Gamer gamer, ThrownPotion potion, double intensity)
	{
		if (gamer.IsType(this.main.genres.Wizard))
		{
			return new Damevent(true, intensity / 1.3);
		}

		return new Damevent(true, intensity);
	}
	
	public Damevent foodChanged(int foodLevel)
	{
		if (this.IsType(this.main.genres.Archer))
		{
			return new Damevent(true, foodLevel + Manipulation.random(0, 1));
		}
		
		return new Damevent(true, foodLevel);
	}
	
	public boolean IsType(int genre)
	{
		return this.information.genre == genre;
	}
	
	private boolean checkBiggestKiller(int gain)
	{
		if (this.main.configuration.maximumKillMoney < gain)
		{
			this.main.configuration.maximumKillMoney = gain;
			
			this.main.broadcastTextAbbrev("player.gain.money.kill.congrat", this.main.colors.server, this.getNameColor(), Integer.toString(gain));

			this.main.log.log("kills", "The biggest killer is now " + this.source.getName() + " with " + gain + " tires");

			return true;
		}
		
		return false;
	}
	
	public boolean blockLeftClick(Location location, Material material, Material inHand)
	{
		if (this.grad.canSelectBlock && inHand == Material.STICK)
		{
			if (this.stockedLeftClickLocation == null || (this.stockedLeftClickLocation.getBlockX() != location.getBlockX() ||
					this.stockedLeftClickLocation.getBlockY() != location.getBlockY() ||
					this.stockedLeftClickLocation.getBlockZ() != location.getBlockZ()))
			{
				this.stockedLeftClickLocation = location;

				this.sendMessage("block.selected1", this.main.colors.diverse,
						Integer.toString(this.stockedLeftClickLocation.getBlockX()),
						Integer.toString(this.stockedLeftClickLocation.getBlockY()),
						Integer.toString(this.stockedLeftClickLocation.getBlockZ()));
			}
			
			return false;
		}

		if (material == Material.ITEM_FRAME || material == Material.PAINTING)
		{
			return this.blockBreakEvent(location, material);
		}
		
		return true;
	}
	
	public boolean blockRightClick(Location location, Material material, ItemStack item)
	{
		if (this.grad.canSelectBlock && (item != null && item.getType() == Material.STICK))
		{
			if (this.stockedRightClickLocation == null || (this.stockedRightClickLocation.getBlockX() != location.getBlockX() ||
					this.stockedRightClickLocation.getBlockY() != location.getBlockY() ||
					this.stockedRightClickLocation.getBlockZ() != location.getBlockZ()))
			{
				this.stockedRightClickLocation = location;
				
				this.sendMessage("block.selected2", this.main.colors.diverse,
						Integer.toString(this.stockedRightClickLocation.getBlockX()),
						Integer.toString(this.stockedRightClickLocation.getBlockY()),
						Integer.toString(this.stockedRightClickLocation.getBlockZ()));
			}
			
			return false;
		}
		
		if (material == Material.SIGN
				|| material == Material.SIGN_POST
				|| material == Material.ITEM_FRAME)
		{
			Shop shop = this.main.shops.get(location);
			
			if (shop != null)
			{
				if (shop.ownable.isOwner(this) || this.grad.canGodSalables)
				{
					if (item == null)
					{
						return false;
					}
					
					shop.update(item);
					
					this.source.getInventory().removeItem(item);
					
					this.sendMessage("shop.updated",this.main.colors.shop);
					
					return false;
				}
				else
				{
					if (!shop.pricable.isToSell())
					{
						this.sendMessage("shop.nottosell", this.main.colors.shop);
						
						return false;
					}
					
					if (shop.material == null || shop.quantity == 0)
					{
						this.sendMessage("shop.empty", this.main.colors.shop);
						
						return false;
					}
					
					if (this.information.money < shop.pricable.price)
					{
						this.sendMessage("shop.nomoney", this.main.colors.shop, Integer.toString(shop.pricable.price));
						
						return false;
					}
					
					if (shop.diminuates)
					{
						shop.quantity--;
						
						Gamer owner = this.main.getGamer(shop.ownable.owner, true);
						
						if (owner != null)
						{
							owner.information.money += shop.pricable.price;
						}
						else
						{
							shop.reserve += shop.pricable.price;
						}
					}
					
					shop.hasChanged = true;
					
					shop.sells++;

					this.information.money -= shop.pricable.price;
					
					this.source.getInventory().addItem(shop.get());
					
					this.updateInventory();
					
					this.sendMessage("shop.buyed", this.main.colors.shop, Integer.toString(shop.pricable.price),
							this.main.getMaterialName(this.information.language, shop.material));
					
					return false;
				}
			}
			else if (material == Material.ITEM_FRAME)
			{
				return this.blockInteractEvent(location, material, item.getType());
			}
		}
		
		return true;
	}
	
	public boolean blockPlaceEvent(Location location, Material material, Material material2)
	{
		boolean state = this.canDo(doableState.blockPlace, location, material, material2);
		
		if (!state)
		{
			return false;
		}

		if (this.grad.noBlockLoose)
		{
			this.source.getInventory().addItem(new ItemStack(material, 1)); //performance nul...
		}
		
		return true;
	}
	
	public boolean blockBreakEvent(Location location, Material material)
	{
		boolean state = this.canDo(doableState.blockBreak, location, material, null);
		
		if (!state)
		{
			return false;
		}
		
		if (material == Material.SIGN
				|| material == Material.SIGN_POST
				|| material == Material.ITEM_FRAME)
		{
			Shop shop = this.main.shops.get(location);
			
			if (shop != null)
			{
				if (!shop.ownable.isOwner(this) && !this.grad.canGodSalables)
				{
					return false;
				}
				
				shop.position.getWorld().dropItem(shop.position, shop.getTotal());
				
				if (shop.inDatabase)
				{
					this.main.deleteShops.add(shop.id);
				}
				
				this.main.shops.remove(shop.position);
				
				this.sendMessage("command.shop.deleted.ok", this.main.colors.shop);
				
				return true;
			}
		}
		
		if (this.grad.canGodJails && location.getBlock().getType() == Material.CHEST)
		{
			Prisgiv prisgiv = this.main.prisgivs.get(location);
			
			if (prisgiv != null)
			{
				if (prisgiv.inDatabase)
				{
					this.main.deletePrisgivs.add(prisgiv.id);
				}
				
				this.main.prisgivs.remove(location);
				
				this.sendMessage("prison.prisgiv.deleted", this.main.colors.prison);
				
				return true;
			}

			Prische prische = this.main.prisches.get(location);
			
			if (prische != null)
			{
				if (prische.inDatabase)
				{
					this.main.deletePrisches.add(prische.id);
				}
				
				this.main.prisches.remove(location);
				
				this.sendMessage("prison.prische.deleted", this.main.colors.prison);
				
				return true;
			}
		}

		final Prispos prispos = this.main.prispos.get(location);
		
		if (this.grad.canGodJails)
		{
			if (prispos != null)
			{
				if (prispos.inDatabase)
				{
					this.main.deletePrispos.add(prispos.id);
				}
				
				this.main.prispos.remove(location);
				
				this.sendMessage("prison.prispos.deleted", this.main.colors.prison);
				
				return true;
			}
		}
		else if (this.isPrison())
		{
			if (prispos == null)
			{
				this.sendMessage("block.break.prison.not", this.main.colors.diverse);
				
				return false;
			}
			
			this.main.getServer().getScheduler().scheduleSyncDelayedTask(this.main, new Runnable()
			{
				   public void run()
				   {
						prispos.setBlock();
				   }
			}, Manipulation.random(prispos.interval.x, prispos.interval.y));
			
			return true;
		}
		
		return true;
	}
	
	/*public boolean blockInteractEvent(PlayerInteractEvent event)
	{
		return this.canDo(doableState.blockInteract, event.getClickedBlock().getLocation(), event.getClickedBlock().getType(), event.getMaterial());
	}*/
	
	public boolean blockInteractEvent(Location location, Material type, Material item)
	{
		return this.canDo(doableState.blockInteract, location, type, item);
	}
	
	public boolean blockSoilEvent(PlayerInteractEvent event)
	{
		return this.canDo(doableState.blockSoil, event.getClickedBlock().getLocation(), event.getClickedBlock().getType(), null);
	}
	
	private boolean canDo(doableState state, Location point, Material material, Material material2)
	{
		Indestructible indestructible = this.main.getIndestructibleFromLocation(point);
		
		if (indestructible != null)
		{
			if (this.grad.canLock)
			{
				return true;
			}
			
			switch (state)
			{
				case blockPlace:
					if (!this.grad.canPlaceWhereLocked)
					{
						this.sendMessage("block.place.locked.not", this.main.colors.diverse);
						return false;
					}
					return true;
				case blockBreak:
					if (!this.grad.canBreakWhereLocked)
					{
						this.sendMessage("block.break.locked.not", this.main.colors.diverse);
						return false;
					}
					return true;
				case blockInteract:
					if (!this.grad.canInteractWhereLocked)
					{
						this.sendMessage("block.interact.locked.not", this.main.colors.diverse);
						return false;
					}
					return true;
				case blockSoil:
					if (!this.grad.canSoilDestroyLocked)
					{
						//on envoie pas de message pour quand le joueur ecrase (du ble par exemple)
						//this.sendMessage(this.main.getText(this.information.language, "block.soil.locked.not"));
						return false;
					}
					return true;
			}
		}
		
		Terrain terrain = this.main.getTerrainFromLocation(point);
		
		if (terrain != null)
		{
			if (terrain.owner.isOwner(this) || terrain.whitisble.canAccess(this))
			{
				return true;
			}
			
			if (this.grad.canGodTerritories)
			{
				return true;
			}
			
			switch (state)
			{
				case blockPlace:
					if (!this.grad.canPlaceWhereReserved)
					{
						this.sendMessage("block.place.reserved.not", this.main.colors.diverse);
						return false;
					}
					return true;
				case blockBreak:
					if (!this.grad.canBreakWhereReserved)
					{
						this.sendMessage("block.break.reserved.not", this.main.colors.diverse);
						return false;
					}
					return true;
				case blockInteract:
					if (!this.grad.canInteractWhereReserved)
					{
						this.sendMessage("block.interact.reserved.not", this.main.colors.diverse);
						return false;
					}
					return true;
				case blockSoil:
					if (!this.grad.canSoilDestroyReserved)
					{
						//this.sendMessage(this.main.getText(this.information.language, "block.soil.locked.not"));
						return false;
					}
					return true;
			}
		}

		Prison prison = this.main.getPrisonFromLocation(point);
		
		if (prison != null)
		{
			if (this.grad.canGodJails)
			{
				return true;
			}
			
			switch (state)
			{
				case blockPlace:
					if (!this.grad.canPlaceWhereReserved)
					{
						this.sendMessage("block.place.prison.not", this.main.colors.diverse);
						return false;
					}
					return true;
				case blockBreak:
					if (!this.grad.canBreakWhereReserved)
					{
						if (!this.isPrison())
						{
							this.sendMessage("block.break.prison.not", this.main.colors.diverse);
							return false;
						}
						else
						{
							return true;
						}
					}
					return true;
				case blockInteract:
					if (!this.grad.canInteractWhereReserved)
					{
						if (!this.isPrison())
						{
							this.sendMessage("block.interact.prison.not", this.main.colors.diverse);
							return false;
						}
						if (material == Material.WORKBENCH || material == Material.FURNACE || material == Material.BURNING_FURNACE)
						{
							return true;
						}
						if (material == Material.CHEST && this.main.prisches.containsKey(point))
						{
							return true;
						}
						if (material == Material.CHEST && this.main.prisgivs.containsKey(point))
						{
							if (material2 == null || material2 == Material.AIR)
							{
								this.sendMessage("prison.give.notitem", this.main.colors.diverse);
								return false;
							}
							Prisval prisval = this.main.getPrsivalFromItem(material2);
							if (prisval == null)
							{
								this.sendMessage("prison.give.notitem", this.main.colors.diverse);
								return false;
							}
							
							this.information.prisonLeft -= this.source.getItemInHand().getAmount() * prisval.value;
							
							this.source.getInventory().remove(this.source.getItemInHand());
							
							if (this.information.prisonLeft <= 0)
							{
								this.unJail();
							}
							
							//make the give
							return false;
						}
						this.sendMessage("block.interact.prison.not", this.main.colors.diverse);
						return false;
					}
					return true;
				case blockSoil:
					if (!this.grad.canSoilDestroyReserved)
					{
						//this.sendMessage(this.main.getText(this.information.language, "block.soil.locked.not"));
						return false;
					}
					return true;
			}
		}
		
		Clan clan = this.main.getClanFromLocation(point);
		
		if (clan != null)
		{
			if (clan.isIn(this))
			{
				return true;
			}
			
			if (this.grad.canGodClans)
			{
				return true;
			}
			
			switch (state)
			{
				case blockPlace:
					if (material == Material.TNT)
					{
						Clan myClan = this.getClan();
						if (myClan == null)
						{
							this.sendMessage("block.clan.need.action", this.main.colors.clan);
							return false;
						}
						if (myClan.connectedGamers.size() <= this.main.configuration.clanMinimumInteract)
						{
							this.sendMessage("block.clan.need.action.member", this.main.colors.clan);
							return false;
						}
						if (clan.connectedGamers.size() <= this.main.configuration.clanMinimumInteract)
						{
							this.sendMessage("block.break.clan.not.noconnected", this.main.colors.clan);
							return false;
						}
						clan.broadcast(this.main, "clan.attacked.tntflow", this.getNameColor());
						return true;
					}
					if (!this.grad.canPlaceWhereClan)
					{
						this.sendMessage("block.place.clan.not", this.main.colors.diverse);
						return false;
					}
					return true;
				case blockBreak:
					if (!this.grad.canBreakWhereClan) //pas possible de detruire le territoire d'un autre clan (en commentaire
					{
						this.sendMessage("block.break.clan.not", this.main.colors.diverse);
						/*Clan myClan = this.getClan();
						if (myClan == null)
						{
							this.sendMessage(this.main.getText(this.information.language, "block.break.clan.not.needclan"));
							return false;
						}
						if (myClan.connectedGamers.size() <= this.main.configuration.clanMinimumInteract)
						{
							this.sendMessage(this.main.getText(this.information.language, "block.break.clan.not.needconnected"));
							return false;
						}
						if (clan.connectedGamers.size() <= this.main.configuration.clanMinimumInteract)
						{
							this.sendMessage(this.main.getText(this.information.language, "block.break.clan.not.noconnected"));
							return false;
						}
						//envoyer message aux membres du clan que un gars detruit
						return true;*/
						return false;
					}
					return true;
				case blockInteract:
					if (material == Material.TNT || material2 == Material.FLINT_AND_STEEL)
					{
						Clan myClan = this.getClan();
						if (myClan == null)
						{
							this.sendMessage("block.clan.need.action", this.main.colors.clan);
							return false;
						}
						if (myClan.connectedGamers.size() < this.main.configuration.clanMinimumInteract)
						{
							this.sendMessage("block.clan.need.member", this.main.colors.clan);
							return false;
						}
						if (clan.connectedGamers.size() < this.main.configuration.clanMinimumInteract)
						{
							this.sendMessage("block.clan.other.member", this.main.colors.clan);
							return false;
						}
						if (material == Material.TNT && material2 == Material.FLINT_AND_STEEL)
						{
							this.main.tntPlaces.put(point, this);
						}
						if (material == Material.TNT)
						{
							clan.broadcast(this.main, "clan.attacked.tntflow", this.getNameColor());
							return true;
						}
						clan.broadcast(this.main, "clan.attacked.flint", this.getNameColor());
						return true;
					}
					if (!this.grad.canInteractWhereClan)
					{
						this.sendMessage("block.interact.clan.not", this.main.colors.diverse);
						return false;
					}
					return true;
				case blockSoil:
					if (!this.grad.canSoilDestroyClan && !clan.isIn(this))
					{
						//this.sendMessage(this.main.getText(this.information.language, "block.soil.clan.not"));
						return false;
					}
					return true;
			}
		}
		
		return true;
	}
	
	public boolean explodeEvent(EntityExplodeEvent event)
	{
		//faire les clans
		Indestructible indestructible;
		
		Terrain terrain;
		
		Prison prison;
		
		Clan clan;
		
		indestructible = this.main.getIndestructibleFromLocation(event.getLocation());
		
		if (indestructible != null)
		{
			if (this.grad.canBreakWhereLocked)
			{
				return true;
			}
			
			return false;
		}
		
		terrain = this.main.getTerrainFromLocation(event.getLocation());
		
		if (terrain != null)
		{
			if (this.grad.canBreakWhereReserved)
			{
				return true;
			}
			
			if (!terrain.owner.isOwner(this) && !terrain.whitisble.canAccess(this)) //-> problem?
			{
				return false;
			}
		}
		
		prison = this.main.getPrisonFromLocation(event.getLocation());
		
		if (prison != null)
		{
			if (this.grad.canGodJails)
			{
				return true;
			}
			
			return false;
		}
		
		clan = this.main.getClanFromLocation(event.getLocation());
		
		boolean message = false;
		
		boolean inClan = false;
		
		if (clan != null)
		{
			if (this.grad.canBreakWhereClan)
			{
				return true;
			}

			Clan myClan = this.getClan();
			
			if (myClan == null)
			{
				return false;
			}
			
			if (myClan.connectedGamers.size() < this.main.configuration.clanMinimumInteract)
			{
				return false;
			}
			
			if (clan.connectedGamers.size() < this.main.configuration.clanMinimumInteract)
			{
				return false;
			}
			
			clan.broadcast(this.main, "clan.attacked.tntflow", this.getNameColor());
			
			message = true;
			
			inClan = true;
		}
		
		for (int i = 0; i < event.blockList().size(); i++)
		{
			indestructible = this.main.getIndestructibleFromLocation(event.blockList().get(i).getLocation());
			
			if (indestructible != null)
			{
				if (this.grad.canBreakWhereLocked)
				{
					return true;
				}
				
				return false;
			}
			
			terrain = this.main.getTerrainFromLocation(event.blockList().get(i).getLocation());
			
			if (terrain != null)
			{
				if (this.grad.canBreakWhereReserved)
				{
					return true;
				}
				
				if (!terrain.owner.isOwner(this) && !terrain.whitisble.canAccess(this)) //-> problem?
				{
					return false;
				}
			}
			
			clan = this.main.getClanFromLocation(event.getLocation());
			
			if (clan != null)
			{
				if (this.grad.canBreakWhereClan)
				{
					return true;
				}

				if (!inClan) //si le point d'explosion initial n'est pas dans le clan, on annule
				{
					return false;
				}

				Clan myClan = this.getClan();
				
				if (myClan == null)
				{
					return false;
				}
				
				if (myClan.connectedGamers.size() < this.main.configuration.clanMinimumInteract)
				{
					return false;
				}
				
				if (clan.connectedGamers.size() < this.main.configuration.clanMinimumInteract)
				{
					return false;
				}
				
				if (!message)
				{
					clan.broadcast(this.main, "clan.attacked.tntflow", this.getNameColor());
					
					message = true;
				}
			}
		}
		
		return true;
	}
	
	public long playedTimeInMin()
	{
		return ((System.currentTimeMillis() - this.joinTime) / 1000)/* / 60*/; //--> donne en sec
	}
	
	public void refreshJoinTime()
	{
		this.joinTime = System.currentTimeMillis();
	}
	
	private enum doableState
	{
		blockPlace,
		blockBreak,
		blockInteract,
		blockSoil
	}
	
	public void ban()
	{
		this.information.banned = true;

		this.source.kickPlayer(this.main.getText(this.information.language, "nologin.player.banned"));
	}

	public void sendData(String channel, String message)
	{
		byte[] data = message.getBytes();
		
		Packet250CustomPayload packet = new Packet250CustomPayload();
		
        packet.tag = channel;
        
        packet.length = data.length;
        
        packet.data = data;

        EntityPlayer handle = ((CraftPlayer)this.source).getHandle();
        
        if (handle == null || handle.playerConnection == null)
        {
        	this.main.log.error("(handle || playerConnection) = null on sendData for player " + this.information.name);
        	
        	return;
        }
        
        handle.playerConnection.sendPacket(packet);
	}
	
	private boolean firstTeleporter = true;
	
	public void useTeleporter(Teleporter teleporter)
	{
		firstTeleporter = !firstTeleporter;
		
		if (!firstTeleporter)
		{
			return;
		}
		
		if (!this.grad.canUseTeleporter || teleporter.initial == null || teleporter.destination == null)
		{
			return;
		}

		this.source.teleport(teleporter.destination);
		
		this.sendMessage("teleporter.ok", this.main.colors.diverse, teleporter.name);
	}
	
	public boolean canLogAfterCombat()
	{
		return !(((System.currentTimeMillis() - this.information.lastDamage) / 1000) / 60 < this.main.configuration.fightMinimumReco * this.information.combatCounter);
	}
	
	public boolean isCochief()
	{
		return this.information.clanRank == 1;
	}
	
	public boolean isPrison()
	{
		return this.information.prisonLeft > 0;
	}
	
	public Prison getPrison()
	{
		if (this.information.prisonIn == 0)
		{
			return null;
		}
		
		return this.main.getPrison(this.information.prisonIn);
	}
	
	public void setInJail(Prison prison, int blocks)
	{
		this.information.prisonCount++;
		
		this.information.prisonIn = prison.id;
		
		this.information.prisonLeft = blocks;
		
		this.source.getInventory().clear();
		
		this.source.teleport(prison.spawn);
		
		this.source.getInventory().addItem(new ItemStack(Material.WOOD_PICKAXE, 1));
		
		this.updateInventory();
		
		this.sendMessage("prison.sended", this.main.colors.prison, this.main.getText(this.information.language, this.main.colors.prison,
				prison.getAbbreviation()), Integer.toString(this.information.prisonLeft));
		
		prison.hasChanged = true;
		
		prison.counter++;

		this.refreshName();
	}
	
	public void unJail()
	{
		Prison prison = this.getPrison();
		
		if (prison.countPlayers() <= 1)
		{
			prison.clearChests();
		}
		
		this.source.getInventory().clear();
		
		this.updateInventory();
		
		this.source.teleport(prison.exit);
		
		this.sendMessage("prison.outed", this.main.colors.prison, this.main.getText(this.information.language, this.main.colors.prison, prison.getAbbreviation()));
		
		this.information.prisonIn = 0;
		
		this.information.prisonLeft = 0;
		
		this.refreshName();
	}
	
	public void updateInventory()
	{
		((CraftPlayer)this.source).getHandle().activeContainer.a();
	}
	
	public boolean is(Gamer gamer)
	{
		return this.information.id == gamer.information.id;
	}

	private long lastUpdateTime = System.currentTimeMillis();
	
	private int addSeconds;
	
	public void refreshTodayPlayTime(long nowTime)
	{
		if (this.information.playableMinutes == 0)
		{
			return;
		}
		
		this.addSeconds += ((nowTime - lastUpdateTime) / 1000);
		
		if (this.addSeconds >= 60)
		{
			this.information.playedMinutes += this.addSeconds / 60;
			
			this.addSeconds = 0;
		}
		
		if (this.information.playedMinutes >= this.information.playableMinutes)
		{
			this.source.kickPlayer(this.main.getText(information.language, "nologin.time.elapsed"));
		}
		
		this.lastUpdateTime = nowTime;
	}

	public void setAFK()
	{
		if (this.isAFK)
		{
			return;
		}
		
		this.isAFK = true;
		
		this.afkThreadId = this.main.getServer().getScheduler().scheduleSyncDelayedTask(this.main, new Runnable()
		{
			public void run()
			{
				source.kickPlayer("afkEnd");
			}
		}, 20 * 60 * 2);
		
		this.main.broadcastTextAbbrev("player.isafk", this.main.colors.diverse, this.getNameColorPersonnalized(this.main.colors.diverse));
	}
	
	public void setUnAFK()
	{
		if (!this.isAFK)
		{
			return;
		}
		
		this.isAFK = false;

		this.main.broadcastTextAbbrev("player.isnoafk", this.main.colors.diverse, this.getNameColorPersonnalized(this.main.colors.diverse));
		
		this.main.getServer().getScheduler().cancelTask(this.afkThreadId);
	}
	
	public boolean isAFK()
	{
		return this.isAFK;
	}
	
	public boolean isRecentDamage()
	{
		return (((System.currentTimeMillis() - this.information.lastDamage) / 1000) / 60 < 1);
	}
	
	public void mute()
	{
		if (this.information.muted)
		{
			return;
		}
		
		this.information.muted = true;

		this.sendMessage("you.are.mute", this.main.colors.admin);
	}
	
	public void unmute()
	{
		if (!this.information.muted)
		{
			return;
		}
		
		this.information.muted = false;
		
		this.sendMessage("you.are.unmute", this.main.colors.admin);
	}
	
	public boolean isInvisible()
	{
		return this.invisible;
	}
}