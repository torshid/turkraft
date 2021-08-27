<?php
	require_once 'includes/page.php';
	
	$Page = new Page('members');
	
	$Page->GetFunctions()->Load('lembers');
	
	$Page->Head();
?>
<div class="title"><?php echo $Page->GetLanguage()->GetText('pages.members.title'); ?></div>
<div class="shows mzero">
	<table class="maintainer" cellpadding="4" cellspacing="0">
		<tr class="heading">
			<td><?php echo $Page->GetLanguage()->GetText('pages.members.name'); ?></td>
			<td align="right"><?php echo $Page->GetLanguage()->GetText('pages.members.date'); ?></td>
		</tr>
		<?php
			if (isset($_GET['p']) && is_numeric($_GET['p']))
			{
				$Lembers = new Lembers($Page, $_GET['p']);
			}
			else
			{
				$Lembers = new Lembers($Page, 1);
			}
			
			if ($Lembers->GetCount() == 0)
			{
			}
			else
			{
				$Lembers->LoadPlayers();
				
				$Counter = 0;
				
				foreach ($Lembers->GetPlayers() as $Player)
				{
					$Counter++;
					
					echo '
						<tr>
							<td ';
							if ($Counter < count($Lembers->GetPlayers())) echo 'class="tainerbot"';
								echo '><a href="' . $Page->Link($Player->GetLink()) . '">' . $Player->GetColored() . '</a>
							</td>
							<td ';
							if ($Counter < count($Lembers->GetPlayers())) echo 'class="tainerbot"';
							echo 'align="right">
								' . utf8_encode(ucfirst(strftime("%A %d %B " . utf8_decode($Page->GetLanguage()->GetText('pages.index.at')). " %H:%M", strtotime($Page->DecaleDate($Player->GetRegister()))))) . '
							</td>
						</tr>';
					
				}
		?>
			</table>
			<br/>
			<center>
			<?php
				for ($i = 1; $i <= $Lembers->GetTotal(); $i++)
				{
					if ($i == $Lembers->GetNumber())
					{
						echo "<b>$i</b> ";
					}
					else
					{
						echo '<a href="members.php?p='.$i.'">'.$i.'</a> ';
					}
				}
			?>
			</center>
		</div>
	<?php
		}
	
	$Page->Foot();
?>