package turkraft;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import turkraft.common.Constants;
import turkraft.common.Logs;
import turkraft.common.Manipulation;
import turkraft.events.Rimer;
import turkraft.listeners.ChannelModify;
import turkraft.listeners.ChannelMoney;
import turkraft.listeners.Gamering;
import turkraft.listeners.Monstering;
import turkraft.listeners.Servering;
import turkraft.stockers.Clan;
import turkraft.stockers.Colors;
import turkraft.stockers.Gamer;
import turkraft.stockers.Genres;
import turkraft.stockers.Grad;
import turkraft.stockers.Group;
import turkraft.stockers.Indestructible;
import turkraft.stockers.Language;
import turkraft.stockers.MobMoney;
import turkraft.stockers.Pennerator;
import turkraft.stockers.Plot;
import turkraft.stockers.Prische;
import turkraft.stockers.Prisgiv;
import turkraft.stockers.Prison;
import turkraft.stockers.Prispos;
import turkraft.stockers.Prisval;
import turkraft.stockers.Shop;
import turkraft.stockers.Teleporter;
import turkraft.stockers.Terrain;

public class Turkraft extends JavaPlugin
{
	public static String VERSION = "0.4.2";
	
	private Map<String, Gamer> gamers;

	public Map<Location, Shop> shops;
	
	public ArrayList<Integer> deleteShops;
	
	public ArrayList<Terrain> terrains;
	
	public ArrayList<Integer> deleteTerrains;
	
	public ArrayList<Indestructible> indestructibles;
	
	public ArrayList<Integer> deleteIndestructibles;
	
	public Logs log;
	
	public Configuration configuration;
	
	public Constants constants;
	
	public Database database;
	
	private Map<Integer, Language> languages;
	//private Map<String, Map<String, String>> languages;
	
	private Map<Integer, Map<Integer, String>> rimes;
	
	private ArrayList<String> bannedIps;
	
	//variable qui empeche la connexion des clients lorsqu'elle vaut false a cause d'une erreur
	public static boolean noError;
	
	//liste des grads
	public ArrayList<Grad> grads;
	
	public boolean refuseConnections;
	
	private Gamering listenerGamer;
	
	//liste des mobs qui donnent de l'argent
	private Map<EntityType, MobMoney> mobMoneys;
	
	public Map<Integer, Group> groups;
	
	public boolean isRestarting;
	
	public Map<Integer, Clan> clans;
	
	public ArrayList<Integer> deleteClans;
	
	public Monstering monstering;
	
	public Colors colors;
	
	public ArrayList<Teleporter> teleporters;
	
	public ArrayList<Integer> deleteTeleporters;
	
	public static boolean DEBUG = true;
	
	public Genres genres;
	
	public Map<Location, Gamer> tntPlaces;
	
	public ArrayList<Prison> prisons;
	
	public ArrayList<Integer> deletePrisons;
	
	public Map<Location, Prische> prisches; //coffres
	
	public ArrayList<Integer> deletePrisches;

	public Map<Location, Prispos> prispos; //spawn de blocs
	
	public ArrayList<Integer> deletePrispos;

	public ArrayList<Prisval> prisvals; //blocs recoltables
	
	public Map<Location, Prisgiv> prisgivs; //blocs ou on pose les minerais
	
	public ArrayList<Integer> deletePrisgivs;
	
	public Pennerator pennerator;
	
	public Networker networker;
	
	public boolean sanguine;
	
	//quand le plugin demarre
	public void onEnable()
	{
		this.refuseConnections = true;
		
		noError = true;
		
		//CraftingManager.getInstance().registerShapedRecipe(new net.minecraft.server.v1_4_6.ItemStack((new ItemFood(404, 5, 0.6F, false)).b("chocolate"), 1),
		//		new Object[] { "XXX", Character.valueOf('X'), Item.COOKIE});
		
		try
		{
			this.initLogs();
			
			this.loadConstants();
			
			this.initListeners();
			
			this.initDatabase();
			
			this.loadConfiguration();
			
			this.loadLanguages();
			
			this.loadRimes();
			
			this.loadBannedIps();
			
			this.loadGrads();
			
			this.loadMobsMoney();
			
			this.initEvents();
			
			if (this.configuration.restart)
			{
				this.restartTimer();
			}
			
			this.initRest();
			
			this.setOnline();

			/*WorldCreator newWorld = new WorldCreator("world_free");
			newWorld.type(WorldType.FLAT);
			newWorld.generateStructures(false);
		
			
			this.getServer().createWorld(newWorld);
			
			this.getServer().createWorld(WorldCreator.name("world_free"));
			this.getServer().getWorld("world_free").setSpawnFlags(false, false);
			this.getServer().getWorld("world_free").setPVP(false);
			this.getServer().getWorld("world_free").setDifficulty(Difficulty.PEACEFUL);*/
		}
		catch (Exception ex)
		{
			this.log.error("ERREUR DE LANCEMENT", ex);

			noError = false;
		}
		
		if (!noError)
		{
			this.log.error("N'ECOUTE PAS LES CONNEXIONS!");
		}
	}
	
	//quand le plugin stop
	public void onDisable()
	{
		this.setOffline();
		
		this.saveBase(true);
	}
	
	//initialise les logs
	public void initLogs()
	{
		this.log = new Logs();
	}
	
	//charger les constantes
	public void loadConstants()
	{
		this.constants = new Constants(this.log, "constants.tk");
	}
	
	//charger la base de donnees
	public void initDatabase()
	{
		this.database = new Database(this);
	}
	
	//charger les configurations depuis la bdd
	public void loadConfiguration()
	{
		this.configuration = this.database.getConfigurations();
	}
	
	//charger les languages
	public void loadLanguages()
	{
		this.languages = this.database.getLanguages();
	}
	
	//charger les languages
	public void loadRimes()
	{
		this.rimes = this.database.getRimes();
	}
	
	//charger les ips bannis
	public void loadBannedIps()
	{
		this.bannedIps = this.database.getBannedIps();
	}
	
	//charger les grades
	public void loadGrads()
	{
		this.grads = this.database.getGrads();
	}
	
	//charger l'argent des mobs
	public void loadMobsMoney()
	{
		this.mobMoneys = this.database.getMobMoneys();
	}
	
	//demarre les evenements
	public void initEvents()
	{
		//pour la version officielle ;)
	}
	
	//demarre les ecouteurs
	public void initListeners()
	{
		//initialise tous les listeners de turkraft.listeners
		
		new Servering(this);
		
		this.listenerGamer = new Gamering(this);
		
		this.monstering = new Monstering(this);
	}
	
	//demarre le timer du redemarrage auto
	public void restartTimer()
	{
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
		{
			public void run()
			{
				alertRestart();
			}
		}, this.configuration.restartTime);
	}
	
	//initilise divers variables
	public void initRest()
	{
		this.isRestarting = false;
		
		this.sanguine = false;
		
		this.tntPlaces = new Hashtable<Location, Gamer>();
		
		this.configuration.spawnPoint.getWorld().setSpawnLocation(
				this.configuration.spawnPoint.getBlockX(),
				this.configuration.spawnPoint.getBlockY(),
				this.configuration.spawnPoint.getBlockZ());
		
		this.groups = new Hashtable<Integer, Group>();
		
		this.gamers = new Hashtable<String, Gamer>();

		//--> charger normalement depuis la bdd
		this.genres = this.database.getGenres();
		
		this.colors = this.database.getColors();
		
		this.shops = this.database.getShops();
		
		this.deleteShops = new ArrayList<Integer>();
		
		this.terrains = this.database.getTerrains();
		
		this.deleteTerrains = new ArrayList<Integer>();
		
		this.indestructibles = this.database.getIndestructibles();
		
		this.deleteIndestructibles = new ArrayList<Integer>();
		
		this.clans = this.database.getClans();
		
		this.deleteClans = new ArrayList<Integer>();
		
		this.teleporters = this.database.getTeleporters();
		
		this.deleteTeleporters = new ArrayList<Integer>();
		
		this.prisvals = this.database.getPrisvals();
		
		this.prisons = this.database.getPrisons();
		
		this.prispos = this.database.getPrispos();
		
		this.prisches = this.database.getPrisches();
		
		this.prisgivs = this.database.getPrisgivs();
		
		this.deletePrisches = new ArrayList<Integer>();
		
		this.deletePrisons = new ArrayList<Integer>();
		
		this.deletePrispos = new ArrayList<Integer>();
		
		this.deletePrisgivs = new ArrayList<Integer>();
		
		this.resetPrisonsRespawns();
		
		this.pennerator = new Pennerator(this, this.database.getGenneds());
		
		this.pennerator.resetPrisonsChests();
		
		
		this.networker = new Networker(this);
		
		new Thread(this.networker).start();
		//<--
		
		new Rimer(this).start();
		
		Messenger messenger = this.getServer().getMessenger();
		
		messenger.registerOutgoingPluginChannel(this, "money");
		
		messenger.registerOutgoingPluginChannel(this, "sanguine");
		
		messenger.registerIncomingPluginChannel(this, "money", new ChannelMoney(this));
		
		messenger.registerIncomingPluginChannel(this, "modify", new ChannelModify(this));
		
		this.startTimeChecker();
		
		this.startSaveAuto();
	}
	
	public void resetPrisonsRespawns()
	{
		for (Map.Entry<Location, Prispos> entry : this.prispos.entrySet())
		{
			entry.getValue().setBlock();
		}
	}
	
	private void startSaveAuto()
	{
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
		{
			   public void run()
			   {
				   if (isRestarting)
				   {
					   return;
				   }
				   
				   saveBase(false);
			   
				   System.out.println("Database saved");
				   
				   for (int i = 0; i < getServer().getWorlds().size(); i++)
				   {
					   getServer().getWorlds().get(i).save();
				   }

				   System.out.println("Worlds saved");
				   
				   startSaveAuto();
			   }
		}, this.configuration.saveInterval);
	}
	
	private void startTimeChecker()
	{
	    getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
	      public void run() {
	        checkTimes();
	        startTimeChecker();
	      }
	    }
	    , 20L);
	}

	//broadcast message de redemarrage auto, attends stopTime, puis lance le programme de redemarrage
	public void alertRestart()
	{
		this.broadcastTextAbbrev("server.restart.broadcast", ChatColor.RED);

		this.isRestarting = true;
		
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
		{
			public void run()
			{
				getServer().savePlayers();
				
				for (int i = 0; i < getServer().getWorlds().size(); i++)
				{
					getServer().getWorlds().get(i).save();
				}
				
				if (configuration.restart)
				{
					try
					{
						Runtime.getRuntime().exec("java -jar " + constants.get("restarter"));
					}
					catch (IOException ex)
					{
						ex.printStackTrace();
					}
				}
				
				getServer().shutdown();
			}
		}, this.configuration.stopTime);
	}
	
	//mets le serveur en ligne
	public void setOnline()
	{
		this.database.setServerOnline();
		
		this.refuseConnections = false;
	}
	
	//arrete le serveur
	public void stop()
	{
		this.setOffline();
		
		this.saveBase(true);
		
		this.getServer().shutdown();
	}

	//redemarre le serveur
	public void restart()
	{
		//lancer l'executable qui fait l'auto redemarrage

		this.stop();
	}
	
	//mets le serveur hors ligne
	public void setOffline()
	{
		this.refuseConnections = true;
		
		this.database.setServerOffline();
	}
	
	//sauvegarde tout
	public void saveBase(boolean serverQuit)
	{
		long startTime = System.currentTimeMillis();
		
		this.saveConfigurations();
		
		this.saveGamers(serverQuit);
		
		this.saveIndustructible(serverQuit);
		
		this.saveTerrains(serverQuit);
		
		this.saveClans(serverQuit);
		
		this.saveShops(serverQuit);
		
		this.saveTeleporters(serverQuit);
		
		this.savePrisons(serverQuit);
		
		this.savePrisches(serverQuit);
		
		this.savePrispos(serverQuit);
		
		this.savePrisgivs(serverQuit);

		float elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000F;
		
		this.log.information("Database saved in " + elapsedSeconds + " second(s)");
	}
	
	//sauvegarde les configurations
	public void saveConfigurations()
	{
		this.database.saveConfiguration("maximumBuyMoney", this.configuration.maximumBuyMoney);

		this.database.saveConfiguration("maximumKillMoney", this.configuration.maximumKillMoney);

		this.database.saveConfiguration("spawnPoint", Manipulation.getLocationWorld(this.configuration.spawnPoint, true));
	}
	
	//sauvegarde les joueurs connectes
	public void saveGamers(boolean serverQuit)
	{
		if (serverQuit)
		{
			for (Map.Entry<String, Gamer> entry : this.gamers.entrySet())
			{
				this.database.setGamerDisconnected(entry.getValue());
			}
			
			return;
		}

		for (Map.Entry<String, Gamer> entry : this.gamers.entrySet())
		{
			this.database.gamerSave(entry.getValue());
		}
	}
	
	//sauvegarde les terrains indestructibles
	public void saveIndustructible(boolean serverQuit)
	{
		if (serverQuit)
		{
			for (int i = 0; i < this.deleteIndestructibles.size(); i++)
			{
				this.database.deleteIndestructible(this.deleteIndestructibles.get(i));
			}
				
			for (int i = 0; i < this.indestructibles.size(); i++)
			{
				if (!this.indestructibles.get(i).inDatabase)
				{
					this.database.addIndestructible(this.indestructibles.get(i));
					
					continue;
				}
				
				if (this.indestructibles.get(i).hasChanged)
				{
					this.database.updateIndestructible(this.indestructibles.get(i));
				}
			}
			
			return;
		}
		
		//quand le serveur ne ferme pas -->
		
		for (int i = 0; i < this.deleteIndestructibles.size(); i++)
		{
			this.database.deleteIndestructible(this.deleteIndestructibles.get(i));
			
			this.deleteIndestructibles.remove(i);
		}
		
		for (int i = 0; i < this.indestructibles.size(); i++)
		{
			if (!this.indestructibles.get(i).inDatabase)
			{
				this.database.addIndestructible(this.indestructibles.get(i));
				
				this.indestructibles.get(i).inDatabase = true;
				
				continue;
			}
			
			if (this.indestructibles.get(i).hasChanged)
			{
				this.database.updateIndestructible(this.indestructibles.get(i));

				this.indestructibles.get(i).hasChanged = false;
			}
		}
	}
	
	//sauvegarde les terrains des joueurs
	public void saveTerrains(boolean serverQuit)
	{
		if (serverQuit)
		{
			for (int i = 0; i < this.deleteTerrains.size(); i++)
			{
				this.database.deleteTerrain(this.deleteTerrains.get(i));
			}
				
			for (int i = 0; i < this.terrains.size(); i++)
			{
				if (!this.terrains.get(i).inDatabase)
				{
					this.database.addTerrain(this.terrains.get(i));
					
					continue;
				}
				
				if (this.terrains.get(i).hasChanged)
				{
					this.database.updateTerrain(this.terrains.get(i));
				}
			}
			
			return;
		}
		
		//quand le serveur ne ferme pas -->
		
		for (int i = 0; i < this.deleteTerrains.size(); i++)
		{
			this.database.deleteTerrain(this.deleteTerrains.get(i));
			
			this.deleteTerrains.remove(i);
		}
		
		for (int i = 0; i < this.terrains.size(); i++)
		{
			if (!this.terrains.get(i).inDatabase)
			{
				this.database.addTerrain(this.terrains.get(i));
				
				this.terrains.get(i).inDatabase = true;
				
				continue;
			}
			
			if (this.terrains.get(i).hasChanged)
			{
				this.database.updateTerrain(this.terrains.get(i));

				this.terrains.get(i).hasChanged = false;
			}
		}
	}
	
	//sauvegarde les terrains des Clans
	public void saveClans(boolean serverQuit)
	{
		if (serverQuit)
		{
			for (int i = 0; i < this.deleteClans.size(); i++)
			{
				this.database.deleteClan(this.deleteClans.get(i));
			}

			for (Map.Entry<Integer, Clan> entry : this.clans.entrySet())
			{
				if (!entry.getValue().inDatabase)
				{
					this.database.addClan(entry.getValue());
					
					continue;
				}
				
				if (entry.getValue().hasChanged)
				{
					this.database.updateClan(entry.getValue());
				}
			}
			
			return;
		}
		
		for (int i = 0; i < this.deleteClans.size(); i++)
		{
			this.database.deleteClan(this.deleteClans.get(i));
			
			this.deleteClans.remove(i);
		}

		for (Map.Entry<Integer, Clan> entry : this.clans.entrySet())
		{
			if (!entry.getValue().inDatabase)
			{
				this.database.addClan(entry.getValue());
				
				entry.getValue().inDatabase = true;
				
				continue;
			}
			
			if (entry.getValue().hasChanged)
			{
				this.database.updateClan(entry.getValue());

				entry.getValue().hasChanged = false;
			}
		}
	}
	
	//sauvegarde les magasins
	public void saveShops(boolean serverQuit)
	{
		if (serverQuit)
		{
			for (int i = 0; i < this.deleteShops.size(); i++)
			{
				this.database.deleteShop(this.deleteShops.get(i));
			}

			for (Map.Entry<Location, Shop> entry : this.shops.entrySet())
			{
				if (!entry.getValue().inDatabase)
				{
					this.database.addShop(entry.getValue());
					
					this.updateShopReserve(entry.getValue(), false);
					
					continue;
				}
				
				if (entry.getValue().hasChanged)
				{
					this.database.updateShop(entry.getValue());
					
					this.updateShopReserve(entry.getValue(), false);
				}
			}
			
			return;
		}
		
		for (int i = 0; i < this.deleteShops.size(); i++)
		{
			this.database.deleteShop(this.deleteShops.get(i));
			
			this.deleteShops.remove(i);
		}

		for (Map.Entry<Location, Shop> entry : this.shops.entrySet())
		{
			if (!entry.getValue().inDatabase)
			{
				this.database.addShop(entry.getValue());
				
				entry.getValue().inDatabase = true;
				
				this.updateShopReserve(entry.getValue(), true);
				
				continue;
			}
			
			if (entry.getValue().hasChanged)
			{
				this.database.updateShop(entry.getValue());

				entry.getValue().hasChanged = false;
				
				this.updateShopReserve(entry.getValue(), true);
			}
		}
	}
	
	//sauvegarde l'argent d'un magasin, envoi ce qui a ete gagner au cours de la deconnexion
	private void updateShopReserve(Shop shop, boolean checkConnection)
	{
		if (shop.ownable.hasOwner() && shop.reserve > 0)
		{
			if (checkConnection)
			{
				Gamer gamer = this.getGamer(shop.ownable.owner, true);
				
				if (gamer != null)
				{
					gamer.information.money += shop.reserve;
				}
				else
				{
					this.database.addPlayerMoney(shop.ownable.owner, shop.reserve);
				}
				
				return;
			}

			this.database.addPlayerMoney(shop.ownable.owner, shop.reserve);
		}
	}
	
	//sauvegarde les teleporteurs
	public void saveTeleporters(boolean serverQuit)
	{
		if (serverQuit)
		{
			for (int i = 0; i < this.deleteTeleporters.size(); i++)
			{
				this.database.deleteTeleporter(this.deleteTeleporters.get(i));
			}
			
			for (int i = 0; i < this.teleporters.size(); i++)
			{
				if (!this.teleporters.get(i).inDatabase)
				{
					this.database.addTeleporter(this.teleporters.get(i));
					
					continue;
				}
				
				if (this.teleporters.get(i).hasChanged)
				{
					this.database.updateTeleporter(this.teleporters.get(i));
				}
			}
			
			return;
		}
		
		for (int i = 0; i < this.deleteTeleporters.size(); i++)
		{
			this.database.deleteTeleporter(this.deleteTeleporters.get(i));
			
			this.deleteTeleporters.remove(i);
		}

		for (int i = 0; i < this.teleporters.size(); i++)
		{
			if (!this.teleporters.get(i).inDatabase)
			{
				this.database.addTeleporter(this.teleporters.get(i));
				
				this.teleporters.get(i).inDatabase = true;
				
				continue;
			}
			
			if (this.teleporters.get(i).hasChanged)
			{
				this.database.updateTeleporter(this.teleporters.get(i));

				this.teleporters.get(i).hasChanged = false;
			}
		}
	}
	
	//sauvegarde les prisons
	public void savePrisons(boolean serverQuit)
	{
		if (serverQuit)
		{
			for (int i = 0; i < this.deletePrisons.size(); i++)
			{
				this.database.deletePrison(this.deletePrisons.get(i));
			}
			
			for (int i = 0; i < this.prisons.size(); i++)
			{
				if (!this.prisons.get(i).inDatabase)
				{
					this.database.addPrison(this.prisons.get(i));
					
					continue;
				}
				
				if (this.prisons.get(i).hasChanged)
				{
					this.database.updatePrison(this.prisons.get(i));
				}
			}
			
			return;
		}
		
		for (int i = 0; i < this.deletePrisons.size(); i++)
		{
			this.database.deletePrison(this.deletePrisons.get(i));
			
			this.deletePrisons.remove(i);
		}

		for (int i = 0; i < this.prisons.size(); i++)
		{
			if (!this.prisons.get(i).inDatabase)
			{
				this.database.addPrison(this.prisons.get(i));
				
				this.prisons.get(i).inDatabase = true;
				
				continue;
			}
			
			if (this.prisons.get(i).hasChanged)
			{
				this.database.updatePrison(this.prisons.get(i));

				this.prisons.get(i).hasChanged = false;
			}
		}
	}

	//sauvegarde les prisches
	public void savePrisches(boolean serverQuit)
	{
		if (serverQuit)
		{
			for (int i = 0; i < this.deletePrisches.size(); i++)
			{
				this.database.deletePrische(this.deletePrisches.get(i));
			}
			
			for (Map.Entry<Location, Prische> entry : this.prisches.entrySet())
			{
				if (!entry.getValue().inDatabase)
				{
					this.database.addPrische(entry.getValue());
					
					continue;
				}
			}
			
			return;
		}
		
		for (int i = 0; i < this.deletePrisches.size(); i++)
		{
			this.database.deletePrische(this.deletePrisches.get(i));
			
			this.deletePrisches.remove(i);
		}

		for (Map.Entry<Location, Prische> entry : this.prisches.entrySet())
		{
			if (!entry.getValue().inDatabase)
			{
				this.database.addPrische(entry.getValue());
				
				entry.getValue().inDatabase = true;
				
				continue;
			}
		}
	}

	//sauvegarde les prispos
	public void savePrispos(boolean serverQuit)
	{
		if (serverQuit)
		{
			for (int i = 0; i < this.deletePrispos.size(); i++)
			{
				this.database.deletePrispos(this.deletePrispos.get(i));
			}

			for (Map.Entry<Location, Prispos> entry : this.prispos.entrySet())
			{
				if (!entry.getValue().inDatabase)
				{
					this.database.addPrispos(entry.getValue());
					
					continue;
				}
			}
			
			return;
		}
		
		for (int i = 0; i < this.deletePrispos.size(); i++)
		{
			this.database.deletePrispos(this.deletePrispos.get(i));
			
			this.deletePrispos.remove(i);
		}

		for (Map.Entry<Location, Prispos> entry : this.prispos.entrySet())
		{
			if (!entry.getValue().inDatabase)
			{
				this.database.addPrispos(entry.getValue());
				
				entry.getValue().inDatabase = true;
				
				continue;
			}
		}
	}

	//sauvegarde les prisgivs
	public void savePrisgivs(boolean serverQuit)
	{
		if (serverQuit)
		{
			for (int i = 0; i < this.deletePrisgivs.size(); i++)
			{
				this.database.deletePrisgiv(this.deletePrisgivs.get(i));
			}

			for (Map.Entry<Location, Prisgiv> entry : this.prisgivs.entrySet())
			{
				if (!entry.getValue().inDatabase)
				{
					this.database.addPrisgiv(entry.getValue());
					
					continue;
				}
			}
			
			return;
		}
		
		for (int i = 0; i < this.deletePrisgivs.size(); i++)
		{
			this.database.deletePrisgiv(this.deletePrisgivs.get(i));
			
			this.deletePrisgivs.remove(i);
		}

		for (Map.Entry<Location, Prisgiv> entry : this.prisgivs.entrySet())
		{
			if (!entry.getValue().inDatabase)
			{
				this.database.addPrisgiv(entry.getValue());
				
				entry.getValue().inDatabase = true;
				
				continue;
			}
		}
	}
	
	//retourne le nombre de joueur en jeu
	public int countGamers()
	{
		return this.gamers.size();
	}
	
	//retourne un joueur a partir de son nom
	public Gamer getGamer(String Name, boolean ignoreCase)
	{
		if (!ignoreCase)
		{
			return this.gamers.get(Name);
		}

		for (Map.Entry<String, Gamer> entry : this.gamers.entrySet())
		{
			if (entry.getKey().equalsIgnoreCase(Name))
			{
				return entry.getValue();
			}
		}
		
		return null;
	}
	
	public String getText(int language, String abbreviation)
	{
		return this.getText(language, abbreviation, ChatColor.WHITE);
	}
	
	public String getText(int language, ChatColor color, String abbreviation)
	{
		return this.getText(language, abbreviation, color, new String[] {});
	}
	
	public String getText(int language, String abbreviation, String ... args)
	{
		return this.getText(language, abbreviation, ChatColor.WHITE, args);
	}
	
	//retourne un texte en fonction d'une langue
	public String getText(int language, String abbreviation, ChatColor color, String ... args)
	{
		if (this.languages.get(language).texts.containsKey(abbreviation))
		{
			if (args == null || args.length == 0)
				return color + this.languages.get(language).texts.get(abbreviation);
			
			String text = color + this.languages.get(language).texts.get(abbreviation);
			
			for (int i = 0; i < args.length; i++)
			{
				text = text.replace("{" + i + "}", args[i] + color);
			}
			
			return text;
		}
		
		if (this.languages.get(this.configuration.defaultLanguage).texts.containsKey(abbreviation))
		{
			if (args == null || args.length == 0)
				return color + this.languages.get(this.configuration.defaultLanguage).texts.get(abbreviation);
			
			String text = color + this.languages.get(this.configuration.defaultLanguage).texts.get(abbreviation);
			
			for (int i = 0; i < args.length; i++)
			{
				text = text.replace("{" + i + "}", args[i] + color);
			}
			
			return text;
		}
		
		this.log.error("Texte non existant pour la langue " + language + " et " + this.configuration.defaultLanguage + " (defaut) pour l'abbreviation " + abbreviation);
		
		return "Inexistant text";
	}
	
	//retourne une rime en fonction d'une langue
	public String getRime(int language, int virtual)
	{
		if (!this.rimes.containsKey(language))
		{
			return this.rimes.get(this.configuration.defaultLanguage).get(virtual);
		}
		
		if (this.rimes.get(language).containsKey(virtual))
		{
			return this.rimes.get(language).get(virtual);
		}
		
		return this.rimes.get(this.configuration.defaultLanguage).get(virtual);
	}
	
	//compte les rimes que y a dans la langue par defaut
	public int countRimes()
	{
		return this.rimes.get(this.configuration.defaultLanguage).size();
	}
	
	//retour une rime en fonction de son index
	public int getRimeIdByIndex(int index)
	{
		return (Integer)this.rimes.get(this.configuration.defaultLanguage).keySet().toArray()[index];
	}
	
	//verifie si l'p est bannie
	public boolean isBannedIp(String address)
	{
		return this.bannedIps.contains(address);
	}
	
	//verifie si l'ip est bannie
	public boolean isBannedIp(InetAddress address)
	{
		return this.isBannedIp(address.getHostAddress());
	}
	
	//retourne un grade en fonction de son id
	public Grad getGradFromId(int id)
	{
		for (int i = 0; i < this.grads.size(); i++)
		{
			if (this.grads.get(i).id == id)
			{
				return this.grads.get(i);
			}
		}
		
		return null;
	}
	
	//ajoute un element gamer a la liste des connectes
	public void addGamer(Gamer gamer)
	{
		if (this.gamers.containsKey(gamer.information.name))
		{
			this.log.error("Nom du joueur deja present dans la liste pour l'ajouter!");
			
			return;
		}
		
		this.gamers.put(gamer.information.name, gamer);
	}
	
	//enleve un joueur a partir de son nom
	public void removeGamer(String name)
	{
		if (!this.gamers.containsKey(name))
		{
			this.log.error("Nom du joueur non present dans la liste pour l'enlever!");
			
			return;
		}
		
		this.gamers.remove(name);
	}
	
	//appele lorsqu'un joueur fait une commande
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		Gamer gamer = this.getGamer(sender.getName(), true);
		
		if (gamer == null)
		{
			if (label.equalsIgnoreCase("restart"))
			{
				this.alertRestart();
				
				return true;
			}

			if (label.equalsIgnoreCase("maj"))
			{
				this.broadcastTextAbbrev("broadcast.maj", this.colors.server);

				return true;
			}
			
			if (label.equalsIgnoreCase("a"))
			{
				if (args.length <= 0)
				{
					return false;
				}
				
				this.broadcastAdminChat(ChatColor.WHITE + "<" + ChatColor.RED + "Server" + ChatColor.WHITE + "> " + ChatColor.RED + Manipulation.charsFromArraySeparator(args, " "));
				
				return true;
			}

			if (label.equalsIgnoreCase("chat") && args.length > 1)
			{
				Gamer sayer = this.getGamer(args[0], true);
				
				if (sayer == null)
				{
					sender.sendMessage(this.getText(this.configuration.defaultLanguage, "general.notconnected", this.colors.money, args[0]));
					
					return true;
				}
				
				sayer.source.chat(Manipulation.charsFromArraySeparator(1, args, " "));

				return true;
			}
			
			if (label.equalsIgnoreCase("lag"))
			{
				Runtime runtime = Runtime.getRuntime();

			    long maxMemory = runtime.maxMemory();
			    long allocatedMemory = runtime.totalMemory();
			    long freeMemory = runtime.freeMemory();
		    
				sender.sendMessage(this.getText(this.configuration.defaultLanguage, "command.lag.ok", this.colors.diverse,
						Long.toString(freeMemory / 1024),
						Long.toString(allocatedMemory / 1024 / 1024),
						Long.toString(maxMemory / 1024),
						Long.toString((freeMemory + (maxMemory - allocatedMemory)) / 1024),
						Integer.toString(this.getServer().getWorld("world").getEntities().size())));
				
				return true;
			}
			
			return false;
		}
		
		Gamering.commandState state = this.listenerGamer.onCommand(gamer, label, args);
		
		if (state != Gamering.commandState.Accepted)
		{
			switch (state)
			{
				case Incorrect:
					sender.sendMessage(this.getText(gamer.information.language, ChatColor.RED, "command.incorrect"));
				break;
				case NoRights:
					sender.sendMessage(this.getText(gamer.information.language, ChatColor.RED, "command.norights"));
					break;
				default:
					sender.sendMessage(this.getText(gamer.information.language, ChatColor.RED, "command.error"));
					break;
			}
		}
		
		return true;
    }
	
	//envoie une rime a tous les joueurs en fonction du numeron (selected) et de leur langue
	public void broadcastRimeText(int selected)
	{
		for (Map.Entry<String, Gamer> entry : this.gamers.entrySet())
		{
			entry.getValue().source.sendMessage(ChatColor.RED.toString() + this.getRime(entry.getValue().information.language, selected));
		}
		
		this.networker.send(ChatColor.RED.toString() + this.getRime(this.configuration.defaultLanguage, selected));
	}
	
	public void broadcastTextAbbrev(String abbrev)
	{
		this.broadcastTextAbbrev(abbrev, ChatColor.WHITE);
	}
	
	public void broadcastTextAbbrev(String abbrev, ChatColor color)
	{
		this.broadcastTextAbbrev(abbrev, color, new String[] {});
	}
	
	public void broadcastTextAbbrev(String abbrev, String ... args)
	{
		this.broadcastTextAbbrev(abbrev, ChatColor.WHITE, args);
	}
	
	//envoie un texte aux joueurs en fonction de leur langue
	public void broadcastTextAbbrev(String abbrev, ChatColor color, String ... args)
	{
		for (Map.Entry<String, Gamer> entry : this.gamers.entrySet())
		{
			entry.getValue().sendMessage(abbrev, color, args);
		}
		
		this.networker.send(this.getText(this.configuration.defaultLanguage, abbrev, args));
	}
	
	public void broadcastSameLanguage(int language, String text)
	{
		for (Map.Entry<String, Gamer> entry : this.gamers.entrySet())
		{
			if (entry.getValue().information.language != language)
			{
				continue;
			}
			
			entry.getValue().sendMessage(text);
		}
	}
	
	public Language[] getLanguages()
	{
		  return this.languages.values().toArray(new Language[] {});
	}
	
	public boolean isConnected(String name, boolean ignoreCase)
	{
		if (!ignoreCase)
			return this.gamers.containsKey(name);
		else
			return this.getServer().getPlayer(name) != null && this.getServer().getPlayer(name).isOnline();
	}
	
	public MobMoney getMobMoney(EntityType type)
	{
		return this.mobMoneys.get(type);
	}
	
	public boolean isIPConnected(String address)
	{
		for (Map.Entry<String, Gamer> entry : this.gamers.entrySet())
		{
			if (entry.getValue().source.getAddress().getAddress().getHostAddress() == address)
			{
				return true;
			}
		}
		
		return false;
	}
	
	public void broadcastAdminChat(String message)
	{
		for (Map.Entry<String, Gamer> entry : this.gamers.entrySet())
		{
			if (entry.getValue().grad.canAccessAdminChat)
			{
				entry.getValue().sendMessage(message);
			}
		}
		
		System.out.println(message);
		
		this.networker.send(message);
	}
	
	public Group getGroup(int id)
	{
		return this.groups.get(id);
	}
	
	public void addGroup(int id, Group group)
	{
		this.groups.put(id, group);
	}
	
	public void removeGroup(int id)
	{
		this.groups.remove(id);
	}

	public collisionType collisionWithPlots(Plot plot)
	{
		for (int i = 0; i < this.indestructibles.size(); i++)
		{
			if (this.indestructibles.get(i).plot.touchPlot(plot))
			{
				return collisionType.Indestructible;
			}
		}
		
		for (int i = 0; i < this.terrains.size(); i++)
		{
			if (this.terrains.get(i).plot.touchPlot(plot))
			{
				return collisionType.Territory;
			}
		}

		for (int i = 0; i < this.prisons.size(); i++)
		{
			if (this.prisons.get(i).plot.touchPlot(plot))
			{
				return collisionType.Prison;
			}
		}

		for (Map.Entry<Integer, Clan> entry : this.clans.entrySet())
		{
			if (entry.getValue().plot.touchPlot(plot))
			{
				return collisionType.Clan;
			}
		}
		
		return collisionType.NoCollision;
	}

	public collisionType collisionWithPlots(Location point)
	{
		for (int i = 0; i < this.indestructibles.size(); i++)
		{
			if (this.indestructibles.get(i).plot.inPlot(point))
			{
				return collisionType.Indestructible;
			}
		}
		
		for (int i = 0; i < this.terrains.size(); i++)
		{
			if (this.terrains.get(i).plot.inPlot(point))
			{
				return collisionType.Territory;
			}
		}

		for (int i = 0; i < this.prisons.size(); i++)
		{
			if (this.prisons.get(i).plot.inPlot(point))
			{
				return collisionType.Prison;
			}
		}

		for (Map.Entry<Integer, Clan> entry : this.clans.entrySet())
		{
			if (entry.getValue().plot.inPlot(point))
			{
				return collisionType.Clan;
			}
		}
		
		return collisionType.NoCollision;
	}

	public Indestructible getIndestructibleFromLocation(Location point, int padding)
	{
		for (int i = 0; i < this.indestructibles.size(); i++)
		{
			if (this.indestructibles.get(i).plot.inPlot(point, padding))
			{
				return this.indestructibles.get(i);
			}
		}
		
		return null;
	}

	public Indestructible getIndestructibleFromLocation(Location point)
	{
		return this.getIndestructibleFromLocation(point, 0);
	}
	
	public Terrain getTerrainFromLocation(Location point, int padding)
	{
		for (int i = 0; i < this.terrains.size(); i++)
		{
			if (this.terrains.get(i).plot.inPlot(point, padding))
			{
				return this.terrains.get(i);
			}
		}
		
		return null;
	}
	
	public Terrain getTerrainFromLocation(Location point)
	{
		return this.getTerrainFromLocation(point, 0);
	}
	
	public Clan getClanFromLocation(Location point, int padding)
	{
		for (Map.Entry<Integer, Clan> entry : this.clans.entrySet())
		{
			if (entry.getValue().plot.inPlot(point, padding))
			{
				return entry.getValue();
			}
		}
		
		return null;
	}
	
	public Clan getClanFromLocation(Location point)
	{
		return this.getClanFromLocation(point, 0);
	}

	public enum collisionType
	{
		Indestructible,
		Territory,
		Prison,
		Clan,
		NoCollision
	}
	
	public boolean isNameOk(String name)
	{
		if (name.length() < 3 || name.length() > 12)
		{
			return false;
		}
		
		return name.matches("^[a-zA-Z0-9]+$");
	}
	
	public boolean clanExist(String name)
	{
		for (Map.Entry<Integer, Clan> entry : this.clans.entrySet())
		{
			if (entry.getValue().name.equalsIgnoreCase(name))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public String getMaterialName(int language, Material material)
	{
		return Manipulation.capitalizeFirst(material.toString().toLowerCase().replace("_", " "));
	}
	
	/*public void broadcastInLanguages(String abbreviation)
	{
		this.broadcastInLanguages(abbreviation, new String[] {});
	}
	
	public void broadcastInLanguages(String abbreviation, String ... args)
	{
		for (Map.Entry<Integer, Language> entry : this.languages.entrySet())
		{
			this.broadcastSameLanguage(entry.getKey(), this.getText(entry.getKey(), abbreviation, args));
		}
	}*/
	
	public Teleporter getTeleporter(Location location)
	{
		for (int i = 0; i < this.teleporters.size(); i++)
		{
			if (location.distance(this.teleporters.get(i).initial) == 0)
			{
				return this.teleporters.get(i);
			}
		}
		
		return null;
	}
	
	public boolean teleporterExists(Location location)
	{
		for (int i = 0; i < this.teleporters.size(); i++)
		{
			if (this.teleporters.get(i).initial == location || this.teleporters.get(i).destination == location)
			{
				return true;
			}
		}
		
		return false;
	}
	
	public void deleteTeleporter(Teleporter teleporter)
	{
		if (teleporter.inDatabase)
		{
			this.deleteTeleporters.add(teleporter.id);
		}
		
		teleporter.dispose();
		
		this.teleporters.remove(teleporter);
	}
	
	public Prison getPrison(int id)
	{
		for (int i = 0; i < this.prisons.size(); i++)
		{
			if (this.prisons.get(i).id == id)
			{
				return this.prisons.get(i);
			}
		}
		
		return null;
	}
	
	public Prison getPrisonFromLocation(Location location, int margin)
	{
		for (int i = 0; i < this.prisons.size(); i++)
		{
			if (this.prisons.get(i).plot.inPlot(location, margin))
			{
				return this.prisons.get(i);
			}
		}
		
		return null;
	}
	
	public Prison getPrisonFromLocation(Location location)
	{
		return this.getPrisonFromLocation(location, 1);
	}
	
	public Prison getPrisonFromName(String name)
	{
		for (int i = 0; i < this.prisons.size(); i++)
		{
			if (this.prisons.get(i).variable.equalsIgnoreCase(name))
			{
				return this.prisons.get(i);
			}
		}
		
		return null;
	}
	
	public boolean prisvalsContain(Material item)
	{
		for (int i = 0; i < this.prisvals.size(); i++)
		{
			System.out.println(item + " : " + this.prisvals.get(i).item);
			if (this.prisvals.get(i).item == item)
			{
				return true;
			}
		}
		
		return false;
	}
	
	public Prisval getPrsivalFromItem(Material item)
	{
		for (int i = 0; i < this.prisvals.size(); i++)
		{
			if (this.prisvals.get(i).item == item)
			{
				return this.prisvals.get(i);
			}
		}
		
		return null;
	}
	
	public int countGamersOfPrison(Prison prison)
	{
		int counter = 0;

		for (Map.Entry<String, Gamer> entry : this.gamers.entrySet())
		{
			if (entry.getValue().information.prisonIn == prison.id)
			{
				counter++;
			}
		}
		
		return counter;
	}
	
	private int pastDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
	
	private void checkTimes()
	{
		int actualDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
	    
	    long l = ((World)this.getServer().getWorlds().get(0)).getTime();

	    if ((l > 0L) && (l <= 19L)) this.timeDawnEvent();
	    else if ((l > 6000L) && (l <= 6029L)) this.timeMiddayEvent();
	    else if ((l > 12000L) && (l <= 12029L)) this.timeDuskEvent();
	    else if ((l > 12500L) && (l <= 12529L)) this.timeNightEvent();
	    else if ((l > 18000L) && (l <= 18029L)) this.timeMidnightEvent();
	    
	    if (this.sanguine)
	    {
			for (int i = 0; i < this.getServer().getOnlinePlayers().length; i++)
			{
				Player target = this.getServer().getOnlinePlayers()[i];
				
				if (Manipulation.random(1, 200) == 10)
				{
					target.getWorld().spawnEntity(target.getLocation(), EntityType.BAT);
				}
				
				if (Manipulation.random(1, 250) == 50)
				{
					if (Manipulation.random(1, 6) == 5)
					{
						target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 *  60 * Manipulation.random(1, 6), Manipulation.random(1, 2)));
					}
					
					if (Manipulation.random(1, 6) == 5)
					{
						target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 60 * Manipulation.random(1, 6), Manipulation.random(1, 2)));
					}
					
					if (Manipulation.random(1, 6) == 5)
					{
						target.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20 *  60 * Manipulation.random(1, 6), Manipulation.random(1, 2)));
					}
					
					if (Manipulation.random(1, 6) == 5)
					{
						target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 *  60 * Manipulation.random(1, 6), Manipulation.random(1, 2)));
					}
				}
			}
	    }
	    
	    if (Manipulation.random(1, 10) == 5)
	    {
	    	long actualTime = System.currentTimeMillis();

			for (Map.Entry<String, Gamer> entry : this.gamers.entrySet())
			{
				entry.getValue().refreshTodayPlayTime(actualTime);
			}
	    }

	    if (this.pastDayOfWeek != actualDayOfWeek)
	    {
	    	this.realDayChanged();
	    }
	    
	    this.pastDayOfWeek = actualDayOfWeek;
	}
	
	private void realDayChanged()
	{
		for (Map.Entry<String, Gamer> entry : this.gamers.entrySet())
		{
			entry.getValue().information.playedMinutes = 0;
		}
	}
	
	private void timeDawnEvent()
	{
		this.sanguineEnd();
	}
	
	private void timeMiddayEvent()
	{
		
	}
	
	private void timeDuskEvent()
	{
		int sanguine = Manipulation.random(1, 35);
		
		if (sanguine == 5)
		{
			this.sanguineStart();
		}
	}
	
	private void timeNightEvent()
	{
		
	}
	
	private void timeMidnightEvent()
	{
		
	}
	
	public void sanguineEnd()
	{
		if (!this.sanguine)
		{
			return;
		}
		
		this.sanguine = false;
		
		this.broadcastAdminChat(ChatColor.RED + "-> Blood Moon has stopped.");
		
		for (int i = 0; i < this.getServer().getOnlinePlayers().length; i++)
		{
			this.getGamer(this.getServer().getOnlinePlayers()[i].getName(), true).sendData("sanguine", "0");
		}
	}
	
	public void sanguineStart()
	{
		if (this.sanguine)
		{
			return;
		}
		
		this.sanguine = true;
		
		this.broadcastAdminChat(ChatColor.RED + "-> Blood Moon has started.");
		
		for (int i = 0; i < this.getServer().getOnlinePlayers().length; i++)
		{
			Gamer gamer = this.getGamer(this.getServer().getOnlinePlayers()[i].getName(), true);
			
			gamer.sendData("sanguine", "1");
		}
	}
	
	public void reloadGrads()
	{
		for (Map.Entry<String, Gamer> entry : this.gamers.entrySet())
		{
			entry.getValue().reloadGrad();
		}
	}
}