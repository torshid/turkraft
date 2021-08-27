<?php
	class Report
	{
		private $Page;
		
		private $Id;
		
		private $Category;
		
		private $Content;
		
		private $Status;
		
		private $Posted;
		
		private $Poster;
		
		private $Deleted;
		
		public function __construct($Page, $Id)
		{
			$this->Page = $Page;
			
			$Query = mysql_query("SELECT * FROM reports WHERE id = $Id");
			
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
			
			$this->Content = htmlspecialchars(html_entity_decode($this->Content, 10));
		}
		
		public function Load($Teports)
		{
			$this->Page->GetFunctions()->Load('player');
		
			$this->Poster = new Player($this->Page, $this->Poster);
			
			foreach ($Teports->GetElements() as $Element)
			{
				if ($Element->GetId() == $this->Category)
				{
					$this->Category = $Element;
					
					break;
				}
			}
		}
		
		public function GetId()
		{
			return $this->Id;
		}
		
		public function GetCategory()
		{
			return $this->Category;
		}
		
		public function GetContent()
		{
			return $this->Content;
		}
		
		public function GetStatus()
		{
			return $this->Status;
		}
		
		public function GetPosted()
		{
			return $this->Posted;
		}
		
		public function GetPoster()
		{
			return $this->Poster;
		}
		
		public function GetDeleted()
		{
			return $this->Deleted;
		}
		
		public function SetStatus($Status)
		{
			$this->Status = $Status;
			
			mysql_query("UPDATE reports SET status = $Status WHERE id = {$this->Id}");
		}
		
		public function SetDeleted($Deleted)
		{
			$this->Deleted = $Deleted;
			
			mysql_query("UPDATE reports SET deleted = $Deleted WHERE id = {$this->Id}");
		}
	}
?>