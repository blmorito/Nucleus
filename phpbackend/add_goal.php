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
if (isset($_POST['user_id'])) {
   
   
    $project_id = $_POST["project_id"];
    $user_id = $_POST["user_id"];
    
    $goal_name = mysql_real_escape_string($_POST["goal_name"]);
    $goal_desc = mysql_real_escape_string($_POST["goal_desc"]);

    mysql_query("START TRANSACTION");
    $wew2 = mysql_query("UPDATE projects set project_status = 'In progress', date_finished = '' where project_id = '$project_id'");

    $result = mysql_query("INSERT INTO goal (goal_name, goal_desc, project_id, user_id, date_created, status) VALUES ('$goal_name', '$goal_desc', '$project_id', '$user_id', now(), 'Open')") or trigger_error(mysql_error());
    $recentkey = mysql_insert_id();

    $result2 = mysql_query("INSERT INTO task (goal_id, user_id, task_name, date_created, date_due) VALUES ('$recentkey','$user_id','This is a sample task made by nucleus',now(), now() + INTERVAL 1 DAY)");
    if($result && $result2){
        mysql_query("INSERT INTO activity (project_id,from_id,from_type,message,date) VALUES ('$project_id', '$user_id', 'User', 'added a goal - ".$goal_name."', now()) ");
        /*
        *
        *NOTIFICATION MAKER
        */
            $queryNotifyPMembers = mysql_query("SELECT user_id FROM `projects_users`where project_id = '$project_id' and user_id <> '$user_id'") or trigger_error(mysql_error());
            while($rowPM = mysql_fetch_array($queryNotifyPMembers)){

                $toid = $rowPM['user_id'];
                $queryNOTIFY = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$toid', 'Goal', '$recentkey', 'added a goal - ".$goal_name."', now())") or trigger_error(mysql_error());
            }


        /*
        * Coded by Brylle..
        */
        mysql_query("COMMIT");
        $response["success"]=1;
        $response["goal_id"]=$recentkey;
        $response["message"]="Successfully added a goal";
        echo json_encode($response);
    }else{
        mysql_query("ROLLBACK");
        $response["success"]=0;
        $response["message"]="Something went wrong";
        echo json_encode($response);
    }

    

    
 

}
?>