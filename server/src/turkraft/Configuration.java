package turkraft;

import java.util.Map;

import org.bukkit.Location;

import turkraft.common.Manipulation;
import turkraft.stockers.Dinger;

public class Configuration
{
	public boolean acceptConnections;

	public int maximumBuyMoney;
	
	public int maximumKillMoney; //utiliser cette variable quand un joueur tue
	
	public int maximumPlayers;
	
	public Location spawnPoint;
	
	public boolean whiteList;
	
	public int defaultLanguage;
	
	public int killMoneyPourcentage;
	
	public int killPourcentageRandom;
	
	public Dinger rimeInterval; //defaut: 2400;4800
	
	public int protectedSpawnTime;
	
	public int minimumLengthTerrain; //minimum de la longueur ou largeur d'un terrain
	
	public boolean mobKillEachOther;
	
	public int maximumGroupGamer;
	
	public int restartTime;
	
	public int stopTime;
	
	public int saveInterval;
	
	public int whitelistMax;
	
	public int maximumLengthTerrain; //maximum de la longueur ou largeur d'un terrain
	
	public int clanCollision; //collision avec les clans pour les tueries
	
	public int minimumLengthClan; //taille minimum pour les terrains
	
	public int alertRestClan; //score a partir duquel on fait des alertes au clan

	public int clanMinimumInteract; //minimum de membres necessaire connectes dans un clan pour faire des attaques, etc
	
	public int maximumClanMembers; //maximum de gens dans un clan
	
	public boolean checkSession; //verifie la session a la connexion
	
	public int fightMinimumReco;
	
	public int fightInterval;
	
	public boolean restart;
	
	public Configuration(Map<String, String> variables)
	{
		this.acceptConnections = Manipulation.isTrue(variables.get("acceptConnections"));
		
		this.maximumBuyMoney = Integer.parseInt(variables.get("maximumBuyMoney"));
		
		this.maximumKillMoney = Integer.parseInt(variables.get("maximumKillMoney"));
		
		this.maximumPlayers = Integer.parseInt(variables.get("maximumPlayers"));
		
		this.spawnPoint = Manipulation.getLocationWorld(variables.get("spawnPoint"), true);

		this.whiteList = Manipulation.isTrue(variables.get("whiteList"));
		
		this.defaultLanguage = Integer.parseInt(variables.get("defaultLanguage"));
		
		this.rimeInterval = Manipulation.getDinger(variables.get("rimeInterval"), true);
		
		this.killMoneyPourcentage = Integer.parseInt(variables.get("killmoneypourcentage"));
		
		this.killPourcentageRandom = Integer.parseInt(variables.get("killpourcentagerandom")); //avoir le pourcentage qui varie entre random(killPourcentageRandom-ca, killPourcentageRandom+ca)
		
		this.protectedSpawnTime = Integer.parseInt(variables.get("protectedspawntime")) * 20; //multiplication par 20 pour les tick scheduler
		
		this.minimumLengthTerrain = Integer.parseInt(variables.get("minimumlengthterrain"));
		
		this.mobKillEachOther = Boolean.parseBoolean(variables.get("mobkilleachother"));

		this.maximumGroupGamer = Integer.parseInt(variables.get("maximumGroupGamer"));
		
		this.restartTime = Integer.parseInt(variables.get("restartTime")) * 20;
		
		this.stopTime = Integer.parseInt(variables.get("stopTime")) * 20;
		
		this.saveInterval = Integer.parseInt(variables.get("saveInterval")) * 20;
		
		this.whitelistMax = Integer.parseInt(variables.get("whitelistMax"));
		
		this.maximumLengthTerrain = Integer.parseInt(variables.get("maximumlengthterrain"));
		
		this.clanCollision = Integer.parseInt(variables.get("clanCollision"));
		
		this.minimumLengthClan = Integer.parseInt(variables.get("minimumLengthClan"));
		
		this.alertRestClan = Integer.parseInt(variables.get("alertRestClan"));
		
		this.clanMinimumInteract = Integer.parseInt(variables.get("clanMinimumInteract"));
		
		this.maximumClanMembers = Integer.parseInt(variables.get("maximumClanMembers"));
		
		this.checkSession = Boolean.parseBoolean(variables.get("checkSession"));
		
		this.fightMinimumReco = Integer.parseInt(variables.get("fightMinimumReco"));
		
		this.fightInterval = Integer.parseInt(variables.get("fightInterval"));
		
		this.restart = Boolean.parseBoolean(variables.get("restart"));
	}
}
