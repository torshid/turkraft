<?php
	require_once 'includes/page.php';
	
	$Page = new Page('download');
	
	$Page->Head();

	echo '<div class="title">' . $Page->GetLanguage()->GetText('pages.download.title') . '</div>';
	echo '<div class="shows mzero">';
	echo '<ul>';
	echo '<li><a href="' . $Page->Link('Turkraft.exe') . '">' . $Page->GetLanguage()->GetText('pages.download.windows') . '</a></li>';
	echo '<li><a href="' . $Page->Link('Turkraft.jar') . '">' . $Page->GetLanguage()->GetText('pages.download.other') . '</a></li>';
	echo '</ul>';
	echo '</div>';
	
	$Page->Foot();
?>