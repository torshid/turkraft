<?php
	class Message
	{
		private $Page;
		
		private $Id;
		
		private $Poster;
		
		private $Posted;
		
		private $Deleted;
		
		private $Editime;
		
		private $Edieditor;
		
		private $Content;
		
		public function __construct($Page, $Id)
		{
			$this->Page = $Page;
			
			$Query = mysql_query("SELECT * FROM f_messages WHERE id = $Id");
			
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
			
			$this->Content = nl2br($this->Page->GetFunctions()->TransformLink(htmlspecialchars(html_entity_decode($this->Page->GetFunctions()->LimitRepetition($this->Content, 10)))));
		}
		
		public function GetId()
		{
			return $this->Id;
		}
		
		public function GetPoster()
		{
			return $this->Poster;
		}
		
		public function GetPosted()
		{
			return $this->Posted;
		}
		
		public function GetDeleted()
		{
			return $this->Deleted;
		}
		
		public function GetEditime()
		{
			return $this->Editime;
		}
		
		public function GetEdieditor()
		{
			return $this->Edieditor;
		}
		
		public function GetContent()
		{
			return $this->Content;
		}
	}
?>