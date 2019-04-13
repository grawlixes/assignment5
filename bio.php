<?php

$username = $_POST["username"];

# connecting
$dbname = "kfranke1_assignment5";
$dbpassword = "Imuabae6eita";

$dbuser = "kfranke1";
$host = "mysql.cs.binghamton.edu";
$cid = mysqli_connect($host, $dbuser, $dbpassword, $dbname);

# Find the max post id
$sql = "select * from users where username='" . $username . "';";
$result = mysqli_query($cid, $sql);
$row = mysqli_fetch_array($result);
$bio = $row['bio'];

echo $bio . "\n";
