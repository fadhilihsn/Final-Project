<?php
	include 'koneksi.php';

	
	class usr{}

	$email = $_POST["email"];
	$password = $_POST["password"];
	$nama = $_POST["nama"];
	$phone = $_POST["phone"];

	if ((empty($email))) {
		$response = new usr();
		$response->status = 0;
		$response->message = "Kolom email tidak boleh kosong";
		$response->data = null;
		die(json_encode($response));

	} else {
		if (!empty($email) && !empty($password) && !empty($nama) && !empty($phone)){
			$num_rows = mysqli_num_rows(mysqli_query($con, "SELECT * FROM user WHERE email='".$email."'"));

			if ($num_rows == 0){
				$query = mysqli_query($con, "INSERT INTO user ( email, password, nama, phone) VALUES('".$email."','".$password."','".$nama."','".$phone."')");

				if ($query){
					$response = new usr();
					$response->status = 1;
					$response->message = "Register berhasil, silahkan login.";
					$response->data = null;
					die(json_encode($response));

				} else {
					$response = new usr();
					$response->status = 0;
					$response->message = "email sudah ada";
					$response->data = null;
					die(json_encode($response));
				}
			} else {		
				$response = new usr();
				$response->status = 0;
				$response->message = "email sudah ada";
				$response->data = null;
				die(json_encode($response));
			}
		}
	}

	mysqli_close($con);
?>