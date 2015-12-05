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
   
   
    $ws_id = $_POST["workspace_id"];

    $result = mysql_query("SELECT * FROM ws_invites where workspace_id = '$ws_id' and email NOT IN (SELECT t.email from tbl_users t LEFT JOIN (SELECT ws_members.workspace_id, ws_members.user_id from ws_members) m ON m.user_id = t.user_id) GROUP BY email ORDER BY `ws_invites`.`date_invited` DESC") or trigger_error(mysql_error());

    if(mysql_num_rows($result)>0){

        $response["pending"] = array();
    	
        while($row = mysql_fetch_array($result)){
       		
       		$member = array();
            
            $member["email"] = $row["email"];
            
            $timeX = "".$row["date_invited"];

            $timeY = strtotime($timeX);
            
            $time = time() - $timeY; // to get the time since that moment

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
            $member["date_invited"] = "Invited ".$ago." ago";
           	
			
			array_push($response["pending"], $member);

            

        }
        $response["success"]=1;
        echo json_encode($response);
    }else{
        $response["success"] = 0;
        $response["message"] = "Error zzzzzzzzzzz";
        $response["errorCode"] = 1;
        echo json_encode($response);
    }

    
 

}
?>