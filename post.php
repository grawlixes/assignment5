<?php 
$username = $_POST["username"];
$post = $_POST["post"];
$type = $_POST["type"];

# connecting
$dbname = "kfranke1_assignment5";
$dbpassword = "Imuabae6eita";

$dbuser = "kfranke1";
$host = "mysql.cs.binghamton.edu";
$cid = mysqli_connect($host, $dbuser, $dbpassword, $dbname);

$sql = "insert into posts (op, post, type) 
    values ('" . $username . "', '" . addslashes($post) . "', " . $type. ");";
echo $sql;
$result = mysqli_query($cid, $sql);
