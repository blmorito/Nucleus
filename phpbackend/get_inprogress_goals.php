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
    $result = mysql_query("SELECT g.goal_id, g.goal_name, g.user_id, tux.full_name, g.date_created, g.date_started FROM `goal` g LEFT JOIN (SELECT tu.user_id, tu.full_name from tbl_usersinfo tu) tux ON tux.user_id = g.user_id WHERE project_id = '$project_id' and status = 'In progress' ORDER BY g.date_created DESC");
    

    if(mysql_num_rows($result)>0){

        $response["open_goals"] = array();
    	
        while($row = mysql_fetch_array($result)){
       		
       		$goal = array();
            $goal["goal_id"] = $row["goal_id"];
            $goal["goal_name"] = $row["goal_name"];
            $goal["goal_creator_id"] = $row["user_id"];
            $goal["goal_creator"] = 'Added by '.$row["full_name"];


            

            $phpdate = strtotime($row["date_created"]);
            $phpdate2 = strtotime($row["date_started"]);
            
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
            $result2 = mysql_query("SELECT t.task_name, t.date_due,t.date_finished, tux.full_name, t.task_status FROM `task` t LEFT JOIN (SELECT a.user_id, a.task_id from assignment a) ax ON ax.task_id = t.task_id LEFT JOIN (SELECT tu.user_id, tu.full_name from tbl_usersinfo tu) tux ON tux.user_id = ax.user_id WHERE goal_id = '$temp_goal_id' ORDER BY t.task_status ASC");

            if(mysql_num_rows($result2)>0){
                $temp_tasks = "";
                while($rowx = mysql_fetch_array($result2)){

                    $phpdate = strtotime($rowx['date_due']);
                    $phpdatex = date( 'M d, Y', $phpdate );

                    if(is_null($rowx['full_name'])){
                        if($rowx['task_status']=='Done'){
                            $phpdate = strtotime($rowx['date_finished']);
                            $phpdatex = date( 'M d, Y', $phpdate );
                            $temp_tasks.='<b>✔ '.$rowx["task_name"].'</b>'.' - Completed on:'.$phpdatex.'<br>';
                        }elseif ($rowx['task_status']=='In progress') {
                            $temp_tasks.='<b>&nbsp;► '.$rowx["task_name"].'</b>'.' ➟ '.$rowx['full_name'].' - Due on:'.$phpdatex.'<br>';
                        
                        }else{
                             $temp_tasks.='<b>❒ '.$rowx["task_name"].'</b>'.' - Due on:'.$phpdatex.'<br>';
                        }
                        
                    }else{
                        if($rowx['task_status']=='Done'){
                            $phpdate = strtotime($rowx['date_finished']);
                            $phpdatex = date( 'M d, Y', $phpdate );
                            $temp_tasks.='<b>✔ '.$rowx["task_name"].'</b>'.' ➟ '.$rowx['full_name'].' - Completed on:'.$phpdatex.'<br>';
                        }elseif ($rowx['task_status']=='In progress') {
                            $temp_tasks.='<b>&nbsp;► '.$rowx["task_name"].'</b>'.' ➟ '.$rowx['full_name'].' - Due on:'.$phpdatex.'<br>';
                        }else{
                             $temp_tasks.='<b>❒ '.$rowx["task_name"].'</b>'.' ➟ '.$rowx['full_name'].' - Due on:'.$phpdatex.'<br>';
                        }
                        
                    }
                    
                }

                $goal["tasks"] = $temp_tasks;

            }else{
                $goal["tasks"] = "";
            }

            $kapoy1 = mysql_query("SELECT COUNT(*) FROM task where task_status = 'Done' and goal_id = '$temp_goal_id'");
            $kapoy2 = mysql_query("SELECT COUNT(*) FROM task where goal_id='$temp_goal_id'");

            $rowkp = mysql_fetch_array($kapoy1);
            $rowpk = mysql_fetch_array($kapoy2);

            $frog = round(($rowkp[0] / $rowpk[0])*100);
            // $result3 = mysql_query("SELECT (SELECT COUNT(*) FROM task where task_status = 'Done' and goal_id = '$temp_goal_id') AS Completed, (SELECT COUNT(*) FROM task where goal_id='$temp_goal_id') AS TOTAL FROM `task` where goal_id = '$temp_goal_id' GROUP BY goal_id");
            // $rowy = mysql_fetch_array($result3);


            // $response["hmm"] = $rowy['Completed'].' '.$rowy['TOTAL'];
            // $prog = round(($rowy['Completed'] / $rowy['TOTAL'])*100);

            $goal["progress"] = $frog;
            

            
			
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