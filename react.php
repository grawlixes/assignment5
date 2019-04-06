<?php 
$id = $_POST["id"];
$like = $_POST["reaction"];

# connecting
$dbname = "kfranke1_assignment5";
$dbpassword = "Imuabae6eita";

$dbuser = "kfranke1";
$host = "mysql.cs.binghamton.edu";
$cid = mysqli_connect($host, $dbuser, $dbpassword, $dbname);

$reaction = "likes";
if ($like == "n") {
    $reaction = "dislikes";
}

$sql = "update posts set " . $reaction . "=" . $reaction . "+1 where id=" . $id . ";";
echo $sql;
$result = mysqli_query($cid, $sql);
