<?php
	if(isset($_POST['email'])){
		session_start();
		$email = $_POST['email'];
		$myemail = $email;
		$encrypt2 = sha1($email);
		$_SESSION['myemail'] = $myemail;
		include('myConnection.php');
		$result = mysql_query("Select * from tbl_users where email = '$encrypt2'");
		$numrows = mysql_num_rows($result);
		if ($numrows == 1)
		{
			$row = mysql_fetch_assoc($result);
			$fname = $row['full_name'];
?>
			<html>
				<head>

				    <meta charset="utf-8">
				    <meta http-equiv="X-UA-Compatible" content="IE=edge">
				    <meta name="viewport" content="width=device-width, initial-scale=1">
				    <meta name="description" content="">
				    <meta name="author" content="">

				    <title>Create Nucleus Account</title>

				    <script src="../js/jquery-1.11.1.min.js"></script>
				    <!-- Bootstrap Core CSS -->
				    <link href="../bower_components/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet">

				    <!-- MetisMenu CSS -->
				    <link href="../bower_components/metisMenu/dist/metisMenu.min.css" rel="stylesheet">

				    <!-- Timeline CSS -->
				    <link href="../dist/css/timeline.css" rel="stylesheet">

				    <!-- Custom CSS -->
				    <link href="../dist/css/sb-admin-2.css" rel="stylesheet">

				    <!-- Morris Charts CSS -->
				    <link href="../bower_components/morrisjs/morris.css" rel="stylesheet">

				    <!-- Custom Fonts -->
				    <link href="../bower_components/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">

				    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
				    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
				    <!--[if lt IE 9]>
				        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
				        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
				    <![endif]-->

				</head>
					
				<body id = "regJSbg">					
				    <div id = "BG">
						<div id = "regBG1">
					    </div>
					    <div id = "regBG2">
					    </div>

					    <div style = "height: 100%;"class = "containerN">
					    	<?php
					    		$body = "
										<div>
					                      <h3>Reset your password</h3>
					                      <p>Hi ".$fname.", <br>
					                       Can't remember your password? Don't worry about it - it happens. <br>
					                       Your username is: <b>".$email."</b>
					                      </p><br>

					                      <b> <a href = 'localhost/nucleus/pages/resetpassword_page.php?token=".$encrypt2."'> Click this link to reset your password </a> </b>
					                    <br><br>
					                      <hr>
					                      <p>
					                         <b> Didn't ask to reset your password? </b> <br>
					                        If you didn't ask for your password, it's likely that another user entered your username or email address by mistake while
					                        trying to reset their password. If that's the case, you don't need to take any further action and can safely disregard this email.
					                      </p>
					                    </div>
					    		";

					    		
					    	?>
					    	
							
							<?php
								include '../PhpMailer/PhpMailerAutoload.php';
								$mail = new PHPMailer;
								$mail->isSMTP();
								$mail->Host = 'smtp.gmail.com';
								$mail->SMTPAuth = true;
								$mail->Username = 'server.nucleus@gmail.com';
								$mail->Password = 'nucleus2015';
								$mail->SMTPSecure = 'tls';
								$mail->Port = 587; // 465 or 587
								$mail->From = 'server.nucleus@gmail.com';
								$mail->FromName = 'Nucleus Team';
								$mail->addAddress($myemail, $fname);
								$mail->addReplyTo('server.nucleus2@gmail.com', 'Wendell');
								$mail->WordWrap = 50;
								$mail->Subject = 'Nucleus | Reset your Password';
								$mail->Body    = $body;
								$mail->isHTML(true);
								if(!$mail->send()) {
								   echo 'Message could not be sent.';
								   echo 'Mailer Error: ' . $mail->ErrorInfo;
								   exit;
								}
								else
								{
									echo "success";
								}
								echo '<div style = "margin-top:50px;"><center>Email verification has been sent</center></div>';

							?>
							
					    	
					    </div>
				    </div>


					<!-- scripts -->
					<script src="js/jquery-1.11.1.min.js"></script>
					<script src="js/bootstrap.min.js"></script>
					
					
				</body>
			</html>

<?php
			echo "sent";
		}
		else
		{
			echo "notexist";
		}
	}
	else
	{
		header('location:errorpage.php');
	}

?>

