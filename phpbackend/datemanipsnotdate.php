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
 

  
  
    $goal_id =15;
    
    mysql_query("START TRANSACTION");
    $result = mysql_query("DELETE FROM goal where goal_id = '$goal_id'") or trigger_error(mysql_error());
    $result1 = mysql_query("DELETE FROM task where goal_id = '$goal_id'")  or trigger_error(mysql_error());
    $result2 = mysql_query("DELETE FROM comment where comment_type = 'Goal' and comment_type_id = '$goal_id'") or trigger_error(mysql_error());
    $result3 = mysql_query("DELETE FROM assignment where task_id IN (Select task_id from task where goal_id = '$goal_id'")) or trigger_error(mysql_error());

      if($result && $result1 && $result2 && $result3){
        mysql_query("COMMIT");
        
      }else{
        mysql_query("ROLLBACK");
        
      }

   
   
       
        
     


?>