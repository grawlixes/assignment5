<?php 
$username = $_POST["username"];
$post = $_POST["post"];

# connecting
$dbname = "kfranke1_assignment5";
$dbpassword = "Imuabae6eita";

$dbuser = "kfranke1";
$host = "mysql.cs.binghamton.edu";
$cid = mysqli_connect($host, $dbuser, $dbpassword, $dbname);

$sql = "select * from users;";
$result = mysqli_query($cid, $sql);
while ($row = mysqli_fetch_array($result, MYSQL_ASSOC)) {
    echo "Username: " . $row['username'] . " Password: " . $row['password'] . "\n";
}

$sql = "select * from posts;";
$result = mysqli_query($cid, $sql);
while ($row = mysqli_fetch_array($result, MYSQL_ASSOC)) {
    echo "Id: " . $row['id'] . " OP: " . $row['op'] . " Post: " . 
        $row['post'] . " Likes: " . $row['likes'] . " Dislikes: " . 
        $row['dislikes'] . "\n";
}
