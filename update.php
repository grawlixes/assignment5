<?php 
$username = $_POST["username"];
$newAvatar = $_POST["newAvatar"];
$newBio = $_POST["newBio"];

# connecting
$dbname = "kfranke1_assignment5";
$dbpassword = "Imuabae6eita";

$dbuser = "kfranke1";
$host = "mysql.cs.binghamton.edu";
$cid = mysqli_connect($host, $dbuser, $dbpassword, $dbname);

$sql = "update users set avatar='" . $newAvatar . "', bio='" . $newBio . "' where username='" . $username . "';";
mysqli_query($cid, $sql);
