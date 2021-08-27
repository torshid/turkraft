<?php
	require_once 'includes/page.php';
	
	$Page = new Page('connect');
	
	if ($Page->IsConnected())
	{
		$Page->GetFunctions()->Redirect();
	}
	
	if (isset($_POST['name']) && isset($_POST['password']))
	{
		$Errors = array();
		
		$Name = $_POST['name'];
		$Password = $_POST['password'];
		
		if (!$Page->GetFunctions()->CheckSize($Name, $Page->GetFunctions()->GetConstants()->GetMinNameSize(), $Page->GetFunctions()->GetConstants()->GetMaxNameSize()) || !preg_match($Page->GetFunctions()->GetConstants()->GetNameRegex(), $Name))
		{
			array_push($Errors, $Page->GetLanguage()->GetText('pages.connect.wrong.username', array($Page->GetFunctions()->GetConstants()->GetMinNameSize(), $Page->GetFunctions()->GetConstants()->GetMaxNameSize())));
		}
		
		if (!$Page->GetFunctions()->CheckSize($Password, $Page->GetFunctions()->GetConstants()->GetMinPasswordSize(), $Page->GetFunctions()->GetConstants()->GetMaxPasswordSize()))
		{
			array_push($Errors, $Page->GetLanguage()->GetText('pages.register.wrong.password', array($Page->GetFunctions()->GetConstants()->GetMinPasswordSize(), $Page->GetFunctions()->GetConstants()->GetMaxPasswordSize())));
		}
		
		if (count($Errors) == 0)
		{
			$Password = md5($Password);
			
			$Page->GetFunctions()->Load('player');
			
			$Me = new Player($Page, $Name, $Password);
			
			if ($Me->GetId() == 0)
			{
				array_push($Errors, $Page->GetLanguage()->GetText('pages.connect.wrong.incorrect'));
			}
			else
			{
				if ($Me->GetBanned())
				{
					array_push($Errors, $Page->GetLanguage()->GetText('pages.connect.wrong.banned'));
				}
				else
				{
					$Page->GetFunctions()->LoginCookie($Me->GetVirtual(), $Password);
					
					$Page->GetFunctions()->Redirect();
				}
			}
		}
	}
	
	$Page->Head();
?>
<div class="title"><?php echo $Page->GetLanguage()->GetText('pages.connect.title'); ?></div>
<div class="shows">
	<?php
		if (isset($Errors))
		{
			echo $Page->GetFunctions()->GetErrors($Errors);
		}
	?>
	<form method="post" action="">
		<table>
			<tr>
				<td>
					<?php echo $Page->GetLanguage()->GetText('pages.connect.account'); ?>
				</td>
				<td align="right">
					<input name="name" maxlength="<?php echo $Page->GetFunctions()->GetConstants()->GetMaxNameSize(); ?>" value="<?php if (isset($_POST['name'])) echo $_POST['name']; ?>" style="width: 100%;"/>
				</td>
			</tr>
			<tr>
				<td>
					<?php echo $Page->GetLanguage()->GetText('pages.connect.password'); ?>
				</td>
				<td align="right">
					<input name="password" maxlength="<?php echo $Page->GetFunctions()->GetConstants()->GetMaxPasswordSize(); ?>" type="password" style="width: 100%;"/>
				</td>
			</tr>
			<tr>
				<td colspan="2" align="center">
					<input type="submit" value="<?php echo $Page->GetLanguage()->GetText('pages.connect.submit'); ?>"/>
				</td>
			</tr>
		</table>
	</form>
	</div>
<?php
	
	$Page->Foot();
?>