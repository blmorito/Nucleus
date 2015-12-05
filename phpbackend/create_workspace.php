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
if (isset($_POST['user_id'])) {


   
    $user_id = $_POST['user_id'];
    $workspace_name = $_POST['workspace_name'];
    $workspace_desc = $_POST['workspace_desc'];


    $myArray = array();
    $myArray = $_POST["invites"];


    
    $count = sizeof($myArray);
    $response["c"] = $myArray;

    mysql_query("START TRANSACTION");
    $result = mysql_query("INSERT INTO workspaces (workspace_name,workspace_desc,ws_date_created) VALUES ('$workspace_name','$workspace_desc',now())") or trigger_error(mysql_error());
    $recentId = mysql_insert_id();
    $result1 = mysql_query("INSERT INTO ws_members (workspace_id, user_id, user_level_id, date_joined) VALUES ('$recentId','$user_id', '1', now())");

    if ($count>0 && $myArray[0] !=""){
        $query = mysql_query("SELECT full_name from tbl_usersinfo where user_id = '$user_id'");
        $row = mysql_fetch_array($query);
        $fullname = $row["full_name"];
        $body = "<div>
                  <h3>Lets collaborate!</h3>
                  <p>Hi there, I am ".$fullname.", <br>
                   And I am inviting you to our workspace, ".$workspace_name.", in Nucleus.
                  </p><br>
                    <p>We will be using Nucleus to work together, share ideas, gather feedbacks, and track the progress of certain projects.</p><br>
                    <p>It is a website that also has an android application for project collaboration.</p>
                  <b> <a href = 'http://".$current_ip."/nucleus/pages/sign-up_page.php'> Click this link to sign up in Nucleus. Please use this email address. </a> </b>
                <br><br>
                <p>If you already have a nucleus account with this email address, this invite can be accepted inside the application.</p>
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
                $mail->FromName = 'Nucleus Team';

                foreach ($myArray as $email) {
                    $result2 = mysql_query("INSERT INTO ws_invites (user_id,workspace_id,type,email,date_invited) VALUES ('$user_id','$recentId','Workspace','$email',now())");

                    $last = explode("@", $email);
                    $last = $last[0];
                       $mail->addAddress($email, $last);
                }
                
                $mail->addReplyTo('server.nucleus@gmail.com', 'Nucleus Team');
                $mail->WordWrap = 50;
                
                $mail->Subject = 'Nucleus | Workspace Invitation by '.$fullname;
                
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
                    $response["workspace_id"] = $recentId;
                    echo json_encode($response);
                    $updateWs = mysql_query("UPDATE tbl_usersinfo set recent_ws = '$recentId' where user_id = '$user_id'");
                    mysql_query("COMMIT");
                }
        
    }else{
         if($result and $result1){
            $updateWs = mysql_query("UPDATE tbl_usersinfo set recent_ws = '$recentId' where user_id = '$user_id'");
            mysql_query("COMMIT");
            $response["success"] = 1;
            $response["workspace_id"] = $recentId;
            echo json_encode($response); 
        }else{
            mysql_query("ROLLBACK");
            $response["success"] = 0;
            echo json_encode($response);
        }
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