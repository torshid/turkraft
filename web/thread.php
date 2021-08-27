<?php
	require_once 'includes/page.php';
	
	$Page = new Page();
	
	$Page->GetFunctions()->Load('subject');
	$Page->GetFunctions()->Load('player');
	
	$Errors = array();
	$Changes = array();
	
	if (!isset($_GET['id']) || !is_numeric($_GET['id']))
	{
		$Page->Head('pages.thread.inexistant', true);
		
		echo '<div class="errors">' . $Page->GetLanguage()->GetText('pages.thread.inexistantthr') . '</div>';
		
		$Page->Foot();
		
		exit;
	}
	else
	{
		$Thread = new Subject($Page, $_GET['id']);
		
		if ($Thread->GetId() == 0)
		{
			$Page->Head('pages.thread.inexistant', true);
			
			echo '<div class="errors">' . $Page->GetLanguage()->GetText('pages.thread.inexistantthr') . '</div>';
			
			$Page->Foot();
		
			exit;
		}
		else
		{
			if ($Page->IsConnected() && (!$Thread->GetLocked() || ($Thread->GetLocked() && $Page->GetPlayer()->GetGrad('webThreadLockPost')->Get())))
			{
				if (isset($_POST['content']))
				{
					if (!$Page->GetFunctions()->CheckSize(trim($_POST['content']), $Page->GetFunctions()->GetConstants()->GetMinReplySize(), $Page->GetFunctions()->GetConstants()->GetMaxReplySize()))
					{
						array_push($Errors, $Page->GetLanguage()->GetText('pages.thread.replynu'));
					}
					else
					{
						$Thread->PostReply($Page->GetPlayer(), $_POST['content']);
						
						array_push($Changes, $Page->GetLanguage()->GetText('pages.thread.posted'));
						
						$_POST['content'] = '';
						
						$Page->GetFunctions()->Redirect('#');
					}
				}
			}
			
			if (!isset($_COOKIE['t' . $Thread->GetId()]))
			{
				$Thread->IncrementViews();
				
				setcookie('t' . $Thread->GetId(), 1, time() + 3600);
			}
			
			$Thread->LoadMessages();
			
			$Page->Head($Thread->GetTitle());
			
			echo '<div class="title">' . $Thread->GetTitle(). '</div>'; 
			
			echo '<div class="shows">';
			
			if (count($Thread->GetElements()) == 0)
			{
				echo '<div class="errors">' . $Page->GetLanguage()->GetText('pages.thread.nomsg') . '</div>';
			}
			else
			{
				foreach ($Thread->GetElements() as $Message)
				{
					$Poster = new Player($Page, $Message->GetPoster());
					
					echo '<div class="msg mzero">';
					
					echo '<table cellpadding="0">';
					
					echo '<tr>';
					
					echo '<td>';
					
					echo '<img height="14px" src="' . $Page->GetFunctions()->GetAvatar($Poster). '" style="vertical-align: middle;"/>';
					
					echo ' <a href="' . $Page->Link($Poster->GetLink()) . '"><span class="msgpost">';
					
					echo $Poster->GetColored();
					
					echo '</span>';
					
					echo '</a>';
					
					echo '</td>';
					
					echo '<td align="right">';
					
					echo utf8_encode(ucfirst(strftime("%A %d %B " . utf8_decode($Page->GetLanguage()->GetText('pages.index.at')). " %H:%M", strtotime($Page->DecaleDate($Message->GetPosted())))));
					
					echo '</td>';
					
					echo '</tr>';
					
					echo '<tr>';
					
					echo '<td align="left" valign="top" colspan="2">';
					
					echo '<div class="shows msgcon">';
					
					echo $Message->GetContent();
					
					echo '</div>';
					
					echo '</td>';
					
					echo '</tr>';
					
					echo '</table>';
					
					echo '</div>';
				}
				
				if ($Page->IsConnected() && (!$Thread->GetLocked() || ($Thread->GetLocked() && $Page->GetPlayer()->GetGrad('webThreadLockPost')->Get())))
				{
					echo '<form method="post" action="">';
					
					echo '<table style="width: 100%;" cellpadding="0" cellspacing="0">';
					
					echo '<tr>';
					
					echo '<td align="center">';
					
					echo '<textarea name="content" class="mzero msgrep" placeholder="' . $Page->GetLanguage()->GetText('pages.thread.reply') . '">';
					
					if (isset($_POST['content']))
					{
						echo $_POST['content'];
					}
					
					echo '</textarea>';
					
					echo '</td>';
					
					echo '</tr>';
					
					echo '<tr>';
					
					echo '<td align="center">';
					
					echo '<input type="submit"/>';
					
					echo '</td>';
					
					echo '</tr>';
					
					echo '</table>';
					
					echo '</form>';
				}
			}
	
			if (isset($Changes))
			{
				echo $Page->GetFunctions()->GetChanges($Changes, true);
			}
			
			if (isset($Errors))
			{
				echo $Page->GetFunctions()->GetErrors($Errors, true);
			}
			
			echo '</div>';
			
			$Page->Foot();
		}
	}
?>