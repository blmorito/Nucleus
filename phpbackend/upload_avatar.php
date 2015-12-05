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
    $avatar = $_POST["avatar"];

    $binary = mysql_real_escape_string(base64_decode($avatar));
    header('Content-Type: bitmap; charset=utf-8');

    mysql_query("START TRANSACTION");
    $result = mysql_query("INSERT INTO avatars (avatar_img) VALUES ('$binary')") or trigger_error(mysql_error());
    $recentId = mysql_insert_id();
    $result2 = mysql_query("UPDATE tbl_usersinfo SET avatar_id = '$recentId' where user_id = '$user_id'");
    if($result && $result2){
        mysql_query("COMMIT");
        $response["success"] = 1;
        $response["message"] = "Uploaded successfully";

        mysql_query("START TRANSACTION");
        $resultz = mysql_query("DELETE FROM avatars where avatar_id IN (SELECT aid from (SELECT avatars.avatar_id as aid FROM avatars LEFT JOIN tbl_usersinfo ON tbl_usersinfo.avatar_id = avatars.avatar_id where tbl_usersinfo.avatar_id is null and avatars.avatar_id >12) AS C)");
        if($resultz){
          mysql_query("COMMIT");
        }else{
          mysql_query("ROLLBACK");
        }
        echo json_encode($response);    
    }else{
        mysql_query("ROLLBACK");
        $response["success"] = 0;
        $response["message"] = "Something went wrong";
        $response["errorCode"] = 1;
        echo json_encode($response);
    }

   //  $result = mysql_query("SELECT * FROM tbl_usersinfo LEFT JOIN avatars ON tbl_usersinfo.avatar_id = avatars.avatar_id where tbl_usersinfo.user_id = '$user_id'") or trigger_error(mysql_error());

   //  if(mysql_num_rows($result)>0){

   //      $row = mysql_fetch_array($result);
        
   //      $response["avatar"] = base64_encode($row["avatar_img"]);

   //      $response["success"] = 1;
   //      $response["message"] = "doneeee";
   //      echo json_encode($response);

   // //      $response["members"] = array();
    	
   // //      while($row = mysql_fetch_array($result)){
       		
   // //     		$member = array();
   // //          $member["user_id"] = $row["user_id"];
   // //          $member["full_name"] = $row["full_name"];
   // //          $member["email"] = $row["email"];
   // //          $member["user_level"] = $row["UserLevel"];
   // //         	$member["avatar"] = base64_encode($row["avatar_img"]);
			
			// // array_push($response["members"], $member);

            

   // //      }
   // //      $response["success"]=1;
   // //      echo json_encode($response);
   //  }else{
   //      $response["success"] = 0;
   //      $response["message"] = "no user found fck";
   //      $response["errorCode"] = 1;
   //      echo json_encode($response);
   //  }

    
 

}
?>