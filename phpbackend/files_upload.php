<?php 
    require_once __DIR__ . '/db_connect.php';
    require '../PHPMailer/PHPMailerAutoload.php';
    // connecting to db
    $db = new DB_CONNECT();
     
    $project_id = $_POST['project_id'];
    $user_id = $_POST['user_id'];
    $query = mysql_query("SELECT project_name from projects where project_id = '$project_id'");
    $row = mysql_fetch_array($query);

    $project_name = $row["project_name"];
    
?>


<html>
    <head>
        <title>Nucleus FILES</title>
        
        
        <link href="css/bootstrap.min.css" rel="stylesheet">
        <link href="css/bootstrap-theme.min.css" rel="stylesheet">
        
        <script src="js/jquery-1.11.1.min.js"></script>
        
    </head>
        
    <body style = "background-color:#00253a;">
        


        <div>
            
            <div class = "col-xs-12 well">
                
                <form method = "post" action = "add_file.php" enctype='multipart/form-data'>
                    <label style = "padding-top:10px; font-size:18px">Add a new file:</label>
                    <input name = "userfile" id = "userfile" class="file" type="file" data-min-file-count="1">
                    <input type = "hidden" id = "pid" name = "project_id" value = "<?php echo $project_id; ?>">
                    <input type = "hidden" id = "idx2" name = "user_id" value = "<?php echo $user_id; ?>">
                    <label style = "padding-top:20px">Add a description to this file:</label>
                    <textarea placeholder = "Ex. This file is for the documentation of the project." rows="2" class = "form-control" name = "filedesc" id = "filedesc" maxlength = "100" style = "margin-bottom:10px;font-size:13px;resize:none;overflow:hidden"></textarea>
                    
                    <div style = "text-align:right;padding-right:10px">
                        <input type = "submit" value = "Upload file" class = "btn btn-primary" onClick = "return empty()"> 
                        
                        
                    </div>
                   
                </form>
                
               
            </div>
            
            

        </div>
        
        
    

        <!-- scripts -->
        <script src="js/jquery-1.11.1.min.js"></script>
        <script src="js/bootstrap.min.js"></script>
        <script type="text/javascript">
            function empty()
            {
              var x;
              x = document.getElementById("userfile").value;
              if (x == "") 
               { 
                  alert("You cannot upload an empty file");
                  return false;
               };
            }
        </script>
        
        
    </body>
</html>

