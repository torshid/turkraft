<?php
	class Retype
	{
		private $Page;
		
		private $Id;
		
		private $Variable;
		
		private $Text;
		
		public function __construct($Page, $Id)
		{
			$this->Page = $Page;
			
			$Query = mysql_query("SELECT * FROM teports WHERE id = $Id");
			
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
			
			$this->Text = $this->Page->GetLanguage()->GetText('teports.' . $this->Variable);
		}
		
		public function GetId()
		{
			return $this->Id;
		}
		
		public function GetVariable()
		{
			return $this->Variable;
		}
		
		public function GetText()
		{
			return $this->Text;
		}
	}
?>