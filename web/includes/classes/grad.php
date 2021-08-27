<?php
	class Grad
	{
		private $Rows;
		
		public function __construct($Id)
		{
			$Rows = 'id,variable,color,canseemoney,webviewmails,canban,webaccessadmin,webnewsdelete,webnewsedit,webreportedit,webreportdelete,webchangebeta,webchangewhite,webalwayscomment,cangodclans';
			
			$Query = mysql_query("SELECT $Rows FROM grad WHERE id = $Id");
			
			if (!mysql_num_rows($Query))
			{
				return;
			}
			
			$Values = mysql_fetch_assoc($Query);
			
			foreach ($Values as $Key => $Value)
			{
				$this->Rows[strtolower($Key)] = $Value;
			}
		}
		
		public function Get($Row)
		{
			$Row = strtolower($Row);
			
			if (!isset($this->Rows[$Row]))
			{
				return false;
			}
			
			return $this->Rows[$Row];
		}
	}
?>