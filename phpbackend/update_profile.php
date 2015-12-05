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
    $full_name = $_POST["full_name"];
    $address = $_POST["address"];

    
    mysql_query("START TRANSACTION");
    $result = mysql_query("UPDATE tbl_usersinfo set full_name = '$full_name', comp_org = '$address' where user_id = '$user_id'");
    if($result){
      mysql_query("COMMIT");
      $response["success"]=1;
      $response["message"] = "Profile Updated";
      echo json_encode($response);
    }else{
      mysql_query("ROLLBACK");
      $response["success"]=0;
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