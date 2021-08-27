package turkraft.stockers;

import java.util.Date;


public class Information
{
	public int id;
	
	public int status = -1;
	
	public String name;
	
	public int language;
	
	public boolean white;
	
	public boolean banned;
	
	public boolean session;
	
	public int money;
	
	public int monsterKill;
	
	public int humanKill;
	
	public int monsterDie;
	
	public int humanDie;
	
	public int nothingDie;
	
	public int clan; //doit etre de type Clan normalement
	
	public int grad;
	
	public long playmin;
	
	public long lastDamage;
	
	public int combatCounter;
	
	public int genre;
	
	public int clanRank;
	
	public int prisonLeft;
	
	public int prisonIn;
	
	public int prisonCount;
	
	public long playableMinutes;
	
	public long playedMinutes;
	
	public Date exition;
	
	public boolean muted;
	
	public Information(int status)
	{
		this.status = status;
	}
	
	public Information(int id, String name, int language, boolean white, boolean banned, boolean session, int money,
			int monsterKill, int humanKill, int monsterDie, int humanDie, int nothingDie, int clan, int grad, long playmin,
			long lastDamage, int combatCounter, int genre, int clanRank, int prisonLeft, int prisonIn, int prisonCount,
			long playableMinutes, long playedMinutes, Date exition, boolean muted)
	{
		this.id = id;
		
		this.name = name;
		
		this.language = language;
		
		this.white = white;
		
		this.banned = banned;
		
		this.session = session;
		
		this.money = money;
		
		this.monsterKill = monsterKill;
		
		this.humanKill = humanKill;
		
		this.monsterDie = monsterDie;
		
		this.humanDie = humanDie;
		
		this.nothingDie = nothingDie;
		
		this.clan = clan;
		
		this.grad = grad;
		
		this.playmin = playmin;
		
		this.lastDamage = lastDamage;
		
		this.combatCounter = combatCounter;
		
		this.genre = genre;
		
		this.clanRank = clanRank;
		
		this.prisonLeft = prisonLeft;
		
		this.prisonIn = prisonIn;
		
		this.prisonCount = prisonCount;

		this.playableMinutes = playableMinutes;
		
		this.playedMinutes = playedMinutes;
		
		this.exition = exition;
		
		this.muted = muted;
		
		this.status = 1;
	}
}
