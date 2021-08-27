<?php
	class Page
	{
		private $Functions;

		private $Title;

		private $Origin;

		private $Language;

		private $Player;

		private $Styles;

		private $Statistics;

		private $Bot;

		public function __construct($Title = 'Title', $Origin = '', $Styles = null)
		{
			require_once 'functions.php';

			date_default_timezone_set('Europe/London');

			$this->Functions = new Functions($this, true);

			$this->Title = $Title;

			$this->Origin = $Origin;

			if ($Styles == null)
			{
				$this->Styles = array('main');
			}
			else
			{
				$this->Styles = $Styles;
			}

			$this->Functions->Load('language');

			$this->Functions->Load('player');

			$this->Functions->Load('statistics');

			$Engines = array('google', 'yahoo');

			if (isset($_SERVER['HTTP_USER_AGENT']) && in_array(strtolower($_SERVER['HTTP_USER_AGENT']), $Engines))
			{
				$this->Bot = true;
			}
			else
			{
				$this->Bot = false;

				if (isset($_COOKIE['codeA']) && is_numeric($_COOKIE['codeA']) && isset($_COOKIE['codeB']) && strlen($_COOKIE['codeB']) == 32)
				{
					$Virtual = mysql_real_escape_string($_COOKIE['codeA']);

					$Password = mysql_real_escape_string($_COOKIE['codeB']);

					if ($Virtual == $_COOKIE['codeA'] && $Password == $_COOKIE['codeB'])
					{
						$Query = mysql_query("SELECT id FROM accounts WHERE virtual = $Virtual");

						if (mysql_num_rows($Query))
						{
							$Rows = mysql_fetch_row($Query);

							$Id = $Rows[0];

							$Me = new Player($this, $Id, $Password);

							if ($Me->GetId() != 0 && !$Me->GetBanned())
							{
								$this->Functions->LoginCookie($Me->GetVirtual(), $Me->GetPassword());

								$this->Player = $Me;

								$this->Player->UpdateActivity();
							}
						}
					}
				}
			}

			if ($this->IsConnected() && !isset($_GET['s']))
			{
				$Speak = $this->Player->GetLanguage();
			}
			else
			{
				if (isset($_GET['l']))
				{
					$Speak = $_GET['l'];
				}
				else
				{
					$Speak = $this->Functions->GetConstants()->GetDefaultLanguage();
				}
			}

			$this->Language = new Language($this, $Speak, true);

			setlocale(LC_TIME, 'UTF-8', $this->GetLanguage()->GetEnglish());

			if ($this->Language->GetId() == 0)
			{
				$this->Language = new Language($this, $this->Functions->GetConstants()->GetDefaultLanguage());
			}

			if ($this->Title != '' && $this->Title != 'Title')
			{
				$this->Title = $this->Language->GetText('titles.' . $this->Title);
			}

			$this->Statistics = new Statistics($this);

			$this->Statistics->Load();
		}

		public function Head($Title = '', $Load = false)
		{
			if ($Title == '')
			{
				$Title = $this->Title;
			}
			else
			{
				if ($Load)
				{
					$this->Title = utf8_decode($this->Language->GetText('titles.' . $Title));
				}
				else
				{
					$this->Title = $Title;
				}
			}
			?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="<?php echo $this->Language->GetAbbreviation(); ?>">
	<head>
		<title><?php echo $this->Title . ' - Turkraft'; ?></title>
		<meta name="keywords" content="<?php echo $this->Language->GetText('head.keywords'); ?>"/>
        <meta name="description" content="<?php echo $this->Language->GetText('head.description'); ?>"/>
        <meta name="robots" content="index,follow"/>
		<meta http-equiv="X-UA-Compatible" content="IE=8"/>
		<meta http-equiv="content-type" content="text/html; charset=utf-8"/>
		<link rel="icon" type="image/x-icon" href="<?php echo $this->Origin . 'includes/images/favicon.ico'; ?>"/>
		<?php
			foreach ($this->Styles as $Style)
			{
				echo '<link rel="stylesheet" media="screen" type="text/css" title="appearence" href="' . $this->Origin . 'includes/styles/' . $Style . '.css"/>';
			}
		?>
	</head>
	<body>
		<div id="toptop">
			<table><tr align="left"><td><?php echo ucfirst(utf8_encode(strftime("%A %d %B, %H:%M", strtotime($this->DecaleDate())))); ?></td>
			<td align="right">
			<?php
				$this->Functions->GetConstants()->LoadLanguages();

				$Count = 0;

				foreach ($this->Functions->GetConstants()->GetLanguages() as $Language)
				{
					$Count++;

					if ($Language->GetId() == $this->Language->GetId() || ($this->IsConnected() && $this->Player->GetLanguage() == $Language->GetId()))
					{
						echo '<a href="' . $this->Link('?l=' . $Language->GetAbbreviation(), false) . '">' . $Language->GetFull() . '</a>';
					}
					else
					{
						echo '<a href="' . $this->Link('?l=' . $Language->GetAbbreviation() . '&s=1', false) . '">' . $Language->GetFull() . '</a>';
					}

					if ($Count < count($this->Functions->GetConstants()->GetLanguages()))
					{
						echo ' - ';
					}
				}
			?>
			</td></tr></table>
		</div>
		<div id="total">
		<div id="head">
			<div id="menu">
				<?php
					if (!$this->IsConnected())
					{
						echo '<a href="' . $this->Link('connect.php') . '"><span class="element">' . $this->Language->GetText('head.visitor.connect') . '</span></a> | ';
						echo '<a href="' . $this->Link('register.php') . '"><span class="element">' . $this->Language->GetText('head.visitor.register') . '</span></a> | ';
					}
					else
					{
						if ($this->Player->GetGrad()->Get('webaccessadmin'))
						{
							echo '<a href="' . $this->Link('administration/') . '"><span class="element">' . $this->Language->GetText('head.member.administration') . '</span></a> | ';
						}

						echo '<a href="' . $this->Link('forum.php') . '"><span class="element">' . $this->Language->GetText('head.member.forum') . '</span></a> | ';

						//echo '<a href="' . $this->Link('messages.php') . '"><span class="element">' . $this->Language->GetText('head.member.messages') . '</span></a> | ';

						if ($this->Player->InClan())
						{
							echo '<a href="' . $this->Link($this->Player->GetClan()->GetLink()) . '"><span class="element">' . $this->Language->GetText('head.member.clan') . '</span></a> | ';
						}

						echo '<a href="' . $this->Link('reports.php') . '"><span class="element">' . $this->Language->GetText('head.member.reports') . '</span></a> | ';
						echo '<a href="' . $this->Link('members.php') . '"><span class="element">' . $this->Language->GetText('head.member.members') . '</span></a> | ';
					}

					echo '<a href="' . $this->Link('guide.php') . '"><span class="element">' . $this->Language->GetText('head.manuel') . '</span></a>';

					if ($this->IsConnected())
					{
						echo ' | <a href="' . $this->Link('disconnect.php') . '"><span class="element">' . $this->Language->GetText('head.member.disconnect') . '</span></a>';
					}
				?>
			</div>
			<?php
				if ($this->IsConnected())
				{
					echo '<div id="welcome"> ' . $this->Language->GetText('head.member.member', array('<b><a href="' . $this->Link($this->Player->GetLink()) . '">' . $this->Player->GetColored(). '</a></b>')) . '</div>';
				}
				else
				{
					echo '<div id="welcome"> ' . $this->Language->GetText('head.visitor.welcome') . '</div>';
				}
			?>
			<div id="turkraft">
				<a href="<?php echo $this->Link('./'); ?>">Turkraft</a>
				<span id="address" class="<?php if ($this->Functions->GetConstants()->GetVariables()->Get('online') == 1) {echo 'online';} else {echo 'offline';} ?>"><?php echo $this->Language->GetText('head.beta'); ?></span>
				<div id="description"><?php echo $this->Language->GetText('head.subtitle'); ?></div>
			</div>
		</div>
		<div id="content">
			<table style="width: 100%;">
				<tr>
					<td id="lpanel" valign="top">
					<?php
		}

		public function Foot()
		{
			?>
			</td>
					<td id="rpanel" valign="top">
						<div class="part">
							<div class="title">
								<?php echo $this->Language->GetText('menu.informations'); ?>
							</div>
							<div class="shows">
								<div class="subpart">
									<span class="subtitle">
										<?php echo $this->Language->GetText('menu.status'); ?>
									</span>
									:
									<span class="subshow">
										<?php
											if ($this->Functions->GetConstants()->GetVariables()->Get('online'))
											{
												echo $this->Language->GetText('menu.server.online');
											}
											else
											{
												echo $this->Language->GetText('menu.server.offline');
											}
										?>
									</span>
								</div>
								<div class="subpart">
									<span class="subtitle">
										<?php echo $this->Language->GetText('menu.whitelist'); ?>
									</span>
									:
									<span class="subshow">
										<?php
											if (!$this->Functions->GetConstants()->GetVariables()->Get('whitelist'))
											{
												echo $this->Language->GetText('menu.whitelist.false');
											}
											else
											{
												echo $this->Language->GetText('menu.whitelist.true');
											}
										?>
									</span>
								</div>
								<div class="subpart">
									<span class="subtitle<?php if ($this->Functions->IsTSOnline()) {echo ' online';} else {echo ' offline';} ?>">
										TS
									</span>
									:
									<span class="subshow">
										<a href="ts3server://www.turkraft.com">www.turkraft.com</a>
									</span>
								</div>
								<div class="subpart">
									<span class="subtitle">
										<a href="<?php echo $this->Link('ingame.php'); ?>"><?php echo $this->Language->GetText('menu.ingame'); ?></a>
									</span>
									:
									<span class="subshow">
										<?php echo $this->Statistics->GetInGame() . '/' . $this->Functions->GetConstants()->GetVariables()->Get('maximumPlayers'); ?>
									</span>
								</div>
							</div>
						</div>
						<br/>
						<div class="part">
							<div class="title">
								<?php echo $this->Language->GetText('menu.statistics'); ?>
							</div>
							<div class="shows">
								<div class="subpart">
									<span class="subtitle">
										<?php echo $this->Language->GetText('menu.members'); ?>
									</span>
									:
									<span class="subshow">
										<?php echo $this->Statistics->GetMembers(); ?>
									</span>
								</div>
								<div class="subpart">
									<span class="subtitle">
										<?php echo $this->Language->GetText('menu.register'); ?>
									</span>
									:
									<span class="subshow">
										<?php echo $this->Statistics->GetRegisters(); ?>
									</span>
								</div>
								<div class="subpart">
									<span class="subtitle">
										<?php echo $this->Language->GetText('menu.connection'); ?>
									</span>
									:
									<span class="subshow">
										<?php echo $this->Statistics->GetConnections(); ?>
									</span>
								</div>
								<div class="subpart">
									<span class="subtitle">
										<?php echo $this->Language->GetText('menu.ingametod'); ?>
									</span>
									:
									<span class="subshow">
										<?php echo $this->Statistics->GetPlaytions(); ?>
									</span>
								</div>
								<div class="subpart">
									<span class="subtitle">
										<?php echo $this->Language->GetText('menu.clans'); ?>
									</span>
									:
									<span class="subshow">
										<?php echo $this->Statistics->GetClans(); ?>
									</span>
								</div>
								<div class="subpart">
									<span class="subtitle">
										<?php echo $this->Language->GetText('menu.shops'); ?>
									</span>
									:
									<span class="subshow">
										<?php echo $this->Statistics->GetShops(); ?>
									</span>
								</div>
								<div class="subpart">
									<span class="subtitle">
										<?php echo $this->Language->GetText('menu.territories'); ?>
									</span>
									:
									<span class="subshow">
										<?php echo $this->Statistics->GetTerritories(); ?>
									</span>
								</div>
								<div class="subpart">
									<span class="subtitle">
										<?php echo $this->Language->GetText('menu.last'); ?>
									</span>
									:
									<span class="subshow">
										<?php echo '<a href="' . $this->Link($this->Statistics->GetLast()->GetLink()) . '">' . $this->Statistics->GetLast()->GetColored() . '</a>' ?>
									</span>
								</div>
							</div>
						</div>
						<br/>
						<div class="part">
							<div class="title">
								<?php echo $this->Language->GetText('menu.links'); ?>
							</div>
							<div class="shows">
								<div class="subpart">
									<a href="<?php echo $this->Link('download.php'); ?>">
										<span class="subtitle">
											<?php echo $this->Language->GetText('titles.download'); ?>
										</span>
									</a>
								</div>
								<div class="subpart">
									<a href="http://www.minecraft.net/">
										<span class="subtitle">
											Minecraft
										</span>
									</a>
								</div>
								<div class="subpart">
									<a href="mailto:khanolone@gmail.com">
										<span class="subtitle">
											<?php echo $this->Language->GetText('menu.links.contact'); ?>
										</span>
									</a>
								</div>
							</div>
						</div>
					</td>
				</tr>
			</table>
		</div>
		<div id="foot">
			<center>
				<?php echo $this->Language->GetText('foot.text'); ?>
			</center>
		</div>
		</div>
	</body>
</html>
		<?php
			$this->Functions->Disconnect();
		}

		public function GetFunctions()
		{
			return $this->Functions;
		}

		public function GetPlayer()
		{
			return $this->Player;
		}

		public function IsConnected()
		{
			return isset($this->Player);
		}

		public function GetStyles()
		{
			return $this->Styles;
		}

		public function GetLanguage()
		{
			return $this->Language;
		}

		public function Link($Page, $ShowLanguage = true)
		{
			$Struct = $this->Origin . $Page;

			if (strstr($Page, '?') == '')
			{
				$Struct .= '?';
			}
			else
			{
				$Struct .= '&';
			}

			$More = false;

			foreach ($_GET as $Key => $Value)
			{
				if (strstr($Struct, $Key . '=') == '')
				{
					$More = true;

					$Struct .= $Key . '=' . $Value . '&';
				}
			}

			if ($More)
			{
				$Struct = substr($Struct, 0, strlen($Struct) -1);
			}

			if ($ShowLanguage)
			{
				if (!isset($_GET['s']))
				{
					$More .= '&l=' . $this->Language->GetAbbreviation();
				}
				else
				{
					$More .= '&l=' . $this->Language->GetAbbreviation() . '&s=1';
				}
			}

			if ($Struct == $this->Origin . $Page . '?' || $Struct == $this->Origin . $Page . '&')
			{
				$Struct = $Page;
			}

			return $this->Origin . $Struct;
		}

		public function IsBot()
		{
			return $this->Bot;
		}

		public function DecaleDate($Date = '')
		{
			if ($Date == '')
			{
				$Date = $this->Functions->GetConstants()->GetActualDate();
			}

			if ($this->IsConnected())
			{
				return $this->Player->GetCountry()->DecaleDate($Date);
			}

			return $Date;
		}
	}
?>
