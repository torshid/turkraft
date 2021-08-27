<?php
	require_once 'includes/page.php';
	
	$Page = new Page();
	
	if (!$Page->IsConnected())
	{
		$Page->GetFunctions()->Redirect();
	}
	
	$Page->GetFunctions()->Load('category');
	
	$Errors = array();
	
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
			if (!$Category->CanAccess($Page->GetPlayer()))
			{
				$Page->GetFunctions()->Redirect();
			}
			
			if (isset($_POST['title']) && isset($_POST['content']))
			{
				$Title = $_POST['title'];
			
				if ($Page->GetFunctions()->CheckSize($Title, $Page->GetFunctions()->GetConstants()->GetMinSubjectSize(), $Page->GetFunctions()->GetConstants()->GetMaxSubjectSize()))
				{
					$Content = $_POST['content'];
					
					if ($Page->GetFunctions()->CheckSize($Content, $Page->GetFunctions()->GetConstants()->GetMinReplySize(), $Page->GetFunctions()->GetConstants()->GetMaxReplySize()))
					{
						$Title = addslashes(htmlentities($Title));
						
						mysql_query("INSERT INTO f_subjects (category, title, poster, posted) VALUES ({$Category->GetId()}, '$Title', {$Page->GetPlayer()->GetId()}, '" . $Page->GetFunctions()->GetConstants()->GetActualDate() . "')") or die(mysql_error());
						
						$Page->GetFunctions()->Load('subject');
	
						$Thread = new Subject($Page, mysql_insert_id());
						
						$Thread->PostReply($Page->GetPlayer(), $Content);
						
						$Page->GetFunctions()->Redirect($Thread->GetLink());
					}
					else
					{
						array_push($Errors, $Page->GetLanguage()->GetText('pages.create.contentnu'));
					}
				}
				else
				{
					array_push($Errors, $Page->GetLanguage()->GetText('pages.create.titlenu'));
				}
			}
			
			$Page->Head($Page->GetLanguage()->GetText('pages.create.in') . ' ' . $Category->GetName(), false);
			
			echo '<div class="title">';
			
			echo $Page->GetLanguage()->GetText('pages.create.in') . ' ' . $Category->GetName();
			
			echo '</div>';
			
			echo '<div class="shows mzero">';
			
			echo '<form method="post" action="">';
			
			echo '<table style="width: 100%;">';
			
			echo '<tr>';
			
			echo '<td align="center">';
			
			echo '<input name="title" type="text" style="width: 99%;" placeholder="' . $Page->GetLanguage()->GetText('pages.create.name') . '"';
			
			if (isset($_POST['title']))
			{
				echo ' value="' . $_POST['title'] . '"';
			}
			
			echo '/>';
			
			echo '</td>';
			
			echo '</tr>';
			//
			echo '<tr>';
			
			echo '<td align="center">';
			
			echo '<textarea name="content" class="mzero createte" placeholder="' . $Page->GetLanguage()->GetText('pages.create.text') . '">';
			
			if (isset($_POST['content']))
			{
				echo $_POST['content'];
			}
			
			echo '</textarea>';
			
			echo '</td>';
			
			echo '</tr>';
			//
			echo '<tr>';
			
			echo '<td align="center">';
			
			echo '<input type="submit"/>';
			
			echo '</td>';
			
			echo '</tr>';
			
			echo '</table>';
			
			echo '</form>';
			
			if (isset($Errors))
			{
				echo $Page->GetFunctions()->GetErrors($Errors, true);
			}
			
			echo '</div>';
			
			$Page->Foot();
		}
	}
?>