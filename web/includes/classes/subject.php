<?php
	class Subject
	{
		private $Page;
		
		private $Id;
		
		private $Category;
		
		private $Title;
		
		private $Poster;
		
		private $Posted;
		
		private $Deleted;
		
		private $Locked;
		
		private $Elements;
		
		private $Last;
		
		private $Views;
		
		private $Action;
		
		public function __construct($Page, $Id)
		{
			$this->Page = $Page;
			
			$Query = mysql_query("SELECT * FROM f_subjects WHERE id = $Id");
			
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
			
			$this->Title = htmlspecialchars(html_entity_decode($this->Title));
			
			$this->Elements = array();
			
			$Query = mysql_query("SELECT * FROM f_messages WHERE subject = $Id");
			
			if (!mysql_num_rows($Query))
			{
				return;
			}
			
			while ($Values = mysql_fetch_assoc($Query))
			{
				array_push($this->Elements, $Values['id']);
			}
			
			$Page->GetFunctions()->Load('message');
			
			$this->Last = new Message($this->Page, end($this->Elements));
		}
		
		public function LoadMessages()
		{
			$New = array();
			
			$this->Page->GetFunctions()->Load('message');
			
			foreach ($this->Elements as $Id)
			{
				array_push($New, new Message($this->Page, $Id));
			}
			
			$this->Elements = $New;
		}
		
		public function GetId()
		{
			return $this->Id;
		}
		
		public function GetTitle()
		{
			return $this->Title;
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
		
		public function GetLocked()
		{
			return $this->Locked;
		}
		
		public function GetLast()
		{
			return $this->Last;
		}
		
		public function GetViews()
		{
			return $this->Views;
		}
		
		public function GetElements()
		{
			return $this->Elements;
		}
		
		public function GetLink()
		{
			return 'thread.php?id=' . $this->Id;
		}
		
		public function PostReply($Poster, $Message)
		{
			$Message = addslashes(htmlentities($Message));
			
			if (mysql_query("INSERT INTO f_messages (posted, poster, subject, content) VALUES ('{$this->Page->GetFunctions()->GetConstants()->GetActualDate()}', {$Poster->GetId()}, '{$this->Id}', '$Message')"))
			{
				//array_push($this->Elements, mysql_insert_id());
				
				$this->SetAction($this->Page->GetFunctions()->GetConstants()->GetActualDate());
			}
		}
		
		public function IncrementViews()
		{
			mysql_query("UPDATE f_subjects SET views  = views + 1 WHERE id = {$this->Id}");
		}
		

		public function SetAction($Action)
		{
			$this->Action = $Action;
			
			mysql_query("UPDATE f_subjects SET action = '$Action' WHERE id = {$this->Id}");
		}
	}
?>