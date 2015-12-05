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
    $goal_id = $_POST["goal_id"];

    $checkProject = mysql_query("SELECT * FROM goal where goal_id = '$goal_id'");
    if(mysql_num_rows($checkProject)>0){
      $r = mysql_fetch_array($checkProject);

      $getPlevel = mysql_query("SELECT user_level_id FROM projects_users where project_id = '$project_id' and user_id = '$user_id'");
      $row = mysql_fetch_array($getPlevel);

      $response["p_user_level_id"] = $row["user_level_id"];


      $getGoal = mysql_query("SELECT g.goal_id, g.goal_name,g.date_created, g.user_id, tu.full_name from goal g LEFT JOIN (SELECT user_id, full_name from tbl_usersinfo) tu ON tu.user_id = g.user_id where g.goal_id = '$goal_id'");
      $rr = mysql_fetch_array($getGoal);

      $response["goal_name"] = $rr["goal_name"];
      $response["goal_creator_id"] = $rr["user_id"];
      $response["goal_creator"] = $rr["full_name"];

      $phpdate = strtotime($rr["date_created"]);
            
            
      $time = time() - $phpdate; // to get the time since that moment

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
      $response["goal_date"] = $ago." ago";  

      $response["success"] = 1;

      $qq = mysql_query("SELECT project_name from projects where project_id = '$project_id'");
      $rp = mysql_fetch_array($qq);

      $response["project_name"] = $rp["project_name"];
      echo json_encode($response);

    }else{
      $response["success"] = 0;
      echo json_encode($response);
    }
    
   
   
       
        
     

}
?>