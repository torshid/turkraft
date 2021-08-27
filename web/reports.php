<?php
	require_once 'includes/page.php';
	
	$Page = new Page('reports');
	
	$Page->GetFunctions()->Load('ranager');
	
	$Page->GetFunctions()->Load('teports');
	
	$Errors = array();
	
	$Changes = array();
	
	$Reports = new Ranager($Page);
	
	if ($Page->IsConnected())
	{
		$ReportTypes = new Teports($Page);
		
		if (isset($_POST['type']) && isset($_POST['content']))
		{
			$Type = $_POST['type'];
			
			$Content = $_POST['content'];
			
			if (is_numeric($Type))
			{
				if ($ReportTypes->TypeExists($Type))
				{
					if ($Page->GetFunctions()->CheckSize(trim($Content), $Page->GetFunctions()->GetConstants()->GetMinReportSize(), $Page->GetFunctions()->GetConstants()->GetMaxReportSize()))
					{
						$Reports->PostReport($Type, $Page->GetPlayer(), $Content);
						
						$_POST['content'] = '';
						
						array_push($Changes, $Page->GetLanguage()->GetText('pages.reports.posted'));
					}
					else
					{
						array_push($Errors, $Page->GetLanguage()->GetText('pages.reports.nu'));
					}
				}
				else
				{
					$Page->GetFunctions()->BanIP('Report type does not exist when posting');
					
					$Page->GetFunctions()->Redirect();
				}
			}
			else
			{
				$Page->GetFunctions()->BanIP('Report type is not numeric when posting');
				
				$Page->GetFunctions()->Redirect();
			}
		}
	}
	
	$Page->Head();
?>
						<div class="title"><?php echo $Page->GetLanguage()->GetText('pages.reports.title'); ?></div>
						<div class="shows">
						<?php
							if (!isset($ReportTypes))
							{
								$ReportTypes = new Teports($Page);
							}
							
							if (count($Reports->GetElements()) > 0)
							{
								$Page->GetFunctions()->GetConstants()->LoadLanguages();
								
								foreach ($Reports->GetElements() as $Report)
								{
									if ($Report->GetDeleted() && (!$Page->IsConnected() || !$Page->GetPlayer()->GetGrad()->Get('WebReportDelete')))
									{
										continue;
									}
									
									$Report->Load($ReportTypes);
									
									echo '<div class="areport" id="report' . $Report->GetId() . '">';
									echo '<div class="reporttop">';
									echo '<img width="18" height="18" class="reportpic" src="includes/images/';
									
									switch ($Report->GetStatus())
									{
										case 1:
											echo 'refused';
											break;
										case 2:
											echo 'working';
											break;
										case 3:
											echo 'finish';
											break;
										default:
											echo 'waiting';
											break;
									}
									
									echo '.png"/> <font';
									
									if ($Report->GetDeleted())
									{
										echo ' color="red"';
									}
									
									echo '><b>[' . $Report->GetCategory()->GetText() . ']</b> ' . $Page->GetLanguage()->GetText('pages.index.by') . ' <a href="' . $Report->GetPoster()->GetLink() . '">'. $Report->GetPoster()->GetColored();
									
									echo '</font></a>';
									
									if ($Page->IsConnected() && $Page->GetPlayer()->GetGrad()->Get('webreportedit'))
									{
										if ($Report->GetStatus() != 0)
										{
											echo ' <a href="report.php?id=' . $Report->GetId() . '&status=0"><img width="18" height="18" class="reportpic" src="includes/images/waiting.png"/></a>';
										}
										
										if ($Report->GetStatus() != 1)
										{
											echo ' <a href="report.php?id=' . $Report->GetId() . '&status=1"><img width="18" height="18" class="reportpic" src="includes/images/refused.png"/></a>';
										}
										
										if ($Report->GetStatus() != 2)
										{
											echo ' <a href="report.php?id=' . $Report->GetId() . '&status=2"><img width="18" height="18" class="reportpic" src="includes/images/working.png"/></a>';
										}
										
										if ($Report->GetStatus() != 3)
										{
											echo ' <a href="report.php?id=' . $Report->GetId() . '&status=3"><img width="18" height="18" class="reportpic" src="includes/images/finish.png"/></a>';
										}
									}
									
									echo '<div class="reportdate">' . ucfirst(utf8_encode(strftime("%A %d %B, %H:%M", strtotime($Page->DecaleDate($Report->GetPosted()))))) . '</div>';
									echo '</div>';
									echo '<div class="shows">' . $Report->GetContent() . '</div>';
									echo '</div>';
								}
							}
							else
							{
								echo $Page->GetLanguage()->GetText('pages.reports.notyet') . '<br/><br/>';
							}
							
							echo '</div>';
							
							if ($Page->IsConnected())
							{
								echo '<div class="title">' . $Page->GetLanguage()->GetText('pages.reports.postnew') . '</div><div class="shows">';
								echo '<form method="post" action="">';
								echo '<table style="width: 100%;">';
								echo '<tr><td align="left" valign="top" width="30"><select name="type">';
								foreach ($ReportTypes->GetElements() as $ReportType)
								{
									echo '<option value="' . $ReportType->GetId() . '"';
									
									echo (isset($_POST['type']) && $_POST['type'] == $ReportType->GetId()) ? ' selected="selected"' : '';
									
									echo '>' . $ReportType->GetText() . '</option>';
								}
								echo '</select></td>';
								echo '<td><textarea name="content" style="width: 100%; height: 64px;" placeholder="' . $Page->GetLanguage()->GetText('pages.reports.postreport') . '">';
								if (isset($_POST['content']))
								{
									echo $_POST['content'];
								}
								echo '</textarea></td></tr>';
								echo '<tr><td align="center" colspan="2"><input type="submit" value="' . $Page->GetLanguage()->GetText('pages.reports.submit') . '"/></td></tr>';
								echo '</table>';
								echo '</form></div>';
							}
	
							if (isset($Changes))
							{
								echo $Page->GetFunctions()->GetChanges($Changes, true);
							}
							
							if (isset($Errors))
							{
								echo $Page->GetFunctions()->GetErrors($Errors, true);
							}
						?>
<?php
	$Page->Foot();
?>