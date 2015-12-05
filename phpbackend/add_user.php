<?php
 
/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */
 
// array for JSON response
$response = array();
 
// check for required fields
if (isset($_POST['email']) && isset($_POST['password']) ) {
    $fullName = $_POST['fullName'];
    
    $comp_org = $_POST['comp_org'];
    
    $email = $_POST['email'];
    $password = $_POST['password'];
    $encrypt = sha1($password);
   
 
    // include db connect class
    require_once __DIR__ . '/db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
    

    $result = mysql_query("Select * from tbl_users where email = '$email'");
    $numrow = mysql_num_rows($result);
    if ($numrow>0) {
        $response["success"] = 0;
        $response["errorCode"] = 1;
        $response["message"] = "Email already exists";
        echo json_encode($response);
    }else{
        if (!filter_var($email, FILTER_VALIDATE_EMAIL) === false) {
            mysql_query("START TRANSACTION");
          // mysql inserting a new row
            $result = mysql_query("INSERT INTO tbl_users(email, password) VALUES('$email','$encrypt')");

            $recentId = mysql_insert_id();

            $rnd = mt_rand(1,12);
            $result2 = mysql_query("INSERT INTO tbl_usersinfo (user_id, full_name, comp_org, date_created, avatar_id) VALUES ('$recentId','$fullName', '$comp_org', now(), '$rnd' )");
            $tempName = $fullName."\'s WS";
            $result3 = mysql_query("INSERT INTO workspaces (workspace_name, workspace_desc, ws_date_created) VALUES ('$tempName','', now())");
            $recentId2 = mysql_insert_id();
            
            $result4 = mysql_query("INSERT INTO ws_members (workspace_id, user_id, user_level_id, date_joined) VALUES ('$recentId2','$recentId','1', now() )");
            $result5 = mysql_query("UPDATE tbl_usersinfo SET recent_ws = '$recentId2' where user_id = '$recentId'");
                // check if row inserted or not
                if ($result and $result2 and $result3 and $result4 and $result5) {
                    mysql_query("COMMIT");
                    // successfully inserted into database
                    $response["success"] = 1;
                    $response["message"] = "successfully signed up.";
                    $response["user_id"] = $recentId;
                    $response["user_email"] = $email;
                    $response["workspace_id"] = $recentId2;
                    $response["ws_user_level_id"] = 1;
             
                    // echoing JSON response
                    echo json_encode($response);
                } else {
                    mysql_query("ROLLBACK");
                    // failed to insert row
                    $response["success"] = 0;
                    $response["errorCode"] = 2;
                    $response["message"] = "Oops! An error occurred. Please try again";
             
                    // echoing JSON response
                    echo json_encode($response);
                }
        } else {

            $response["success"] = 0;
            $response["errorCode"] = 2;
            $response["message"] = "Invalid email. Please try again.";
     
            // echoing JSON response
            echo json_encode($response);
          
        }

        
    }
    
} else {
    // required field is missing
    $response["success"] = 0;
    $response["errorCode"] = 1;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>