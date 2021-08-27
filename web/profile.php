<?php
	require_once 'includes/page.php';
	
	$Page = new Page();
	
	$Page->GetFunctions()->Load('player');
	
	$Target = null;
	
	$Id = 0;
	
	if (isset($_GET['id']))
	{
		if (is_numeric($_GET['id']))
		{
			$Id = $_GET['id'];
			
			if ($Page->IsConnected() && $Page->GetPlayer()->GetId() == $Id)
			{
				$Target = $Page->GetPlayer();
			}
			else
			{
				$Target = new Player($Page, $Id);
				
				if ($Target->GetId() == 0)
				{
					$Target = null;
				}
			}
		}
		else
		{
			if ($Page->IsConnected())
			{
				$Target = $Page->GetPlayer();
			}
		}
	}
	else
	{
		if ($Page->IsConnected())
		{
			$Target = $Page->GetPlayer();
			
			$Id = $Target->GetId();
		}
	}
	
	$Changes = array();
	
	$Errors = array();
	
	if (!isset($Target) || $Target == null || (isset($Id) && $Id == 0))
	{
		$Page->Head('profile.inexistant', true);
		
		array_push($Errors, $Page->GetLanguage()->GetText('pages.profile.represent.inexistant.text'));
	}
	else if ($Page->IsConnected() && $Page->GetPlayer()->GetId() == $Id)
	{
		$Page->GetFunctions()->GetConstants()->LoadLanguages();
		
		$Page->GetFunctions()->GetConstants()->LoadCountries();
		
		if (isset($_POST['misible']) && is_numeric($_POST['misible']) && ($_POST['misible'] == 0 || $_POST['misible'] == 1))
		{
			if ($Target->GetMisible() != $_POST['misible'])
			{
				$Target->SetMisible($_POST['misible']);
				
				array_push($Changes, $Page->GetLanguage()->GetText('pages.profile.updated.misible'));
			}
		}
		
		if (isset($_POST['old']) && isset($_POST['new']) && isset($_POST['renew']))
		{
			$Old = $_POST['old'];
			
			$New = $_POST['new'];
			
			$Renew = $_POST['renew'];
			
			if ($Old != '' && $New != '' && $Renew != '')
			{
				$Old = md5($Old);
				
				if ($Target->GetPassword() != $Old)
				{
					array_push($Errors, $Page->GetLanguage()->GetText('pages.profile.error.passnotsame'));
				}
				else
				{
					if (!$Page->GetFunctions()->CheckSize($New, $Page->GetFunctions()->GetConstants()->GetMinPasswordSize(), $Page->GetFunctions()->GetConstants()->GetMaxPasswordSize()))
					{
						array_push($Errors, $Page->GetLanguage()->GetText('pages.register.wrong.passnu', array($Page->GetFunctions()->GetConstants()->GetMinPasswordSize(), $Page->GetFunctions()->GetConstants()->GetMaxPasswordSize())));
					}
					else if ($New != $Renew)
					{
						array_push($Errors, $Page->GetLanguage()->GetText('pages.register.wrong.notcorr'));
					}
					else
					{
						$New = md5($New);
						
						if ($Target->GetPassword() != $New)
						{
							$Target->SetPassword($New);
				
							array_push($Changes, $Page->GetLanguage()->GetText('pages.profile.updated.password'));
							
							$Page->GetFunctions()->LoginCookie($Target->GetVirtual(), $Target->GetPassword());
						}
					}
				}
			}
			
			if (isset($_POST['country']) && is_numeric($_POST['country']))
			{
				if ($Page->GetFunctions()->GetConstants()->CountryExists($_POST['country']))
				{
					$Target->SetCountry($_POST['country']);
				}
				else
				{
					$Page->GetFunctions()->BanIP('Country does not exist when updating in profile');
					
					$Page->GetFunctions()->Redirect();
				}
			}
			
			if (isset($_POST['language']) && is_numeric($_POST['language']) && $_POST['language'] != $Target->GetLanguage())
			{
				if ($Page->GetFunctions()->GetConstants()->LanguageExists($_POST['language']))
				{
					$Target->SetLanguage($_POST['language']);
					
					$Page->GetFunctions()->Redirect('', '#');
				}
				else
				{
					$Page->GetFunctions()->BanIP('Language does not exist when updating in profile');
					
					$Page->GetFunctions()->Redirect();
				}
			}
		}
		
		$Page->Head('profile.my', true);
		?>
		<table style="width: 100%;" class="profiletop mzero">
			<tr>
				<td>
					<div class="profilename">
						<?php echo $Target->GetName(); if ($Target->GetBeta() == true) echo '<img class="betat" src="' . $Page->Link('includes/images/beta.png') . '"/>'; ?>
					</div>
					<div class="profilerest">
						<?php
							echo $Page->GetLanguage()->GetText('pages.profile.represent.status') . ': ';
							
							if ($Target->GetPlaying() == true)
							{
								echo $Page->GetLanguage()->GetText('pages.profile.represent.status.yes');
							}
							else
							{
								echo $Page->GetLanguage()->GetText('pages.profile.represent.status.no');
							}
							
							echo '<br/>';
							
							echo $Page->GetLanguage()->GetText('pages.profile.represent.local') . ': ' . utf8_encode(strftime("%A %d %B, %H:%M", strtotime($Target->GetCountry()->DecaleDate())));
						?>
					</div>
				</td>
				<td align="right" valign="top">
					<?php echo '<a href="' . $Page->Link('skin.php') . '"><img src="' . $Page->GetFunctions()->GetAvatar($Target) . '"/></a>'; ?>
				</td>
			</tr>
		</table>
		<table style="width: 100%;" cellpadding="0" cellspacing="0">
			<tr>
				<td valign="top" style="padding-right: 4px;" width="60%">
					<table style="width: 100%;" class="profiletop" cellpadding="2" cellspacing="0">
						<tr class="heading">
							<td colspan="2"><?php echo $Page->GetLanguage()->GetText('pages.profile.firsttable.title'); ?></td>
						</tr>
						<tr class="profilel1">
							<td><?php echo $Page->GetLanguage()->GetText('pages.profile.firsttable.account'); ?></td>
							<td align="right"><?php echo $Target->GetName(); ?></td>
						</tr>
						<tr class="profilel2">
							<td><?php echo $Page->GetLanguage()->GetText('pages.profile.firsttable.grad'); ?></td>
							<td align="right"><?php echo $Target->GetColored($Page->GetLanguage()->GetText('grads.' . $Target->GetGrad()->Get('variable'))); ?></td>
						</tr>
						<tr class="profilel1">
							<td><?php echo $Page->GetLanguage()->GetText('pages.profile.firsttable.registered'); ?></td>
							<td align="right"><?php echo utf8_encode(strftime("%A %d %B, %H:%M", strtotime($Page->GetPlayer()->GetCountry()->DecaleDate($Target->GetRegister())))); ?></td>
						</tr>
						<tr class="profilel2">
							<td><?php echo $Page->GetLanguage()->GetText('pages.profile.firsttable.connection'); ?></td>
							<td align="right"><?php echo utf8_encode(strftime("%A %d %B, %H:%M", strtotime($Page->GetPlayer()->GetCountry()->DecaleDate($Target->GetConnection())))); ?></td>
						</tr>
						<tr class="profilel1">
							<td><?php echo $Page->GetLanguage()->GetText('pages.profile.firsttable.playtion'); ?></td>
							<td align="right"><?php if ($Target->GetPlaytion() == $Page->GetFunctions()->GetConstants()->GetEmptyDate()) { echo $Page->GetLanguage()->GetText('pages.profile.firsttable.never'); } else { echo utf8_encode(strftime("%A %d %B, %H:%M", strtotime($Page->GetPlayer()->GetCountry()->DecaleDate($Target->GetPlaytion())))); } ?></td>
						</tr>
					</table>
					<table style="width: 100%;" class="profiletop" cellpadding="2" cellspacing="0">
						<tr class="heading">
							<td colspan="2"><?php echo $Page->GetLanguage()->GetText('pages.profile.secondtable.title'); ?></td>
							
						</tr>
						<tr class="profilel1">
							<td><?php echo $Page->GetLanguage()->GetText('pages.profile.secondtable.community'); ?></td>
							<td align="right"><?php $Page->GetFunctions()->Load('language'); $Language = new Language($Page, $Target->GetLanguage(), false); echo $Language->GetFull(); ?></td>
						</tr>
						<tr class="profilel2">
							<td><?php echo $Page->GetLanguage()->GetText('pages.profile.secondtable.whitelist'); ?></td>
							<td align="right"><?php if ($Target->GetWhite()) { echo $Page->GetLanguage()->GetText('pages.profile.represent.status.yes'); } else { echo $Page->GetLanguage()->GetText('pages.profile.represent.status.no'); } ?></td>
						</tr>
						<tr class="profilel1">
							<td><?php echo $Page->GetLanguage()->GetText('pages.profile.secondtable.money'); ?></td>
							<td align="right"><?php echo $Target->GetMoney(); ?></td>
						</tr>
						<?php
							if ($Target->InClan())
							{
						?>
						<tr class="profilel2">
							<td><?php echo $Page->GetLanguage()->GetText('pages.profile.secondtable.clan'); ?></td>
							<td align="right"><?php echo '<a href="' . $Page->Link($Target->GetClan()->GetLink()) . '">' . $Target->GetClan()->GetName() . '</a>'; ?></td>
						</tr>
						<?php
							}
						?>
					</table>
					<form method="post" action="">
						<table style="width: 100%;" class="profiletop" cellpadding="2" cellspacing="0">
							<tr class="heading">
								<td colspan="2"><?php echo $Page->GetLanguage()->GetText('pages.profile.fifthtable.title'); ?></td>
							</tr>
							<tr class="profilel1">
								<td width="50%"><?php echo $Page->GetLanguage()->GetText('pages.profile.fifthtable.misible'); ?></td>
								<td align="right"><select name="misible"><option value="1"<?php if ($Target->GetMisible()) { echo ' selected'; } ?>><?php echo ucfirst($Page->GetLanguage()->GetText('pages.profile.represent.status.yes')); ?></option><option value="0"<?php if (!$Target->GetMisible()) { echo ' selected'; } ?>><?php echo ucfirst($Page->GetLanguage()->GetText('pages.profile.represent.status.no')); ?></option></select></td>
							</tr>
							<tr class="profilel2">
								<td valign="top" width="50%"><?php echo $Page->GetLanguage()->GetText('pages.profile.fifthtable.cpass'); ?></td>
								<td align="right"><input name="old" maxlength="<?php echo $Page->GetFunctions()->GetConstants()->GetMaxPasswordSize(); ?>" type="password" placeholder="<?php echo $Page->GetLanguage()->GetText('pages.profile.fifthtable.cpass.old'); ?>"/><input name="new" maxlength="<?php echo $Page->GetFunctions()->GetConstants()->GetMaxPasswordSize(); ?>" type="password" placeholder="<?php echo $Page->GetLanguage()->GetText('pages.profile.fifthtable.cpass.new'); ?>"/><input name="renew" maxlength="<?php echo $Page->GetFunctions()->GetConstants()->GetMaxPasswordSize(); ?>" type="password" placeholder="<?php echo $Page->GetLanguage()->GetText('pages.profile.fifthtable.cpass.renew'); ?>"/></td>
							</tr>
							<tr class="profilel1">
								<td><?php echo $Page->GetLanguage()->GetText('pages.profile.fifthtable.language'); ?></td>
								<td align="right">
									<select name="language">
									<?php
										foreach ($Page->GetFunctions()->GetConstants()->GetLanguages() as $Language)
										{
											echo '<option value="' . $Language->GetId() . '"';
											
											echo ($Target->GetLanguage() == $Language->GetId()) ? ' selected="selected"' : '';
											
											echo '>' . $Language->GetFull() . '</option>';
										}
									?>
									</select>
								</td>
							</tr>
							<tr class="profilel2">
								<td><?php echo $Page->GetLanguage()->GetText('pages.profile.fifthtable.country'); ?></td>
								<td align="right">
									<select name="country">
									<?php
										foreach ($Page->GetFunctions()->GetConstants()->GetCountries() as $Country)
										{
											echo '<option value="' . $Country->GetId() . '"';
											
											echo ($Target->GetCountry()->GetId() == $Country->GetId()) ? ' selected="selected"' : '';
											
											echo '>' . $Country->GetName() . '</option>';
										}
									?>
									</select>
								</td>
							</tr>
							<tr class="profilel1">
								<td colspan="2" align="center" width="50%"><input type="submit" value="<?php echo $Page->GetLanguage()->GetText('pages.profile.fifthtable.submit'); ?>"/></td>
							</tr>
						</table>
					</form>
				</td>
				<td valign="top" style="padding-left: 4px;" width="50%">
					<table style="width: 100%;" class="profiletop" cellpadding="2" cellspacing="0">
						<tr class="heading">
							<td colspan="2"><?php echo $Page->GetLanguage()->GetText('pages.profile.thirdtable.title'); ?></td>
						</tr>
						<tr class="profilel1">
							<td><?php echo $Page->GetLanguage()->GetText('pages.profile.thirdtable.gametime'); ?></td>
							<td align="right"><?php $Times = $Page->GetFunctions()->SecondsFormat($Target->GetPlaymin()); echo $Times['h'] . $Page->GetLanguage()->GetText('pages.profile.thirdtable.hours') . ' ' . $Times['m'] . $Page->GetLanguage()->GetText('pages.profile.thirdtable.minutes') . ' ' . $Times['s'] . $Page->GetLanguage()->GetText('pages.profile.thirdtable.seconds'); ?></td>
						</tr>
						<tr class="profilel2">
							<td><?php echo $Page->GetLanguage()->GetText('pages.profile.thirdtable.mill'); ?></td>
							<td align="right"><?php echo $Target->GetMill(); ?></td>
						</tr>
						<tr class="profilel1">
							<td><?php echo $Page->GetLanguage()->GetText('pages.profile.thirdtable.hill'); ?></td>
							<td align="right"><?php echo $Target->GetHill(); ?></td>
						</tr>
						<tr class="profilel2">
							<td><?php echo $Page->GetLanguage()->GetText('pages.profile.thirdtable.mie'); ?></td>
							<td align="right"><?php echo $Target->GetMie(); ?></td>
						</tr>
						<tr class="profilel1">
							<td><?php echo $Page->GetLanguage()->GetText('pages.profile.thirdtable.hie'); ?></td>
							<td align="right"><?php echo $Target->GetHie(); ?></td>
						</tr>
						<tr class="profilel2">
							<td><?php echo $Page->GetLanguage()->GetText('pages.profile.thirdtable.nie'); ?></td>
							<td align="right"><?php echo $Target->GetNie(); ?></td>
						</tr>
						<tr class="profilel1">
							<td><?php echo $Page->GetLanguage()->GetText('pages.profile.thirdtable.comments'); ?></td>
							<td align="right"><?php echo $Page->GetFunctions()->CountComments($Target); ?></td>
						</tr>
						<tr class="profilel2">
							<td><?php echo $Page->GetLanguage()->GetText('pages.profile.thirdtable.reports'); ?></td>
							<td align="right"><?php echo $Page->GetFunctions()->CountReports($Target); ?></td>
						</tr>
					</table>
					<table style="width: 100%; margin-bottom: 0;" class="profiletop" cellpadding="2" cellspacing="0">
						<tr class="heading">
							<td colspan="2"><?php echo $Page->GetLanguage()->GetText('pages.profile.forthtable.title'); ?></td>
						</tr>
						<tr class="profilel1">
							<td valign="top"><?php echo $Page->GetLanguage()->GetText('pages.profile.forthtable.mail'); ?></td>
							<td align="right" valign="top"><?php echo '<a href="mailto:' . $Target->GetMail() . '">' . $Target->GetMail() . '</a>'; ?></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
		<?php
	}
	else
	{
		$Page->Head($Page->GetLanguage()->GetText('titles.profile.his', array($Target->GetName())));
	?>
		<table style="width: 100%;" class="profiletop mzero">
			<tr>
				<td>
					<div class="profilename">
						<?php echo $Target->GetName(); if ($Target->GetBeta() == true) echo '<img class="betat" src="' . $Page->Link('includes/images/beta.png') . '"/>'; ?>
					</div>
					<div class="profilerest">
						<?php
							if ($Page->IsConnected() && $Page->GetPlayer()->GetGrad()->Get('canban'))
							{
								if ($Page->GetPlayer()->GetGrad()->Get('canban'))
								{
									if ($Target->GetBanned())
									{
										echo '<a href="' . $Page->Link('ban.php?id=' . $Target->GetId() . '&type=0') . '">' . $Page->GetLanguage()->GetText('pages.profile.represent.unban') . '</a>';
									}
									else
									{
										echo '<a href="' . $Page->Link('ban.php?id=' . $Target->GetId() . '&type=1') . '">' . $Page->GetLanguage()->GetText('pages.profile.represent.ban') . '</a>';
									}
									
									echo '<br/>';
								}
								
								if ($Page->GetPlayer()->GetGrad()->Get('webchangebeta'))
								{
									if ($Target->GetBeta())
									{
										echo '<a href="' . $Page->Link('beta.php?id=' . $Target->GetId() . '&type=0') . '">' . $Page->GetLanguage()->GetText('pages.profile.represent.unbeta') . '</a>';
									}
									else
									{
										echo '<a href="' . $Page->Link('beta.php?id=' . $Target->GetId() . '&type=1') . '">' . $Page->GetLanguage()->GetText('pages.profile.represent.beta') . '</a>';
									}
									
									echo '<br/>';
								}
								
								if ($Page->GetPlayer()->GetGrad()->Get('webchangewhite'))
								{
									if ($Target->GetWhite())
									{
										echo '<a href="' . $Page->Link('white.php?id=' . $Target->GetId() . '&type=0') . '">' . $Page->GetLanguage()->GetText('pages.profile.represent.unwhite') . '</a>';
									}
									else
									{
										echo '<a href="' . $Page->Link('white.php?id=' . $Target->GetId() . '&type=1') . '">' . $Page->GetLanguage()->GetText('pages.profile.represent.white') . '</a>';
									}
									
									echo '<br/>';
								}
							}
							
							echo $Page->GetLanguage()->GetText('pages.profile.represent.status') . ': ';
							
							if ($Target->GetPlaying() == true)
							{
								echo $Page->GetLanguage()->GetText('pages.profile.represent.status.yes');
							}
							else
							{
								echo $Page->GetLanguage()->GetText('pages.profile.represent.status.no');
							}
							
							echo '<br/>';
							
							echo $Page->GetLanguage()->GetText('pages.profile.represent.local') . ': ' . utf8_encode(strftime("%A %d %B, %H:%M", strtotime($Target->GetCountry()->DecaleDate())));
						?>
					</div>
				</td>
				<td align="right" valign="top">
					<img src="<?php echo $Page->GetFunctions()->GetAvatar($Target); ?>"/>
				</td>
			</tr>
		</table>
		<table style="width: 100%;" cellpadding="0" cellspacing="0">
			<tr>
				<td valign="top" style="padding-right: 4px;" width="60%">
					<table style="width: 100%;" class="profiletop" cellpadding="2" cellspacing="0">
						<tr class="heading">
							<td colspan="2"><?php echo $Page->GetLanguage()->GetText('pages.profile.firsttable.title'); ?></td>
						</tr>
						<tr class="profilel1">
							<td><?php echo $Page->GetLanguage()->GetText('pages.profile.firsttable.account'); ?></td>
							<td align="right"><?php echo $Target->GetName(); ?></td>
						</tr>
						<tr class="profilel2">
							<td><?php echo $Page->GetLanguage()->GetText('pages.profile.firsttable.grad'); ?></td>
							<td align="right"><?php echo $Target->GetColored($Page->GetLanguage()->GetText('grads.' . $Target->GetGrad()->Get('variable'))); ?></td>
						</tr>
						<tr class="profilel1">
							<td><?php echo $Page->GetLanguage()->GetText('pages.profile.firsttable.registered'); ?></td>
							<td align="right"><?php echo utf8_encode(strftime("%A %d %B, %H:%M", strtotime($Page->DecaleDate($Target->GetRegister())))); ?></td>
						</tr>
						<tr class="profilel2">
							<td><?php echo $Page->GetLanguage()->GetText('pages.profile.firsttable.connection'); ?></td>
							<td align="right"><?php echo utf8_encode(strftime("%A %d %B, %H:%M", strtotime($Page->DecaleDate($Target->GetConnection())))); ?></td>
						</tr>
						<tr class="profilel1">
							<td><?php echo $Page->GetLanguage()->GetText('pages.profile.firsttable.playtion'); ?></td>
							<td align="right"><?php if ($Target->GetPlaytion() == $Page->GetFunctions()->GetConstants()->GetEmptyDate()) { echo $Page->GetLanguage()->GetText('pages.profile.firsttable.never'); } else { echo utf8_encode(strftime("%A %d %B, %H:%M", strtotime($Page->DecaleDate($Target->GetPlaytion())))); } ?></td>
						</tr>
					</table>
					<table style="width: 100%;" class="profiletop" cellpadding="2" cellspacing="0">
						<tr class="heading">
							<td colspan="2"><?php echo $Page->GetLanguage()->GetText('pages.profile.secondtable.title'); ?></td>
							
						</tr>
						<tr class="profilel1">
							<td><?php echo $Page->GetLanguage()->GetText('pages.profile.secondtable.community'); ?></td>
							<td align="right"><?php $Page->GetFunctions()->Load('language'); $Language = new Language($Page, $Target->GetLanguage(), false); echo $Language->GetFull(); ?></td>
						</tr>
						<tr class="profilel2">
							<td><?php echo $Page->GetLanguage()->GetText('pages.profile.secondtable.whitelist'); ?></td>
							<td align="right"><?php if ($Target->GetWhite()) { echo $Page->GetLanguage()->GetText('pages.profile.represent.status.yes'); } else { echo $Page->GetLanguage()->GetText('pages.profile.represent.status.no'); } ?></td>
						</tr>
						<?php
							if ($Target->InClan())
							{
						?>
						<tr class="profilel1">
							<td><?php echo $Page->GetLanguage()->GetText('pages.profile.secondtable.clan'); ?></td>
							<td align="right"><?php echo '<a href="' . $Page->Link($Target->GetClan()->GetLink()) . '">' . $Target->GetClan()->GetName() . '</a>'; ?></td>
						</tr>
						<?php
							}
						?>
					</table>
				</td>
				<td valign="top" style="padding-left: 4px;" width="50%">
					<table style="width: 100%;" class="profiletop" cellpadding="2" cellspacing="0">
						<tr class="heading">
							<td colspan="2"><?php echo $Page->GetLanguage()->GetText('pages.profile.thirdtable.title'); ?></td>
						</tr>
						<tr class="profilel1">
							<td><?php echo $Page->GetLanguage()->GetText('pages.profile.thirdtable.gametime'); ?></td>
							<td align="right"><?php $Times = $Page->GetFunctions()->SecondsFormat($Target->GetPlaymin()); echo $Times['h'] . $Page->GetLanguage()->GetText('pages.profile.thirdtable.hours') . ' ' . $Times['m'] . $Page->GetLanguage()->GetText('pages.profile.thirdtable.minutes') . ' ' . $Times['s'] . $Page->GetLanguage()->GetText('pages.profile.thirdtable.seconds'); ?></td>
						</tr>
						<tr class="profilel2">
							<td><?php echo $Page->GetLanguage()->GetText('pages.profile.thirdtable.mill'); ?></td>
							<td align="right"><?php echo $Target->GetMill(); ?></td>
						</tr>
						<tr class="profilel1">
							<td><?php echo $Page->GetLanguage()->GetText('pages.profile.thirdtable.hill'); ?></td>
							<td align="right"><?php echo $Target->GetHill(); ?></td>
						</tr>
						<tr class="profilel2">
							<td><?php echo $Page->GetLanguage()->GetText('pages.profile.thirdtable.mie'); ?></td>
							<td align="right"><?php echo $Target->GetMie(); ?></td>
						</tr>
						<tr class="profilel1">
							<td><?php echo $Page->GetLanguage()->GetText('pages.profile.thirdtable.hie'); ?></td>
							<td align="right"><?php echo $Target->GetHie(); ?></td>
						</tr>
						<tr class="profilel2">
							<td><?php echo $Page->GetLanguage()->GetText('pages.profile.thirdtable.nie'); ?></td>
							<td align="right"><?php echo $Target->GetNie(); ?></td>
						</tr>
						<tr class="profilel1">
							<td><?php echo $Page->GetLanguage()->GetText('pages.profile.thirdtable.comments'); ?></td>
							<td align="right"><?php echo $Page->GetFunctions()->CountComments($Target); ?></td>
						</tr>
						<tr class="profilel2">
							<td><?php echo $Page->GetLanguage()->GetText('pages.profile.thirdtable.reports'); ?></td>
							<td align="right"><?php echo $Page->GetFunctions()->CountReports($Target); ?></td>
						</tr>
					</table>
					<table style="width: 100%; margin-bottom: 0;" class="profiletop" cellpadding="2" cellspacing="0">
						<tr class="heading">
							<td colspan="2"><?php echo $Page->GetLanguage()->GetText('pages.profile.forthtable.title'); ?></td>
						</tr>
						<tr class="profilel1">
							<td><?php echo $Page->GetLanguage()->GetText('pages.profile.forthtable.mail'); ?></td>
							<?php
								if ($Target->GetMisible() || ($Page->IsConnected() && $Page->GetPlayer()->GetGrad()->Get('webviewmails')))
								{
									echo '<td align="right" valign="top"><a href="mailto:' . $Target->GetMail() . '">' . $Target->GetMail() . '</a></td>'; 
								}
								else
								{
									echo '<td valign="top" align="right">' . $Page->GetLanguage()->GetText('pages.profile.forthtable.mailinvisible') . '</td>';
								}
							?>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	<?php
	}
	
	if (isset($Changes))
	{
		echo $Page->GetFunctions()->GetChanges($Changes, true);
	}
	
	if (isset($Errors))
	{
		echo $Page->GetFunctions()->GetErrors($Errors, true);
	}
	
	$Page->Foot();
?>