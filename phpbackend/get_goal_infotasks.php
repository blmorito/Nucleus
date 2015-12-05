<?php
 date_default_timezone_set('Asia/Manila');

 
// array for JSON response
$response = array();
 
// include db connect class
require_once __DIR__ . '/db_connect.php';
 
// connecting to db
$db = new DB_CONNECT();
 
// check for post data
if (isset($_POST['goal_id'])) {
   
   
    $goal_id = $_POST["goal_id"];

    $result = mysql_query("SELECT g.goal_name, g.goal_desc, g.date_created,g.date_finished, g.status FROM `goal` g WHERE goal_id = '$goal_id'");

    $row = mysql_fetch_array($result);

    

    if($row["goal_desc"]==""){
        $response["goal_desc"] = "This goal doesn't have any description.";
    }else{
        $response["goal_desc"] = $row["goal_desc"];

    }

    $response["goal_name"] = $row["goal_name"];
    $phpdate = strtotime($row["date_created"]);
    $phpdatex = date( 'M d, Y', $phpdate );

    if($row["status"] == 'Done'){
        $phpdate = strtotime($row["date_finished"]);
        $phpdatex = date( 'M d, Y', $phpdate );
        $response["date_created"] = 'Completed Last '.$phpdatex.' •';

    }else{
        $phpdate = strtotime($row["date_created"]);
        $phpdatex = date( 'M d, Y', $phpdate );
        $response["date_created"] = 'Created last '.$phpdatex.' •';
    }
   


    $response["status"] = 'Status: '.$row["status"];

    $result2 = mysql_query("SELECT t.task_id, t.task_name, t.date_due, t.task_status, t.date_finished, ax.user_id, tu.full_name, t.completed_by FROM `task` t LEFT JOIN (SELECT a.user_id, a.task_id from assignment a) ax ON ax.task_id = t.task_id LEFT JOIN (SELECT t.user_id, t.full_name from tbl_usersinfo t) tu ON tu.user_id = ax.user_id where goal_id = '$goal_id' ORDER BY t.task_status ASC");

    $response["tasks"] = array();

    while($rowx = mysql_fetch_array($result2)){ 
        //get completor name if naa
        $completorId = $rowx["completed_by"];
        $getName = mysql_query("SELECT full_name from tbl_usersinfo where user_id = '$completorId'");
        $rowk = mysql_fetch_array($getName);

        $task = array();
        $task["task_id"] = $rowx["task_id"];
        $task["task_name"] = $rowx["task_name"];

        $phpdate = strtotime($rowx["date_due"]);
        $phpdatex = date( 'M d, Y', $phpdate );

        
        $task["task_status"] = $rowx["task_status"];

        if($rowx["task_status"]=='Done'){
            $phpdate2 = strtotime($rowx["date_finished"]);
            $phpdatex2 = date( 'M d, Y', $phpdate2 );
            $task["date_due"] = 'Completed on '.$phpdatex2.' by '.$rowk['full_name'];
        }else{
            $task["date_due"] = 'Due on '.$phpdatex;
        }

        if(is_null($rowx["full_name"])){
            $task["task_assignee_id"] = 0;
            $task["task_assignee"] = 'Anyone';
        }else{
            $task["task_assignee_id"] = $rowx["user_id"];
            $task["task_assignee"] = $rowx["full_name"];
        }


        $tempd = strtotime($rowx["date_due"]);
        $deadline = strtotime(date('Y-m-d',$tempd));
        $now = strtotime(date('Y-m-d',time()));

        $task["date1"] = $deadline;
        $task["date2"] = $now;

        if($rowx["task_status"]=='Done'){
            $task["isLate"] = "Not";
        }else{
            if($tempd<$now){
                $task["isLate"] = "Late";
            }else{
                $task["isLate"] = "Not";
            }
        }

        array_push($response["tasks"], $task);
    }

    $response["success"] = 1;
    echo json_encode($response);


    

   
    
 

}
?>