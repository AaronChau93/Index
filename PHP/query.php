<?php
// check for required fields
if (isset($_GET['sql'])) {
    // include db connect class
    require_once __DIR__ . '/db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();

    // mysql inserting a new row
    $result = mysql_query($_GET['sql']);

    // Add the results into an array.
    $resultAsArray = array();
    if ($result) {
        while($row = mysql_fetch_array($result)) {
            $resultAsArray[] = $row;
        }
    }

    // Echo the array as a json.
    echo json_encode($resultAsArray);
}
?>