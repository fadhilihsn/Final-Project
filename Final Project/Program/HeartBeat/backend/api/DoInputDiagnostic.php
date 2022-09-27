<?php
	include 'koneksi.php';

	
	class usr{}

	$user_id = $_POST["user_id"];
	$perokok = $_POST["perokok"];
	$diabetes = $_POST["diabetes"];
	$kolestrol = $_POST["kolestrol"];
	$hipertensi = $_POST["hipertensi"];

	if ((empty($user_id))) {
		$response = new usr();
		$response->status = 0;
		$response->message = "user_id tidak boleh kosong";
		$response->data = null;
		die(json_encode($response));
	} else {
		$query = mysqli_query($con, "INSERT INTO `diagnostic` (`user_id`, `perokok`, `diabetes`, `kolestrol`, `hipertensi`) VALUES ('$user_id', '$perokok', '$diabetes', '$kolestrol', '$hipertensi')");

		if ($query){
		    $response = new usr();
			$response->status = 1;
			$response->message = "Menambahkan diagnosa Berhasil";
			$datas = array();
			
			die(json_encode($response));
			
		} else {
			$response = new usr();
			$response->status = 0;
			$response->message = "Gagal menambahkan diagnosa";
			$response->data = null;
			die(json_encode($response));
		}
	}

	mysqli_close($con);
?>