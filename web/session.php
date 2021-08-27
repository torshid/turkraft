<?php
if (!isset($_POST['login']) || !isset($_POST['password']) || !isset($_POST['version']) || !is_numeric($_POST['version'])) {
    exit;
} else {
    require_once 'includes/page.php';

    $Page = new Page();

    if ($Page->GetFunctions()->GetConstants()->GetVariables()->Get('online') == 0) {
        echo '3';

        exit;
    }

    if ($Page->GetFunctions()->GetConstants()->GetLauncherVersion() > $_POST['version']) {
        echo '1';

        exit;
    }

    $Page->GetFunctions()->Load('player');

    $Client = new Player($Page, $_POST['login'], md5($_POST['password']));

    if ($Client->GetId() != 0) {
        $Page->GetFunctions()->Load('language');

        $Lang = new Language($Page, $Client->GetLanguage());

        if (isset($_GET['security']) && $_GET['security'] == 'evet') {
            $Client->SetSession(1);
        }

        echo $Page->GetFunctions()->GetConstants()->GetGameVersion() . ':' . rand(1000, 9999) . ':' . $Client->GetName() . ':' . $Client->GetId() . ':' . $Lang->GetDetail() . ':' . $Page->GetFunctions()->GetConstants()->GetServerPort();

        //ex: 13:2465:Torkhan:5:en_US:1453
    } else {
        echo '0';
    }
}
