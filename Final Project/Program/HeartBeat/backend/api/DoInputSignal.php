<?php
	include 'koneksi.php';

	
	class usr{}

	$user_id = $_POST["user_id"];
	$pulse = $_POST["pulse"];
	$cycles = $_POST["cycles"];
	$duration = $_POST["duration"];
	$signals = $_POST["signals"];

	if ((empty($user_id))) {
		$response = new usr();
		$response->status = 0;
		$response->message = "user_id tidak boleh kosong";
		$response->data = null;
		die(json_encode($response));
	} else {
		$query = mysqli_query($con, "INSERT INTO `signal` (`pulse`, `cycles`, `duration`, `signals`, `user_id`) VALUES ('$pulse', '$cycles', '$duration', '$signals', '$user_id')");

		if ($query){
			$query2 = mysqli_query($con, "SELECT LAST_INSERT_ID() as lastId");
					
			if ($query2){
				$response = new usr();
				$response->status = 1;
				$response->message = "Menambahkan signal Berhasil";
				$datas = array();
		
				while($row = mysqli_fetch_assoc($query2)){
					$datas = array(
						"id" => $row['lastId'],
						"pulse" => $pulse,
						"cycles" => $cycles,
						"duration" => $duration,
						"signals" => $signals,
						"user_id" => $user_id,
						"timestamp" => date("Y-m-d h:i:s")
					);		
				}
				
				$response->data = $datas;
		
				die(json_encode($response));
				
			}
		} else {
			$response = new usr();
			$response->status = 0;
			$response->message = "Gagal menambahkan signal";
			$response->data = null;
			die(json_encode($response));
		}
	}

	mysqli_close($con);
?>