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
   
    $workspace_creator = $_POST["workspace_creator"];

    $workspace_id = $_POST["workspace_id"];
    $workspace_name = mysql_real_escape_string($_POST["workspace_name"]);
    $workspace_desc = mysql_real_escape_string($_POST["workspace_desc"]);
    mysql_query("START TRANSACTION");
    $xx = mysql_query("SELECT user_id from ws_members where user_level_id = '1' and workspace_id = '$workspace_id'");
    $yu = mysql_fetch_array($xx);
    $yuu = $yu["user_id"];

    $res = mysql_query("UPDATE ws_members set user_level_id = '3' where user_level_id = '1' and workspace_id = '$workspace_id'");

    $rez = mysql_query(("UPDATE ws_members set user_level_id = '2' where user_id = '$yuu' and workspace_id = '$workspace_id'"));
    $res2 = mysql_query("UPDATE ws_members set user_level_id = '1' where workspace_id = '$workspace_id' and user_id = '$workspace_creator'");
    $result = mysql_query("UPDATE workspaces set workspace_name = '$workspace_name', workspace_desc = '$workspace_desc' where workspace_id = '$workspace_id'") or trigger_error(mysql_error());

    if($result && $res && $res){
        mysql_query("COMMIT");
        $response["success"] = 1;
        echo json_encode($response);
    }else{
        mysql_query("ROLLBACK");
        $response["success"] =0;
        $response["message"] = "Something went wrong";
        echo json_encode($response);
    }


    
 

}
?>

