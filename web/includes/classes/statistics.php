<?php
	class Statistics
	{
		private $Page;
		
		private $Members;
		
		private $Registers;
		
		private $Connections;
		
		private $Playtions;
		
		private $InGame;
		
		private $Last;
		
		private $Shops;
		
		private $Clans;
		
		private $Territories;
		
		public function __construct($Page)
		{
			$this->Page = $Page;
			
			$this->Members = 0;
			
			$this->Registers = 0;
			
			$this->Connections = 0;
			
			$this->Playtions = 0;
			
			$this->InGame = 0;
			
			$this->Last = null;
			
			$this->Shops = 0;
			
			$this->Clans = 0;
			
			$this->Territories = 0;
		}
		
		public function Load()
		{
			$this->Members = mysql_num_rows(mysql_query('SELECT id FROM accounts'));
			
			$this->Registers = mysql_num_rows(mysql_query('SELECT id FROM accounts WHERE register >= DATE_SUB(CURDATE(), INTERVAL 1 DAY)'));
			
			$this->Connections = mysql_num_rows(mysql_query('SELECT id FROM accounts WHERE connection >= DATE_SUB(CURDATE(), INTERVAL 1 DAY)'));
			
			$this->Playtions = mysql_num_rows(mysql_query('SELECT id FROM accounts WHERE playtion >= DATE_SUB(CURDATE(), INTERVAL 1 DAY)'));
			
			$this->InGame = mysql_num_rows(mysql_query('SELECT id FROM accounts WHERE playing = 1'));
			
			$this->Page->GetFunctions()->Load('player');
			
			$Rows = mysql_fetch_row(mysql_query('SELECT id FROM accounts ORDER BY id DESC LIMIT 0,1'));
			
			$this->Last = new Player($this->Page, $Rows[0]);
			
			$this->Shops = mysql_num_rows(mysql_query('SELECT id FROM shops'));
			
			$this->Clans = mysql_num_rows(mysql_query('SELECT virtual FROM clans'));
			
			$this->Territories = mysql_num_rows(mysql_query('SELECT id FROM territories'));
		}
		
		public function GetMembers()
		{
			return $this->Members;
		}
		
		public function GetRegisters()
		{
			return $this->Registers;
		}
		
		public function GetConnections()
		{
			return $this->Connections;
		}
		
		public function GetPlaytions()
		{
			return $this->Playtions;
		}
		
		public function GetInGame()
		{
			return $this->InGame;
		}
		
		public function GetLast()
		{
			return $this->Last;
		}
		
		public function GetShops()
		{
			return $this->Shops;
		}
		
		public function GetClans()
		{
			return $this->Clans;
		}
		
		public function GetTerritories()
		{
			return $this->Territories;
		}
	}
?>