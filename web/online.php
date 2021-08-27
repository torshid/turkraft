<?php
	include('includes/page.php');
	
	$Page = new Page('online');
	
	$Page->GetFunctions()->Load('onlines');
	
	$Onlines = new Onlines($Page);
	
	$Page->Head();
?>
<div class="title"><?php echo $Page->GetLanguage()->GetText('pages.online.title'); ?></div>
<div class="shows mzero">
<?php
	$Count = 1;
	
	if (count($Onlines->GetPlayers()) > 0)
	{
		$Onlines->LoadPlayers();
		
		foreach ($Onlines->GetPlayers() as $Player)
		{
			echo '<a href="' . $Page->Link($Player->GetLink()) . '">' . $Player->GetName() . '</a>';
			
			if ($Count < count($Onlines->GetPlayers()))
			{
				echo ', ';
			}
			
			$Count++;
		}
	}
	else
	{
		echo $Page->GetLanguage()->GetText('pages.online.nobody');
	}
?>
</div>
<?php
	$Page->Foot();
?>