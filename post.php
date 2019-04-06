<?php 
$username = $_POST["username"];
$post = $_POST["post"];

# connecting
$dbname = "kfranke1_assignment5";
$dbpassword = "Imuabae6eita";

$dbuser = "kfranke1";
$host = "mysql.cs.binghamton.edu";
$cid = mysqli_connect($host, $dbuser, $dbpassword, $dbname);

$sql = "insert into posts (op, post) 
    values ('" . $username . "', '" . addslashes($post) . "');";
echo $sql;
$result = mysqli_query($cid, $sql);
