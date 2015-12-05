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
    $sortType = $_POST["sortType"];

    if($sortType=="newest"){
        $result = mysql_query("SELECT d.discussion_id, d.user_id, tu.full_name, d.date_posted, d.d_subject, d.d_body, c.comment_count FROM `discussion` d LEFT JOIN tbl_usersinfo tu ON tu.user_id = d.user_id LEFT JOIN (SELECT comment_type_id, count(*) as comment_count from comment where comment_type='Discussion' GROUP BY comment_type_id) c ON c.comment_type_id = d.discussion_id where project_id = '$project_id' ORDER BY date_posted DESC") or trigger_error(mysql_error());
    }else{
        $result = mysql_query("SELECT d.discussion_id, d.user_id, tu.full_name, d.date_posted, d.d_subject, d.d_body, c.comment_count FROM `discussion` d LEFT JOIN tbl_usersinfo tu ON tu.user_id = d.user_id LEFT JOIN (SELECT comment_type_id, count(*) as comment_count from comment where comment_type='Discussion' GROUP BY comment_type_id) c ON c.comment_type_id = d.discussion_id where project_id = '$project_id' ORDER BY date_posted ASC") or trigger_error(mysql_error());
    }
    

    if(mysql_num_rows($result)>0){

        $response["discussions"] = array();
    	
        while($row = mysql_fetch_array($result)){
       		
       		$discussion = array();
            $discussion["discussion_id"] = $row["discussion_id"];
            $discussion["discussion_author"] = $row["full_name"];
            $discussion["discussion_author_id"] = $row["user_id"];


            if($row["comment_count"]>0){
                $discussion["discussion_num_comments"] = $row["comment_count"];
            }else{
                $discussion["discussion_num_comments"] = 0;
            }

            $phpdate = strtotime($row["date_posted"]);
            
            date_default_timezone_set('Asia/Manila');
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
            $discussion["ago"] = $ago." ago";  
            $discussion["subject"] = $row["d_subject"];
            $discussion["body"] = $row["d_body"];

            
			
			array_push($response["discussions"], $discussion);

            

        }
        $response["success"]=1;
        echo json_encode($response);
    }else{
        $response["success"] = 0;
        $response["message"] = "Error CYA";
        $response["errorCode"] = 1;
        echo json_encode($response);
    }

    
 

}
?>