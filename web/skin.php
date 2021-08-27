<?php
	require_once 'includes/page.php';
	
	$Page = new Page('skin');
	
	if (!$Page->IsConnected())
	{
		$Page->GetFunctions()->Redirect();
	}
	
	$Changes = array();
	
	$Errors = array();
	
	if (!empty($_FILES['file']) && $_FILES['file']['error'] == 0)
	{
		if ($_FILES['file']['type'] == 'image/png' && $_FILES['file']['size'] < 8000)
		{
			if (!(move_uploaded_file($_FILES['file']['tmp_name'], dirname(__FILE__) . '/skins/' . $Page->GetPlayer()->GetName() . '.png')))
			{
				array_push($Errors, $Page->GetLanguage()->GetText('pages.skin.servererror'));
			}
			else
			{
				array_push($Changes, $Page->GetLanguage()->GetText('pages.skin.updateok'));
			}
		}
		else
		{
			array_push($Errors, $Page->GetLanguage()->GetText('pages.skin.minerror'));
		}
	}
	
	$Page->Head();
?>
<div class="title"><?php echo $Page->GetLanguage()->GetText('pages.skin.title'); ?></div>
<div class="shows mzero">
	<?php
		if (isset($Changes))
		{
			echo $Page->GetFunctions()->GetChanges($Changes);
		}
		if (isset($Errors))
		{
			echo $Page->GetFunctions()->GetErrors($Errors);
		}
	?>
	<center>
		<form enctype="multipart/form-data" action="" method="post">
			<input type="hidden" name="MAX_FILE_SIZE" value="30000"/>
			<?php echo $Page->GetLanguage()->GetText('pages.skin.text'); ?> <input name="file" type="file"/>
			<input type="submit"/>
		</form>
		<br/>
		<br/>
		<img src="skins/body.php?n=<?php echo $Page->GetPlayer()->GetName(); ?>"/>
	</center>
</div>
<?php
	$Page->Foot();
?>