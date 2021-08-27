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
	
	if (!$Page->GetPlayer()->GetGrad()->Get('webnewsdelete'))
	{
		$Page->GetFunctions()->Redirect();
	}
	
	$Page->GetFunctions()->Load('news');
	
	$News = new News($Page, $_GET['id']);
	
	if ($News->GetId() == 0)
	{
		$Page->GetFunctions()->Redirect();
	}
	
	$News->SetDeleted($_GET['type']);
	
	if (isset($_GET['page']) && is_numeric($_GET['page']))
	{
		$Page->GetFunctions()->Redirect('?p=' . $_GET['page']);
	}
	else
	{
		$Page->GetFunctions()->Redirect($News->GetLink());
	}
?>