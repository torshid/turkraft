<?php
	class Nanager
	{
		private $NewsPerPage = 10;
		
		private $Page;
		
		private $Number;
		
		private $News;
		
		private $Pages;
		
		private $Total;
		
		public function __construct($Page, $Number)
		{
			$this->Page = $Page;
			
			$this->Number = $Number;
			
			$this->News = array();
		
			$Query = mysql_query ("SELECT COUNT(*) AS total FROM news WHERE lang = {$this->Page->GetLanguage()->GetId()}");
			
			$Result = mysql_fetch_array($Query) ;
			
			$this->Total = $Result['total'] ;
			
			if ($this->Total <= 0)
			{
				return;
			}
			
			$this->Pages = ceil($this->Total / $this->NewsPerPage);
			
			if ($this->Number > $this->Pages)
			{
				$this->Number = $this->Pages;
			}
			else if ($this->Number <= 0)
			{
				$this->Number = 1;
			}
			
			$BeginEntry = ($this->Number - 1) * $this->NewsPerPage;
			
			$Query = mysql_query("SELECT id FROM news WHERE lang = {$this->Page->GetLanguage()->GetId()} ORDER BY id DESC LIMIT $BeginEntry, {$this->NewsPerPage}");
			
			while ($Values = mysql_fetch_assoc($Query))
			{
				array_push($this->News, $Values['id']);
			}
		}
		
		public function GetNumber()
		{
			return $this->Number;
		}
		
		public function GetNews()
		{
			return $this->News;
		}
		
		public function GetPages()
		{
			return $this->Pages;
		}
		
		public function GetTotal()
		{
			return $this->Total;
		}
	}
?>