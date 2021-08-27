<?php
	require_once '../includes/page.php';
	
	$Page = new Page('administration', '../');
	
	if (!$Page->IsConnected() || !$Page->GetPlayer()->GetGrad()->Get('webaccessadmin'))
	{
		$Page->GetFunctions()->Redirect('../../');
	}
	
	$Page->GetFunctions()->GetConstants()->LoadLanguages();
	
	$Changes = array();
	
	$Errors = array();
	
	if (isset($_POST['title']) && isset($_POST['lang']) && isset($_POST['content']) && $_POST['title'] != '' && $_POST['lang'] != '' && $_POST['content'] != '' && is_numeric($_POST['lang']))
	{
		$Page->GetFunctions()->Load('language');
		
		$Language = new Language($Page, $_POST['lang']);
		
		if ($Language->GetId() != 0)
		{
			$Title = $_POST['title'];
			
			if ($Page->GetFunctions()->CheckSize($Title, 6, 64))
			{
				$Content = $_POST['content'];
				
				if ($Page->GetFunctions()->CheckSize($Content, 12, 2048))
				{
					$Title = addslashes(htmlentities($Title));
					
					$Content = addslashes(htmlentities($Content));
					
					mysql_query("INSERT INTO news (posted, poster, title, lang, content) VALUES ('" . $Page->GetFunctions()->GetConstants()->GetActualDate() . "', " . $Page->GetPlayer()->GetId() . ", '$Title', " . $Language->GetId() . ", '$Content')");
					
					array_push($Changes, $Page->GetLanguage()->GetText('pages.administration.newsposted'));
					
					$_POST['lang'] = '';
					
					$_POST['title'] = '';
					
					$_POST['content'] = '';
				}
				else
				{
					array_push($Errors, $Page->GetLanguage()->GetText('pages.administration.contentnu'));
				}
			}
			else
			{
				array_push($Errors, $Page->GetLanguage()->GetText('pages.administration.titlenu'));
			}
		}
		else
		{
			array_push($Errors, $Page->GetLanguage()->GetText('pages.administration.inexistantlanguage'));
		}
	}
	
	$Page->Head();
?>
<div class="title"><?php echo $Page->GetLanguage()->GetText('pages.administration.title'); ?></div>
<div class="shows">
	<form method="post" action="">
		<table style="width: 100%;">
			<tr>
				<td><?php echo $Page->GetLanguage()->GetText('pages.administration.table.title'); ?></td>
				<td><input name="title" style="width: 100%;" value="<?php if (isset($_POST['title'])) echo $_POST['title']; ?>"/></td>
				<td align="right" width="100">
					<select name="lang">
					<?php
						foreach ($Page->GetFunctions()->GetConstants()->GetLanguages() as $Language)
						{
							echo '<option value="' . $Language->GetId() . '"';
							
							echo (isset($_POST['lang']) && $_POST['lang'] == $Language->GetId()) ? ' selected="selected"' : '';
							
							echo '>' . $Language->GetFull() . '</option>';
						}
					?>
					</select>
				</td>
			</tr>
			<tr>
				<td colspan="3"><textarea name="content" style="width: 100%;" rows="24"><?php if (isset($_POST['content'])) echo $_POST['content']; ?></textarea></td>
			</tr>
			<tr>
				<td colspan="3" align="center">
					<input type="submit" value="<?php echo $Page->GetLanguage()->GetText('pages.administration.table.submit'); ?>"/>
				</td>
			</tr>
		</table>
	</form>
</div>
<?php
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