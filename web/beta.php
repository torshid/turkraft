<?php
	require_once 'includes/page.php';
	
	$Page = new Page();
	
	if (!$Page->IsConnected())
	{
		$Page->GetFunctions()->Redirect();
	}
	
	if (!isset($_GET['type']) || !is_numeric($_GET['type']) || ($_GET['type'] != 0 && $_GET['type'] != 1) || !isset($_GET['id']) || !is_numeric($_GET['id']))
	{
		$Page->GetFunctions()->Redirect();
	}
	
	if (!$Page->GetPlayer()->GetGrad()->Get('webchangebeta'))
	{
		$Page->GetFunctions()->Redirect();
	}
	
	$Page->GetFunctions()->Load('player');
	
	$Target = new Player($Page, $_GET['id']);
	
	if ($Target->GetId() == 0)
	{
		$Page->GetFunctions()->Redirect();
	}
	
	if ($Target->GetId() == $Page->GetPlayer()->GetId())
	{
		$Page->GetFunctions()->Redirect($Target->GetLink());
	}
	
	$Target->SetBeta($_GET['type']);
	
	unset($_GET['type']);
	
	$Page->GetFunctions()->Redirect($Target->GetLink());
?>