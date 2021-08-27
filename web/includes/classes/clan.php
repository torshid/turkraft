<?php
	class Clan
	{
		private $Page;
		
		private $Virtual;
	
		private $Name;
		
		private $Score;
		
		private $Chief;
		
		private $Money;
		
		private $Taken;
		
		private $Total;
		
		private $Connected;
		
		public function __construct($Page, $Abbreviation)
		{
			$this->Page = $Page;
			
			$Rows = 'virtual,name,chief,money,score,total,taken';
			
			if (!is_numeric($Abbreviation))
			{
				$Query = mysql_query("SELECT $Rows FROM clans WHERE name = '$Abbreviation'");
			}
			else
			{
				$Query = mysql_query("SELECT $Rows FROM clans WHERE virtual = $Abbreviation");
			}
			
			if (!mysql_num_rows($Query))
			{
				return;
			}
			
			$Values = mysql_fetch_assoc($Query);
			
			foreach ($Values as $Key => $Value)
			{
				$Key = ucfirst($Key);
				
				$this->$Key = $Value;
			}
			
			$this->Connected = array();
			
			$Query = mysql_query("SELECT id FROM accounts WHERE playing = 1 AND clan = " . $this->Virtual);
			
			if (!mysql_num_rows($Query))
			{
				return;
			}
			
			while ($Values = mysql_fetch_assoc($Query))
			{
				array_push($this->Connected, $Values['id']);
			}
		}
		
		public function GetVirtual()
		{
			return $this->Virtual;
		}
		
		public function GetName()
		{
			return $this->Name;
		}
		
		public function GetScore()
		{
			return $this->Score;
		}
		
		public function GetChief()
		{
			return $this->Chief;
		}
		
		public function GetMoney()
		{
			return $this->Money;
		}
		
		public function GetTaken()
		{
			return $this->Taken;
		}
		
		public function GetTotal()
		{
			return $this->Total;
		}
		
		public function GetConnected()
		{
			return $this->Connected;
		}
		
		public function GetLink()
		{
			return 'clan.php?id=' . $this->Virtual;
		}
		
		public function LoadConnected()
		{
			$this->Page->GetFunctions()->Load('player');
			
			$New = array();
			
			foreach ($this->Connected as $Id)
			{
				array_push($New, new Player($this->Page, $Id));
			}
			
			$this->Connected = $New;
		}
	}
?>