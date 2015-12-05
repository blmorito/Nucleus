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
if (isset($_POST['discussion_id'])) {
   
   
    $discussion_id = $_POST["discussion_id"];
    $d_subject = mysql_real_escape_string($_POST["d_subject"]);
    $d_body = mysql_real_escape_string($_POST["d_body"]);
    $user_id = $_POST["user_id"];

    $getProjectId = mysql_query("SELECT project_id from discussion where discussion_id = '$discussion_id'");
    $gpid = mysql_fetch_array($getProjectId);

    $project_id = $gpid["project_id"];
    

    mysql_query("START TRANSACTION");

    $result = mysql_query("UPDATE discussion SET d_subject = '$d_subject', d_body = '$d_body' where discussion_id = '$discussion_id'") or trigger_error(mysql_error());

    if($result){
        #NOTIFYYYYYYYYYY
         $queryNotifyPMembers = mysql_query("SELECT user_id FROM projects_users where project_id = '$project_id' and user_id <> '$user_id'") or trigger_error(mysql_error());
        while($rowPM = mysql_fetch_array($queryNotifyPMembers)){

            $toid = $rowPM['user_id'];
            $queryNOTIFY = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$toid', 'Discussion', '$discussion_id', 'updated a discussion - ".$d_subject."', now())") or trigger_error(mysql_error());
        }

        #ENDDD
        mysql_query("COMMIT");
        $response["success"]=1;
        $response["message"]="Successfully updated a discussion";
        echo json_encode($response);
    }else{
        mysql_query("ROLLBACK");
        $response["success"]=0;
        $response["message"]="Something went wrong";
        echo json_encode($response);
    }

    

    
 

}
?>