<?php
	require_once __DIR__ . '/db_connect.php';
 	session_start();
 	$_SESSION["fromCheckToken"] = "true";

    // connecting to db
    $db = new DB_CONNECT();

    $response = array();

    if (isset($_GET["token"])){
    	$token = $_GET["token"];
    	date_default_timezone_set('Asia/Manila');
		
		$dateNow = date('m/d/Y h:i a', time());

		//converting phptime to datetime
		$phpdate = strtotime($dateNow);
		$dateNowFinal = date( 'Y-m-d H:i:s', $phpdate );

		$result = mysql_query("SELECT * from forgot_password where token = '$token'");
		$num = mysql_num_rows($result);
		if ($num >0 ){
			//token exists
			echo "token exists<br>";
			$result2 = mysql_query("SELECT * from forgot_password where token = '$token' AND token_expiry > '$dateNowFinal'");
			$num2 = mysql_num_rows($result2);
			if ($num2 > 0){
				//token is valid and active
				echo "token is valid and active";
				$_SESSION["tokenStatus"] = "active";
				$_SESSION["token"]= $token;
				header('Location: http://192.168.8.120/nucleus/pages/resetpassword_page.php');   
			}else{
				//token is dead as fuck
				echo "token is dead";
				$_SESSION["tokenStatus"] = "expired";
				header('Location: http://192.168.8.120/nucleus/pages/retrievepassword_page.php');   
			}

		}else{
			//no token found
			echo "no token found";
			$_SESSION["tokenStatus"] = "none";
			header('Location: http://192.168.8.120/nucleus/pages/retrievepassword_page.php');  
		}
    }

?>