<?php
	require_once 'includes/page.php';
	
	$Page = new Page();
	
	if (!$Page->IsConnected())
	{
		$Page->GetFunctions()->Redirect();
	}
	
	if (!isset($_GET['id']) || !is_numeric($_GET['id']))
	{
		$Page->GetFunctions()->Redirect();
	}
	
	if (!$Page->GetPlayer()->GetGrad()->Get('webnewsedit'))
	{
		$Page->GetFunctions()->Redirect();
	}
	
	$Page->GetFunctions()->Load('news');
	
	$News = new News($Page, $_GET['id']);
	
	if ($News->GetId() == 0)
	{
		$Page->GetFunctions()->Redirect();
	}
	
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
					
					$News->MakeEdition($Title, $Content, $Language->GetId());
					
					if (isset($_GET['page']))
					{
						$Page->GetFunctions()->Redirect('?p=' . $_GET['page']);
					}
					else
					{
						$Page->GetFunctions()->Redirect($News->GetLink());
					}
					
					$_POST['lang'] = '';
					
					$_POST['title'] = '';
					
					$_POST['content'] = '';
				}
				else
				{
					array_push($Errors, $Page->GetLanguage()->GetText('pages.edit.contentnu'));
				}
			}
			else
			{
				array_push($Errors, $Page->GetLanguage()->GetText('pages.edit.titlenu'));
			}
		}
		else
		{
			array_push($Errors, $Page->GetLanguage()->GetText('pages.edit.inexistantlanguage'));
		}
	}
	
	$Page->GetFunctions()->GetConstants()->LoadLanguages();
	
	$Page->Head($Page->GetLanguage()->GetText('titles.edit', array($News->GetTitle())), false);
?>
<div class="title"><?php echo $Page->GetLanguage()->GetText('pages.edit.title'); ?></div>
<div class="shows">
	<form method="post" action="">
		<table style="width: 100%;">
			<tr>
				<td><?php echo $Page->GetLanguage()->GetText('pages.edit.text'); ?></td>
				<td><input name="title" style="width: 100%;" value="<?php if (isset($_POST['title'])) echo $_POST['title']; else echo $News->GetTitle();?>"/></td>
				<td align="right" width="100">
					<select name="lang">
					<?php
						foreach ($Page->GetFunctions()->GetConstants()->GetLanguages() as $Language)
						{
							echo '<option value="' . $Language->GetId() . '"';
							
							if (isset($_POST['lang']) && $_POST['lang'] == $Language->GetId()) echo ' selected="selected"'; else if ($News->GetLang() == $Language->GetId()) echo ' selected="selected"';
							
							echo '>' . $Language->GetFull() . '</option>';
						}
					?>
					</select>
				</td>
			</tr>
			<tr>
				<td colspan="3"><textarea name="content" style="width: 100%;" rows="24"><?php if (isset($_POST['content'])) echo $_POST['content']; else echo $News->GetContent();?></textarea></td>
			</tr>
			<tr>
				<td colspan="3" align="center">
					<input type="submit" value="<?php echo $Page->GetLanguage()->GetText('pages.edit.submit'); ?>"/>
				</td>
			</tr>
		</table>
	</form>
</div>
<?php
	if (isset($Errors))
	{
		echo $Page->GetFunctions()->GetErrors($Errors, true);
	}
	
	$Page->Foot();
?>