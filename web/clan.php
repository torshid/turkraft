<?php
	require_once 'includes/page.php';
	
	$Page = new Page();
	
	if (!isset($_GET['id']) || !is_numeric($_GET['id']))
	{
		$Page->GetFunctions()->Redirect();
	}
	
	$Page->GetFunctions()->Load('clan');
	
	$Clan = new Clan($Page, $_GET['id']);
	
	if ($Clan->GetVirtual() == 0)
	{
		$Page->GetFunctions()->Redirect();
	}
	
	$Myne = false;
	
	if ($Page->IsConnected())
	{
		if ($Page->GetPlayer()->GetClan()->GetVirtual() == $Clan->GetVirtual())
		{
			$Myne = true;
		}
	}
	
	$Page->GetFunctions()->Load('player');
	
	if ($Myne)
	{
		$Page->Head('pages.myclan', true);
	}
	else
	{
		$Page->Head($Clan->GetName());
	}
?>
<table style="width: 100%;" class="profiletop mzero">
	<tr>
		<td>
			<div class="profilename">
				<?php echo $Clan->GetName(); ?>
			</div>
		</td>
		<td align="right" valign="top">
			<?php //echo '<a href="' . $Page->Link('skin.php') . '"><img src="' . $Page->GetFunctions()->GetAvatar($Target) . '"/></a>'; ?>
		</td>
	</tr>
</table>
<table style="width: 100%;" class="profiletop" cellpadding="2" cellspacing="0">
	<tr class="heading">
		<td colspan="2"><?php echo $Page->GetLanguage()->GetText('pages.clan.firsttable.title'); ?></td>
	</tr>
	<tr class="profilel1">
		<td><?php echo $Page->GetLanguage()->GetText('pages.clan.firsttable.chief'); ?></td>
		<td align="right"><?php $Chief = new Player($Page, $Clan->GetChief()); echo '<a href="' . $Page->Link($Chief->GetLink()) . '">' . $Chief->GetColored() . '</a>'; ?></td>
	</tr>
	<tr class="profilel2">
		<td><?php echo $Page->GetLanguage()->GetText('pages.clan.firsttable.count'); ?></td>
		<td align="right"><?php echo $Clan->GetTotal(); ?></td>
	</tr>
	<tr class="profilel1">
		<td><?php echo $Page->GetLanguage()->GetText('pages.clan.firsttable.taken'); ?></td>
		<td align="right"><?php echo $Clan->GetTaken(); ?></td>
	</tr>
	<?php
	if ($Myne || ($Page->IsConnected() && $Page->GetPlayer()->GetGrad()->Get('cangodclans')))
	{
		?>
	<tr class="profilel2">
		<td><?php echo $Page->GetLanguage()->GetText('pages.clan.firsttable.score'); ?></td>
		<td align="right"><?php echo $Clan->GetScore(); ?></td>
	</tr>
	<tr class="profilel1">
		<td><?php echo $Page->GetLanguage()->GetText('pages.clan.firsttable.money'); ?></td>
		<td align="right"><?php echo $Clan->GetMoney(); ?></td>
	</tr>
		<?php
	}
	?>
</table>

<table style="width: 100%;" class="profiletop" cellpadding="2" cellspacing="0">
	<tr class="heading">
		<td><?php echo $Page->GetLanguage()->GetText('pages.clan.second.title'); ?></td>
		<td align="right"><?php echo $Page->GetLanguage()->GetText('pages.clan.second.ratio'); ?></td>
	</tr>
	<tr>
		<?php
			if (count($Clan->GetConnected()) == 0)
			{
				echo '<td colspan="2">' . $Page->GetLanguage()->GetText('pages.clan.second.nomember') . '</td>';
			}
			else
			{
				$Clan->LoadConnected();
				
				$Color = true;
				
				foreach ($Clan->GetConnected() as $Member)
				{
					echo '<tr><td';
					
					if ($Color)
					{
						echo ' class="profilel1"';
					}
					else
					{
						echo ' class="profilel2"';
					}
					
					echo '><a href="' . $Page->Link($Member->GetLink()) . '">' . $Member->GetColored() . '</a></td>';
					
					echo '<td';
					
					if ($Color)
					{
						echo ' class="profilel1"';
					}
					else
					{
						echo ' class="profilel2"';
					}

					echo ' align="right">' . round($Member->GetHill() / $Member->GetHie(), 2). '</td></tr>';
					
					$Color = !$Color;
				}
			}
		?>
	</tr>
</table>
<?php
	$Page->Foot();
?>