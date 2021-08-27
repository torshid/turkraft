<?php
class Functions
{
    private $Page;

    private $Constants;

    private $Connection;

    public function __construct($Page, $Connect = false)
    {
        $this->Page = $Page;

        $this->Load('parameters');

        $this->Constants = new Parameters($this->Page);

        if ($Connect) {
            $this->Connect();

            if ($this->Connection) {
                $this->Load('variables');

                $this->Constants->LoadVariables();
            }
        }
    }

    public function Load($Name)
    {
        require_once 'classes/' . $Name . '.php';
    }

    public function Start()
    {
        if (session_id()) {
            return;
        }

        session_start();
    }

    public function Destroy()
    {
        $this->Start();

        session_destroy();
    }

    public function Connect($Constants = null)
    {
        if ($Constants == null) {
            $Constants = $this->Constants;
        }

        $this->Connection = mysql_connect('localhost', $Constants->GetLogin(), $Constants->GetPassword());

        $this->Select($this->Constants->GetDatabase());
    }

    public function Select($Database)
    {
        mysql_select_db($Database, $this->Connection);
    }

    public function Disconnect()
    {
        if (!$this->Connection) {
            return;
        }

        mysql_close($this->Connection);
    }

    public function Redirect($Link = './', $More = '')
    {
        if (isset($this->Page)) {
            header('Location: ' . $this->Page->Link($Link) . $More);

            exit;
        }

        header('Location: ' . $Link . $More);

        exit;
    }

    public function GetConstants()
    {
        return $this->Constants;
    }

    public function CheckSize($Text, $Min, $Max)
    {
        return !(strlen($Text) < $Min || strlen($Text) > $Max);
    }

    public function Error($Type)
    {
        header("HTTP/1.1 $Type Not Found");

        echo file_get_contents("includes/errors/$Type.php");

        exit;
    }

    public function GetErrors($Errors, $Center = false)
    {
        $Result = '';

        if (isset($Errors) && count($Errors) > 0) {
            if ($Center) {
                $Result .= '<center>';
            }

            $Result .= '<div class="errors">';

            foreach ($Errors as $change) {
                $Result .= $change . '<br/>';
            }

            $Result .= '</div>';

            if ($Center) {
                $Result .= '</center>';
            }
        }

        return $Result;
    }

    public function GetChanges($Changes, $Center = false)
    {
        $Result = '';

        if (isset($Changes) && count($Changes) > 0) {
            if ($Center) {
                $Result .= '<center>';
            }

            $Result .= '<div class="changes">';

            foreach ($Changes as $Change) {
                $Result .= $Change . '<br/>';
            }

            $Result .= '</div>';

            if ($Center) {
                $Result .= '</center>';
            }
        }

        return $Result;
    }

    public function IsBannedIP()
    {
        if (!$this->Connection) {
            return false;
        }

        $IP = $this->GetIP();

        $Query = mysql_query("SELECT id FROM wan WHERE ip = '$IP'");

        if (!mysql_num_rows($Query)) {
            return false;
        }

        return true;
    }

    public function GetIP()
    {
        if (isset($_SERVER['HTTP_X_FORWARDED_FOR'])) {
            return $_SERVER['HTTP_X_FORWARDED_FOR'];
        } else {
            return $_SERVER['REMOTE_ADDR'];
        }
    }

    public function BanIP($IP = null, $Reason = '')
    {
        if (!isset($IP) || $IP == '') {
            $IP = $this->GetIP();
        }

        $Reason = mysql_real_escape_string($Reason);

        mysql_query("INSERT INTO wan (ip, date, reason) VALUES ('$IP', '" . $this->Constants->GetActualDate() . "', '$Reason')");
    }

    public function LoginCookie($Virtual, $Password)
    {
        setcookie('codeA', $Virtual, (time() + 3600 * 12 * 7));

        setcookie('codeB', $Password, (time() + 3600 * 12 * 7));
    }

    public function SecondsFormat($Seconds)
    {
        $Hours = floor($Seconds / (60 * 60));

        $DivisorMinute = $Seconds % (60 * 60);

        $Minutes = floor($DivisorMinute / 60);

        $DivisorSecond = $DivisorMinute % 60;

        $Seconds = ceil($DivisorSecond);

        $Values = array("h" => (int) $Hours, "m" => (int) $Minutes, "s" => (int) $Seconds);

        return $Values;
    }

    public function GetAvatar($Player)
    {
        return $this->Page->Link('skins/head.php?n=' . $Player->GetName(), false);
    }

    public function SetLink($Text)
    {
        $Codes = array(
            '#\[url\](http://.*?)\[/url]#si',
            '#\[url=(http://.*?)\](.*?)\[/url]#si',
        );

        $Replacements = array(
            '<a href="\\1" target="_blank">\\1</a>',
            '<a href="\\1" target="_blank">\\2</a>',
        );

        return preg_replace($Codes, $Replacements, $Text);
    }

    public function SetBBCode($Text)
    {
        $Replacements = array(
            '<b>\\1</b>',
            '<i>\\1</i>',
            '<u>\\1</u>',
            '<strike>\\1</strike>',
            '<center>\\1</center>',
            '<font color="\\1">\\2</font>',
            '<blink>\\1</blink>',
            '<marquee>\\1</marquee>',
            '<div class="code"><div class="codelanguage">Code: \\1</div><div class="codecontent">\\2</div></div>',
            '<div class="code"><div class="codelanguage">Code:</div><div class="codecontent">\\1</div></div>',
            '<div class="quote"><div class="quoteauthor">Quote: \\1</div><div class="quotecontent">\\2</div></div>',
            '<div class="quote"><div class="quoteauthor">Quote:</div><div class="quotecontent">\\1</div></div>',
            '<img src="\\1">',
            '<a href="mailto:\\1">\\1</a>',
            '<div class="shows">\\1</div>',
            '<div class="tutit">\\1</div>',
        );

        return preg_replace($this->Constants->GetBBCode(), $Replacements, $Text);
    }

    public function RemoveBBCode($Text)
    {
        $Replacements = array();

        for ($i = 0; $i < count($this->Constants->GetBBCode()); $i++) {
            array_push($Replacements, '');
        }

        return preg_replace($this->Constants->GetBBCode(), $Replacements, $Text);
    }

    public function TransformLink($Text)
    {
        return preg_replace("#((http|https|ftp)://(\S*?\.\S*?))(\s|\;|\)|\]|\[|\{|\}|,|\"|'|:|\<|$|\.\s)#ie", "'<a href=\"$1\" target=\"_blank\">$3</a>$4'", $Text);
    }

    public function LimitRepetition($Text, $Limit)
    {
        return preg_replace('/(\S)\1{' . $Limit . ',}/', '\1', $Text);
    }

    /*public function UserBBCode($Text)
    {
    $Replacements = array(
    '/([\w\-\d]+\@[\w\-\d]+\.[\w\-\d]+)/' => '<a href="mailto:$1">$1</a>',
    '/\[@([^\]]*)\]/' => '<b class="reply">@$1</b>',
    );

    return preg_replace(array_keys($Replacements), array_values($Replacements), $Text);
    }*/

    public function CountSQL($Rows, $Table, $Syntax)
    {
        return mysql_num_rows(mysql_query("SELECT '$Rows' FROM $Table WHERE $Syntax"));
    }

    public function CountComments($Player)
    {
        return $this->CountSQL('id', 'comments', 'poster = ' . $Player->GetId());
    }

    public function CountReports($Player)
    {
        return $this->CountSQL('id', 'reports', 'poster = ' . $Player->GetId());
    }

    public function IsTSOnline()
    {
        global $errno, $errstr;

        $host = "127.0.0.1";

        $port = "10011";

        $timeout = "0";

        $socket = @fsockopen($host, $port, $errno, $errstr, $timeout);

        $online = true;

        if (!$socket) {
            $online = false;
        } else {
            fputs($socket, "quit");

            fclose($socket);
        }

        return $online;
    }
}

function Register($Name, $Password, $Retype)
{
    if (defined('ME')) {
        exit;
    }

    $Name = addslashes(htmlentities($Name));
    $Password = $Password;
    $Retype = $Retype;

    if (!CheckSize($Name, MinNameSize, MaxNameSize)) //nom de compte trop court ou trop long
    {
        return 2;
    }

    if (!CheckSize($Password, MinPasswordSize, MaxPasswordSize)) //mot de passe trop court ou trop long
    {
        return 3;
    }

    if ($Password != $Retype) //le retyping ne correspond pas
    {
        return 4;
    }

    if (!preg_match(NameRegex, $Name)) //caracteres speciaux interdits
    {
        return 5;
    }

    $Password = md5($Password);

    if (!mysql_query("INSERT INTO accounts (name, password, register, last) VALUES ('$Name', '$Password', '" . ActualDate . "', '" . ActualDate . "')")) //nom de compte existant? autre erreur?
    {
        return 6;
    }

    UpdateSessionID(mysql_insert_id());

    return 1;
}

function Login($Name, $Password)
{
    $Name = mysql_real_escape_string($Name);

    $Password = md5($Password);

    $Query = mysql_query("SELECT * FROM accounts WHERE name = '$Name' AND password = '$Password'");

    if (!mysql_num_rows($Query)) {
        return false;
    }

    UpdateSessionID(mysql_result($Query, 0));

    return true;
}

function Account($ID)
{
    $ID = mysql_real_escape_string($ID);

    $Query = mysql_query('SELECT * FROM accounts WHERE id = $id');

    if (!mysql_num_rows($Query)) {
        return false;
    }

    $Rows = mysql_fetch_row($Query);

    $Account['ID'] = $Rows[0];
    $Account['Name'] = $Rows[1];
    $Account['Register'] = $Rows[3];
    $Account['Last'] = $Rows[4];
    $Account['Access'] = $Rows[5];

    return $Account;
}

function UpdateLastAction($id)
{
    mysql_query("UPDATE accounts SET last = '" . ActualDate . "' WHERE id = $id");
}

function UpdateSessionID($ID)
{
    $_SESSION['ID'] = $ID;

    define('ME', $ID);
}

function CheckSize($Text, $Min, $Max)
{
    return !(strlen($Text) < $Min || strlen($Text) > $Max);
}
