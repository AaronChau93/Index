<?php
 
// array for JSON response
$response = array();
 


// check for required fields
// if (isset($_POST['table']) && isset($_POST['cols']) && isset($_POST['data'])) {
 
    $table = $_POST['table'];
    $cols = $_POST['table']; // split by commas
    $data = $_POST['data']; // split by commas
 
    // include db connect class
    require_once __DIR__ . '/db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
 
    // mysql inserting a new row
    $result = mysql_query("SELECT * FROM Branch");
    $resultAsArray = array();
    
    // echo "JSON Encode:<br>";
    // echo json_encode($result);

    // echo "<br><br>While loop:<br>";
    while($row = mysql_fetch_array($result)) {
        $resultAsArray[] = $row;
    }
    echo json_encode($resultAsArray);

?>