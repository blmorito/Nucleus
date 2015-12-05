<?php
 date_default_timezone_set('Asia/Manila');
/*
 * Following code will get single product details
 * A product is identified by product id (pid)
 */
 
// array for JSON response
$response = array();
$current_ip = '192.168.8.120';
// include db connect class
require_once __DIR__ . '/db_connect.php';
require '../PHPMailer/PHPMailerAutoload.php';
// connecting to db
$db = new DB_CONNECT();
 
// check for post data
if (isset($_POST['project_id'])) {
   
   
    $project_id = $_POST["project_id"];
    $email = $_POST["email"];
    $user_id = $_POST["user_id"];

    $query = mysql_query("SELECT p.project_id, email, date_invited,p.project_name, (SELECT full_name from tbl_usersinfo where user_id = '$user_id') as resender FROM `ws_invites` w LEFT JOIN (SELECT projects.project_id, projects.project_name from projects) p ON p.project_id = w.project_id where w.project_id = '$project_id' and email = '$email' GROUP BY email");
    $row = mysql_fetch_array($query);

    $sender = $row["resender"];
    $project_name = $row["project_name"];

    $phpdate = strtotime($row["date_invited"]);
    $phpdatex = date( 'M d, Y', $phpdate );

    $date = $phpdatex;
    //mysql_query("START TRANSACTION");
    $body = "<div>
                  <h3>Lets collaborate!</h3>
                  <p>Hi there, I am ".$sender.", <br>
                   And I am reminding you that you were invited to our project, ".$project_name." in Nucleus. You were invited last ".$date.".
                  </p><br>
                    <p>We will be using Nucleus to work together, share ideas, gather feedbacks, and track the progress of this project.</p><br>
                    <p>It is a website that also has an android application for project collaboration.</p>
                  <b> <a href = 'http://".$current_ip."/nucleus/pages/sign-up_page.php'> Click this link to sign up in Nucleus. Please use this email address. </a> </b>
                <br><br>
                  <hr>
                  <p>
                     <b> Don't know the person inviting you? </b> <br>
                    If you have no idea what this is all about, then kindly ignore this email.
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
                $mail->FromName = $sender." | Nucleus";

                $last = explode("@", $email);
                $last = $last[0];
                $mail->addAddress($email, $last);
                            
                $mail->addReplyTo('server.nucleus@gmail.com', 'Nucleus Team');
                $mail->WordWrap = 50;
                
                $mail->Subject = 'Nucleus | Project Invitation Reminder by '.$sender;
                
                $mail->Body    = $body;
                $mail->isHTML(true);

                

                if(!$mail->send()) {
                    $response["success"] = 0;
                    $response["message"] = 'Mailer Error: ' . $mail->ErrorInfo;
                    $response["errorCode"] = 0;
                    mysql_query("ROLLBACK");
                    echo json_encode($response);
                    
                 
                   exit;
                }
                else{
                    $response["success"] = 1;
                    $response["message"] = "Successaaaaaaaaaaa";
                    
                    echo json_encode($response);
                    
                }
  

    
 

}
else{
       $response["success"] = 0;
        $response["message"] = "something went wrong";
        $response["errorCode"] = 1;
        echo json_encode($response);
    }
?>