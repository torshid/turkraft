<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<title>Reports - Turkraft</title>
<meta name="keywords" content="minecraft,turkraft,mmorpg,free,clan,guild,money,territory,whitelist,shop,site,forum,chat"/>
<meta name="description" content="Unique Minecraft server, 100% home made."/>
<meta name="robots" content="index,follow"/>
<meta http-equiv="X-UA-Compatible" content="IE=8"/> 
<meta http-equiv="content-type" content="text/html; charset=utf-8"/>
</head>
<body>
<form method="post" action="">
<textarea name="entity"></textarea>
<input type="submit"/>
</form>
<?php
	if (isset($_POST['entity']))
	{
		echo '<textarea>';
		echo htmlentities($_POST['entity']);
		echo '</textarea>';
	}
?>
</body>
</html>