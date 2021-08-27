<?php
	require_once 'includes/page.php';
	
	$Page = new Page('home');
	
	$Number = 1;
	
	if (isset($_GET['p']) && is_numeric($_GET['p']))
	{
		$Number = $_GET['p'];
	}
	
	$Page->GetFunctions()->Load('nanager');
	
	$Newses = new Nanager($Page, $Number);
	
	$Page->Head();
	
	if ($Newses->GetTotal() == 0)
	{
		echo $Page->GetLanguage()->GetText('pages.index.nonewsyet');
	}
	else
	{
		$Page->GetFunctions()->Load('player');
		
		$Page->GetFunctions()->Load('news');
		
		foreach ($Newses->GetNews() as $Id)
		{
			$News = new News($Page, $Id, 1);
			
			if ($News->GetDeleted() && (!$Page->IsConnected() || !$Page->GetPlayer()->GetGrad()->Get('webnewsdelete')))
			{
				continue;
			}
			
			$Poster = new Player($Page, $News->GetPoster());
			
			echo '<table style="width: 100%;" id="news' . $Id . '" cellspacing="0">';
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
					echo ' <a href="' . $Page->Link('edit.php?page=' . $Number . '&id=' . $News->GetId()) . '"><img src="' . $Page->Link('includes/images/edit.png', false) . '" class="commentpic"/></a>';
				}
				
				if ($Page->GetPlayer()->GetGrad()->Get('webnewsdelete'))
				{
					if ($News->GetDeleted())
					{
						echo ' <a href="' . $Page->Link('delete.php?type=0&page=' . $Number . '&id=' . $News->GetId()) . '"><img src="' . $Page->Link('includes/images/delete.png', false) . '" class="commentpic"/></a>';
					}
					else
					{
						echo ' <a href="' . $Page->Link('delete.php?type=1&page=' . $Number . '&id=' . $News->GetId()) . '"><img src="' . $Page->Link('includes/images/delete.png', false) . '" class="commentpic"/></a>';
					}
				}
			}
			
			echo '</td><td align="right" class="newsdate">'.utf8_encode(ucfirst(strftime("%A %d %B " . utf8_decode($Page->GetLanguage()->GetText('pages.index.at')). " %H:%M", strtotime($Page->DecaleDate($News->GetPosted()))))).'</td>';
			echo '</tr>';
			echo '<tr>';
			echo '<td colspan="2"><div class="shows">';
			if (strlen($News->GetContent()) > $Page->GetFunctions()->GetConstants()->GetNewsLength())
			{
				echo substr($News->GetContent(), 0, $Page->GetFunctions()->GetConstants()->GetNewsLength()) . ' <b><a href="' . $Page->Link($News->GetLink()) . '">[...]</a></b>';
			}
			else
			{
				echo $News->GetContent();
			}
			echo '</div></td>';
			echo '</tr>';
			echo '</table>';
			echo '<br/>';
		}
		
		echo '<center>';
		
		for ($i = 1; $i <= $Newses->GetPages(); $i++)
		{
			if ($i == $Newses->GetNumber())
			{
				echo "<b>$i</b> ";
			}
			else
			{
				echo '<a href="' . $Page->Link('?p=' . $i) . '">'.$i.'</a> ';
			}
		}
		
		echo '</center>';
	}
	
	$Page->Foot();
?>