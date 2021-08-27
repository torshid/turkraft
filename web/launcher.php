<html>
	<head>
		<title>Launcher</title>
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
		<style>
			body
			{
				background-image: url('includes/images/launcher.png');
				background-color: #222222;
				color: #e0d0d0;
				font-family: arial;
			}
			
			.title
			{
				color: #ffffff;
				font-size: 14px;
			}

			.shows
			{
				font-size: 12px;
				margin-left: 4px;
				margin-right: 4px;
				margin-bottom: 4px;
			}
			
			.newsposter
			{
				font-size: 12px;
			}

			.newsdate
			{
				font-size: 14px;
				color: white;
			}

			.commentpic
			{
				vertical-align: middle;
			}
			
			a
			{
				color: #aaaaff;
				text-decoration: none;
			}
			
			table
			{
			
			}
		</style>	</head>	<body style="border-style: none;"><?php	require_once 'includes/page.php';		$Page = new Page();		$Page->GetFunctions()->Load('nanager');		$Page->GetFunctions()->Load('news');		$Page->GetFunctions()->Load('player');		$Newses = new Nanager($Page, 1);		foreach ($Newses->GetNews() as $Id)	{		$News = new News($Page, $Id, 1);				if ($News->GetDeleted())		{			continue;		}				$Poster = new Player($Page, $News->GetPoster());		echo '<table style="width: 100%;" id="news' . $Id . '">';		echo '<tr>';		echo '<td class="title">' . $News->GetTitle() . ' <a href="news.php?id=' . $Id . '#comments">(' . count($News->GetComments()) . ' <img border="0" src="includes/images/comment.png" class="commentpic"/>)</a> <span class="newsposter">' . $Page->GetLanguage()->GetText('pages.index.by') . ' <a href="' . $Page->Link($Poster->GetLink()) . '">' . $Poster->GetColored() . '</a></span></td>';		echo '<td align="right" class="newsdate">' . utf8_encode(ucfirst(strftime("%A %d %B " . utf8_decode($Page->GetLanguage()->GetText('pages.index.at')). " %H:%M", strtotime($Page->DecaleDate($News->GetPosted()))))) . '</td>';		echo '</tr>';		echo '<tr>';		echo '<td colspan="2"><div class="shows">' . $News->GetContent() . '</div></td>';		echo '</tr>';		echo '</table>';		echo '<br/>';	}?>	</body></html>