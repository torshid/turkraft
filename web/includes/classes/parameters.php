<?php
	class Parameters
	{
		private $Page;
		
		private $Database;
		private $Login;
		private $Password;
		
		private $MaxNameSize;
		private $MinNameSize;
		
		private $MaxPasswordSize;
		private $MinPasswordSize;
		
		private $MaxMailSize;
		private $MinMailSize;
		
		private $MaxCommentSize;
		private $MinCommentSize;
		
		private $MaxReplySize;
		private $MinReplySize;
		
		private $MaxSubjectSize;
		private $MinSubjectSize;
		
		private $MaxReportSize;
		private $MinReportSize;
		
		private $NameRegex;
		
		private $MailRegex;
		
		private $TextRegex;
		
		private $Launcher;
		
		private $DefaultLanguage;
		
		private $DateFormat;
		
		private $EmptyDate;
		
		private $ActualDate;
		
		private $Variables;
		
		private $Languages;
		
		private $Countries;
		
		private $NewsLength;
		
		private $BBCode;
		
		private $LauncherVersion;
		
		private $ServerPort;
		
		private $GameVersion;
		
		public function __construct($Page)
		{
			$this->Page = $Page;
		
			$this->Database = 'database';
			$this->Login = 'login';
			$this->Password = 'password';
			
			$this->MaxNameSize = 16;
			$this->MinNameSize = 3;
			
			$this->MaxPasswordSize = 32;
			$this->MinPasswordSize = 4;
			
			$this->MaxMailSize = 64;
			$this->MinMailSize = 10;
			
			$this->MaxCommentSize = 512;
			$this->MinCommentSize = 6;
			
			$this->MaxReplySize = 1024;
			$this->MinReplySize = 8;
			
			$this->MaxSubjectSize = 64;
			$this->MinSubjectSize = 6;
			
			$this->MaxReportSize = 256;
			$this->MinReportSize = 16;
			
			$this->NameRegex = '/^[a-zA-Z0-9]+$/';
			
			$this->MailRegex = "#^[a-z0-9._-]+@[a-z0-9._-]{2,}\.[a-z]{2,4}$#";
			
			$this->TextRegex = '/^[a-zA-Z]+$/';
			
			$this->Launcher = 13;
			
			$this->DefaultLanguage = 'en';
			
			$this->DateFormat = 'Y/m/d H:i:s';
			
			$this->EmptyDate = '0000-00-00 00:00:00';
			
			$this->ActualDate = date($this->DateFormat);
			
			$this->NewsLength = 512;
			
			$this->BBCode = array(
				'#\[b\](.*?)\[/b\]#si',
				'#\[i\](.*?)\[/i\]#si',
				'#\[u\](.*?)\[/u\]#si',
				'#\[s\](.*?)\[/s\]#si',
				'#\[center\](.*?)\[/center\]#si',
				'#\[color=(.*?)\](.*?)\[/color\]#si',
				'#\[bl\](.*?)\[/bl\]#si',
				'#\[marquee\](.*?)\[/marquee\]#si',
				'#\[code=([a-zA-Z0-9\#\+]+\s?)\](.*?)\[/code\]#si',
				'#\[code\](.*?)\[/code]#si',
				'#\[quote=([a-zA-Z0-9]+\s?)\](.*?)\[/quote\]#si',
				'#\[quote\](.*?)\[/quote\]#si',
				'#\[img\](.*?)\[/img\]#si',
				'#\[email\](.*?)\[/email\]#si',
				'#\[show\](.*?)\[/show\]#si',
				'#\[title\](.*?)\[/title\]#si',
			);
			
			$this->LauncherVersion = 13;
			
			$this->ServerPort = 1453;
			
			$this->GameVersion = '0.3.5';
		}
		
		public function GetDatabase()
		{
			return $this->Database;
		}
		
		public function GetLogin()
		{
			return $this->Login;
		}
		
		public function GetPassword()
		{
			return $this->Password;
		}
		
		public function GetMaxNameSize()
		{
			return $this->MaxNameSize;
		}
		
		public function GetMinNameSize()
		{
			return $this->MinNameSize;
		}
		
		public function GetMaxPasswordSize()
		{
			return $this->MaxPasswordSize;
		}
		
		public function GetMinPasswordSize()
		{
			return $this->MinPasswordSize;
		}
		
		public function GetMaxMailSize()
		{
			return $this->MaxMailSize;
		}
		
		public function GetMinMailSize()
		{
			return $this->MinMailSize;
		}
		
		public function GetMaxCommentSize()
		{
			return $this->MaxCommentSize;
		}
		
		public function GetMinCommentSize()
		{
			return $this->MinCommentSize;
		}
		
		public function GetMaxReplySize()
		{
			return $this->MaxReplySize;
		}
		
		public function GetMinReplySize()
		{
			return $this->MinReplySize;
		}
		
		public function GetMaxSubjectSize()
		{
			return $this->MaxSubjectSize;
		}
		
		public function GetMinSubjectSize()
		{
			return $this->MinSubjectSize;
		}
		
		public function GetMaxReportSize()
		{
			return $this->MaxReportSize;
		}
		
		public function GetMinReportSize()
		{
			return $this->MinReportSize;
		}
		
		public function GetNameRegex()
		{
			return $this->NameRegex;
		}
		
		public function GetMailRegex()
		{
			return $this->MailRegex;
		}
		
		public function GetTextRegex()
		{
			return $this->TextRegex;
		}
		
		public function GetLauncher()
		{
			return $this->Launcher;
		}
		
		public function GetDefaultLanguage()
		{
			return $this->DefaultLanguage;
		}
		
		public function GetActualDate($Player = null)
		{
			if (!isset($Player))
			{
				return $this->ActualDate;
			}
			
			return $Player->GetCountry()->DecaleDate($this->DateFormat);
		}
		
		public function GetDateFormat()
		{
			return $this->DateFormat;
		}
		
		public function GetEmptyDate()
		{
			return $this->EmptyDate;
		}
		
		public function LoadVariables()
		{
			$this->Variables = new Variables();
		}
		
		public function LoadLanguages()
		{
			if (isset($this->Languages))
			{
				return;
			}
			
			$this->Languages = array();
			
			$this->Page->GetFunctions()->Load('language');
			
			$Query = mysql_query('SELECT id FROM lang');
			
			while ($Values = mysql_fetch_assoc($Query))
			{
				array_push($this->Languages, new Language($this->Page, $Values['id']));
			}
		}
		
		public function LanguageExists($Abbreviation)
		{
			if (!isset($this->Languages))
			{
				return false;
			}
			
			if (is_numeric($Abbreviation))
			{
				foreach ($this->Languages as $Language)
				{
					if ($Language->GetId() == $Abbreviation)
					{
						return true;
					}
				}
				
				return false;
			}
			
			foreach ($this->Languages as $Language)
			{
				if ($Language->GetAbbreviation() == $Abbreviation)
				{
					return true;
				}
			}
			
			return false;
		}
		
		public function GetLanguages()
		{
			return $this->Languages;
		}
		
		public function LoadCountries()
		{
			if (isset($this->Countries))
			{
				return;
			}
			
			$this->Countries = array();
			
			$this->Page->GetFunctions()->Load('country');
			
			$Query = mysql_query('SELECT id FROM country');
			
			while ($Values = mysql_fetch_assoc($Query))
			{
				array_push($this->Countries, new Country($this->Page, $Values['id']));
			}
		}
		public function CountryExists($Id)
		{
			if (!isset($this->Countries))
			{
				return false;
			}
			
			foreach ($this->Countries as $Country)
			{
				if ($Country->GetId() == $Id)
				{
					return true;
				}
			}
			
			return false;
		}
		
		public function GetCountries()
		{
			return $this->Countries;
		}
		
		public function GetNewsLength()
		{
			return $this->NewsLength;
		}
		
		public function GetBBCode()
		{
			return $this->BBCode;
		}
		
		public function GetLauncherVersion()
		{
			return $this->LauncherVersion;
		}
		
		public function GetVariables()
		{
			return $this->Variables;
		}
		
		public function GetServerPort()
		{
			return $this->ServerPort;
		}
		
		public function GetGameVersion()
		{
			return $this->GameVersion;
		}
	}
?>