<?php
	if(isset($_POST['email'])){
		$token = $_POST['token'];
		$password = $_POST['password'];
		$encrypt = sha1($password);
		
		// include db connect class
	    require_once __DIR__ . '/db_connect.php';
	 
	    // connecting to db
	    $db = new DB_CONNECT();
		//include('myConnection.php');

		$query = "Update tbl_users set password = '$encrypt' where email = (Select email from forgot_password where token = '$token')";
		$result = mysql_query($query);
		echo 'done';
		//echo $encrypt. " ". $email;
	}
	else
	{
		//header('location:errorpage.php');
	}

?>