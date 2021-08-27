<?php
	$filename = 'char.png';
	
	if (isset($_GET['n']) && file_exists($_GET['n'] . '.png'))
	{
		$filename = $_GET['n'] . '.png';
	}

	$offsetX = 8;
	
	$offsetY = 8;

	$image = imagecreatefrompng($filename);
	
	$new_image = imagecreatetruecolor(8, 8);
	
	imagecopy($new_image, $image, 0, 0, $offsetX, $offsetY, 8, 8);
	
	$new_image2 = imagecreatetruecolor(32, 32);
	
	imagealphablending($new_image2, true);
	
	imagesavealpha($new_image2, true);
	
	imagecopyresized($new_image2, $new_image, 0, 0, 0, 0, 128, 128, 32, 32);
	
	header('Content-Type: image/png');
	
	imagepng($new_image2);
	
	imagedestroy($new_image2);
?>