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
    
    $workspace_id = $_POST["workspace_id"];

    $count = mysql_query("SELECT * FROM notifications where to_id = '$user_id' and project_id IN (SELECT project_id from projects where workspace_id = '$workspace_id') and status = 'Unread' ");
    $counter = mysql_num_rows($count);
    $limit = $counter + 10;


    $query = mysql_query("SELECT n.project_id, p.project_name, n.from_id, tu.full_name,a.avatar_img, n.to_id, n.type, n.type_id, n.content, n.date, n.status from notifications n LEFT JOIN (SELECT project_id, project_name from projects) p ON p.project_id = n.project_id LEFT JOIN (SELECT user_id, full_name, avatar_id from tbl_usersinfo) tu ON tu.user_id = n.from_id LEFT JOIN (SELECT avatar_id, avatar_img from avatars) a ON a.avatar_id = tu.avatar_id where n.to_id = '$user_id'  and n.project_id IN (SELECT project_id from projects where workspace_id='$workspace_id') ORDER BY n.date DESC LIMIT ".$limit."");

    if(mysql_num_rows($query)>0){

        $response["notifications"] = array();
        while($row = mysql_fetch_array($query)){

            $notif = array();
            $notif["project_id"] = $row["project_id"];
            $notif["project_name"] = $row["project_name"];
            $notif["type"] = $row["type"];
            $notif["type_id"] = $row["type_id"];
            $notif["status"] = $row["status"];
            $notif["content"] = '<b>'.$row["full_name"].'</b>&nbsp;'.$row["content"];

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

            $notif["date"] = $ago." ago";
            $notif["avatar"] = base64_encode($row["avatar_img"]);
           

            array_push($response["notifications"], $notif);
        }


        $changeStatus = mysql_query("UPDATE notifications set status = 'Read' where to_id = '$user_id' and project_id IN (SELECT project_id from projects where workspace_id = '$workspace_id')");

        $response["success"] = 1;
        echo json_encode($response);


    }else{
        $response["success"] = 0;
        $response["message"] = "No notifications yet on this user";
        echo json_encode($response);
    }

    

    
 

}
?>