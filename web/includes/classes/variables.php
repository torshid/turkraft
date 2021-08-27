<?php
	class Variables
	{
		private $Rows;
		
		public function __construct()
		{
			$Query = mysql_query("SELECT * FROM variables");
			
			$this->Rows = array();
			
			if (mysql_num_rows($Query))
			{
				while ($Values = mysql_fetch_assoc($Query))
				{
					$this->Rows[strtolower($Values['name'])] = $Values['value'];
				}
			}
			
			$Query = mysql_query("SELECT variable,value FROM configuration");
			
			if (mysql_num_rows($Query))
			{
				while ($Values = mysql_fetch_assoc($Query))
				{
					$this->Rows[strtolower($Values['variable'])] = $Values['value'];
				}
			}
		}
		
		public function Get($Row)
		{
			$Row = strtolower($Row);
			
			if (!isset($this->Rows[$Row]))
			{
				return $Row;
			}
			
			return $this->Rows[$Row];
		}
	}
?>