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
    $user_id = $_POST["user_id"];

    $wew = mysql_query("SELECT user_level_id from ws_members where workspace_id = '$ws_id' and user_id='$user_id'");
    $rowww = mysql_fetch_array($wew);

    $response["ws_user_level_id"] = $rowww["user_level_id"];


	$result =  mysql_query("SELECT w.workspace_id , w.workspace_name as ws_name,w.workspace_desc as ws_desc, w.ws_date_created as ws_date, m.user_id , p.full_name as ws_creator, c.memberNum as memberNum, j.projectNum as projectNum from workspaces w LEFT JOIN (SELECT ws_members.workspace_id, ws_members.user_id from ws_members where user_level_id = '1') m ON m.workspace_id = w.workspace_id LEFT JOIN (SELECT tbl_usersinfo.user_id, tbl_usersinfo.full_name from tbl_usersinfo) p ON p.user_id = m.user_id LEFT JOIN (SELECT ws_members.workspace_id, COUNT(*) as memberNum from ws_members GROUP BY ws_members.workspace_id) c ON c.workspace_id = w.workspace_id LEFT JOIN (SELECT projects.workspace_id, count(*) as projectNum from projects GROUP BY projects.workspace_id) j ON j.workspace_id = w.workspace_id where w.workspace_id = '$ws_id'") or trigger_error(mysql_error());

	if(mysql_num_rows($result)>0){
		$row = mysql_fetch_array($result);
		$response["ws_name"] = $row["ws_name"];
		$response["ws_desc"] = $row["ws_desc"];

		$phpdate = strtotime($row["ws_date"]);
		$phpdatex = date( 'F d, Y', $phpdate );
		$response["ws_date"] = $phpdatex;

		$response["ws_creator"] = $row["ws_creator"];
		$response["ws_memberNum"] = $row["memberNum"];
		$response["ws_projectNum"] = $row["projectNum"];

		$response["success"] = 1;
		echo json_encode($response);

		

	}else{
		 $response["success"] = 0;
        $response["message"] = "sql error";
        echo json_encode($response);
	}
   
   
    // $user_id = $_POST["user_id"];

    // $result = mysql_query("SELECT * FROM ws_invites where email = (SELECT email from tbl_users where user_id = '$user_id') ORDER BY date_invited DESC");

    // if(mysql_num_rows($result)>0){

    //     $response["invites"] = array();

    //     while($row = mysql_fetch_array($result)){
    //         $temp = $row["user_id"];
    //         $resultx = mysql_query("SELECT full_name from tbl_usersinfo where user_id = '$temp'");
    //         $rowx = mysql_fetch_array($resultx);
            
    //         $temp2 = $row["workspace_id"];
    //         $resulty = mysql_query("SELECT workspace_name from workspaces where workspace_id = '$temp2'");
    //         $rowy = mysql_fetch_array($resulty);

    //         $tempz = $row["project_id"];
    //         $resultz = mysql_query("SELECT project_name from projects where project_id = '$tempz'");
    //         $rowz = mysql_fetch_array($resultz);

    //         $invite = array();
    //         $invite["invite_id"] = $row["invite_id"];
    //         $invite["full_name"] = $rowx["full_name"];
    //         $invite["workspace_name"] = $rowy["workspace_name"];
    //         $invite["project_name"] = $rowz["project_name"];
    //         $timeX = "".$row["date_invited"];

    //         $timeY = strtotime($timeX);
    //         date_default_timezone_set('Asia/Manila');
    //         $time = time() - $timeY; // to get the time since that moment

    //         $tokens = array (
    //             31536000 => 'year',
    //             2592000 => 'month',
    //             604800 => 'week',
    //             86400 => 'day',
    //             3600 => 'hour',
    //             60 => 'minute',
    //             1 => 'second'
    //         );
    //         $ago = null;
    //         foreach ($tokens as $unit => $text) {
    //             if ($time < $unit) continue;
    //             $numberOfUnits = floor($time / $unit);
    //             $ago .= $numberOfUnits.' '.$text.(($numberOfUnits>1)?'s':'');

    //             if ($ago!=null){
    //                 break;
    //             }   
    //         }
    //         $invite["date_invited"] = $ago." ago";

    //         $result_ava = mysql_query("SELECT avatar_img from avatars where avatar_id = (SELECT avatar_id from tbl_usersinfo where user_id = '$temp')");
    //         $row_ava = mysql_fetch_array($result_ava);

        
    //         $invite["avatar_path"] = base64_encode($row_ava["avatar_img"]);
            

    //         array_push($response["invites"], $invite);

            

    //     }
    //     $response["success"]=1;
    //     echo json_encode($response);
}else{
    $response["success"] = 0;
    $response["message"] = "Data error";
    echo json_encode($response);
}

    
 


?>