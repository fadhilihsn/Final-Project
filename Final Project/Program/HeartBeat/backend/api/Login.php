<?php
 include_once "koneksi.php";

	class usr{}
	
	$email = $_POST["email"];
	$password = $_POST["password"];
	
	if ((empty($email)) || (empty($password))) { 
		$response = new usr();
		$response->status = 0;
		$response->message = "Kolom tidak boleh kosong"; 
		$response->data = null;
		die(json_encode($response));
	}
	
	$query = mysqli_query($con, "SELECT * FROM user WHERE email='$email' AND password='$password'");

	$num = mysqli_num_rows($query);
	if ($num > 0){
		$row = mysqli_fetch_array($query);

		$response = new usr();
		$response->status = 1;
		$response->message = "Login success";
		$response->data = array(
			"user_id" => $row['id'],
			"email" => $row['email'],
			"nama" => $row['nama'],
			"phone" => $row['phone']
		);

		die(json_encode($response));
		
	} else { 
		$response = new usr();
		$response->status = 0;
		$response->message = "Login gagal";
		$response->data = null;
		die(json_encode($response));
	}
	
	mysqli_close($con);
?>