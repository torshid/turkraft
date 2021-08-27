<?php
	require_once 'includes/page.php';
	
	$Page = new Page('ingame');
	
	$Page->GetFunctions()->Load('ingame');
	
	$Page->Head();
?>
<div class="title"><?php echo $Page->GetLanguage()->GetText('pages.ingame.title'); ?></div>
<div class="shows">
<?php
	$Counter = 1;
	
	$Members = new Ingame($Page);
	
	$Total = count($Members->GetElements());
	
	if ($Total > 0)
	{
		foreach ($Members->GetElements() as $Member)
		{
			echo '<a href="' . $Page->Link($Member->GetLink()) . '">' . $Member->GetColored() . '</a>';
			
			if ($Counter < $Total)
			{
				echo ', ';
			}
			
			$Counter++;
		}
	}
	else
	{
		echo $Page->GetLanguage()->GetText('pages.ingame.nobody');
	}
	
	echo '</div>';

	$Page->Foot();
?>