<?php 
    require_once __DIR__ . '/db_connect.php';
    require '../PHPMailer/PHPMailerAutoload.php';
    // connecting to db
    $db = new DB_CONNECT();
    
    if(isset($_FILES['userfile']))
    {
        $fileName = $_FILES['userfile']['name'];
        $tmpName  = $_FILES['userfile']['tmp_name'];
        $fileSize = $_FILES['userfile']['size'];
        $fileType = $_FILES['userfile']['type']; 
        $mimeType = $fileType;
        $filedesc = mysql_real_escape_string($_POST['filedesc']); 
        $idx2 = $_POST['user_id']; 
        $pid = $_POST['project_id']; 


        $fp      = fopen($tmpName, 'r');
        $content = fread($fp, filesize($tmpName));
        $content = addslashes($content);
        fclose($fp);

        $fileType = current(explode('/', $fileType));
        

        if(!get_magic_quotes_gpc())
        {
            $fileName = addslashes($fileName);
        }
        

        $query = "Insert into file (project_id,user_id,file_content,file_name,file_size,file_type,file_desc,mime_type,date_uploaded) values ('$pid','$idx2','$content','$fileName','$fileSize','$fileType','$filedesc','$mimeType',now())";
        $result = mysql_query($query);
       
        echo '<h1>File Uploaded Successfully</h1>';

    }
    else
    {
        echo '<h1>Something went wrong. Unable to upload the file.<br> Note: Some mobile browsers have bugs when using HTML5 features. Try using a different browser</h1>';
    }

?>