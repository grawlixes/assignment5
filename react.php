<?php 
$id = $_POST["id"];
$like = $_POST["reaction"];
$isPost = $_POST["isPost"];

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

$table = "posts";
if ($isPost == 0) {
    $table = "comments";
}

$sql = "update " . $table . " set " . $reaction . "=" . $reaction . "+1 where id=" . $id . ";";
echo $sql;
$result = mysqli_query($cid, $sql);
