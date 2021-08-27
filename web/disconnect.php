<?php
require_once 'includes/page.php';

$Page = new Page();

setcookie('codeA', '', time() - 3600);

setcookie('codeB', '', time() - 3600);

unset($_COOKIE['codeA']);

unset($_COOKIE['codeB']);

unset($_COOKIE);

session_start();

session_destroy();

$Page->GetFunctions()->Redirect();
