<?php
	require_once 'includes/page.php';
	
	$Page = new Page('guide');
	
	$Page->Head();
	
	echo '<div class="title">' . $Page->GetLanguage()->GetText('pages.guide.title') . '</div>';
	
	echo '<div class="shows">';
	
	echo $Page->GetFunctions()->SetBBCode($Page->GetLanguage()->GetText('pages.guide.content'));
	
	echo '</div>';
	
	$Page->Foot();
?>