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

    $result = mysql_query("SELECT ac.activity_id, ac.project_id,pr.project_name, ac.from_id,ac.from_type,tu.full_name,g.goal_name, ac.message, ac.date, av.avatar_img FROM `activity` ac  LEFT JOIN (SELECT p.project_id, p.project_name from projects p) pr ON pr.project_id = ac.project_id LEFT JOIN (SELECT t.user_id, t.full_name, t.avatar_id from tbl_usersinfo t) tu ON tu.user_id = ac.from_id and ac.from_type = 'User' LEFT JOIN (SELECT avx.avatar_id, avx.avatar_img from avatars avx) av ON av.avatar_id = tu.avatar_id LEFT JOIN (SELECT goal_id, goal_name from goal) g ON g.goal_id = ac.from_id and ac.from_type = 'Goal' WHERE ac.project_id = '$project_id' ORDER BY ac.date DESC");

    if(mysql_num_rows($result)>0){

        $response["activities"] = array();
        while($row = mysql_fetch_array($result)){

            $act = array();
            #$act["act_pname"] = $row["project_name"];
            if (is_null($row["full_name"])) {
                $act["act_fname"] = 'The goal '.$row["goal_name"];
            }else{
                $act["act_fname"] = $row["full_name"];
            }

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


            if (is_null($row["avatar_img"])) {
                $act["act_avatar"] = "";
            }else{
                $act["act_avatar"] = base64_encode($row["avatar_img"]);
            }
            

            array_push($response["activities"], $act);
        }

        $response["success"] = 1;
        echo json_encode($response);


    }else{
        $response["success"] = 0;
        $response["message"] = "No activities yet on this workspace";
        echo json_encode($response);
    }

    

    
 

}
?>