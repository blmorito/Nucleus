<?php

    require_once __DIR__ . '/db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
	$bits = 7;

	$token = bin2hex(openssl_random_pseudo_bytes($bits));
	date_default_timezone_set('Asia/Manila');
	$wew = date('m/d/Y h:i a', time()-86400);
	
	$phpdate = strtotime($wew);
	$phpdatex = date( 'Y-m-d H:i:s', $phpdate );
	echo $phpdatex;
	//echo (date('m/d/Y h:i a', time()-86400));
	$email = "b@y.c";

	// $request = mysql_query("INSERT INTO forgot_password (forgot_email,token,token_made) VALUES ('$email','$token',now())");
	
?>