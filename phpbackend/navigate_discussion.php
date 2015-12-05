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
    $project_id = $_POST["project_id"];
    $discussion_id = $_POST["discussion_id"];

    $checkDiscussion = mysql_query("SELECT * FROM discussion where discussion_id = '$discussion_id'") or trigger_error(mysql_error());
    $num = mysql_num_rows($checkDiscussion);

    if ($num>0){

      $getPlevel = mysql_query("SELECT user_level_id FROM projects_users where project_id = '$project_id' and user_id = '$user_id'");
      $row2 = mysql_fetch_array($getPlevel);

      $response["p_user_level_id"] = $row2["user_level_id"];

      $getDiscussionInfo = mysql_query("SELECT d.discussion_id, d.user_id, tu.full_name, d.date_posted, d.d_subject, d.d_body, c.comment_count FROM `discussion` d LEFT JOIN tbl_usersinfo tu ON tu.user_id = d.user_id LEFT JOIN (SELECT comment_type_id, count(*) as comment_count from comment where comment_type='Discussion' GROUP BY comment_type_id) c ON c.comment_type_id = d.discussion_id where d.discussion_id = '$discussion_id' ") or trigger_error(mysql_error());

      $row = mysql_fetch_array($getDiscussionInfo);
      $response["discussion_id"] = $row["discussion_id"];
      $response["discussion_author"] = $row["full_name"];
      #$discussion["discussion_author_id"] = $row["user_id"];


      // if($row["comment_count"]>0){
      //     $discussion["discussion_num_comments"] = $row["comment_count"];
      // }else{
      //     $discussion["discussion_num_comments"] = 0;
      // }

      $response["subject"] = $row["d_subject"];
      $response["body"] = $row["d_body"];

      $response["success"] = 1;
      $response["message"] = 'Succeswsss';

      echo json_encode($response);



    }else{
      $response["success"] = 0;
      $response["message"] = "The discussion has been deleted or non existent";
      echo json_encode($response);
    }

    
   
   
       
        
     

}
?>