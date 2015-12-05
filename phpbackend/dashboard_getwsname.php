<?php
 
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
   
    $workspace_id = $_POST['workspace_id'];
    $user_id = $_POST["user_id"];
 
    // get a product from products table
    $result = mysql_query("Select * FROM workspaces WHERE workspace_id = $workspace_id");
 
    if (!empty($result)) {
        // check for empty result
        if (mysql_num_rows($result) > 0) {
 
            $row = mysql_fetch_array($result);
            $result2 = mysql_query("SELECT DISTINCT workspace_id, project_id from ws_invites where email = (SELECT email from tbl_users where user_id = '$user_id')");
            $numrowx = mysql_num_rows($result2);

            $response["myInvites"] = $numrowx;
            $response["workspace_name"] = $row["workspace_name"];

            $queryNotifications = mysql_query("SELECT * from notifications where to_id = '$user_id' and status = 'Unread' and project_id IN (SELECT project_id from projects where workspace_id = '$workspace_id')") or trigger_error(mysql_error());

            $numNotif = mysql_num_rows($queryNotifications);
            $response["myNotifications"] = $numNotif;
            
            
            
            // success
            $response["success"] = 1;

            $response["projects"] = array();
            $resultX = mysql_query("SELECT pu.project_id, px.project_name, px.workspace_id, pu.user_level_id FROM projects_users pu LEFT JOIN (SELECT tx.user_id, tx.full_name from tbl_usersinfo tx) tu ON tu.user_id = pu.user_id LEFT JOIN (SELECT p.project_id, p.workspace_id, p.project_name from projects p) px ON px.project_id = pu.project_id where pu.user_id = '$user_id' and px.workspace_id = '$workspace_id' ");

            while($rowX = mysql_fetch_array($resultX)){
                $project = array();
                $project["p_id"] = $rowX["project_id"];
                $project["p_name"] = $rowX["project_name"];
                $project["p_level"] = $rowX["user_level_id"];

                array_push($response["projects"], $project);
            }
 
           
 
            // echoing JSON response
            echo json_encode($response);
        } else {
            // no product found
            $response["success"] = 0;
            $response["message"] = "Unable to retrieve workspace namezzzzz";
 
            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no product found
        $response["success"] = 0;
        $response["message"] = "Unable to retrieve workspace namezzz";
 
        // echo no users JSON
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["errorCode"] = 1;
    $response["success"] = 0;
    $response["message"] = "Something went wrong";
 
    // echoing JSON response
    echo json_encode($response);
}
?>