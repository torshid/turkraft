package turkraft.stockers;


public class Grad
{
	//id du groupe
	public int id;
	
	//couleur du nom sur la chat
	public String color;
	
	//heures de jeu par jour
	public int playTime;
	
	//peut se connecter
	public boolean canConnect;
	
	//peut detruire les cases
	public boolean canBreak;
	
	//peut poser des cases
	public boolean canPlace;
	
	//peut intereagir
	public boolean canInteract;
	
	//peut bouger
	public boolean canMove;
	
	//peut parler
	public boolean canChat;
	
	//peut attaquer
	public boolean canAttack;
	
	//peut se faire attaquer
	public boolean canBeAttacked;
	
	//peut creer un territoire
	public boolean canTerritory;
	
	//peut dropper des items
	public boolean canDropItems;
	
	//peut utiliser les portails (nether, etc)
	public boolean canUsePortal;
	
	//peut etre silencieux
	public boolean canSneak;
	
	//peut courir
	public boolean canSprint;
	
	//peut ouvrir son inventaire
	public boolean canOpenInventory;
	
	//peut gerer les territoires
	public boolean canGodTerritories;
	
	//peut bloquer completement une zone
	public boolean canLock;
	
	//peut creer un magasin
	public boolean canSalable;
	
	//peut gerer les magasins
	public boolean canGodSalables;
	
	//peut creer/modifer des magasins a vie
	public boolean canShops;
	
	//peut donner de son argent a un joueur
	public boolean canMoney;
	
	//peut donner ou prendre de l'argent a un joueur
	public boolean canGodMoney;
	
	//peut voir l'argent d'un joueur
	public boolean canSeeMoney;
	
	//peut envoyer de son argent
	public boolean canSendMoney;
	
	//peut deconnecter un joueur
	public boolean canKick;
	
	//peut bannir un compte
	public boolean canBan;
	
	//peut bannir une ip
	public boolean canBanIP;
	
	//peut modifier les heures de jeu d'un joueur
	public boolean canGodPlayTime;
	
	//peut modifier le temps du serveur
	public boolean canGodGameTime;
	
	//peut se teleporter
	public boolean canTeleport;
	
	//peut voler
	public boolean canFly;
	
	//peut faire des /give item
	public boolean canGodItems;
	
	//peut changer le point de spawn
	public boolean canChangeSpawn;
	
	//peut se teleporter au spawn
	public boolean canTeleportToSpawn;
	
	//peut redemarrer le serveur
	public boolean canRestartServer;
	
	//peut modifier l'xp d'un joueur
	public boolean canModifyXP;
	
	//peut faire la pluie sur le serveur
	public boolean canModifyRain;
	
	//peut voir la position d'un joueur
	public boolean canSeeLocations;
	
	//peut se mettre afk
	public boolean canAFK;
	
	//peut voir si un joueur est afk
	public boolean canSeeAFK;
	
	//peut voir la liste de ceux qui sont afk
	public boolean canSeeAllAFK;
	
	//peut kick ceux qui sont afk
	public boolean canKickAllAFK;
	
	//a acces au chat des admins
	public boolean canAccessAdminChat;
	
	//peut spawner une creature
	public boolean canSpawnElement;
	
	//peut chat en priver
	public boolean canPrivateChat;
	
	//peut se connecter depuis la meme ip plusieurs fois
	public boolean canMultiIP;
	
	//peut selectionner un bloc
	public boolean canSelectBlock;
	
	//peut se teleporter a la case la plus haute
	public boolean canTeleportTop;
	
	//peut se teleporter a son lit
	public boolean canTeleportBed;
	
	//tue en un coup
	public boolean canOneShot;
	
	//peut decider de la vie
	public boolean canSetHealth;
	
	//peut decider de la nourriture
	public boolean canSetFood;
	
	//peut decider le niveau
	public boolean canSetLevel;
	
	//peut donner de l'xp
	public boolean canGiveExp;
	
	//detruit les blocs en un coup
	public boolean isPowerful;
	
	//peut avoir un groupe
	public boolean canGroup;
	
	//peut se teleporter a son dernier point de mort
	public boolean canGotoDeath;
	
	//commande pour tuer en un coup
	public boolean canKill;
	
	//garde son niveau quand il meurt
	public boolean keepDeathLevel;
	
	//peut detruire dans les territoires indestructible
	public boolean canBreakWhereLocked;
	
	//peut placer des blocs dans les territoires indestructibles
	public boolean canPlaceWhereLocked;
	
	//peut intereagir dans les territoires indestructibles
	public boolean canInteractWhereLocked;
	
	//peut ecraser dans les territories indestructibles
	public boolean canSoilDestroyLocked;

	//peut detruire dans les territoires non a lui
	public boolean canBreakWhereReserved;
	
	//peut placer des blocs dans les territoires non a lui
	public boolean canPlaceWhereReserved;
	
	//peut intereagir dans les territoires non a lui
	public boolean canInteractWhereReserved;
	
	//peut ecraser dans les territories non a lui
	public boolean canSoilDestroyReserved;
	
	//ne perd pas de bloc en posant
	public boolean noBlockLoose;
	
	//peut tout faire dans les clans
	public boolean canGodClans;

	//peut detruire dans les clans non a lui
	public boolean canBreakWhereClan;
	
	//peut placer des blocs dans les clans non a lui
	public boolean canPlaceWhereClan;
	
	//peut intereagir dans les clans non a lui
	public boolean canInteractWhereClan;
	
	//peut ecraser dans les clans non a lui
	public boolean canSoilDestroyClan;
	
	//envoyer un message a tous les joueurs disant que y a une maj
	public boolean canBroadcastMaj;
	
	//peut definir des zones de spawn
	public boolean canSpawner;
	
	//peut utiliser les teleporteurs
	public boolean canUseTeleporter;
	
	//peut creer des teleporteurs
	public boolean canCreateTeleporter;
	
	//peut se teleporter entre les mondes
	public boolean canWorldTp;
	
	//peut se mettre invisible
	public boolean canInvisible;
	
	//peut voir la distance
	public boolean canSeeDistance;
	
	//peut voir l'ip
	public boolean canSeeIp;
	
	//peut remettre a 0 la classe d'un joueur
	public boolean canResetClass;
	
	//peut voir des statistiques sur le lag
	public boolean canLag;

	//peut envoyer quelqu'un en prison
	public boolean canJail;
	
	//peut retirer quelqu'un de prison
	public boolean canUnjail;
	
	//peut modifier les prisons
	public boolean canGodJails;
	
	//ne peut etre kick
	public boolean cantBeKicked;
	
	//peut mettre sanguine
	public boolean canPutSanguine;
	
	//peut enlever sanguine
	public boolean canRemoveSanguine;
	
	//peut mute un joueur
	public boolean canMute;
	
	//peut unmute un joueur
	public boolean canUnMute;
	
	//peut ouvrir un inventaire
	public boolean canOpenPlayerInventory;
	
	//peut recharger les grades de la bdd
	public boolean canReloadGrads;
	
	//peut recharger les grades des joueurs
	public boolean canReloadPlayerGrads;
}
