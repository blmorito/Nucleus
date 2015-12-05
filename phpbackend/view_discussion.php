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
if (isset($_POST['discussion_id'])) {

    $discussion_id = $_POST["discussion_id"];

    $result = mysql_query("SELECT d.user_id, d.discussion_id, d.d_subject, d.d_body, d.date_posted, d.user_id,tui.full_name, a.avatar_img FROM `discussion`d LEFT JOIN (SELECT tu.user_id, tu.full_name, tu.avatar_id FROM tbl_usersinfo tu) tui ON tui.user_id = d.user_id LEFT JOIN (SELECT av.avatar_id, av.avatar_img from avatars av) a ON a.avatar_id = tui.avatar_id where d.discussion_id = '$discussion_id'");

    $row = mysql_fetch_array($result);

    $response["avatar_img"] = base64_encode($row["avatar_img"]);

    $phpdate = strtotime($row["date_posted"]);
    $phpdatex = date( 'M d, Y \a\t g:i a', $phpdate );
    $response["author_id"] = $row["user_id"];
    $response["date_posted"] = "Posted on ".$phpdatex;


    $result2 = mysql_query("SELECT c.comment_id, c.content, c.date_posted,c.user_id,  tu.full_name, av.avatar_img FROM `comment` c LEFT JOIN (SELECT t.user_id, t.full_name, t.avatar_id from tbl_usersinfo t) tu ON tu.user_id = c.user_id LEFT JOIN (SELECT a.avatar_id, a.avatar_img from avatars a) av ON av.avatar_id = tu.avatar_id WHERE comment_type = 'Discussion' and comment_type_id = $discussion_id ");

    if(mysql_num_rows($result2)>0){
        $response["hasComments"] = true;

        $response["comments"] = array();

        while($row = mysql_fetch_array($result2)){
            $comment = array();
            $comment["comment_id"] = $row["comment_id"];
            $comment["comment_content"] = $row["content"];

            $phpdate = strtotime($row["date_posted"]);
            
            
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
            $comment["comment_ago"] = $ago." ago";  
            $phpdate = strtotime($row["date_posted"]);
            $phpdatex = date( 'M d, Y \a\t g:i a', $phpdate );

            $comment["comment_date"] = $phpdatex;
            $comment["comment_author"] = $row["full_name"];
            $comment["comment_author_id"] = $row["user_id"];
            $comment["comment_avatar"] = base64_encode($row["avatar_img"]);

            array_push($response["comments"], $comment);


        }

    }else{
        $response["hasComments"] = false;
    }

    $response["success"] = 1;
    echo json_encode($response);
   
   
       
        
     

}
?>