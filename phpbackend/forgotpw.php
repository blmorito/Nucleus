<?php

    require_once __DIR__ . '/db_connect.php';
 	require '../PHPMailer/PHPMailerAutoload.php';
    // connecting to db
    $db = new DB_CONNECT();

    $response = array();
    $current_ip = '192.168.8.120';
	
  	if (isset($_POST["email"])){

  		$email = $_POST["email"];
  		$response['test'] = $email;
  		  //creating token
		$result = mysql_query("SELECT * from tbl_users where email = '$email'");
		$num = mysql_num_rows($result);


		if ($num > 0){
				$result2 = mysql_query("SELECT full_name from tbl_usersinfo WHERE user_id = (SELECT user_id from tbl_users WHERE email = '$email')");
				$row2 = mysql_fetch_array($result2);
				$name = $row2["full_name"];
				$bits = 10;
				$token = bin2hex(openssl_random_pseudo_bytes($bits));


				date_default_timezone_set('Asia/Manila');
				//24hours expiry
				$wew = date('m/d/Y h:i a', time()+86400);
				$phpdate = strtotime($wew);
				$phpdatex = date( 'Y-m-d H:i:s', $phpdate );

				mysql_query("START TRANSACTION");
				$result = mysql_query("INSERT INTO forgot_password (email, token, token_expiry) VALUES ('$email','$token','$phpdatex')");
				if ($result){

						$body = "<div>
			                      <h3>Reset your password</h3>
			                      <p>Hi ".$name.", <br>
			                       Can't remember your password? Don't worry about it - it happens. <br>
			                       Your username is: <b>".$email."</b>
			                      </p><br>

			                      <b> <a href = 'http://".$current_ip."/nucleus/api/checktoken.php?token=".$token."'> Click this link to reset your password </a> </b>
			                    <br><br>
			                      <hr>
			                      <p>
			                         <b> Didn't ask to reset your password? </b> <br>
			                        If you didn't ask for your password, it's likely that another user entered your username or email address by mistake while
			                        trying to reset their password. If that's the case, you don't need to take any further action and can safely disregard this email.
			                      </p>
			                    </div>";
					//email start
						$mail = new PHPMailer;
						$mail->isSMTP();
						$mail->Host = 'smtp.gmail.com';
						$mail->SMTPAuth = true;
						$mail->Username = 'server.nucleus@gmail.com';
						$mail->Password = 'nucleus2015';
						$mail->SMTPSecure = 'tls';
						$mail->Port = 587;
						$mail->From = 'server.nucleus@gmail.com';
						$mail->FromName = 'Nucleus Team';
						$mail->addAddress($email, $name);
						$mail->addReplyTo('server.nucleus@gmail.com', 'Nucleus Team');
						$mail->WordWrap = 50;
						
						$mail->Subject = 'Nucleus | Reset Password';
						
						$mail->Body    = $body;
						$mail->isHTML(true);

						

						if(!$mail->send()) {
							$response["success"] = 0;
		  					$response["message"] = 'Mailer Error: ' . $mail->ErrorInfo;
		  					$response["errorCode"] = 0;
		  					echo json_encode($response);
		  					mysql_query("ROLLBACK");
						 
						   exit;
						}
						else{
							$response["success"] = 1;
							$response["message"] = "Success";
							echo json_encode($response);
							mysql_query("COMMIT");
						}

					//email end

					
				}else{
					$response["success"] = 0;
  					$response["message"] = "Something went wrong (Query Error)";
  					$response["errorCode"] = 0;
  					echo json_encode($response);
				}

				

				
				
		}else{

			$response["success"] = 0;
			$response["message"] = "Sorry, we couldn't find anyone with that email address";
			$response["errorCode"] = 0;
			echo json_encode($response);
		}
  	}else{
  		$response["success"] = 0;
  		$response["message"] = "Something went wrong";
  		$response["errorCode"] = 0;
  		echo json_encode($response);
  	}
	
	

	
	
?>