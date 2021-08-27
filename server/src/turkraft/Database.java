package turkraft;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import turkraft.common.Manipulation;
import turkraft.stockers.Clan;
import turkraft.stockers.Colors;
import turkraft.stockers.Gamer;
import turkraft.stockers.Genned;
import turkraft.stockers.Genres;
import turkraft.stockers.Grad;
import turkraft.stockers.Indestructible;
import turkraft.stockers.Information;
import turkraft.stockers.Language;
import turkraft.stockers.MobMoney;
import turkraft.stockers.Ownable;
import turkraft.stockers.Pricable;
import turkraft.stockers.Prische;
import turkraft.stockers.Prisgiv;
import turkraft.stockers.Prison;
import turkraft.stockers.Prispos;
import turkraft.stockers.Prisval;
import turkraft.stockers.Shop;
import turkraft.stockers.Teleporter;
import turkraft.stockers.Terrain;
import turkraft.stockers.Whitisble;

public class Database
{
	private Turkraft main;
	
	private MySQL connection;
	
	public Database(Turkraft main)
	{
		this.main = main;
		
		this.connection = new MySQL(this.main.log, this.main.constants.get("mysql.host"), this.main.constants.get("mysql.base"),
				this.main.constants.get("mysql.login"), this.main.constants.get("mysql.password"));
	}
	
	public void setServerOnline()
	{
		this.setServerStatus(1);
	}
	
	public void setServerOffline()
	{
		this.setServerStatus(0);
	}

	public void setServerStatus(int status)
	{
		this.connection.ExecuteUpdate("UPDATE variables SET value = " + status + " WHERE name = 'online'");
	}
	
	public Configuration getConfigurations()
	{
		Map<String, String> configuration = new Hashtable<String, String>();
		
		ResultSet queryResult = this.connection.ExecuteSelect("SELECT variable,value FROM configuration");

        try
        {
        	while (queryResult.next())
        	{
        		configuration.put(queryResult.getString("variable"), queryResult.getString("value"));
        	}
        }
        catch (Exception ex)
        {
        	this.main.log.error(ex);
        	
        	Turkraft.noError = false;
        }
        
		return new Configuration(configuration);
	}
	
	/*
	 * public Map<String, Map<String, String>> getLanguages()
	{
		Map<String, Map<String, String>> languages = new Hashtable<String, Map<String, String>>();

		ResultSet queryResult = this.connection.ExecuteSelect("SELECT lang,abbrev,content FROM translation");

        try
        {
        	while (queryResult.next())
        	{
        		if (!languages.containsKey(queryResult.getString("lang")))
        		{
        			languages.put(queryResult.getString("lang"), new Hashtable<String, String>());	
        		}
        		
        		languages.get(queryResult.getString("lang")).put(queryResult.getString("abbrev"), queryResult.getString("content").replace("\r\n", "\n"));
        	}
        }
        catch (Exception ex)
        {
        	this.main.log.error(ex);
        }
        
		return languages;
	}
	 */
	
	public Map<Integer, Language> getLanguages()
	{
		Map<Integer, Language> languages = new Hashtable<Integer, Language>();
		
		ResultSet queryResult = this.connection.ExecuteSelect("SELECT * FROM lang");
		
        try
        {
        	while (queryResult.next())
        	{
        		int id = queryResult.getInt("id");
        		
        		String abbrev = queryResult.getString("abbrev");
        		
        		String detail = queryResult.getString("detail");
            	
            	languages.put(id,  new Language(id, abbrev, detail, new Hashtable<String, String>()));
        	}

    		for (Map.Entry<Integer, Language> entry : languages.entrySet())
    		{
        		Map<String, String> texts = new Hashtable<String, String>();

        		ResultSet queryTexts = this.connection.ExecuteSelect("SELECT abbrev, content FROM translation WHERE lang = " + entry.getKey());

            	while (queryTexts.next())
            	{
            		texts.put(queryTexts.getString("abbrev"), queryTexts.getString("content").replace("\r\n", "\n"));
            	}
            	
            	entry.getValue().texts = texts;
        	}
        }
        catch (Exception ex)
        {
        	this.main.log.error(ex);
        	
        	Turkraft.noError = false;
        }
        
		return languages;
	}

	public Map<Integer, Map<Integer, String>> getRimes()
	{
		Map<Integer, Map<Integer, String>> rimes = new LinkedHashMap<Integer, Map<Integer, String>>();
		
		ResultSet queryResult = this.connection.ExecuteSelect("SELECT virtual,lang,content FROM rimes");

        try
        {
        	while (queryResult.next())
        	{
        		if (!rimes.containsKey(queryResult.getInt("lang")))
        		{
        			rimes.put(queryResult.getInt("lang"), new Hashtable<Integer, String>());
        		}
        		
        		rimes.get(queryResult.getInt("lang")).put(queryResult.getInt("virtual"), queryResult.getString("content"));
        	}
        }
        catch (Exception ex)
        {
        	this.main.log.error(ex);
        	
        	Turkraft.noError = false;
        }
        
		return rimes;
	}
	
	public Information getPlayerInformation(String name)
	{
		//information(2) = pas enregistrer
		//information(3) = erreur interne
		
    	ResultSet queryResult = this.connection.ExecuteSelect("SELECT * FROM accounts WHERE upper(name) = '" + name.toUpperCase() + "'");
    	
    	if (queryResult == null)
    	{
    		this.main.log.information("Not existant player trying to login: " + name);
    		
        	return new Information(2);
    	}
    	
		try
		{
			if (!queryResult.first())
			{
	    		this.main.log.information("Not existant player trying to login: " + name);
	    		
	        	return new Information(2);
			}
			
			return new Information(queryResult.getInt("id"), queryResult.getString("name"), queryResult.getInt("language"), queryResult.getBoolean("white"),
					queryResult.getBoolean("banned"), queryResult.getBoolean("session"), queryResult.getInt("money"),
					queryResult.getInt("mill"), queryResult.getInt("hill"), queryResult.getInt("mie"), queryResult.getInt("hie"), queryResult.getInt("nie"),
					queryResult.getInt("clan"), queryResult.getInt("grad"), queryResult.getInt("playmin"), queryResult.getLong("lastDamage"), queryResult.getInt("combatCounter"),
					queryResult.getInt("class"), queryResult.getInt("clanRank"), queryResult.getInt("prisonLeft"), queryResult.getInt("prisonIn"), queryResult.getInt("prisonCount"),
					queryResult.getLong("playableMinutes"), queryResult.getLong("playedMinutes"), queryResult.getDate("exition"), queryResult.getBoolean("muted"));
		}
    	catch (Exception ex)
    	{
    		this.main.log.error(ex);

        	return new Information(3);
    	}
	}
	
	public ArrayList<String> getBannedIps()
	{
		ArrayList<String> bannedIps = new ArrayList<String>();
		
		ResultSet queryResult = this.connection.ExecuteSelect("SELECT ip FROM banip");

        try
        {
        	while (queryResult.next())
        	{
        		bannedIps.add(queryResult.getString("ip"));
        	}
        }
        catch (Exception ex)
        {
        	this.main.log.error(ex);
        	
        	Turkraft.noError = false;
        }
        
		return bannedIps;
	}
	
	public Colors getColors()
	{
		Colors colors = new Colors();
		
		Field[] fields = Colors.class.getFields();

		ResultSet queryResult = this.connection.ExecuteSelect("SELECT * FROM colors");

        try
        {
        	while (queryResult.next())
        	{
    			this.getField(fields, queryResult.getString("name")).set(colors, ChatColor.valueOf(queryResult.getString("value").toUpperCase()));
        	}
        }
        catch (Exception ex)
        {
        	this.main.log.error(ex);
        	
        	Turkraft.noError = false;
        }
		
		return colors;
	}
	
	private Field getField(Field[] fields, String name)
	{
		for (int i = 0; i < fields.length; i++)
		{
			if (fields[i].getName().equalsIgnoreCase(name))
			{
				return fields[i];
			}
		}
		
		return null;
	}
	
	public ArrayList<Grad> getGrads()
	{
		ArrayList<Grad> grads = new ArrayList<Grad>();

		Field[] fields = Grad.class.getFields();
		
		String types = "";
		
		for (int i = 0; i < fields.length; i++)
		{
			types += fields[i].getName() + ",";
		}
		
		types = types.substring(0, types.length() - 1);

		ResultSet queryResult = this.connection.ExecuteSelect("SELECT " + types + " FROM grad");

        try
        {
        	while (queryResult.next())
        	{
        		Grad grad = new Grad();

        		for (int i = 0; i < fields.length; i++)
        		{
        			fields[i].set(grad, queryResult.getObject(fields[i].getName()));
        		}
        		
        		if (grad.color.equalsIgnoreCase("black"))
        		{
        			grad.color = "white";
        		}
        		
        		grad.color = ChatColor.valueOf(grad.color.toUpperCase()).toString();
        		
        		grads.add(grad);
        	}
        }
        catch (Exception ex)
        {
        	this.main.log.error(ex);
        	
        	Turkraft.noError = false;
        }
        
		return grads;
	}
	
	public void setGamerConnected(int ID)
	{
		this.connection.ExecuteUpdate("UPDATE accounts SET playing = 1, session = 0, playtion = '" + Manipulation.getDateTime() + "' WHERE id = " + ID);
	}
	
	public void setGamerDisconnected(Gamer gamer)
	{
		this.connection.ExecuteUpdate("UPDATE accounts SET playing = 0, money = " + gamer.information.money + ", clan = " + gamer.information.clan
				+ ", mill = " + gamer.information.monsterKill + ", hill = " + gamer.information.humanKill
				+ ", mie = " + gamer.information.monsterDie + ", hie = " + gamer.information.humanDie + ", playmin = playmin + " + gamer.playedTimeInMin() + ", clanRank = " + gamer.information.clanRank
				+ ", banned = " + (gamer.information.banned? 1 : 0) + ", lastDamage = " + gamer.information.lastDamage + ", combatCounter = " + gamer.information.combatCounter
				+ ", prisonIn = " + gamer.information.prisonIn + ", prisonLeft = " + gamer.information.prisonLeft + ", prisonCount = " + gamer.information.prisonCount
				+ ", exition = '" + Manipulation.getDateTime() + "'"
				+ ", playedMinutes = " + gamer.information.playedMinutes + ", muted = " + gamer.information.muted
				+ " WHERE id = " + gamer.information.id);
	}
	
	public void gamerSave(Gamer gamer)
	{
		this.connection.ExecuteUpdate("UPDATE accounts SET money = " + gamer.information.money + ", clan = " + gamer.information.clan
				+ ", mill = " + gamer.information.monsterKill + ", hill = " + gamer.information.humanKill
				+ ", mie = " + gamer.information.monsterDie + ", hie = " + gamer.information.humanDie
				+ ", playmin = playmin + " + gamer.playedTimeInMin() + ", clanRank = " + gamer.information.clanRank
				+ ", prisonIn = " + gamer.information.prisonIn + ", prisonLeft = " + gamer.information.prisonLeft + ", prisonCount = " + gamer.information.prisonCount
				+ ", playedMinutes = " + gamer.information.playedMinutes + ", muted = " + gamer.information.muted
				+ " WHERE id = " + gamer.information.id);
		
		gamer.refreshJoinTime();
	}
	
	public Map<EntityType, MobMoney> getMobMoneys()
	{
		Map<EntityType, MobMoney> mobMoneys = new Hashtable<EntityType, MobMoney>();

		ResultSet queryResult = this.connection.ExecuteSelect("SELECT * FROM mobmon");
		
        try
        {
        	while (queryResult.next())
        	{
        		EntityType type = null;
        		
        		try
        		{
        			type = EntityType.fromId(Integer.parseInt(queryResult.getString(2)));
        		}
        		catch (Exception ex)
        		{
        			type = EntityType.fromName(queryResult.getString(2));
        		}

        		mobMoneys.put(type, new MobMoney(queryResult.getInt(1), type, queryResult.getInt(3), queryResult.getInt(4), queryResult.getBoolean(5)));
        	}
        }
        catch (SQLException ex)
        {
        	this.main.log.error(ex);
        	
        	Turkraft.noError = false;
		}
        
		return mobMoneys;
	}
	
	public ArrayList<Terrain> getTerrains()
	{
		ArrayList<Terrain> terrains = new ArrayList<Terrain>();

		ResultSet queryResult = this.connection.ExecuteSelect("SELECT * FROM territories");

        try
        {
        	while (queryResult.next())
        	{
        		terrains.add(new Terrain(queryResult.getInt("id"), new Ownable(queryResult.getString("owner")), 
        				Manipulation.plotFromString(this.main, queryResult.getString("location")),
        				new Whitisble(Manipulation.stringArrayFromSplit(queryResult.getString("whitelist"), Manipulation.separator)),
        				new Pricable(queryResult.getInt("price")), queryResult.getBoolean("breakable")));
        	}
        }
        catch (SQLException ex)
        {
        	this.main.log.error(ex);
        	
        	Turkraft.noError = false;
		}
        
		return terrains;
	}
	
	public ArrayList<Indestructible> getIndestructibles()
	{
		ArrayList<Indestructible> indestructibles = new ArrayList<Indestructible>();

		ResultSet queryResult = this.connection.ExecuteSelect("SELECT * FROM blocks");

        try
        {
        	while (queryResult.next())
        	{
        		indestructibles.add(new Indestructible(queryResult.getInt("id"), new Ownable(queryResult.getString("creator")), 
        				Manipulation.plotFromString(this.main, queryResult.getString("location"))));
        	}
        }
        catch (SQLException ex)
        {
        	this.main.log.error(ex);
        	
        	Turkraft.noError = false;
		}
        
		return indestructibles;
	}
	
	public void saveConfiguration(String name, String value)
	{
		this.connection.ExecuteUpdate("UPDATE configuration SET value = '" + value + "' WHERE variable = '" + name + "'");
	}
	
	public void saveConfiguration(String name, int value)
	{
		this.saveConfiguration(name, Integer.toString(value));
	}
	
	public void deleteIndestructible(int id)
	{
		this.connection.ExecuteDelete("DELETE FROM blocks WHERE id = " + id);
	}
	
	public void addIndestructible(Indestructible indestructible)
	{
		this.connection.ExecuteInsert("INSERT INTO blocks(creator, location) VALUES('" +
				indestructible.creator.owner + "', '" + Manipulation.plotLocationToString(indestructible.plot) + "')");
		
		indestructible.id = this.connection.GetLastId("blocks");
	}
	
	public void updateIndestructible(Indestructible indestructible)
	{
		this.connection.ExecuteUpdate("UPDATE blocks SET location = '" + Manipulation.plotLocationToString(indestructible.plot) + "' WHERE id = " + indestructible.id);
	}
	
	public void deleteTerrain(int id)
	{
		this.connection.ExecuteDelete("DELETE FROM territories WHERE id = " + id);
	}

	public void addTerrain(Terrain terrain)
	{
		this.connection.ExecuteInsert("INSERT INTO territories(owner, location, breakable, whitelist, price) VALUES('" +
				terrain.owner.owner + "', '" + Manipulation.plotLocationToString(terrain.plot) + "', " 
				+ (terrain.breakable ? 1 : 0) + ", '" + Manipulation.charsFromArraySeparator(terrain.whitisble.whiteGamers.toArray(new String[0]), Manipulation.separator) + "', "
				+ terrain.pricable.price + ")");
		
		terrain.id = this.connection.GetLastId("territories");
	}
	
	public void updateTerrain(Terrain terrain)
	{
		this.connection.ExecuteUpdate("UPDATE territories SET owner = '" + terrain.owner.owner + "', location = '" + Manipulation.plotLocationToString(terrain.plot) + "' "
				+ ", whitelist = '" + Manipulation.charsFromArraySeparator(terrain.whitisble.whiteGamers.toArray(new String[0]), Manipulation.separator) + "', "
				+ "price = " + terrain.pricable.price + ", breakable = " + (terrain.breakable ? 1 : 0) + " WHERE id = " + terrain.id);
	}
	
	public void addPlayerMoney(String name, int money)
	{
		this.connection.ExecuteUpdate("UPDATE accounts SET money = money + " + money + " WHERE name = '" + name + "'");
	}
	
	public Map<Integer, Clan> getClans()
	{
		Map<Integer, Clan> clans = new Hashtable<Integer, Clan>();

		ResultSet queryResult = this.connection.ExecuteSelect("SELECT * FROM clans");

        try
        {
        	while (queryResult.next())
        	{
        		clans.put(queryResult.getInt("virtual"), new Clan(queryResult.getInt("virtual"), queryResult.getString("name"), 
        				Manipulation.plotFromString(this.main, queryResult.getString("location")), queryResult.getInt("score"),
        				queryResult.getInt("chief"), queryResult.getInt("money"), queryResult.getInt("taken"),
        				Manipulation.getLocationWorld(queryResult.getString("spawn"), true), queryResult.getInt("total")));
        	}
        }
        catch (SQLException ex)
        {
        	this.main.log.error(ex);
        	
        	Turkraft.noError = false;
		}
        
		return clans;
	}
	
	public void addClan(Clan clan)
	{
		this.connection.ExecuteInsert("INSERT INTO clans(virtual, name, location, score, chief, money, taken, spawn, total) VALUES(" +
				 + clan.virtual + ", '" + clan.name + "', '" + Manipulation.plotLocationToString(clan.plot) + "', " + clan.score + ", "
				 + clan.chiefId + ", " + clan.money + ", " + clan.taken + ", '" + Manipulation.getLocationWorld(clan.spawn, true) + "', " + clan.countMembers + ")");
	}
	
	public void updateClan(Clan clan)
	{
		this.connection.ExecuteUpdate("UPDATE clans SET money = " + clan.money + ", score = " + clan.score + ", taken = " + clan.taken + ", "
				+ "total = " + clan.countMembers + ", spawn = '" + Manipulation.getLocationWorld(clan.spawn, true) + "' WHERE virtual = " + clan.virtual);
	}
	
	public void deleteClan(int id)
	{
		this.connection.ExecuteDelete("DELETE FROM clans WHERE virtual = " + id);
	}
	
	public Map<Location, Shop> getShops()
	{
		Map<Location, Shop> shops = new Hashtable<Location, Shop>();

		ResultSet queryResult = this.connection.ExecuteSelect("SELECT * FROM shops");

        try
        {
        	while (queryResult.next())
        	{
        		Location location = Manipulation.getLocationWorld(queryResult.getString("position"));

        		shops.put(location, new Shop(queryResult.getInt("id"), new Ownable(queryResult.getString("owner")),
        				new Pricable(queryResult.getInt("price")), queryResult.getBoolean("diminuates"), Material.getMaterial(queryResult.getInt("material")),
        				queryResult.getInt("quantity"), location, queryResult.getInt("sells"), queryResult.getShort("data")));
        	}
        }
        catch (SQLException ex)
        {
        	this.main.log.error(ex);
        	
        	Turkraft.noError = false;
		}
        
		return shops;
	}
	
	public void addShop(Shop shop)
	{
		this.connection.ExecuteInsert("INSERT INTO shops(owner, price, material, position, quantity, diminuates, sells, data) VALUES('"
			+ shop.ownable.owner + "', " + shop.pricable.price + ", " + shop.material.getId() + ", '" + Manipulation.getLocationWorld(shop.position) + "', "
			+ shop.quantity + ", "  + (shop.diminuates? 1 : 0)  + ", " + shop.sells + ", " + shop.data + ")");
		
		shop.id = this.connection.GetLastId("shops");
	}
	
	public void updateShop(Shop shop)
	{
		this.connection.ExecuteUpdate("UPDATE shops SET price = " + shop.pricable.price + ", material = " + shop.material.getId() + ", quantity = "
				+ shop.quantity + ", sells = " + shop.sells + ", data = " + shop.data + " WHERE id = " + shop.id);
	}
	
	public void deleteShop(int id)
	{
		this.connection.ExecuteDelete("DELETE FROM shops WHERE id = " + id);
	}
	
	public ArrayList<Teleporter> getTeleporters()
	{
		ArrayList<Teleporter> teleporters = new ArrayList<Teleporter>();

		ResultSet queryResult = this.connection.ExecuteSelect("SELECT * FROM teleporter");

        try
        {
        	while (queryResult.next())
        	{
        		teleporters.add(new Teleporter(queryResult.getInt("id"), queryResult.getString("name"),
        				 Manipulation.getLocationWorld(queryResult.getString("initial")), Manipulation.getLocationWorld(queryResult.getString("destination"), true)));
        	}
        }
        catch (SQLException ex)
        {
        	this.main.log.error(ex);
        	
        	Turkraft.noError = false;
		}
        
		return teleporters;
	}
	
	public void deleteTeleporter(int id)
	{
		this.connection.ExecuteDelete("DELETE FROM teleporter WHERE id = " + id);
	}
	
	public void addTeleporter(Teleporter teleporter)
	{
		this.connection.ExecuteInsert("INSERT INTO teleporter(initial, destination, name) VALUES('" +
				Manipulation.getLocationWorld(teleporter.initial) + "', '" + Manipulation.getLocationWorld(teleporter.destination) + 
				"', '" + teleporter.name + "')");
		
		teleporter.id = this.connection.GetLastId("teleporter");
	}
	
	public void updateTeleporter(Teleporter teleporter)
	{
		this.connection.ExecuteUpdate("UPDATE teleporter SET name = '" + teleporter.name + "', initial = '" + Manipulation.getLocationWorld(teleporter.initial) + "', " +
				"destination = '" + Manipulation.getLocationWorld(teleporter.destination) + "'" +
				"' WHERE id = " + teleporter.id);
	}
	
	public Genres getGenres()
	{
		Genres genres = new Genres();

		ResultSet queryResult = this.connection.ExecuteSelect("SELECT * FROM classes");

        try
        {
        	while (queryResult.next())
        	{
        		genres.getClass().getField(Manipulation.capitalizeFirst(queryResult.getString("variable"))).setInt(genres, queryResult.getInt("id"));
        	}
        }
        catch (Exception ex)
        {
        	this.main.log.error(ex);
        	
        	Turkraft.noError = false;
        }
        
		return genres;
	}

	public void setGamerGenre(Gamer gamer, int genre)
	{
		this.connection.ExecuteUpdate("UPDATE accounts SET class = " + genre + " WHERE id = " + gamer.information.id);
	}
	
	public void resetGamerGenre(Gamer gamer)
	{
		this.setGamerGenre(gamer, 0);
	}
	
	public ArrayList<Prisval> getPrisvals()
	{
		ArrayList<Prisval> prisvals = new ArrayList<Prisval>();

		ResultSet queryResult = this.connection.ExecuteSelect("SELECT * FROM prisvals");

        try
        {
        	while (queryResult.next())
        	{
        		prisvals.add(new Prisval(queryResult.getInt("id"), Material.getMaterial(queryResult.getInt("block")), queryResult.getInt("value")));
        	}
        }
        catch (Exception ex)
        {
        	this.main.log.error(ex);
        	
        	Turkraft.noError = false;
        }
        
		return prisvals;
	}
	
	public ArrayList<Prison> getPrisons()
	{
		ArrayList<Prison> prisons = new ArrayList<Prison>();

		ResultSet queryResult = this.connection.ExecuteSelect("SELECT * FROM prisons");

        try
        {
        	while (queryResult.next())
        	{
        		prisons.add(new Prison(this.main, queryResult.getInt("id"), queryResult.getString("variable"), Manipulation.plotFromString(this.main, queryResult.getString("location")), 
        				Manipulation.getLocationWorld(queryResult.getString("spawn")),
        				 Manipulation.getLocationWorld(queryResult.getString("quit")),
        				  queryResult.getInt("counter")));
        	}
        }
        catch (Exception ex)
        {
        	this.main.log.error(ex);
        	
        	Turkraft.noError = false;
        }
        
		return prisons;
	}
	
	public void addPrison(Prison prison)
	{
		this.connection.ExecuteInsert("INSERT INTO prisons(spawn, quit, variable, counter, location) VALUES("
				+ "'" + Manipulation.getLocationWorld(prison.spawn) + "', "+ "'" + Manipulation.getLocationWorld(prison.exit) + "', "
				+ "'" + prison.variable + "'" + ", " + prison.counter + ", "
				+ "'" + Manipulation.plotLocationToString(prison.plot) + "'"
				+ ")");
			
		prison.id = this.connection.GetLastId("prisons");
	}
	
	public void updatePrison(Prison prison)
	{
		this.connection.ExecuteUpdate("UPDATE prisons SET "
				+ "counter = " + prison.counter + ", spawn = '" + Manipulation.getLocationWorld(prison.spawn) + "', "
				+ "quit = '" + Manipulation.getLocationWorld(prison.exit) + "'"
 				+ " WHERE id = " + prison.id);
	}
	
	public void deletePrison(int id)
	{
		this.connection.ExecuteDelete("DELETE FROM prisons WHERE id = " + id);
	}
	
	public Map<Location, Prische> getPrisches()
	{
		Map<Location, Prische> prisches = new Hashtable<Location, Prische>();

		ResultSet queryResult = this.connection.ExecuteSelect("SELECT * FROM prisches");

        try
        {
        	while (queryResult.next())
        	{
        		Location location = Manipulation.getLocationWorld(queryResult.getString("location"));
        		
        		prisches.put(location, new Prische(queryResult.getInt("id"), this.main.getPrison(queryResult.getInt("prison")),
        				location, Manipulation.getDinger(queryResult.getString("timing"), true)));
        	}
        }
        catch (Exception ex)
        {
        	this.main.log.error(ex);
        	
        	Turkraft.noError = false;
        }
		
		return prisches;
	}
	
	public void addPrische(Prische prische)
	{
		this.connection.ExecuteInsert("INSERT INTO prisches(prison, location, timing) VALUES("
				+ prische.prison.id + ", "
				+ "'" + Manipulation.getLocationWorld(prische.location) + "', "
				+ "'" + Manipulation.getDinger(prische.interval) + "'"
				+ ")");
			
		prische.id = this.connection.GetLastId("prisches");
	}
	
	/*public void Prische(Prische prische) //pas besoin de update ca, on construit et detruit juste
	{
		this.connection.ExecuteUpdate("UPDATE prisches SET "
				+ "counter = " + prison.counter + ", spawn = '" + Manipulation.getLocationWorld(prison.spawn) + "', "
				+ "exit = '" + Manipulation.getLocationWorld(prison.exit) + "'"
 				+ " WHERE id = " + prison.id);
	}*/
	
	public void deletePrische(int id)
	{
		this.connection.ExecuteDelete("DELETE FROM prisches WHERE id = " + id);
	}
	
	public Map<Location, Prispos> getPrispos()
	{
		Map<Location, Prispos> prispos = new Hashtable<Location, Prispos>();

		ResultSet queryResult = this.connection.ExecuteSelect("SELECT * FROM prispos");

        try
        {
        	while (queryResult.next())
        	{
        		Location location = Manipulation.getLocationWorld(queryResult.getString("location"));
        		
        		prispos.put(location, new Prispos(queryResult.getInt("id"), this.main.getPrison(queryResult.getInt("prison")),
        				location, Manipulation.getDinger(queryResult.getString("timing"), true), Material.getMaterial(queryResult.getInt("item"))));
        	}
        }
        catch (Exception ex)
        {
        	this.main.log.error(ex);
        	
        	Turkraft.noError = false;
        }
		
		return prispos;
	}
	
	public void addPrispos(Prispos prispos)
	{
		this.connection.ExecuteInsert("INSERT INTO prispos(prison, location, timing, item) VALUES("
				+ prispos.prison.id + ", "
				+ "'" + Manipulation.getLocationWorld(prispos.location) + "', "
				+ "'" + Manipulation.getDinger(prispos.interval) + "', "
				+ prispos.item.getId()
				+ ")");
			
		prispos.id = this.connection.GetLastId("prisches");
	}
	
	/*public void Prische(Prische prische) //pas besoin de update ca, on construit et detruit juste
	{
		this.connection.ExecuteUpdate("UPDATE prisches SET "
				+ "counter = " + prison.counter + ", spawn = '" + Manipulation.getLocationWorld(prison.spawn) + "', "
				+ "exit = '" + Manipulation.getLocationWorld(prison.exit) + "'"
 				+ " WHERE id = " + prison.id);
	}*/
	
	public void deletePrispos(int id)
	{
		this.connection.ExecuteDelete("DELETE FROM prispos WHERE id = " + id);
	}
	
	public Map<Location, Prisgiv> getPrisgivs()
	{
		Map<Location, Prisgiv> prisgivs = new Hashtable<Location, Prisgiv>();

		ResultSet queryResult = this.connection.ExecuteSelect("SELECT * FROM prisgivs");

        try
        {
        	while (queryResult.next())
        	{
        		Location location = Manipulation.getLocationWorld(queryResult.getString("location"));
        		
        		prisgivs.put(location, new Prisgiv(queryResult.getInt("id"), this.main.getPrison(queryResult.getInt("prison")), location));
        	}
        }
        catch (Exception ex)
        {
        	this.main.log.error(ex);
        	
        	Turkraft.noError = false;
        }
        
		return prisgivs;
	}
	
	public void addPrisgiv(Prisgiv prisgiv)
	{
		this.connection.ExecuteInsert("INSERT INTO prisgivs(prison, location) VALUES("
				+ prisgiv.prison.id + ", "
				+ "'" + Manipulation.getLocationWorld(prisgiv.location) + "'"
				+ ")");
			
		prisgiv.id = this.connection.GetLastId("prisgivs");
	}
	
	public void deletePrisgiv(int id)
	{
		this.connection.ExecuteDelete("DELETE FROM prisgivs WHERE id = " + id);
	}
	
	public Genned[] getGenneds()
	{
		ArrayList<Genned> genneds = new ArrayList<Genned>();

		ResultSet queryResult = this.connection.ExecuteSelect("SELECT * FROM genneds");

        try
        {
        	while (queryResult.next())
        	{
        		genneds.add(new Genned(queryResult.getInt("id"), queryResult.getInt("chance"), queryResult.getInt("value"), Material.getMaterial(queryResult.getInt("item"))));
        	}
        }
        catch (Exception ex)
        {
        	this.main.log.error(ex);
        	
        	Turkraft.noError = false;
        }
        
		return genneds.toArray(new Genned[] {});
	}
	
	public int getPlayerGrad(Gamer gamer)
	{
    	ResultSet queryResult = this.connection.ExecuteSelect("SELECT grad FROM accounts WHERE id = " + gamer.information.id);
    	
		try
		{
			if (!queryResult.first())
			{
				System.out.println("getPlayerGrad !queryResult.first() (" + gamer.information.name + ")");
				
				return 0;
			}
			
			return queryResult.getInt("grad");
		}
    	catch (Exception ex)
    	{
    		this.main.log.error(ex);
    	}
		
		this.main.log.error("No grad loaded for player " + gamer.information.id);
		
		return 0;
	}
}