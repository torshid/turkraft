<?php
	class Teports
	{
		private $Page;
		
		private $Elements;
		
		public function __construct($Page)
		{
			$this->Page = $Page;
			
			$this->Elements = array();
			
			$Query = mysql_query('SELECT id FROM teports');
			
			$this->Page->GetFunctions()->Load('retype');
			
			while ($Values = mysql_fetch_assoc($Query))
			{
				array_push($this->Elements, new Retype($this->Page, $Values['id']));
			}
		}
		
		public function GetElements()
		{
			return $this->Elements;
		}
		
		public function TypeExists($Id)
		{
			foreach ($this->Elements as $Element)
			{
				if ($Element->GetId() == $Id)
				{
					return true;
				}
			}
			
			return false;
		}
	}
?>