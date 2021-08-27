<?php
	require_once 'includes/page.php';
	
	$Page = new Page('register');
	
	if ($Page->IsConnected())
	{
		$Page->GetFunctions()->Redirect();
	}
	
	$Page->GetFunctions()->GetConstants()->LoadLanguages();
	
	$Page->GetFunctions()->GetConstants()->LoadCountries();
	
	if (isset($_POST['name']) && isset($_POST['mail']) && isset($_POST['password']) && isset($_POST['repassword']) && isset($_POST['language']) && isset($_POST['country']))
	{
		$Errors = array();
		
		$Name = $_POST['name'];
		$Mail = $_POST['mail'];
		$Password = $_POST['password'];
		$Retype = $_POST['repassword'];
		$Language = $_POST['language'];
		$Country = $_POST['country'];
		
		if (!is_numeric($Language) || !is_numeric($Country))
		{
			$Page->GetFunctions()->BanIP('Language or country are not numbers when registering');
			
			$Page->GetFunctions()->Redirect();
		}
		
		if (!$Page->GetFunctions()->CheckSize($Name, $Page->GetFunctions()->GetConstants()->GetMinNameSize(), $Page->GetFunctions()->GetConstants()->GetMaxNameSize()))
		{
			array_push($Errors, $Page->GetLanguage()->GetText('pages.register.wrong.namenu', array($Page->GetFunctions()->GetConstants()->GetMinNameSize(), $Page->GetFunctions()->GetConstants()->GetMaxNameSize())));
		}
		else if (!preg_match($Page->GetFunctions()->GetConstants()->GetNameRegex(), $Name))
		{
			array_push($Errors, $Page->GetLanguage()->GetText('pages.register.wrong.namesy'));
		}
		
		if (!$Page->GetFunctions()->CheckSize($Password, $Page->GetFunctions()->GetConstants()->GetMinPasswordSize(), $Page->GetFunctions()->GetConstants()->GetMaxPasswordSize()))
		{
			array_push($Errors, $Page->GetLanguage()->GetText('pages.register.wrong.passnu', array($Page->GetFunctions()->GetConstants()->GetMinPasswordSize(), $Page->GetFunctions()->GetConstants()->GetMaxPasswordSize())));
		}
		else if ($Password != $Retype)
		{
			array_push($Errors, $Page->GetLanguage()->GetText('pages.register.wrong.notcorr'));
		}
		
		if (!$Page->GetFunctions()->CheckSize($Mail, $Page->GetFunctions()->GetConstants()->GetMinMailSize(), $Page->GetFunctions()->GetConstants()->GetMaxMailSize()))
		{
			array_push($Errors,  $Page->GetLanguage()->GetText('pages.register.wrong.mailnu', array($Page->GetFunctions()->GetConstants()->GetMinMailSize(), $Page->GetFunctions()->GetConstants()->GetMaxMailSize())));
		}
		else if (!preg_match($Page->GetFunctions()->GetConstants()->GetMailRegex(), $Mail))
		{
			array_push($Errors, $Page->GetLanguage()->GetText('pages.register.wrong.mailsy'));
		}
		
		if (count($Errors) == 0)
		{
			$Password = md5($Password);
			
			if (!$Page->GetFunctions()->GetConstants()->LanguageExists($Language))
			{
				$Page->GetFunctions()->BanIP('Inexistant language id when registering');
				
				$Page->GetFunctions()->Redirect();
			}
			else if (!$Page->GetFunctions()->GetConstants()->CountryExists($Country))
			{
				$Page->GetFunctions()->BanIP('Inexistant country id when registering');
				
				$Page->GetFunctions()->Redirect();
			}
			
			$Time = $Page->GetFunctions()->GetConstants()->GetActualDate();
			
			$Virtual = rand(100000, 99999999);
			
			$Grad = 3;
			
			$Money = 50;
			
			if (!mysql_query("INSERT INTO accounts (name, password, register, connection, virtual, language, country, grad, mail, money) VALUES ('$Name', '$Password', '$Time', '$Time', $Virtual, $Language, $Country, $Grad, '$Mail', $Money)"))
			{
				array_push($Errors, $Page->GetLanguage()->GetText('pages.register.cant'));
			}
			else
			{
				$Page->GetFunctions()->LoginCookie($Virtual, $Password);
			
				unset($_GET['s']);
				
				$Page->GetFunctions()->Redirect();
			}
		}
	}
	
	$Page->Head();
?>
<div class="title"><?php echo $Page->GetLanguage()->GetText('pages.register.title'); ?></div>
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
					<?php echo $Page->GetLanguage()->GetText('pages.register.account'); ?>
				</td>
				<td align="right">
					<input name="name" maxlength="<?php echo $Page->GetFunctions()->GetConstants()->GetMaxNameSize(); ?>" value="<?php if (isset($_POST['name'])) echo $_POST['name']; ?>" style="width: 100%;"/>
				</td>
			</tr>
			<tr>
				<td>
					<?php echo $Page->GetLanguage()->GetText('pages.register.mail'); ?>
				</td>
				<td align="right">
					<input name="mail" maxlength="<?php echo $Page->GetFunctions()->GetConstants()->GetMaxMailSize(); ?>" value="<?php if (isset($_POST['mail'])) echo $_POST['mail']; ?>" style="width: 100%;"/>
				</td>
			</tr>
			<tr>
				<td>
					<?php echo $Page->GetLanguage()->GetText('pages.register.password'); ?>
				</td>
				<td align="right">
					<input name="password" maxlength="<?php echo $Page->GetFunctions()->GetConstants()->GetMaxPasswordSize(); ?>" type="password" style="width: 100%;"/>
				</td>
			</tr>
			<tr>
				<td>
					<?php echo $Page->GetLanguage()->GetText('pages.register.repass'); ?>
				</td>
				<td align="right">
					<input name="repassword" maxlength="<?php echo $Page->GetFunctions()->GetConstants()->GetMaxPasswordSize(); ?>" type="password" style="width: 100%;"/>
				</td>
			</tr>
			<tr>
				<td>
					<?php echo $Page->GetLanguage()->GetText('pages.register.language'); ?>
				</td>
				<td align="right">
					<select name="language" style="width: 100%;">
					<?php
						foreach ($Page->GetFunctions()->GetConstants()->GetLanguages() as $Language)
						{
							echo '<option value="' . $Language->GetId() . '"';
							
							echo (isset($_POST['language']) && $_POST['language'] == $Language->GetId()) ? ' selected="selected"' : '';
							
							echo '>' . $Language->GetFull() . '</option>';
						}
					?>
					</select>
				</td>
			</tr>
			<tr>
				<td>
					<?php echo $Page->GetLanguage()->GetText('pages.register.country'); ?>
				</td>
				<td align="right">
					<select name="country" style="width: 100%;">
					<?php
						foreach ($Page->GetFunctions()->GetConstants()->GetCountries() as $Country)
						{
							echo '<option value="' . $Country->GetId() . '"';
							
							echo (isset($_POST['country']) && $_POST['country'] == $Country->GetId()) ? ' selected="selected"' : '';
							
							echo '>' . $Country->GetName() . '</option>';
						}
					?>
					</select>
				</td>
			</tr>
			<tr>
				<td colspan="2" align="center">
					<input type="submit" value="<?php echo $Page->GetLanguage()->GetText('pages.register.submit'); ?>"/>
				</td>
			</tr>
		</table>
	</form>
</div>
<?php
	$Page->Foot();
?>