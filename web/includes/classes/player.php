<?php
	class Player
	{
		private $Page;
		
		private $Id;
		
		private $Name;
		
		private $Password;
		
		private $Register;
		
		private $Connection;
		
		private $Playtion;
		
		private $Exition;
		
		private $Playmin;
		
		private $Virtual;
		
		private $Language;
		
		private $Country;
		
		private $Grad;
		
		private $Beta;
		
		private $White;
		
		private $Banned;
		
		private $Playing;
		
		private $Session;
		
		private $Mail;
		
		private $Misible;
		
		private $Money;
		
		private $Clan;
		
		private $Mill;
		
		private $Hill;
		
		private $Mie;
		
		private $Hie;
		
		private $Nie;
		
		function __construct($Page, $Name, $Password = null)
		{
			$this->Page = $Page;
			
			if (!isset($Password))
			{
				//$Rows = 'id,name,register,connection,playtion,playmin,grad,beta,mail,misible,clan,mill,hill,mie,hie,nie';
				
				$Rows = '*';
				
				if (!is_numeric($Name) && preg_match($this->Page->GetFunctions()->GetConstants()->GetTextRegex(), $Name))
				{
					$Query = mysql_query("SELECT $Rows FROM accounts WHERE name = '$Name'");
				}
				else 
				{
					$Query = mysql_query("SELECT $Rows FROM accounts WHERE id = $Name");
				}
				
				if (!isset($Query) || !mysql_num_rows($Query))
				{
					return;
				}
				
				$this->Parse($Query);
				
				return;
			}
			
			if (!is_numeric($Name) && preg_match($Page->GetFunctions()->GetConstants()->GetNameRegex(), $Name))
			{
				$Query = mysql_query("SELECT * FROM accounts WHERE name = '$Name' AND password = '$Password'");
			}
			else
			{
				$Query = mysql_query("SELECT * FROM accounts WHERE id = $Name AND password = '$Password'");
			}
			
			if (!isset($Query) || !mysql_num_rows($Query))
			{
				return;
			}
			
			$this->Parse($Query);
			
			return;
		}
		
		public function Parse($Query)
		{
			$Values = mysql_fetch_assoc($Query);
			
			foreach ($Values as $Key => $Value)
			{
				$Key = ucfirst($Key);
				
				$this->$Key = $Value;
			}
			
			$this->Page->GetFunctions()->Load('grad');
			
			$this->Grad = new Grad($this->Grad);
			
			$this->Page->GetFunctions()->Load('clan');
			
			$this->Clan = new Clan($this->Page, $this->Clan);
			
			$this->Page->GetFunctions()->Load('country');
			
			$this->Country = new Country($this->Page, $this->Country);
		}
		
		public function GetId()
		{
			return $this->Id;
		}
		
		public function GetName()
		{
			return $this->Name;
		}
		
		public function GetPassword()
		{
			return $this->Password;
		}
		
		public function GetRegister()
		{
			return $this->Register;
		}

		public function GetConnection()
		{
			return $this->Connection;
		}
		
		public function GetPlaytion()
		{
			return $this->Playtion;
		}
		
		public function GetExition()
		{
			return $this->Exition;
		}
		
		public function GetPlaymin()
		{
			return $this->Playmin;
		}

		public function GetVirtual()
		{
			return $this->Virtual;
		}

		public function GetLanguage()
		{
			return $this->Language;
		}

		public function GetCountry()
		{
			return $this->Country;
		}

		public function GetGrad()
		{
			return $this->Grad;
		}

		public function GetBeta()
		{
			return $this->Beta;
		}

		public function GetWhite()
		{
			return $this->White;
		}

		public function GetBanned()
		{
			return $this->Banned;
		}

		public function GetPlaying()
		{
			return $this->Playing;
		}

		public function GetSession()
		{
			return $this->Session;
		}

		public function GetMail()
		{
			return $this->Mail;
		}

		public function GetMisible()
		{
			return $this->Misible;
		}

		public function GetMoney()
		{
			return $this->Money;
		}

		public function GetClan()
		{
			return $this->Clan;
		}

		public function GetMill()
		{
			return $this->Mill;
		}

		public function GetHill()
		{
			return $this->Hill;
		}

		public function GetMie()
		{
			return $this->Mie;
		}

		public function GetHie()
		{
			return $this->Hie;
		}

		public function GetNie()
		{
			return $this->Nie;
		}
		
		public function SetPassword($Password)
		{
			$this->Password = $Password;
			
			mysql_query("UPDATE accounts SET password = '$Password' WHERE id = {$this->Id}");
		}

		public function SetConnection($Connection)
		{
			$this->Connection = $Connection;
			
			mysql_query("UPDATE accounts SET connection = '$Connection' WHERE id = {$this->Id}");
		}

		public function SetLanguage($Language)
		{
			$this->Language = $Language;
			
			mysql_query("UPDATE accounts SET language = $Language WHERE id = {$this->Id}");
		}

		public function SetCountry($Country)
		{
			$this->Page->GetFunctions()->Load('country');
			
			$this->Country = new Country($this->Page, $Country);
			
			mysql_query("UPDATE accounts SET country = $Country WHERE id = {$this->Id}");
		}

		public function SetGrad($Grad)
		{
			$this->Grad = $Grad;
			
			mysql_query("UPDATE accounts SET grad = $Grad WHERE id = {$this->Id}");
		}

		public function SetBeta($Beta)
		{
			$this->Beta = $Beta;
			
			mysql_query("UPDATE accounts SET beta = $Beta WHERE id = {$this->Id}");
		}

		public function SetWhite($White)
		{
			$this->White = $White;
			
			mysql_query("UPDATE accounts SET white = $White WHERE id = {$this->Id}");
		}

		public function SetBanned($Banned)
		{
			$this->Banned = $Banned;
			
			mysql_query("UPDATE accounts SET banned = $Banned WHERE id = {$this->Id}");
		}

		public function SetSession($Session)
		{
			$this->Session = $Session;
			
			mysql_query("UPDATE accounts SET session = $Session WHERE id = {$this->Id}");
		}

		public function SetMail($Mail)
		{
			$this->Mail = $Mail;
			
			mysql_query("UPDATE accounts SET mail = '$Mail' WHERE id = {$this->Id}");
		}

		public function SetMisible($Misible)
		{
			$this->Misible = $Misible;
			
			mysql_query("UPDATE accounts SET misible = $Misible WHERE id = {$this->Id}");
		}
		
		public function UpdateActivity()
		{
			$this->SetConnection($this->Page->GetFunctions()->GetConstants()->GetActualDate());
		}
		
		public function GetColored($Text = '')
		{
			if ($Text == '')
			{
				$Text = $this->Name;
			}
			
			return '<font color="' . $this->Grad->Get('color') . '">' . $Text . '</font>';
		}
		
		public function GetLink()
		{
			return 'profile.php?id=' . $this->Id;
		}
		
		public function InClan()
		{
			return $this->Clan->GetVirtual() != 0;
		}
	}
?>