<?php 

$username = $_POST["username"];

# connecting
$dbname = "kfranke1_assignment5";
$dbpassword = "Imuabae6eita";

$dbuser = "kfranke1";
$host = "mysql.cs.binghamton.edu";
$cid = mysqli_connect($host, $dbuser, $dbpassword, $dbname);

# Find the max post id
$sql = "select MAX(id) as mid from posts where op='" . $username . "';";
$result = mysqli_query($cid, $sql);
$row = mysqli_fetch_array($result);
$maxId = $row['mid'];

# Find the min post id
$sql = "select MIN(id) as mid from posts where op='" . $username . "';";
$result = mysqli_query($cid, $sql);
$row = mysqli_fetch_array($result);
$minId = $row['mid'];

$ct = 0;
# Find the ten most recent posts (at most) using these ids
for ($i = $maxId; $i >= $minId and $ct < 10; $i--) {
    $sql = "select * from posts where id=" . $i . " and op='" . $username . "';";
    $result = mysqli_query($cid, $sql);
    if ($result->num_rows) {
        $row = mysqli_fetch_array($result);
        $id = $row["id"];
        $type = $row["type"];
        $op = $row["op"];
        $post = $row["post"];
        $likes = $row["likes"];
        $dislikes = $row["dislikes"];
        echo $id . "\n";
        echo $type . "\n";
        echo $op . "\n";
        echo $post . "\n";
        echo $likes . "\n";
        echo $dislikes . "\n";
        
        $sql = "select * from users where username='" . $op . "';";
        $result = mysqli_query($cid, $sql);
        $row = mysqli_fetch_array($result);
        $img = $row["avatar"];
        echo $img . "\n";
        $ct++;
    }
}
?>
