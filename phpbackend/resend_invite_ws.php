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
if (isset($_POST['workspace_id'])) {
   
   
    $ws_id = $_POST["workspace_id"];
    $email = $_POST["email"];

    $result = mysql_query("SELECT w.workspace_id,ws.workspace_name as workspace_name, w.email, w.project_id, p.project_name as project_name FROM `ws_invites` w LEFT JOIN projects p ON p.project_id = w.project_id LEFT JOIN workspaces ws ON ws.workspace_id = w.workspace_id where w.project_id !=0 and w.workspace_id = '$ws_id' and w.email='$email'");

    if(mysql_num_rows($result)>0){
        $projectName= "";
        $wsname="";
        while($row = mysql_fetch_array($result)){
            $projectName .= $row["project_name"]."<br><br>";

            $wsname = $row["workspace_name"];
        }
        $body = "<div>
                  <h3>Lets collaborate!</h3>
                  <p>Hi there, We are from the workspace ".$wsname.", in Nucleus<br><br>
                   You might have received an invitation from us awhile back and didnt respond to it. And now, we are inviting you again to our workspace, ".$wsname.", in Nucleus.<br>

                   <p>These are the projects that we are inviting you to join:</p><br>
                    ".$projectName.".
                  </p>
                    <p>We will be using Nucleus to work together, share ideas, gather feedbacks, and track the progress of certain projects.</p><br>
                    <p>It is a website that also has an android application for project collaboration. The download link for the android application of Nucleus is also in the website.</p>
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
                $mail->FromName = $wsname." | Nucleus";

               
                $last = explode("@", $email);
                $last = $last[0];
                $mail->addAddress($email, $last);
                
                $mail->addReplyTo('server.nucleus@gmail.com', 'Nucleus Team');
                $mail->WordWrap = 50;
                
                $mail->Subject = 'Nucleus | Follow Up Invitation by '.$wsname;
                
                $mail->Body    = $body;
                $mail->isHTML(true);

                

                if(!$mail->send()) {
                    $response["success"] = 0;
                    $response["message"] = 'Mailer Error: ' . $mail->ErrorInfo;
                    $response["errorCode"] = 0;
                    echo json_encode($response);
                    //mysql_query("ROLLBACK");
                 
                   exit;
                }
                else{
                    $response["success"] = 1;
                    $response["message"] = "Success";
                    echo json_encode($response);
                    //mysql_query("COMMIT");
                }
    }else{
        $result = mysql_query("SELECT * FROM workspaces where workspace_id = '$ws_id'");
        $row = mysql_fetch_array($result);

        $ws_name = $row["workspace_name"];
        $body = "<div>
                  <h3>Lets collaborate!</h3>
                  <p>Hi there, We are from the workspace ".$ws_name.", in Nucleus<br><br>
                   You might have received an invitation from us awhile back and didnt respond to it. And now, we are inviting you again to our workspace, ".$ws_name.", in Nucleus.<br>

                 
                    <p>We will be using Nucleus to work together, share ideas, gather feedbacks, and track the progress of certain projects.</p><br>
                    <p>It is a website that also has an android application for project collaboration. The download link for the android application of Nucleus is also in the website.</p>
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
                $mail->FromName = $ws_name." | Nucleus";

               
                $last = explode("@", $email);
                $last = $last[0];
                $mail->addAddress($email, $last);
                
                $mail->addReplyTo('server.nucleus@gmail.com', 'Nucleus Team');
                $mail->WordWrap = 50;
                
                $mail->Subject = 'Nucleus | Follow Up Invitation by '.$ws_name;
                
                $mail->Body    = $body;
                $mail->isHTML(true);

                

                if(!$mail->send()) {
                    $response["success"] = 0;
                    $response["message"] = 'Mailer Error: ' . $mail->ErrorInfo;
                    $response["errorCode"] = 0;
                    echo json_encode($response);
                    //mysql_query("ROLLBACK");
                 
                   exit;
                }
                else{
                    $response["success"] = 1;
                    $response["message"] = "Success";
                    echo json_encode($response);
                    //mysql_query("COMMIT");
                }


    }
    // if($result){

    //     // $response["success"]=1;
    //     // $response["message"] = "deletion success";
    //     // echo json_encode($response);
    // }else{
    //     $response["success"]=0;
    //     $response["message"] = "deletion failed";
    //     echo json_encode($response);
    // }

   //  $result = mysql_query("SELECT * FROM `ws_invites` WHERE workspace_id = '$ws_id' GROUP BY email ORDER BY `ws_invites`.`date_invited` DESC") or trigger_error(mysql_error());

   //  if(mysql_num_rows($result)>0){

   //      $response["pending"] = array();
    	
   //      while($row = mysql_fetch_array($result)){
       		
   //     		$member = array();
            
   //          $member["email"] = $row["email"];
            
   //          $timeX = "".$row["date_invited"];

   //          $timeY = strtotime($timeX);
            
   //          $time = time() - $timeY; // to get the time since that moment

   //          $tokens = array (
   //              31536000 => 'year',
   //              2592000 => 'month',
   //              604800 => 'week',
   //              86400 => 'day',
   //              3600 => 'hour',
   //              60 => 'minute',
   //              1 => 'second'
   //          );
   //          $ago = null;
   //          foreach ($tokens as $unit => $text) {
   //              if ($time < $unit) continue;
   //              $numberOfUnits = floor($time / $unit);
   //              $ago .= $numberOfUnits.' '.$text.(($numberOfUnits>1)?'s':'');

   //              if ($ago!=null){
   //                  break;
   //              }   
   //          }
   //          $member["date_invited"] = "Invited ".$ago." ago";
           	
			
			// array_push($response["pending"], $member);

            

   //      }
   //      $response["success"]=1;
   //      echo json_encode($response);
   //  }else{
   //      $response["success"] = 0;
   //      $response["message"] = "Error zzzzzzzzzzz";
   //      $response["errorCode"] = 1;
   //      echo json_encode($response);
   //  }

    
 

}
else{
       $response["success"] = 0;
        $response["message"] = "something went wrong";
        $response["errorCode"] = 1;
        echo json_encode($response);
    }
?>