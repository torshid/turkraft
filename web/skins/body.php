<?php
	$filename = 'char.png';
	
	if (isset($_GET['n']) && file_exists($_GET['n'].'.png'))
	{
		$filename = $_GET['n'] . '.png';
	}

	$image = imagecreatefrompng($filename);
	
	$nwidth = 64 * 8;
	
	$nheight = 32 * 8;
	
	$newimage = imagecreatetruecolor($nwidth, $nheight);
	
	imagealphablending($newimage, true);
	
	imagesavealpha($newimage, true);
	
	imagecopyresized($newimage, $image, 0, 0, 0, 0, $nwidth, $nheight, 64, 32);
	
	header('Content-Type: image/png');
	
	imagepng($newimage);
	
	imagedestroy($newimage);
?>