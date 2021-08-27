<?php
	class Country
	{
		private $Page;
		
		private $Id;
		
		private $Name;
		
		private $Detail;
		
		private $Decalage;
		
		public function __construct($Page, $Abbreviation)
		{
			$this->Page = $Page;
			
			if (is_numeric($Abbreviation))
			{
				$Result = mysql_query("SELECT * FROM country WHERE id = $Abbreviation");
			}
			else
			{
				$Result = mysql_query("SELECT * FROM country WHERE name = '$Abbreviation'");
			}
		
			if (!mysql_num_rows($Result))
			{
				return;
			}
			
			$Row = mysql_fetch_row($Result);
			
			$this->Id = $Row[0];
			
			$this->Name = utf8_encode($Row[1]);
		
			$this->Detail = $Row[2];
		
			$this->Decalage = $Row[3];
		}
		
		public function GetId()
		{
			return $this->Id;
		}
		
		public function GetName()
		{
			return $this->Name;
		}
		
		public function GetDetail()
		{
			return $this->Detail;
		}
		
		public function GetDecalage()
		{
			return $this->Decalage;
		}
		
		public function DecaleDate($Date = '')
		{
			if ($Date == '')
			{
				$Date = $this->Page->GetFunctions()->GetConstants()->GetActualDate();
			}
			
			return date($this->Page->GetFunctions()->GetConstants()->GetDateFormat(), strtotime($Date) + 3600 * $this->Decalage);
		}
	}
?>