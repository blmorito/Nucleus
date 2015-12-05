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
if (isset($_POST['workspace_id'])) {
   
    $user_id = $_POST["user_id"];
    $profile_id = $_POST["profile_id"];
    $workspace_id = $_POST["workspace_id"];

    $result = mysql_query("SELECT a.activity_id, a.project_id,p.project_name, a.from_id, tu.full_name, a.message, a.date  FROM activity a LEFT JOIN (SELECT project_id, project_name from projects) p ON p.project_id = a.project_id LEFT JOIN (SELECT user_id, full_name from tbl_usersinfo) tu ON tu.user_id = a.from_id  where from_id = '$profile_id' and from_type = 'User' and a.project_id IN (SELECT project_id from projects_users where user_id = '$user_id'  and project_id IN (SELECT project_id from projects where workspace_id = '$workspace_id')) ORDER BY p.project_name ASC, a.date DESC");

    if(mysql_num_rows($result)>0){

        $response["activities"] = array();
        while($row = mysql_fetch_array($result)){

            $act = array();
            $act["act_pname"] = $row["project_name"];
            $act["act_fname"] = $row["full_name"];
            $act["act_content"] = $row["message"];
            

            $phpdate = strtotime($row["date"]);
            
            
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

            $act["act_date"] = $ago." ago";

           

            array_push($response["activities"], $act);
        }

        $response["success"] = 1;
        echo json_encode($response);


    }else{
        $response["success"] = 0;
        $response["message"] = "No activities yet on this user";
        echo json_encode($response);
    }

    

    
 

}
?>