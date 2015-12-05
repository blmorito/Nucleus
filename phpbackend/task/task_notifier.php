<?php
 date_default_timezone_set('Asia/Manila');
/*
 * Following code will get single product details
 * A product is identified by product id (pid)
 */
 
// array for JSON response
$response = array();
 
// include db connect class
require_once __DIR__ . '../../db_connect.php';
require 'C:\xampp\htdocs\nucleus\PHPMailer\PHPMailerAutoload.php';
 
// connecting to db
$db = new DB_CONNECT();
 	$wew2 = date('M d, Y h:i a', time());
 	error_log(PHP_EOL."".$wew2." - Task Reminder initialized", 3, "my-errors.log");
	                    
// check for post data
	$getTasks = mysql_query("SELECT t.task_id, t.goal_id,p.project_id,pn.project_name,pl.user_id AS projectLeader,ple.email AS projectLeaderEmail, t.user_id AS taskCreator, tuh.full_name As taskCreatorName, u.email AS creatorEmail, t.task_name, t.date_due, t.task_status, t.started_by, a.user_id AS assignee, ue.email AS assigneeEmail FROM `task`t LEFT JOIN (SELECT user_id, email FROM tbl_users) u ON u.user_id = t.user_id LEFT JOIN (SELECT goal_id,project_id from goal) p ON p.goal_id = t.goal_id LEFT JOIN (SELECT project_id,project_name FROM projects) pn ON pn.project_id = p.project_id LEFT JOIN (SELECT user_id, project_id from projects_users where user_level_id = 1)pl  ON pl.project_id = p.project_id  LEFT JOIN (SELECT user_id, task_id from assignment) a ON a.task_id = t.task_id LEFT JOIN (SELECT user_id, email from tbl_users) ple ON ple.user_id = pl.user_id LEFT JOIN (SELECT user_id, email FROM tbl_users) ue ON ue.user_id = a.user_id LEFT JOIN (SELECT user_id, full_name from tbl_usersinfo) tuh ON tuh.user_id = t.user_id  where date_due = CURDATE() + INTERVAL 2 DAY and t.task_status <> 'Done'") or trigger_error(mysql_error());

	if(mysql_num_rows($getTasks)>0){
		while($row = mysql_fetch_array($getTasks)){
			//error_log(PHP_EOL."SUBJECT: Task Nearing Deadline".PHP_EOL."PROJECT: ".$row['project_name'].PHP_EOL.PHP_EOL."TASK: ".$row['task_name'].PHP_EOL, 3, "my-errors.log");
			$wew = date('M d, Y', time()+86400+86400);
			
		//email for project leader

   		 $body = "<div>
                  <h3>Task Reminder by Nucleus</h3><br>

                  <p>The deadline of the task ".$row["task_name"]." created by ".$row["taskCreatorName"]." is two days from now (".$wew.").</p><br>
                  <p>Please act on this task as soon as possible.</p>
                  <br>
                  <small><i>This email is auto generated. Pls do not reply.</i></small>
                  
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
                $mail->FromName = "Nucleus | ".$row["project_name"];

                
                $mail->addAddress($row["projectLeaderEmail"], 'Nucleus');
               
                
                $mail->addReplyTo('server.nucleus@gmail.com', 'Nucleus Team');
                $mail->WordWrap = 50;
                
                $mail->Subject = 'Nucleus | '.$row["project_name"].' | Task Nearing Deadline';
                
                $mail->Body    = $body;
                $mail->isHTML(true);

                

                if(!$mail->send()) {
                    error_log(PHP_EOL."Mailing for ".$row["projectLeaderEmail"]." failed", 3, "my-errors.log");
                    
                 
                   //exit;
                }
                else{
                   error_log(PHP_EOL."Mailing for ".$row["projectLeaderEmail"]." succeeded", 3, "my-errors.log");
                    //exit;
                    
                }

           if ($row["projectLeaderEmail"]==$row["creatorEmail"]){
           		
           }else{
           		$body = "<div>
                  <h3>Task Reminder by Nucleus</h3><br>

                  <p>The deadline of the task ".$row["task_name"]." created by ".$row["taskCreatorName"]." is two days from now (".$wew.").</p><br>
                  <p>Please act on this task as soon as possible.</p>
                  <br>
                  <small><i>This email is auto generated. Pls do not reply.</i></small>
                  
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
                $mail->FromName = "Nucleus | ".$row["project_name"];

                
                $mail->addAddress($row["creatorEmail"], 'Nucleus');
               
                
                $mail->addReplyTo('server.nucleus@gmail.com', 'Nucleus Team');
                $mail->WordWrap = 50;
                
                $mail->Subject = 'Nucleus | '.$row["project_name"].' | Task Nearing Deadline';
                
                $mail->Body    = $body;
                $mail->isHTML(true);

                

                if(!$mail->send()) {
                    error_log(PHP_EOL."Mailing for ".$row["creatorEmail"]." failed", 3, "my-errors.log");
                    
                 
                   //exit;
                }
                else{
                   error_log(PHP_EOL."Mailing for ".$row["creatorEmail"]." succeeded", 3, "my-errors.log");
                    //exit;
                    
                }
           }


           if ($row["assignee"]==0){

           }else{
           		if($row["assigneeEmail"] == $row["projectLeaderEmail"] || $row["assigneeEmail"] == $row["creatorEmail"]){

           		}else{
           			$body = "<div>
                  <h3>Task Reminder by Nucleus</h3><br>

                  <p>The deadline of the task ".$row["task_name"]." created by ".$row["taskCreatorName"]." is two days from now (".$wew.").</p><br>
                  <p>Please act on this task as soon as possible.</p>
                  <br>
                  <small><i>This email is auto generated. Pls do not reply.</i></small>
                  
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
	                $mail->FromName = "Nucleus | ".$row["project_name"];

	                
	                $mail->addAddress($row["assigneeEmail"], 'Nucleus');
	               
	                
	                $mail->addReplyTo('server.nucleus@gmail.com', 'Nucleus Team');
	                $mail->WordWrap = 50;
	                
	                $mail->Subject = 'Nucleus | '.$row["project_name"].' | Task Nearing Deadline';
	                
	                $mail->Body    = $body;
	                $mail->isHTML(true);

	                

	                if(!$mail->send()) {
	                    error_log(PHP_EOL."".$wew2."Mailing for ".$row["assigneeEmail"]." failed", 3, "my-errors.log");
	                    
	                 
	                   //exit;
	                }
	                else{
	                   error_log(PHP_EOL."".$wew2."Mailing for ".$row["assigneeEmail"]." succeeded", 3, "my-errors.log");
	                    //exit;
	                    
	                }
           		}
           }     


		}
	}
?>