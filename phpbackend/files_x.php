<?php
    $project_id = $_GET['project_id'];
    $user_id = $_GET['user_id'];
?>
<html>
<head>
    <title></title>
</head>
<body>
    <form id = "myForm" method = "POST" action = "files_upload.php">
        <input type = "hidden" name="project_id" id = "id" value = "<?php echo $project_id ?>">
        <input type = "hidden" name="user_id" id = "id" value = "<?php echo $user_id ?>">

    </form>
</body>

<script>
    
   document.getElementById("myForm").submit();
</script>
</html>