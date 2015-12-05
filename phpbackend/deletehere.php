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

$result = mysql_query("SELECT * FROM avatars WHERE avatar_id = 32");
$row = mysql_fetch_array($result);

echo '<img src="data:image/jpeg;base64,'.base64_encode( $row['avatar_img'] ).'"/>';

?>