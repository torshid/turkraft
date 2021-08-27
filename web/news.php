<?php
	require_once 'includes/page.php';
	
	$Page = new Page();
	
	$Page->GetFunctions()->Load('news');
	$Page->GetFunctions()->Load('player');
	$Page->GetFunctions()->Load('comment');
	
	$Errors = array();
	$Changes = array();
	
	if (isset($_GET['id']))
	{
		$News = new News($Page, $_GET['id'], 2);
		
		if ($News->GetId() != 0 && (!$News->GetDeleted() || ($Page->IsConnected() && $Page->GetPlayer()->GetGrad()->Get('webnewsdelete'))))
		{
			if ($Page->IsConnected())
			{
				if (isset($_POST['content']))
				{
					if (!$Page->GetFunctions()->CheckSize(trim($_POST['content']), $Page->GetFunctions()->GetConstants()->GetMinCommentSize(), $Page->GetFunctions()->GetConstants()->GetMaxCommentSize()))
					{
						array_push($Errors, $Page->GetLanguage()->GetText('pages.news.commentnu'));
					}
					else
					{
						if (count($News->GetComments()) > 0)
						{
							$LastComment = new Comment($Page, current(array_slice($News->GetComments(), -1)));
						}
						
						if ($Page->GetPlayer()->GetGrad()->Get('webalwayscomment') || count($News->GetComments()) == 0 || (isset($LastComment) && $LastComment->GetPoster()->GetId() != $Page->GetPlayer()->GetId()))
						{
							$News->PostComment($Page->GetPlayer(), $_POST['content']);
							
							array_push($Changes, $Page->GetLanguage()->GetText('pages.news.posted'));
							
							$_POST['content'] = '';
						}
					}
				}
			}
			
			if (!isset($_COOKIE['n' . $News->GetId()]))
			{
				$News->IncrementViews();
				
				setcookie('n' . $News->GetId(), 1, time() + 3600);
			}
			
			$Page->Head($News->GetTitle());
			
			$Poster = new Player($Page, $News->GetPoster());
			
			echo '<table style="width: 100%;" id="news' . $News->GetId() . '" cellspacing="0">';
			echo '<tr>';
			echo '<td class="title"><span';
			if ($News->GetDeleted())
			{
				echo ' style="color: red;"';
			}
			echo '>' . $News->GetTitle();
			echo '</span> <a href="' . $Page->Link($News->GetLink()). '#comments">('.count($News->GetComments()).' <img src="' . $Page->Link('includes/images/comment.png'). '" class="commentpic"/>)</a> <span class="newsposter">' . $Page->GetLanguage()->GetText('pages.index.by') . ' <a href="'.$Page->Link($Poster->GetLink()).'">'.$Poster->GetColored().'</a></span>';
			
			if ($Page->IsConnected())
			{
				if ($Page->GetPlayer()->GetGrad()->Get('webnewsedit'))
				{
					echo ' <a href="' . $Page->Link('edit.php?id=' . $News->GetId()) . '"><img src="' . $Page->Link('includes/images/edit.png', false) . '" class="commentpic"/></a>';
				}
				
				if ($Page->GetPlayer()->GetGrad()->Get('webnewsdelete'))
				{
					if ($News->GetDeleted())
					{
						echo ' <a href="' . $Page->Link('delete.php?type=0&id=' . $News->GetId()) . '"><img src="' . $Page->Link('includes/images/delete.png', false) . '" class="commentpic"/></a>';
					}
					else
					{
						echo ' <a href="' . $Page->Link('delete.php?type=1&id=' . $News->GetId()) . '"><img src="' . $Page->Link('includes/images/delete.png', false) . '" class="commentpic"/></a>';
					}
				}
			}
			
			echo '<td align="right" class="newsdate">'.utf8_encode(ucfirst(strftime("%A %d %B " . utf8_decode($Page->GetLanguage()->GetText('pages.index.at')). " %H:%M", strtotime($Page->DecaleDate($News->GetPosted()))))).'</td>';
			echo '</tr>';
			echo '<tr>';
			echo '<td colspan="2"><div class="shows">' . $News->GetContent() . '</div></td>';
			echo '</tr>';
			echo '</table>';
			echo '<hr id="comments"/>';
			
			echo '<div>';
			
			if (count($News->GetComments()) == 0)
			{
				echo '<center>' . $Page->GetLanguage()->GetText('pages.news.nocomment') . '</center>';
			}
			else
			{
				$News->LoadComments();
				
				foreach ($News->GetComments() as $Comment)
				{
					$User = new Player($Page, $Comment->GetPoster()->GetId());
					
					echo '<div class="acomment" id="comment'.$Comment->GetId().'">';
					echo '<div class="commenttop">';
					echo $Page->GetLanguage()->GetText('pages.index.by') . ' <a href="' . $Page->Link($Comment->GetPoster()->GetLink()) . '">'.$Comment->GetPoster()->GetColored().'</a>';
					echo '<div class="commentdate">'.utf8_encode(ucfirst(strftime("%A %d %B " . utf8_decode($Page->GetLanguage()->GetText('pages.index.at')). " %H:%M", strtotime($Page->DecaleDate($Comment->GetPosted()))))).'</div>';
					echo '</div>';
					echo '<div class="shows comment">' . $Comment->GetContent() . '</div>';
					echo '</div>';
				}
			}
			
			echo '</div>';
			echo '<div id="postcom">';
			
			if ($Page->IsConnected())
			{
				if ($Page->GetPlayer()->GetGrad()->Get('webalwayscomment') || count($News->GetComments()) == 0 || current(array_slice($News->GetComments(), -1))->GetPoster()->GetId() != $Page->GetPlayer()->GetId())
				{
					echo '<div class="title">' . $Page->GetLanguage()->GetText('pages.news.postcomment') . '</div>';
					echo '<form method="post" action="#postcom">';
					echo '<table style="width: 100%;">';
					echo '<tr><td><textarea name="content" style="width: 100%; height: 64px;" placeholder="' . $Page->GetLanguage()->GetText('pages.news.writecomment') . '">';
					if (isset($_POST['content']))
					{
						echo $_POST['content'];
					}
					echo '</textarea></td></tr>';
					echo '<tr><td align="center"><input type="submit" value="' . $Page->GetLanguage()->GetText('pages.news.submit') . '"/></td></tr>';
					echo '</table>';
					echo '</form>';
				}
				else if (!isset($_POST['content']))
				{
					echo '<center>' . $Page->GetLanguage()->GetText('pages.news.needtowait') . '</center>';
				}
			}
			
			echo '</div>';
		}
		else
		{
			$Page->Head($Page->GetLanguage()->GetText('pages.news.inexistant'));
			
			array_push($Errors, $Page->GetLanguage()->GetText('pages.news.inexistant.text'));
		}
	}
	else
	{
		$Page->Head($Page->GetLanguage()->GetText('pages.news.inexistant'));
		
		array_push($Errors, $Page->GetLanguage()->GetText('pages.news.inexistant.text'));
	}
	
	if (isset($Changes))
	{
		echo $Page->GetFunctions()->GetChanges($Changes, true);
	}
	
	if (isset($Errors))
	{
		echo $Page->GetFunctions()->GetErrors($Errors, true);
	}
			
	$Page->Foot();
?>