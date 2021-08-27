<?php
	require_once 'includes/page.php';
	
	$Page = new Page('forum');
	
	$Page->GetFunctions()->Load('canager');
	
	$Canager = new Canager($Page, 0);
	
	$Page->Head();
	
	echo '<div class="title">' . $Page->GetLanguage()->GetText('pages.forum.title'). '</div>'; 

	echo '<div class="shows mzero">';

	$Canager->Display();
	
	echo '</div>';
	
	$Page->Foot();
?>