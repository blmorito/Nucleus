<?php

	if(isset($_GET['fileid'])){
		$fileid = $_GET['fileid'];

		require_once __DIR__ . '/db_connect.php';
 
		// connecting to db
		$db = new DB_CONNECT();

		try {
			$querysel = "Select * from file where file_id = '$fileid'";
			$resultsel = mysql_query($querysel);
			$rowsel = mysql_fetch_array($resultsel);

			$filename = $rowsel['file_name'];
		    $mimetype = $rowsel['mime_type'];
		    $filesize = $rowsel['file_size'];
		    $filecontent = $rowsel['file_content'];

		    header("Content-length: ".$filesize);
		    header("Content-type: ".$mimetype);
		    header("Content-disposition: attachment; filename=".$filename);
  			header("Content-Description: PHP Generated Data");
  			header("Content-transfer-encoding: binary");
		    echo $filecontent;
		 }

		 catch (Exception $e) {
		 	echo "error";
		 }
	}

?>