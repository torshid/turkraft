<?php
	class News
	{
		private $Page;
		
		private $Id;
		
		private $Posted;
		
		private $Poster;
		
		private $Title;
		
		private $Lang;
		
		private $Content;
		
		private $Deleted;
		
		private $Comments;
		
		public function __construct($Page, $Id, $Clean = 0)
		{
			$this->Page = $Page;
			
			if (is_numeric($Id))
			{
				$Query = mysql_query("SELECT * FROM news WHERE id = $Id");
			}
			
			if (!isset($Query) || !mysql_num_rows($Query))
			{
				return;
			}
			
			$Values = mysql_fetch_assoc($Query);
			
			foreach ($Values as $Key => $Value)
			{
				$Key = ucfirst($Key);
				
				$this->$Key = $Value;
			}
			
			$this->Title = htmlspecialchars(html_entity_decode($this->Title));
			
			$this->Content = htmlspecialchars(html_entity_decode($this->Content));
			
			if ($Clean == 1)
			{
				$this->Content = $this->Page->GetFunctions()->SetLink($this->Content);
				
				$this->Content = $this->Page->GetFunctions()->SetBBCode($this->Content);
				//$this->Content = $this->Page->GetFunctions()->RemoveBBCode($this->Content);
			}
			else if ($Clean == 2)
			{
				$this->Content = $this->Page->GetFunctions()->SetLink($this->Content);
				
				$this->Content = nl2br($this->Page->GetFunctions()->SetBBCode($this->Content));
			}
			
			$this->Comments = array();
			
			$Query = mysql_query("SELECT * FROM comments WHERE news = {$this->Id}");
			
			if (!mysql_num_rows($Query))
			{
				return;
			}
			
			while ($Values = mysql_fetch_assoc($Query))
			{
				array_push($this->Comments, $Values['id']);
			}
		}
		
		public function LoadComments()
		{
			$NewComments = array();
			
			$this->Page->GetFunctions()->Load('comment');
			
			foreach ($this->Comments as $Comment)
			{
				array_push($NewComments, new Comment($this->Page, $Comment));
			}
			
			$this->Comments = $NewComments;
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
	
		public function GetTitle()
		{
			return $this->Title;
		}
	
		public function GetLang()
		{
			return $this->Lang;
		}
	
		public function GetContent()
		{
			return $this->Content;
		}
	
		public function GetDeleted()
		{
			return $this->Deleted;
		}
	
		public function GetComments()
		{
			return $this->Comments;
		}
		
		public function SetTitle($Title)
		{
			$this->Title = $Title;
			
			mysql_query("UPDATE news SET title = '$Title' WHERE id = {$this->Id}");
		}
		
		public function SetContent($Content)
		{
			$this->Content = $Content;
			
			mysql_query("UPDATE news SET content = '$Content' WHERE id = {$this->Id}");
		}
		
		public function SetLang($Lang)
		{
			$this->Lang = $Lang;
			
			mysql_query("UPDATE news SET lang = $Lang WHERE id = {$this->Id}");
		}
		
		public function SetDeleted($Deleted)
		{
			$this->Deleted = $Deleted;
			
			mysql_query("UPDATE news SET deleted = $Deleted WHERE id = {$this->Id}");
		}
		
		public function MakeEdition($Title, $Content, $Lang)
		{
			$this->Title = $Title;
			
			$this->Content = $Content;
			
			$this->Lang = $Lang;
			
			mysql_query("UPDATE news SET title = '$Title', content = '$Content', lang = $Lang WHERE id = {$this->Id}");
		}
		
		public function GetLink()
		{
			return 'news.php?id=' . $this->Id;
		}
		
		public function IncrementViews()
		{
			mysql_query("UPDATE news SET views  = views + 1 WHERE id = {$this->Id}");
		}
		
		public function PostComment($Poster, $Message)
		{
			$Message = addslashes(htmlentities($Message));
			
			if (mysql_query("INSERT INTO comments (posted, poster, news, content) VALUES ('{$this->Page->GetFunctions()->GetConstants()->GetActualDate()}', {$Poster->GetId()}, '{$this->Id}', '$Message')"))
			{
				array_push($this->Comments, mysql_insert_id());
			}
		}
	}
?>