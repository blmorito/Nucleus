<?php
 date_default_timezone_set('Asia/Manila');

 
// array for JSON response
$response = array();
 
// include db connect class
require_once __DIR__ . '/db_connect.php';
 
// connecting to db
$db = new DB_CONNECT();
 
// check for post data
if (isset($_POST['user_id'])) {
   
   
    $goal_id = $_POST["goal_id"];
    $user_id = $_POST["user_id"];
    $task_id = $_POST["task_id"];

    $checkGoal = mysql_query("SELECT * FROM goal where goal_id = '$goal_id'");
    $row = mysql_fetch_array($checkGoal);
    $project_id = $row["project_id"];

    $getProjectLeader = mysql_query("SELECT user_id from projects_users where project_id = '$project_id' and user_level_id = '1'");
    $rowPL = mysql_fetch_array($getProjectLeader);
    $project_leader = $rowPL["user_id"];

    $getGoalname = mysql_query("SELECT goal_name from goal where goal_id = '$goal_id'");
    $r = mysql_fetch_array($getGoalname);
    $goal_name = $r["goal_name"];

    $getTaskName = mysql_query("SELECT task_name, user_id,completed_by, started_by from task where task_id = '$task_id'");
    $rr = mysql_fetch_array($getTaskName);
    $task_name = $rr["task_name"];
    $task_creator = $rr["user_id"];
    $task_completor = $rr["completed_by"];
    $task_starter = $rr["started_by"];


    //mysql_query("START TRANSACTION");
    if($row["status"]=="Open"){

        //started a goal and completed a task 
        $updateStatus = mysql_query("UPDATE goal set status = 'In progress', date_started = now() where goal_id = '$goal_id'");
        $q = mysql_query("INSERT INTO activity (project_id,from_id,from_type,message,date) VALUES ('$project_id', '$user_id', 'User', 'started the goal - ".$goal_name."',now())");
        $updateTask = mysql_query("UPDATE task set task_status = 'Done',date_started = now(), date_finished = now(), started_by = '$user_id', completed_by = '$user_id' where task_id = '$task_id'");
        $qq = mysql_query("INSERT INTO activity (project_id,from_id,from_type,message,date) VALUES ('$project_id', '$user_id', 'User', 'completed the task - ".$task_name."',now())");

        /*
         * NOTIFY 
         *
         */

        #notify project leader

        if($project_leader==$user_id){

        }else{
            $notifyPLstart = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$project_leader', 'Task', '$task_id','started the task - ".$task_name."', now())");
            $notifyPL = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$project_leader', 'Task', '$task_id','completed the task - ".$task_name."', now())");
        }

        #notify task creator
        if($task_creator==$user_id || $task_creator==$project_leader){

        }else{
            $notifyTLstart = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$task_creator', 'Task', '$task_id','started the task - ".$task_name."', now())");
            $notifyTL = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$task_creator', 'Task', '$task_id','completed the task - ".$task_name."', now())");
        }

        #notify assignee
        $getAssignee = mysql_query("SELECT user_id from assignment where task_id = '$task_id'");
        $assign_id = 0;
        if(mysql_num_rows($getAssignee)>0){

            $rowAsgn = mysql_fetch_array($getAssignee);
            $assign_id = $rowAsgn["user_id"];

            if($assign_id==$user_id || $assign_id==$project_leader || $assign_id==$task_creator){

            }else{
                $notifyAsgnStart = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$assign_id', 'Task', '$task_id','started the task - ".$task_name."', now())");
                $notifyAsgn = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$assign_id', 'Task', '$task_id','completed the task - ".$task_name."', now())");
            }
        }

        $getCommenters = mysql_query("SELECT DISTINCT user_id from comment where comment_type = 'Task' and comment_type_id = '$task_id'");
        if(mysql_num_rows($getCommenters)>0){
            while ($rowComment = mysql_fetch_array($getCommenters)) {
                $kid = $rowComment["user_id"];
                if ($kid == $user_id || $kid == $project_leader || $kid == $task_creator) {
                    # code...
                }else{
                    $notifyCommCenter = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$kid', 'Task', '$task_id','completed the task - ".$task_name."', now())");
                }
                # code...
            }
        }

        /*
         * DONE 
         *
         */

        if($updateTask && $updateStatus){
            //mysql_query("COMMIT");
            $countDone = mysql_query("SELECT COUNT(*) as Done FROM task where goal_id = '$goal_id' and task_status ='Done'");
            $rowDone = mysql_fetch_array($countDone);

            $countTotal = mysql_query("SELECT COUNT(*) as Total FROM task where goal_id = '$goal_id'");
            $rowTotal = mysql_fetch_array($countTotal);

            if($rowDone['Done'] == $rowTotal['Total']){
                    //complete the goal
                    //mysql_query("START TRANSACTION");
                    $completeGoal = mysql_query("UPDATE goal set status = 'Done', date_finished = now() where goal_id = '$goal_id'");
                    if($completeGoal){
                        //mysql_query("COMMIT");

                        $qqq = mysql_query("INSERT INTO activity (project_id,from_id,from_type,message,date) VALUES ('$project_id', '$goal_id', 'Goal', 'has been completed',now())");

                        $queryGOALS = mysql_query("SELECT p.project_id, p.project_name, p.project_status, (SELECT COUNT(*) FROM goal where project_id = '$project_id') as TotalGoals, (SELECT COUNT(*) FROM goal where project_id = '$project_id' and status = 'Done') as DoneGoals FROM `projects` p where project_id = '$project_id'");

                        $row1 = mysql_fetch_array($queryGOALS);


                        $totalG = $row1["TotalGoals"];
                        $completeG = $row1["DoneGoals"];

                        if($totalG !=0){
                            if($totalG==$completeG){
                                $queryProCom = mysql_query("UPDATE projects set project_status = 'Done', date_finished = now() where project_id = '$project_id'");
                            }
                        }


                        $response["success"] = 1;
                        $response["message"] = "completed a goal";
                        echo json_encode($response);
                    }else{
                        //mysql_query("ROLLBACK");
                        $response["success"] = 0;
                        $response["message"] = "Something went wrong";
                        echo json_encode($response);
                       
                        
                    }
            }else{
                 $response["success"] = 1;
                $response["message"] = $rowDone['Done']."dilicompletedone".$rowTotal['Total'];
                echo json_encode($response);
            }

           
        }else{
            //mysql_query("ROLLBACK");
            $response["success"] = 0;
            $response["message"] = 'Something went wrong';
            echo json_encode($response);
        }

    }elseif ($row["status"]=="In progress") {

       //check if the task is for reopen or completion

        $checkTask = mysql_query("SELECT task_status from task where task_id='$task_id'");
        $rowx = mysql_fetch_array($checkTask);

        if($rowx["task_status"]=="Open"){
            //complete the task

            $completeTask = mysql_query("UPDATE task set task_status = 'Done',date_started = now(), date_finished = now(), started_by = '$user_id', completed_by = '$user_id' where task_id='$task_id'");
            $qq = mysql_query("INSERT INTO activity (project_id,from_id,from_type,message,date) VALUES ('$project_id', '$user_id', 'User', 'completed the task - ".$task_name."',now())");
            if($completeTask){
                //mysql_query("COMMIT");


                /*
                 * NOTIFY 
                 *
                 */

                #notify project leader

                if($project_leader==$user_id){

                }else{
                    $notifyPLstart = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$project_leader', 'Task', '$task_id','started the task - ".$task_name."', now())");
                    $notifyPL = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$project_leader', 'Task', '$task_id','completed the task - ".$task_name."', now())");
                }

                #notify task creator
                if($task_creator==$user_id || $task_creator==$project_leader){

                }else{
                    $notifyTLstart = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$task_creator', 'Task', '$task_id','started the task - ".$task_name."', now())");
                    $notifyTL = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$task_creator', 'Task', '$task_id','completed the task - ".$task_name."', now())");
                }

                #notify assignee
                $getAssignee = mysql_query("SELECT user_id from assignment where task_id = '$task_id'");
                $assign_id = 0;
                if(mysql_num_rows($getAssignee)>0){

                    $rowAsgn = mysql_fetch_array($getAssignee);
                    $assign_id = $rowAsgn["user_id"];

                    if($assign_id==$user_id || $assign_id==$project_leader || $assign_id==$task_creator){

                    }else{
                        $notifyAsgnStart = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$assign_id', 'Task', '$task_id','started the task - ".$task_name."', now())");
                        $notifyAsgn = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$assign_id', 'Task', '$task_id','completed the task - ".$task_name."', now())");
                    }
                }

                $getCommenters = mysql_query("SELECT DISTINCT user_id from comment where comment_type = 'Task' and comment_type_id = '$task_id'");
                if(mysql_num_rows($getCommenters)>0){
                    while ($rowComment = mysql_fetch_array($getCommenters)) {
                        $kid = $rowComment["user_id"];
                        if ($kid == $user_id || $kid == $project_leader || $kid == $task_creator) {
                            # code...
                        }else{
                            $notifyCommCenterStart = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$kid', 'Task', '$task_id','started the task - ".$task_name."', now())");
                            $notifyCommCenter = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$kid', 'Task', '$task_id','completed the task - ".$task_name."', now())");
                        }
                        # code...
                    }
                }

                /*
                 * DONE 
                 *
                 */

                $countDone = mysql_query("SELECT COUNT(*) as Done FROM task where goal_id = '$goal_id' and task_status ='Done'");
                $rowDone = mysql_fetch_array($countDone);

                $countTotal = mysql_query("SELECT COUNT(*) as Total FROM task where goal_id = '$goal_id'");
                $rowTotal = mysql_fetch_array($countTotal);
                
                $checkIfDone = mysql_query("SELECT (SELECT COUNT(*) from task where task_status = 'Done' and goal_id = 'goal_id') AS Completed, (SELECT COUNT(*) from task where goal_id = '$goal_id') As Total FROM `task` WHERE goal_id = '$goal_id' GROUP BY goal_id");
                $rowz = mysql_fetch_array($checkIfDone);

                if($rowDone['Done'] == $rowTotal['Total']){
                    //complete the goal
                    //mysql_query("START TRANSACTION");
                    $qqq = mysql_query("INSERT INTO activity (project_id,from_id,from_type,message,date) VALUES ('$project_id', '$goal_id', 'Goal', 'has been completed',now())");
                    $completeGoal = mysql_query("UPDATE goal set status = 'Done', date_finished = now() where goal_id = '$goal_id'");
                    if($completeGoal){
                        $queryGOALS = mysql_query("SELECT p.project_id, p.project_name, p.project_status, (SELECT COUNT(*) FROM goal where project_id = '$project_id') as TotalGoals, (SELECT COUNT(*) FROM goal where project_id = '$project_id' and status = 'Done') as DoneGoals FROM `projects` p where project_id = '$project_id'");

                        $row1 = mysql_fetch_array($queryGOALS);


                        $totalG = $row1["TotalGoals"];
                        $completeG = $row1["DoneGoals"];

                        if($totalG !=0){
                            if($totalG==$completeG){
                                $queryProCom = mysql_query("UPDATE projects set project_status = 'Done', date_finished = now() where project_id = '$project_id'");
                            }
                        }
                        //mysql_query("COMMIT");
                        $response["success"] = 1;
                        $response["message"] = "completed a goal";
                        echo json_encode($response);
                    }else{
                        //mysql_query("ROLLBACK");
                        $response["success"] = 0;
                        $response["message"] = "Something went wrong";
                        echo json_encode($response);
                       
                        
                    }
                }else{
                     $response["success"] = 1;
                    $response["message"] = $rowDone['Done']."dilicompletedone".$rowTotal['Total'];
                    echo json_encode($response);
                }
            }else{
                //mysql_query("ROLLBACK");
                $response["success"] = 0;
                $response["message"] = 'Something went wrong';
                echo json_encode($response);
            }


           
        }elseif ($rowx["task_status"]=="In progress") {
            $completeTask = mysql_query("UPDATE task set task_status = 'Done', date_finished = now(), completed_by = '$user_id' where task_id='$task_id'");
            $qq = mysql_query("INSERT INTO activity (project_id,from_id,from_type,message,date) VALUES ('$project_id', '$user_id', 'User', 'completed the task - ".$task_name."',now())");
            if($completeTask){
                //mysql_query("COMMIT");

                 /*
                 * NOTIFY 
                 *
                 */

                #notify project leader

                if($project_leader==$user_id){

                }else{
                    
                    $notifyPL = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$project_leader', 'Task', '$task_id','completed the task - ".$task_name."', now())");
                }

                #notify task creator
                if($task_creator==$user_id || $task_creator==$project_leader){

                }else{
                    
                    $notifyTL = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$task_creator', 'Task', '$task_id','completed the task - ".$task_name."', now())");
                }

                #notify assignee
                $getAssignee = mysql_query("SELECT user_id from assignment where task_id = '$task_id'");
                $assign_id = 0;
                if(mysql_num_rows($getAssignee)>0){

                    $rowAsgn = mysql_fetch_array($getAssignee);
                    $assign_id = $rowAsgn["user_id"];

                    if($assign_id==$user_id || $assign_id==$project_leader || $assign_id==$task_creator){

                    }else{
                        
                        $notifyAsgn = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$assign_id', 'Task', '$task_id','completed the task - ".$task_name."', now())");
                    }
                }

                $getCommenters = mysql_query("SELECT DISTINCT user_id from comment where comment_type = 'Task' and comment_type_id = '$task_id'");
                if(mysql_num_rows($getCommenters)>0){
                    while ($rowComment = mysql_fetch_array($getCommenters)) {
                        $kid = $rowComment["user_id"];
                        if ($kid == $user_id || $kid == $project_leader || $kid == $task_creator) {
                            # code...
                        }else{
                            
                            $notifyCommCenter = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$kid', 'Task', '$task_id','completed the task - ".$task_name."', now())");
                        }
                        # code...
                    }
                }

                /*
                 * DONE 
                 *
                 */


                $countDone = mysql_query("SELECT COUNT(*) as Done FROM task where goal_id = '$goal_id' and task_status ='Done'");
                $rowDone = mysql_fetch_array($countDone);

                $countTotal = mysql_query("SELECT COUNT(*) as Total FROM task where goal_id = '$goal_id'");
                $rowTotal = mysql_fetch_array($countTotal);
                
                $checkIfDone = mysql_query("SELECT (SELECT COUNT(*) from task where task_status = 'Done' and goal_id = 'goal_id') AS Completed, (SELECT COUNT(*) from task where goal_id = '$goal_id') As Total FROM `task` WHERE goal_id = '$goal_id' GROUP BY goal_id");
                $rowz = mysql_fetch_array($checkIfDone);

                if($rowDone['Done'] == $rowTotal['Total']){
                    //complete the goal
                    //mysql_query("START TRANSACTION");
                    $qqq = mysql_query("INSERT INTO activity (project_id,from_id,from_type,message,date) VALUES ('$project_id', '$goal_id', 'Goal', 'has been completed',now())");
                    $completeGoal = mysql_query("UPDATE goal set status = 'Done', date_finished = now() where goal_id = '$goal_id'");
                    if($completeGoal){
                        $queryGOALS = mysql_query("SELECT p.project_id, p.project_name, p.project_status, (SELECT COUNT(*) FROM goal where project_id = '$project_id') as TotalGoals, (SELECT COUNT(*) FROM goal where project_id = '$project_id' and status = 'Done') as DoneGoals FROM `projects` p where project_id = '$project_id'");

                        $row1 = mysql_fetch_array($queryGOALS);


                        $totalG = $row1["TotalGoals"];
                        $completeG = $row1["DoneGoals"];

                        if($totalG !=0){
                            if($totalG==$completeG){
                                $queryProCom = mysql_query("UPDATE projects set project_status = 'Done', date_finished = now() where project_id = '$project_id'");
                            }
                        }
                        //mysql_query("COMMIT");
                        $response["success"] = 1;
                        $response["message"] = "completed a goal";
                        echo json_encode($response);
                    }else{
                        //mysql_query("ROLLBACK");
                        $response["success"] = 0;
                        $response["message"] = "Something went wrong";
                        echo json_encode($response);
                       
                        
                    }
                }else{
                     $response["success"] = 1;
                    $response["message"] = $rowDone['Done']."dilicompletedone".$rowTotal['Total'];
                    echo json_encode($response);
                }
            }else{
                //mysql_query("ROLLBACK");
                $response["success"] = 0;
                $response["message"] = 'Something went wrong';
                echo json_encode($response);
            }
        }else{
            //reopen the task
            $reopenTask = mysql_query("UPDATE task set task_status = 'In progress', completed_by = '', date_finished='' where task_id = '$task_id'");
            $qqx = mysql_query("INSERT INTO activity (project_id,from_id,from_type,message,date) VALUES ('$project_id', '$user_id', 'User', 're-opened the task - ".$task_name."',now())");
            if($reopenTask){

                #NOTIFYYYYYYYYYYY
                 /*
                 * NOTIFY 
                 *
                 */

                #notify project leader

                if($project_leader==$user_id){

                }else{
                    
                    $notifyPL = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$project_leader', 'Task', '$task_id','reopened the task - ".$task_name."', now())");
                }

                #notify task creator
                if($task_creator==$user_id || $task_creator==$project_leader){

                }else{
                    
                    $notifyTL = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$task_creator', 'Task', '$task_id','reopened the task - ".$task_name."', now())");
                }

                #notify assignee
                $getAssignee = mysql_query("SELECT user_id from assignment where task_id = '$task_id'");
                $assign_id = 0;
                if(mysql_num_rows($getAssignee)>0){

                    $rowAsgn = mysql_fetch_array($getAssignee);
                    $assign_id = $rowAsgn["user_id"];

                    if($assign_id==$user_id || $assign_id==$project_leader || $assign_id==$task_creator){

                    }else{
                        
                        $notifyAsgn = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$assign_id', 'Task', '$task_id','reopened the task - ".$task_name."', now())");
                    }
                }

                $getCommenters = mysql_query("SELECT DISTINCT user_id from comment where comment_type = 'Task' and comment_type_id = '$task_id'");
                if(mysql_num_rows($getCommenters)>0){
                    while ($rowComment = mysql_fetch_array($getCommenters)) {
                        $kid = $rowComment["user_id"];
                        if ($kid == $user_id || $kid == $project_leader || $kid == $task_creator) {
                            # code...
                        }else{
                            
                            $notifyCommCenter = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$kid', 'Task', '$task_id','reopened the task - ".$task_name."', now())");
                        }
                        # code...
                    }
                }

                /*
                 * DONE 
                 *
                 */
                #ENDDD

                //mysql_query("COMMIT");
                $response["success"] = 1;
                echo json_encode($response);
            }else{
                //mysql_query("ROLLBACK");
                $response["success"] = 0;
                $response["message"] = 'Something went wrong';
                echo json_encode($response);
            }
        }
    }elseif ($row["status"]=="Done") {
       
       //revert goal & project to in progresss

        $revertGoal = mysql_query("UPDATE goal set status = 'In progress', date_finished='' where goal_id = '$goal_id'");
        $qwq = mysql_query("INSERT INTO activity (project_id,from_id,from_type,message,date) VALUES ('$project_id', '$goal_id', 'Goal', 'has been re-opened', now())");
        $revertProject = mysql_query("UPDATE projects set project_status = 'In progress', date_finished = '' where project_id = '$project_id'");
        //reopen the task
        $reopenTask2 = mysql_query("UPDATE task set task_status = 'In progress', completed_by = '', date_finished='' where task_id = '$task_id'");
        $qqxx = mysql_query("INSERT INTO activity (project_id,from_id,from_type,message,date) VALUES ('$project_id', '$user_id', 'User', 're-opened the task - ".$task_name."',now())");
        if($reopenTask2 && $revertGoal){
            //mysql_query("COMMIT");
                #notify task creator
                if($task_creator==$user_id || $task_creator==$project_leader){

                }else{
                    
                    $notifyTL = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$task_creator', 'Task', '$task_id','reopened the task - ".$task_name."', now())");
                }

                #notify assignee
                $getAssignee = mysql_query("SELECT user_id from assignment where task_id = '$task_id'");
                $assign_id = 0;
                if(mysql_num_rows($getAssignee)>0){

                    $rowAsgn = mysql_fetch_array($getAssignee);
                    $assign_id = $rowAsgn["user_id"];

                    if($assign_id==$user_id || $assign_id==$project_leader || $assign_id==$task_creator){

                    }else{
                        
                        $notifyAsgn = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$assign_id', 'Task', '$task_id','reopened the task - ".$task_name."', now())");
                    }
                }

                $getCommenters = mysql_query("SELECT DISTINCT user_id from comment where comment_type = 'Task' and comment_type_id = '$task_id'");
                if(mysql_num_rows($getCommenters)>0){
                    while ($rowComment = mysql_fetch_array($getCommenters)) {
                        $kid = $rowComment["user_id"];
                        if ($kid == $user_id || $kid == $project_leader || $kid == $task_creator) {
                            # code...
                        }else{
                            
                            $notifyCommCenter = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$kid', 'Task', '$task_id','reopened the task - ".$task_name."', now())");
                        }
                        # code...
                    }
                }

                /*
                 * DONE 
                 *
                 */
                #ENDDD

            $response["success"] = 1;
            echo json_encode($response);
        }else{
            //mysql_query("ROLLBACK");
            $response["success"] = 0;
            $response["message"] = 'Something went wrong';
            echo json_encode($response);
        }
    }
    


    

   
    
 

}
?>