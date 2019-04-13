<?php
$post_id = $_POST["post_id"];
$op = $_POST["op"];
$comment = $_POST["comment"];
$type = $_POST["type"];
$avatar = $_POST["avatar"];

# connecting
$dbname = "kfranke1_assignment5";
$dbpassword = "Imuabae6eita";

$dbuser = "kfranke1";
$host = "mysql.cs.binghamton.edu";
$cid = mysqli_connect($host, $dbuser, $dbpassword, $dbname);

$sql = "insert into comments (post_id, op, comment, avatar, type) 
    values (" . $post_id . ", '" . $op . "', '" . addslashes($comment) . "', '" . $avatar . "', " . $type . ");";
echo $sql;
$result = mysqli_query($cid, $sql);
