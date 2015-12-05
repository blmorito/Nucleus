<?php
require_once __DIR__ . '/db_connect.php';
$db = new DB_CONNECT();

		$queryInsertToProjMembers = mysql_query("INSERT INTO projects_users (project_id, user_id, date_joined, user_level_id) VALUES ('5','5',now(), '3')") or trigger_error(mysql_error());

?>