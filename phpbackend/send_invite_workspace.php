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


    $myArray = array();
    $myArray = $_POST["invites"];

    $myArray2 = array();
    $myArray2 = $_POST["selectedProjects"];
    
    $count = sizeof($myArray2);

    if ($count==0 || $myArray2[0]==""){
        mysql_query("START TRANSACTION");
        $query = mysql_query("SELECT w.workspace_id, w.user_id, ww.workspace_name as workspace_name, u.full_name as full_name from ws_members w LEFT JOIN(SELECT workspaces.workspace_id, workspaces.workspace_name from workspaces) ww ON ww.workspace_id = w.workspace_id LEFT JOIN (SELECT tbl_usersinfo.user_id, tbl_usersinfo.full_name from tbl_usersinfo)u ON u.user_id = w.user_id where w.user_id = '$user_id' and w.workspace_id = '$workspace_id'");
        $row = mysql_fetch_array($query);
        $fullname = $row["full_name"];
        $workspace_name = $row["workspace_name"];
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
                $mail->FromName = $fullname." | Nucleus";

                foreach ($myArray as $email) {
                    $result2 = mysql_query("INSERT INTO ws_invites (user_id,workspace_id,type,email,date_invited) VALUES ('$user_id','$workspace_id','Workspace','$email',now())");

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
                    echo json_encode($response);
                    mysql_query("COMMIT");
                }
    }else{

        $temps = "";
        $projectNames = array();
        foreach ($myArray2 as $project_id ) {
            $result =mysql_query("Select project_name from projects where project_id = '$project_id'");
            $row = mysql_fetch_array($result);

            array_push($projectNames, $row["project_name"]); 
            $temps.="".$row["project_name"]."<br>";
        }

        mysql_query("START TRANSACTION");
        $query = mysql_query("SELECT w.workspace_id, w.user_id, ww.workspace_name as workspace_name, u.full_name as full_name from ws_members w LEFT JOIN(SELECT workspaces.workspace_id, workspaces.workspace_name from workspaces) ww ON ww.workspace_id = w.workspace_id LEFT JOIN (SELECT tbl_usersinfo.user_id, tbl_usersinfo.full_name from tbl_usersinfo)u ON u.user_id = w.user_id where w.user_id = '$user_id' and w.workspace_id = '$workspace_id'");
        $row = mysql_fetch_array($query);
        $fullname = $row["full_name"];
        $workspace_name = $row["workspace_name"];
        $body = "<div>
                  <h3>Lets collaborate!</h3>
                  <p>Hi there, I am ".$fullname.", <br>
                   And I am inviting you to our workspace, ".$workspace_name.", in Nucleus.
                  </p><br>
                    <p>We will be using Nucleus to work together, share ideas, gather feedbacks, and track the progress of certain projects.</p><br>
                    <p>These are the projects that we are inviting you to join:</p><br>
                    ".$temps.".<br>
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
                $mail->FromName = $fullname." | Nucleus";

                

                foreach ($myArray as $email) {
                    foreach ($myArray2 as $pid ) {
                        $result2 = mysql_query("INSERT INTO ws_invites (user_id,workspace_id,project_id,type,email,date_invited) VALUES ('$user_id','$workspace_id','$pid','Project','$email',now())");

                       
                            # code...
                    }
                     $last = explode("@", $email);
                    $last = $last[0];
                       $mail->addAddress($email, $last);
                }
                
                $mail->addReplyTo('server.nucleus@gmail.com', 'Nucleus Team');
                $mail->WordWrap = 50;
                
                $mail->Subject = 'Nucleus | Project Invitation by '.$fullname;
                
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
                    mysql_query("COMMIT");
                    echo json_encode($response);
                    
                }



        // $response["success"] = 1;
        // $response["message"] = "Success";
        // $response["count"] = $count;
        
        // echo json_encode($response);


    }

    //AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
    // mysql_query("START TRANSACTION");
    // $result = mysql_query("INSERT INTO projects (project_name,project_desc,project_created,workspace_id) VALUES ('$projectName','$projectDesc',now(),'$workspace_id')");
    // $recentId = mysql_insert_id();
    // $result1 = mysql_query("INSERT INTO projects_users (user_id, date_joined, user_level_id) VALUES ('$user_id',now(), '1')");

    // if ($count>0){
    //     $query = mysql_query("SELECT full_name from tbl_usersinfo where user_id = '$user_id'");
    //     $row = mysql_fetch_array($query);
    //     $fullname = $row["full_name"];
    //     $body = "<div>
    //               <h3>Lets collaborate!</h3>
    //               <p>Hi there, I am ".$fullname.", <br>
    //                And I am inviting you to use Nucleus to work on our project
    //               </p><br>
    //                 <p>We will be using Nucleus to work together, share ideas, gather feedbacks, and track the progress of this project.</p><br>
    //                 <p>It is a website that also has an android application for project collaboration.</p>
    //               <b> <a href = 'http://".$current_ip."/nucleus/pages/sign-up_page.php'> Click this link to sign up in Nucleus. Please use this email address. </a> </b>
    //             <br><br>
    //               <hr>
    //               <p>
    //                  <b> Don't know the person inviting you? </b> <br>
    //                 If you have no idea what this is all about, then kindly ignore this email.
    //               </p>
    //             </div>";
    //         //email start
    //             $mail = new PHPMailer;
    //             $mail->isSMTP();
    //             $mail->Host = 'smtp.gmail.com';
    //             $mail->SMTPAuth = true;
    //             $mail->Username = 'server.nucleus@gmail.com';
    //             $mail->Password = 'nucleus2015';
    //             $mail->SMTPSecure = 'tls';
    //             $mail->Port = 587;
    //             $mail->From = 'server.nucleus@gmail.com';
    //             $mail->FromName = 'Nucleus Team';

    //             foreach ($myArray as $email) {
    //                 $result2 = mysql_query("INSERT INTO ws_invites (user_id,workspace_id,project_id,type,email,date_invited) VALUES ('$user_id','$workspace_id','$recentId','Project','$email',now())");

    //                 $last = explode("@", $email);
    //                 $last = $last[0];
    //                    $mail->addAddress($email, $last);
    //             }
                
    //             $mail->addReplyTo('server.nucleus@gmail.com', 'Nucleus Team');
    //             $mail->WordWrap = 50;
                
    //             $mail->Subject = 'Nucleus | Project Invitation by '.$fullname;
                
    //             $mail->Body    = $body;
    //             $mail->isHTML(true);

                

    //             if(!$mail->send()) {
    //                 $response["success"] = 0;
    //                 $response["message"] = 'Mailer Error: ' . $mail->ErrorInfo;
    //                 $response["errorCode"] = 0;
    //                 echo json_encode($response);
    //                 mysql_query("ROLLBACK");
                 
    //                exit;
    //             }
    //             else{
    //                 $response["success"] = 1;
    //                 $response["message"] = "Success";
    //                 echo json_encode($response);
    //                 mysql_query("COMMIT");
    //             }
        
    // }else{
    //      if($result and $result1){
    //         mysql_query("COMMIT");
    //         $response["success"] = 1;
    //         echo json_encode($response); 
    //     }else{
    //         mysql_query("ROLLBACK");
    //         $response["success"] = 0;
    //         echo json_encode($response);
    //     }
    // }

} else {
    // required field is missing
    $response["errorCode"] = 1;
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>