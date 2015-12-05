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
if (isset($_POST['user_id'])) {
   
   
    $user_id = $_POST["user_id"];

    $result = mysql_query("SELECT w.invite_id as invite_id, w.user_id as inviter_id, w.workspace_id as workspace_id, w.project_id as project_id, w.type as type, w.email, w.date_invited as date_invited, tu.full_name as FullName, ws.workspace_name as workspace_name, p.project_name as project_name, av.avatar_img as avatar_img, count(*) as num from ws_invites w LEFT JOIN (SELECT tbl_usersinfo.user_id, tbl_usersinfo.full_name, tbl_usersinfo.avatar_id from tbl_usersinfo) tu ON tu.user_id = w.user_id LEFT JOIN (SELECT avatars.avatar_id, avatars.avatar_img from avatars) av ON tu.avatar_id = av.avatar_id LEFT JOIN (SELECT workspaces.workspace_id, workspaces.workspace_name from workspaces) ws ON ws.workspace_id = w.workspace_id LEFT JOIN (SELECT projects.project_id, projects.project_name from projects) p ON p.project_id = w.project_id\n"
    . " where w.email = (SELECT tbl_users.email from tbl_users where tbl_users.user_id = '$user_id')  GROUP BY w.workspace_id, w.project_id ORDER BY `w`.`date_invited` DESC");

    if(mysql_num_rows($result)>0){

        $response["invites"] = array();

        while($row = mysql_fetch_array($result)){
            

            $invite = array();
            $invite["invite_id"] = $row["invite_id"];
            $ws = $row['workspace_id'];
            $p = $row['project_id'];
            $u = $row['inviter_id'];
            $count = $row["num"];
            if ($count>1){
                if ($count==2){
                    $result2 = mysql_query("SELECT * FROM `ws_invites` w LEFT JOIN (SELECT tbl_usersinfo.user_id, tbl_usersinfo.full_name from tbl_usersinfo) tu on w.user_id = tu.user_id WHERE email =  (SELECT tbl_users.email from tbl_users where tbl_users.user_id = '$user_id') and workspace_id = '$ws' and project_id = '$p' and w.user_id != '$u'");
                    $rowx = mysql_fetch_array($result2);
                    $invite["full_name"] = $row["FullName"]." and ".$rowx["full_name"];
                }else{
                    $count--;
                    $invite["full_name"] = $row["FullName"]." and ".$count." others";
                }
            }else{
                $invite["full_name"] = $row["FullName"];
            }


            
            $invite["workspace_name"] = $row["workspace_name"];
            $invite["project_name"] = $row["project_name"];
            $invite["type"] = $row["type"];
            $timeX = "".$row["date_invited"];

            $timeY = strtotime($timeX);
            date_default_timezone_set('Asia/Manila');
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
            $invite["date_invited"] = $ago." ago";

            

            
            $invite["avatar_path"] = base64_encode($row["avatar_img"]);
            

            array_push($response["invites"], $invite);

            

        }
        $response["success"]=1;
        echo json_encode($response);
    }else{
        $response["success"] = 0;
        $response["message"] = "No invites for this user";
        echo json_encode($response);
    }

    
 

}
?>