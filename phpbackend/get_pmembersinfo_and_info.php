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
   
    $task_id = $_POST["task_id"];
   
    $project_id = $_POST["project_id"];
    $wew = mysql_query("SELECT project_deadline from projects where project_id = '$project_id'");
    $roo = mysql_fetch_array($wew);
    $response["project_deadline"] = $roo["project_deadline"];

    $result = mysql_query("SELECT pu.user_id, tu.full_name  FROM `projects_users` pu LEFT JOIN (SELECT t.user_id, t.full_name from tbl_usersinfo t) tu ON tu.user_id = pu.user_id where project_id = '$project_id'") or trigger_error(mysql_error());

    if(mysql_num_rows($result)>0){

        $response["membersName"] = array();
    	
        while($row = mysql_fetch_array($result)){
       		
       		$member = array();
            $member["user_id"] = $row["user_id"];
            $member["full_name"] = $row["full_name"];
			
			array_push($response["membersName"], $member);

            

        }
        $result2 = mysql_query("SELECT user_id from assignment where task_id = '$task_id'");
        $rowResult2 = mysql_fetch_array($result2);
        if(mysql_num_rows($result2)>0){
            $response["assignee_id"] = $rowResult2["user_id"];
        }else{
            $response["assignee_id"] = 0;
        }

        $result3 = mysql_query("SELECT t.task_name, t.user_id, tu.full_name, t.date_due, (SELECT project_deadline from projects where project_id = (Select project_id from goal where goal_id = (SELECT goal_id from task where task_id ='$task_id'))) AS pdead FROM `task`t LEFT JOIN (SELECT u.user_id, u.full_name from tbl_usersinfo u) tu ON tu.user_id = t.user_id WHERE t.task_id = '$task_id'");
        $rowResult3 = mysql_fetch_array($result3);

        $response["task_name"] = $rowResult3["task_name"];
        $response["date_due"] = $rowResult3["date_due"];
        $response["project_deadline"] = $rowResult3["pdead"];




        $response["success"]=1;
        echo json_encode($response);
    }else{
        $response["success"] = 0;
        $response["message"] = "Error CaaaYA";
        $response["errorCode"] = 1;
        echo json_encode($response);
    }

    
 

}
?>