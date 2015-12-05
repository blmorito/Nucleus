<?php
date_default_timezone_set('Asia/Manila');
//24hours expiry
$wew = date('M d, Y', time()+86400+86400);

//converting phptime to datetime
$phpdate = strtotime("2015-09-26");
$newphpdate = date('Y-m-d', $phpdate);
//echo $newphpdate."<br><br><br>";
$phpdatex = date( 'M d, Y', $phpdate );

$date1 = strtotime("2015/09/08");
$date2 = date( 'Y-m-d', $date1 );
echo $wew;

?>