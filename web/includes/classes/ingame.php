<?php
	class Ingame
	{
		private $Page;
		
		private $Elements;
		
		public function __construct($Page)
		{
			$this->Page = $Page;
			
			$this->Elements = array();
			
			$Query = mysql_query('SELECT id FROM accounts WHERE playing = 1 ORDER BY playtion');
			
			if (!mysql_num_rows($Query))
			{
				return;
			}
			
			$this->Page->GetFunctions()->Load('player');
			
			while ($Values = mysql_fetch_assoc($Query))
			{
				array_push($this->Elements, new Player($this->Page, $Values['id']));
			}
		}
		
		public function GetElements()
		{
			return $this->Elements;
		}
	}
?>