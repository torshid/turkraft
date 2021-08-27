<?php
	class Comment
	{
		private $Page;
		
		private $Id;
		
		private $Posted;
		
		private $Poster;
		
		private $News;
		
		private $Content;
		
		private $Deleted;
		
		private $Edited;
		
		private $Editor;
		
		public function __construct($Page, $Id)
		{
			$this->Page = $Page;
			
			$Query = mysql_query("SELECT * FROM comments WHERE id = $Id");
			
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
			
			$this->Page->GetFunctions()->Load('player');
			
			$this->Poster = new Player($this->Page, $this->Poster);
			
		}
		
		public function GetId()
		{
			return $this->Id;
		}
		
		public function GetPosted()
		{
			return $this->Posted;
		}
		
		public function GetPoster()
		{
			return $this->Poster;
		}
		
		public function GetNews()
		{
			return $this->News;
		}
		
		public function GetContent()
		{
			return $this->Content;
		}
		
		public function GetDeleted()
		{
			return $this->Deleted;
		}
		
		public function GetEdited()
		{
			return $this->Edited;
		}
		
		public function GetEditor()
		{
			return $this->Editor;
		}
		
		public function SetDeleted($Deleted)
		{
			$this->Deleted = $Deleted;
			
			mysql_query("UPDATE comments SET deleted = $Deleted WHERE id = {$this->Id}");
		}
		
		public function MakeEdition($Editor, $Content, $Edited)
		{
			$this->Editor = $Editor;
			
			$this->Content = $Content;
			
			$this->Edited = $Edited;
			
			mysql_query("UPDATE comments SET editor = $Editor, content = '$Content', edited = '$Edited' WHERE id = {$this->Id}");
		}
	}
?>