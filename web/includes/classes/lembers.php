<?php
	class Lembers
	{
		private $Page;
		
		private $Number;
		
		private $Count;
		
		private $Players;
		
		private $Total;
		
		private $MemberPerPage = 20;
		
		public function __construct($Page, $Number)
		{
			$this->Page = $Page;
			
			$this->Number = $Number;
			
			$Query = mysql_query ("SELECT COUNT(*) AS total FROM accounts"/* WHERE language = {$Page->GetLanguage()->GetId()}"*/);
			
			$Result = mysql_fetch_array($Query);
			
			$this->Count = $Result['total'];
			
			$this->Total = ceil($this->Count / $this->MemberPerPage);
			
			if ($this->Number > $this->Total)
			{
				$this->Number = $this->Total;
			}
			else if ($this->Number <= 0)
			{
				$this->Number = 1;
			}
			
			$this->Players = array();
			
			$BeginEntry = ($this->Number - 1) * $this->MemberPerPage;
			
			$Result = mysql_query("SELECT id FROM accounts"/* WHERE language = {$Page->GetLanguage()->GetId()} */ . " ORDER BY name LIMIT $BeginEntry, {$this->MemberPerPage}");
			
			while ($Values = mysql_fetch_assoc($Result))
			{
				array_push($this->Players, $Values['id']);
			}
		}
		
		public function LoadPlayers()
		{
			$NewPlayers = array();
			
			$this->Page->GetFunctions()->Load('player');
			
			foreach ($this->Players as $Player)
			{
				array_push($NewPlayers, new Player($this->Page, $Player));
			}
			
			$this->Players = $NewPlayers;
		}
		
		public function GetPlayers()
		{
			return $this->Players;
		}
		
		public function GetCount()
		{
			return $this->Count;
		}
		
		public function GetTotal()
		{
			return $this->Total;
		}
		
		public function GetNumber()
		{
			return $this->Number;
		}
	}
?>