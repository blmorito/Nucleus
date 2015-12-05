<?php 
    require_once __DIR__ . '/db_connect.php';
    require '../PHPMailer/PHPMailerAutoload.php';
    // connecting to db
    $db = new DB_CONNECT();
     
    $project_id = $_GET['project_id'];
    $user_id = $_GET['user_id'];
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
                
                <table class = "table table-striped table-bordered" >
                    <?php
                        $result = mysql_query("SELECT f.file_id,f.project_id, f.user_id, tu.full_name, f.file_name, f.date_uploaded FROM `file` f LEFT JOIN (SELECT t.user_id, t.full_name from tbl_usersinfo t) tu ON tu.user_id = f.user_id where f.project_id = '$project_id'");


                        if(mysql_num_rows($result)>0){
                            ?>
                                <div class = "col-xs-12 well">
                                
                                    

                                    <div class = "col-xs-12">
                                        <!-- <center><b> Want to share a file? Add it now to your project's files hub &nbsp;</b><a href="http://192.168.8.120/nucleus/api/files.php?project_id=<?php echo $project_id.'&user_id='.$user_id; ?>" class = "btn btn-primary">Upload a File</a></center> -->

                                       
                                            <form method = "get" action = "files_x.php" >
                                                
                                                
                                                
                                                <input type = "hidden" id = "pid" name = "project_id" value = "<?php echo $project_id; ?>">
                                                <input type = "hidden" id = "idx2" name = "user_id" value = "<?php echo $user_id; ?>">
                                                
                                                <div class = "col-xs-12">
                                                    <b>Add a file now and start sharing with other project members</b>
                                                </div>
                                                <div class = "col-xs-12">
                                                    <input type="submit" class = "btn btn-default"value = "Upload a File">
                                                </div>
                                            </form>

                                        
                                      
                                    </div>

                                    
                                    
                                </div>
                            <?php

                            while($row = mysql_fetch_array($result)){

                                     $phpdate = strtotime($row["date_uploaded"]);
                                     $phpdatex = date( 'M d, Y \a\t g:i a', $phpdate );
                                     ?>
                                        <tr>
                                            <div class = "col-xs-12">
                                                <td class = "col-xs-1"><img src="fileicon.png" alt=""  height=50 width=40></img></td>
                                                <td class = "col-xs-9"><small><b><?php echo $row["file_name"]; ?></b><br><?php echo $phpdatex." -"." Uploaded by ".$row["full_name"] ?></smalll></td>

                                                <?php 

                                                    echo ' <td class = "col-xs-2"><a href = "http://192.168.8.120/nucleus/api/download_file.php?fileid='.$row["file_id"].'" class = "btn btn-info">Download</a></td>'
                                                ?>
                                               
                                                
                                            </div>
                                            
                                        </tr>
                                    <?php

                            }
                           

                        }else{

                            ?>
                            <div class="jumbotron">
                              <h1>Welcome to <?php echo $project_name."'s ";?> Files hub</h1>
                              <p>There are still no files uploaded on this project. Upload a file now and share it with everyone in your project.</p>
                              <p><a class="btn btn-primary btn-lg" href="http://192.168.8.120/nucleus/api/files_x.php?project_id=<?php echo $project_id.'&user_id='.$user_id; ?>" role="button">Upload File</a></p>
                            </div>
                            <?php


                        }
                    ?>
                    

                    



                </table>

                <!-- <div class = "col-xs-12" style = "margin-top:5px;">
                    <div class = "row col-xs-8">
                        <p><b>Welcome to my world bitch</b><small> 25mb</small></p>
                        <br>
                        <p>Uploaded by Brylle</p>

                        
                    </div>
                    <div class = "col-xs-4">
                        <p class = "pull-right small">Uploaded on September 5, 2015</p>
                        <a href  ="#" class = "btn btn-success">Download</a>


                    </div>


                    
                </div> -->
                
               
            </div>
            
            

        </div>
        
        
    

        <!-- scripts -->
        <script src="js/jquery-1.11.1.min.js"></script>
        <script src="js/bootstrap.min.js"></script>
        
        
    </body>
</html>

