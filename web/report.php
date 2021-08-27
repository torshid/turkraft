<?php
	require_once 'includes/page.php';
	
	$Page = new Page();
	
	if (!$Page->IsConnected() || !$Page->GetPlayer()->GetGrad()->Get('webreportedit') || !isset($_GET['id']) || !isset($_GET['status']) || !is_numeric($_GET['id']) || !is_numeric($_GET['status']))
	{
		$Page->GetFunctions()->Redirect();
	}
	
	$Page->GetFunctions()->Load('report');
	
	$Report = new Report($Page, $_GET['id']);
	
	if ($Report->GetId() == 0)
	{
		$Page->GetFunctions()->Redirect('reports.php');
	}
	
	$Report->SetStatus($_GET['status']);
	
	$Page->GetFunctions()->Redirect('reports.php', '#report' . $Report->GetId());
	
	exit;
?>