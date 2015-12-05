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
    $result = mysql_query("SELECT g.goal_id, g.goal_name, g.user_id, tux.full_name, g.date_created FROM `goal` g LEFT JOIN (SELECT tu.user_id, tu.full_name from tbl_usersinfo tu) tux ON tux.user_id = g.user_id WHERE project_id = '$project_id' and status = 'Open'");
    

    if(mysql_num_rows($result)>0){

        $response["open_goals"] = array();
    	
        while($row = mysql_fetch_array($result)){
       		
       		$goal = array();
            $goal["goal_id"] = $row["goal_id"];
            $goal["goal_name"] = $row["goal_name"];
            $goal["goal_creator_id"] = $row["user_id"];
            $goal["goal_creator"] = 'Added by '.$row["full_name"];


            

            $phpdate = strtotime($row["date_created"]);
            
            
            $time = time() - $phpdate; // to get the time since that moment

            $tokens = array (
                31536000 => 'year',
                2592000 => 'month',
                604800 => 'week',
                86400 => 'day',
                3600 => 'hour',
                60 => 'minute',
                1 => 'second'
            );
            $ago = null;
            foreach ($tokens as $unit => $text) {
                if ($time < $unit) continue;
                $numberOfUnits = floor($time / $unit);
                $ago .= $numberOfUnits.' '.$text.(($numberOfUnits>1)?'s':'');

                if ($ago!=null){
                    break;
                }   
            }
            $goal["date_created"] = $ago." ago";  

            $temp_goal_id = $row["goal_id"];
            $result2 = mysql_query("SELECT t.task_name, t.date_due, tux.full_name FROM `task` t LEFT JOIN (SELECT a.user_id, a.task_id from assignment a) ax ON ax.task_id = t.task_id LEFT JOIN (SELECT tu.user_id, tu.full_name from tbl_usersinfo tu) tux ON tux.user_id = ax.user_id WHERE goal_id = '$temp_goal_id'");

            if(mysql_num_rows($result2)>0){
                $temp_tasks = "";
                while($rowx = mysql_fetch_array($result2)){

                    $phpdate = strtotime($rowx['date_due']);
                    $phpdatex = date( 'M d, Y', $phpdate );

                    if(is_null($rowx['full_name'])){
                        $temp_tasks.='<b>'.$rowx["task_name"].'</b>'.' - Due on:'.$phpdatex.'<br>';
                    }else{
                        $temp_tasks.='<b>'.$rowx["task_name"].'</b>'.' ➟ '.$rowx['full_name'].' - Due on:'.$phpdatex.'<br>';
                    }
                    
                }

                $goal["tasks"] = $temp_tasks;

            }else{
                $goal["tasks"] = "";
            }
            

            
			
			array_push($response["open_goals"], $goal);

            

        }
        $response["success"]=1;
        echo json_encode($response);
    }else{
        $response["success"] = 0;
        $response["message"] = "Something went horribly wrong. If this shows up in the defense, good luck.";
        $response["errorCode"] = 1;
        echo json_encode($response);
    }

    
 

}
?>