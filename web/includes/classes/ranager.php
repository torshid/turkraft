<?php
	class Ranager
	{
		private $Page;
		
		private $Elements;
		
		public function __construct($Page)
		{
			$this->Page = $Page;
			
			$this->Elements = array();
			
			$Query = mysql_query('SELECT id FROM reports ORDER BY posted DESC');
			
			if (!mysql_num_rows($Query))
			{
				return;
			}
			
			$this->Page->GetFunctions()->Load('report');
			
			while ($Values = mysql_fetch_assoc($Query))
			{
				array_push($this->Elements, new Report($this->Page, $Values['id']));
			}
		}
		
		public function GetElements()
		{
			return $this->Elements;
		}
		
		public function PostReport($Type, $Poster, $Content)
		{
			$Content = addslashes(htmlentities($Content));
			
			$this->Page->GetFunctions()->Load('report');
			
			if (mysql_query("INSERT INTO reports (category, content, posted, poster) VALUES ($Type, '$Content', '{$this->Page->GetFunctions()->GetConstants()->GetActualDate()}', {$Poster->GetId()})"))
			{
				array_push($this->Elements, new Report($this->Page, mysql_insert_id()));
			}
		}
	}
?>