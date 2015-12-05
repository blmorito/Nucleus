<?php
 date_default_timezone_set('Asia/Manila');
/*
 * Following code will get single product details
 * A product is identified by product id (pid)
 */
 
// array for JSON response
$response = array();
 
// include db connect class
require_once __DIR__ . '/db_connect.php';
 
// connecting to db
$db = new DB_CONNECT();
 
// check for post data
if (isset($_POST['project_id'])) {
   
   
    $project_id = $_POST["project_id"];
    $user_id = $_POST["user_id"];
    
    $subject = mysql_real_escape_string($_POST["subject"]);
    $body = mysql_real_escape_string($_POST["body"]);

    mysql_query("START TRANSACTION");

    $result = mysql_query("INSERT INTO discussion (project_id, user_id, d_subject, d_body, date_posted) VALUES ('$project_id', '$user_id', '$subject', '$body', now())") or trigger_error(mysql_error());
    $recentKey = mysql_insert_id();


    if($result){

        #NOTIFYYYYYYYYYY
         $queryNotifyPMembers = mysql_query("SELECT user_id FROM projects_users where project_id = '$project_id' and user_id <> '$user_id'") or trigger_error(mysql_error());
        while($rowPM = mysql_fetch_array($queryNotifyPMembers)){

            $toid = $rowPM['user_id'];
            $queryNOTIFY = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$toid', 'Discussion', '$recentKey', 'posted a discussion - ".$subject."', now())") or trigger_error(mysql_error());
        }

        #ENDDD

        $q = "INSERT INTO activity (project_id,from_id,from_type,message,date) VALUES ('$project_id','$user_id','User','posted a discussion - ".$subject."',now())";
        $rra = mysql_query($q);
        mysql_query("COMMIT");
        $response["success"]=1;
        $response["message"]="Successfully posted a discussion";
        echo json_encode($response);
    }else{
        mysql_query("ROLLBACK");
        $response["success"]=0;
        $response["message"]="Something went wrong";
        echo json_encode($response);
    }

    

    
 

}
?>