<?php
// check for required fields
if (isset($_GET['sql'])) {
    // include db connect class
    require_once __DIR__ . '/db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
    $query = $_GET['sql'];

    // mysql inserting a new row
    $result = mysql_query($query);

    // Add the results into an array.
    $resultAsArray = array();
    if ($result) {
        if (strpos($query, "SELECT") === 0) {
            while($row = mysql_fetch_array($result)) {
                $resultAsArray[] = $row;
            }
        } else if (strpos($query, "INSERT") === 0) {
            $resultAsArray[] = array('id'=>mysql_insert_id());
        }
    } else if(!$result) {
        $resultAsArray[] = array('error'=>mysql_error(), 'query'=>$query);
    }

    // Echo the array as a json.
    echo json_encode($resultAsArray);
}
?>