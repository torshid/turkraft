<?php
	require_once 'includes/page.php';
	
	$Page = new Page();
	
	$Page->GetFunctions()->Load('canager');
	$Page->GetFunctions()->Load('category');
	
	if (!isset($_GET['id']) || !is_numeric($_GET['id']))
	{
		$Page->Head('pages.category.inexistant', true);
		
		echo '<div class="errors">' . $Page->GetLanguage()->GetText('pages.category.inexistantcat') . '</div>';
		
		$Page->Foot();
		
		exit;
	}
	else
	{
		$Category = new Category($Page, $_GET['id']);
		
		if ($Category->GetId() == 0)
		{
			$Page->Head('pages.category.inexistant');
			
			echo '<div class="errors">' . $Page->GetLanguage()->GetText('pages.category.inexistantcat') . '</div>';
			
			$Page->Foot();
			
			exit;
		}
		else
		{
			$Canager = new Canager($Page, $Category->GetId());
			
			$Page->Head($Category->GetName());
			
			echo '<div class="title">' . $Category->GetName(). '</div>'; 

			echo '<div class="shows mzero">';
			
			$Canager->Display();
			
			echo '<div class="mzero">';
			
			$Category->Display();
			
			echo '</div>';
			
			echo '</div>';
			
			$Page->Foot();
		}
	}
?>