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
    $password = $_POST["password"];
    $passwordSHA = sha1($password);

    $new_email = $_POST["new_email"];
    $new_password = $_POST["new_password"];
    $new_passwordSHA = sha1($new_password);

    
    $check = mysql_query("SELECT * FROM tbl_users where user_id = '$user_id' and password = '$passwordSHA'");
    $row = mysql_fetch_array($check);
    $old_email = $row["email"];

    if (mysql_num_rows($check)>0) {

      $check2 = mysql_query("SELECT * FROM tbl_users where email = '$new_email'");

      if(mysql_num_rows($check2)>0){
        $response["success"] = 0;
        $response["errorCode"] = 2;
        $response["message"] = "Email is already being used";
        echo json_encode($response);
      }else{
            
            $check3 = mysql_query("SELECT * from ws_invites where email = '$new_email'");
            if(mysql_num_rows($check3)>0){
                $response["success"] = 0;
                $response["errorCode"] = 4;
                $response["message"] = "Email is currently invited to a nucleus workspace/project";
                echo json_encode($response);
            }else{
                mysql_query("START TRANSACTION");

              if($new_password===""){
                  $result = mysql_query("UPDATE tbl_users set email = '$new_email' where user_id = '$user_id'");
                  $resultt = mysql_query("UPDATE ws_invites set email = '$new_email' where email = '$old_email'");
                  if($result && $resultt){
                     mysql_query("COMMIT");
                     $response["success"] = 1;
                     $response["message"] = "success changing email";
                     echo json_encode($response);
                  }else{
                     mysql_query("ROLLBACK");
                     $response["success"] = 0;
                     $response["errorCode"] = 3;
                     $response["message"] = "something went wrong";
                     echo json_encode($response);
                  }
              }else{
                  $result2 = mysql_query("UPDATE tbl_users set email = '$new_email', password = '$new_passwordSHA' where user_id = '$user_id'");
                  $result22 = mysql_query("UPDATE ws_invites set email = '$new_email' where email = '$old_email'");
                  if($result2 & $result22){
                     mysql_query("COMMIT");
                     $response["success"] = 1;
                     $response["message"] = "success changing email and pw";
                     echo json_encode($response);

                  }else{
                     mysql_query("ROLLBACK");
                     $response["success"] = 0;
                     $response["errorCode"] = 3;
                     $response["message"] = "something went wrong";
                     echo json_encode($response);
                  }
                }
            }
      
      }
      
    }else{
      $response["success"] = 0;
      $response["errorCode"] = 1;
      $response["message"] = "Invalid password";
      echo json_encode($response);
    }

    // mysql_query("START TRANSACTION");
    // $result = mysql_query("UPDATE tbl_usersinfo set full_name = '$full_name', comp_org = '$address' where user_id = '$user_id'");
    // if($result){
    //   mysql_query("COMMIT");
    //   $response["success"]=1;
    //   $response["message"] = "Profile Updated";
    //   echo json_encode($response);
    // }else{
    //   mysql_query("ROLLBACK");
    //   $response["success"]=0;
    //   $response["message"] = "Something went wrong";
    //   $response["errorCode"] = 1;

    //   echo json_encode($response);
    // }

   

    
 

}
?>