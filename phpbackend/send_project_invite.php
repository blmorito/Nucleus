<?php
 
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
if (isset($_POST['workspace_id'])) {


   
    $workspace_id = $_POST['workspace_id'];
    $user_id = $_POST['user_id'];
    $project_id = $_POST["project_id"];

    $myArray = array();
    $myArray = $_POST["invites"];


    $result = mysql_query("SELECT p.project_name, tu.full_name FROM `projects` p LEFT JOIN (SELECT px.project_id, px.user_id from projects_users px) pu ON pu.project_id = p.project_id LEFT JOIN (SELECT tbl_usersinfo.user_id, tbl_usersinfo.full_name from tbl_usersinfo) tu ON tu.user_id = pu.user_id WHERE p.project_id = '$project_id' and pu.user_id = '$user_id'");

    $row = mysql_fetch_array($result);
    $name = $row["full_name"];
    $project_name = $row["project_name"];
    
    mysql_query("START TRANSACTION");
    $body = "<div>
                  <h3>Lets collaborate!</h3>
                  <p>Hi there, I am ".$name.", <br>
                   And I am inviting you to use Nucleus to work on our project, ".$project_name."
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
                $mail->FromName = $name." | Nucleus";

                

                foreach ($myArray as $email) {
                
                    $result2 = mysql_query("INSERT INTO ws_invites (user_id,workspace_id,project_id,type,email,date_invited) VALUES ('$user_id','$workspace_id','$project_id','Project','$email',now())");

                   
                        # code...
                   
                    $last = explode("@", $email);
                    $last = $last[0];
                       $mail->addAddress($email, $last);
                }
                
                $mail->addReplyTo('server.nucleus@gmail.com', 'Nucleus Team');
                $mail->WordWrap = 50;
                
                $mail->Subject = 'Nucleus | Project Invitation by '.$name;
                
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
                    if($result){
                        mysql_query("COMMIT");
                    }else{
                        mysql_query("ROLLBACK");
                    }
                    echo json_encode($response);
                    
                }

       
    } else {
        // required field is missing
        $response["errorCode"] = 1;
        $response["success"] = 0;
        $response["message"] = "Required field(s) is missing";
     
        // echoing JSON response
        echo json_encode($response);
   }
?>